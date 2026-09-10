// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.uniforms.UniformEventSink;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The balanced per-entity and per-block-entity scope state machine (PHASE_9_DOC §4.12).
 * Fixed-capacity primitive stacks hold the pushed prior values; only the exact LIFO
 * token closes a scope. Overflow, wrong order, or a post-drain token neutralizes the
 * affected cells, disables that producer for the frame, reports once on the frame
 * channel, and leaves the drain to {@link #resetFrame()}. Render-thread confined through
 * the admission's owner thread.
 */
final class PerDrawDynamicsImpl implements PerDrawDynamics {

    /** Ordinary nesting depth; growth beyond this cap is a hostile-renderer guard. */
    static final int STACK_CAP = 64;

    private final long generation;
    private final AliasLookup lookup;
    private final UniformEventSink sink;
    private final DiagnosticReporter diagnostics;
    private final Set<String> reported = new HashSet<>();

    private final IdScopeToken[] entityTokens = new IdScopeToken[STACK_CAP];
    private final int[] entityPrior = new int[STACK_CAP];
    private final IdScopeToken[] blockTokens = new IdScopeToken[STACK_CAP];
    private final int[] blockPrior = new int[STACK_CAP];

    private int entityDepth;
    private int blockDepth;
    private int entityValue;
    private int blockValue;
    private long epoch;
    private boolean entityDisabled;
    private boolean blockDisabled;

    PerDrawDynamicsImpl(long generation, AliasLookup lookup, UniformEventSink sink,
            DiagnosticReporter diagnostics) {
        this.generation = generation;
        this.lookup = lookup;
        this.sink = sink;
        this.diagnostics = diagnostics;
    }

    @Override
    public IdScopeResult enterEntity(IdScopeAdmission admission, int entityOrdinal) {
        IdScopeRejection rejection = authenticate(admission, entityDisabled);
        if (rejection != null) {
            return new IdScopeResult.Rejected(rejection);
        }
        if (entityDepth == STACK_CAP) {
            neutralize(IdScopeToken.Kind.ENTITY, "stack-overflow");
            return new IdScopeResult.Rejected(IdScopeRejection.STACK_LIMIT);
        }
        return enter(admission, IdScopeToken.Kind.ENTITY, entityOrdinal);
    }

    @Override
    public IdScopeResult enterBlockEntity(IdScopeAdmission admission, int stateOrdinal) {
        IdScopeRejection rejection = authenticate(admission, blockDisabled);
        if (rejection != null) {
            return new IdScopeResult.Rejected(rejection);
        }
        if (blockDepth == STACK_CAP) {
            neutralize(IdScopeToken.Kind.BLOCK_ENTITY, "stack-overflow");
            return new IdScopeResult.Rejected(IdScopeRejection.STACK_LIMIT);
        }
        return enter(admission, IdScopeToken.Kind.BLOCK_ENTITY, stateOrdinal);
    }

    private IdScopeResult enter(IdScopeAdmission admission, IdScopeToken.Kind kind,
            int ordinal) {
        int mapped = kind == IdScopeToken.Kind.ENTITY
                ? mappedEntityId(ordinal)
                : mappedBlockEntityId(ordinal);
        IdScopeToken token = new IdScopeToken(kind, depthOf(kind), generation,
                admission.frameId(), epoch, admission.shadow());
        tokenOf(kind)[depthOf(kind)] = token;
        priorOf(kind)[depthOf(kind)] = valueOf(kind);
        setDepth(kind, depthOf(kind) + 1);
        setValue(kind, mapped);
        updateSink(kind, mapped);
        return new IdScopeResult.Entered(token);
    }

    @Override
    public void leave(IdScopeToken token) {
        if (token == null) {
            // Kind unknowable: neutralize both producers for the frame.
            neutralize(IdScopeToken.Kind.ENTITY, "wrong-token");
            neutralize(IdScopeToken.Kind.BLOCK_ENTITY, "wrong-token");
            return;
        }
        if (token.epoch() != epoch) {
            // Already drained at the frame boundary: report once, change nothing.
            report("stale-token", token.kind());
            return;
        }
        IdScopeToken.Kind kind = token.kind();
        if (disabled(kind) || depthOf(kind) == 0 || tokenOf(kind)[depthOf(kind) - 1] != token) {
            neutralize(kind, "wrong-token");
            return;
        }
        token.markClosed();
        setDepth(kind, depthOf(kind) - 1);
        tokenOf(kind)[depthOf(kind)] = null;
        int prior = priorOf(kind)[depthOf(kind)];
        setValue(kind, prior);
        updateSink(kind, prior);
    }

    @Override
    public void resetFrame() {
        epoch++;
        entityDepth = 0;
        blockDepth = 0;
        java.util.Arrays.fill(entityTokens, null);
        java.util.Arrays.fill(blockTokens, null);
        entityDisabled = false;
        blockDisabled = false;
        if (entityValue != 0) {
            entityValue = 0;
            sink.updateEntityId(0);
        }
        if (blockValue != 0) {
            blockValue = 0;
            sink.updateBlockEntityId(0);
        }
    }

    // ------------------------------------------------------------------ internals

    private IdScopeRejection authenticate(IdScopeAdmission admission, boolean disabled) {
        if (admission == null || !admission.active() || disabled) {
            return IdScopeRejection.STALE_ADMISSION;
        }
        if (admission.generation() != generation) {
            return IdScopeRejection.STALE_GENERATION;
        }
        if (admission.ownerThread() != Thread.currentThread()) {
            return IdScopeRejection.WRONG_THREAD;
        }
        return null;
    }

    private int mappedEntityId(int entityOrdinal) {
        AliasValue alias = lookup.entityId(entityOrdinal);
        return alias.present() ? alias.shaderId() : 0;
    }

    private int mappedBlockEntityId(int stateOrdinal) {
        AliasValue alias = lookup.blockId(stateOrdinal);
        return alias.present() ? alias.shaderId() : 0;
    }

    /** Clears one stack, writes neutral zero, and disables that producer for the frame. */
    private void neutralize(IdScopeToken.Kind kind, String violation) {
        setDepth(kind, 0);
        java.util.Arrays.fill(kind == IdScopeToken.Kind.ENTITY ? entityTokens : blockTokens,
                null);
        if (valueOf(kind) != 0) {
            setValue(kind, 0);
            updateSink(kind, 0);
        }
        setDisabled(kind, true);
        report(violation, kind);
    }

    private void report(String violation, IdScopeToken.Kind kind) {
        if (!reported.add(violation + ":" + kind)) {
            return;
        }
        diagnostics.report(new EngineDiagnostic(DiagnosticSeverity.WARN, UserChannel.LOG_ONLY,
                "schmaloogium.warn.ids.scope_protocol", List.of(kind, violation),
                "generation " + generation, LogChannels.FRAME));
    }

    private int depthOf(IdScopeToken.Kind kind) {
        return kind == IdScopeToken.Kind.ENTITY ? entityDepth : blockDepth;
    }

    private void setDepth(IdScopeToken.Kind kind, int depth) {
        if (kind == IdScopeToken.Kind.ENTITY) {
            entityDepth = depth;
        } else {
            blockDepth = depth;
        }
    }

    private int valueOf(IdScopeToken.Kind kind) {
        return kind == IdScopeToken.Kind.ENTITY ? entityValue : blockValue;
    }

    private void setValue(IdScopeToken.Kind kind, int value) {
        if (kind == IdScopeToken.Kind.ENTITY) {
            entityValue = value;
        } else {
            blockValue = value;
        }
    }

    private IdScopeToken[] tokenOf(IdScopeToken.Kind kind) {
        return kind == IdScopeToken.Kind.ENTITY ? entityTokens : blockTokens;
    }

    private int[] priorOf(IdScopeToken.Kind kind) {
        return kind == IdScopeToken.Kind.ENTITY ? entityPrior : blockPrior;
    }

    private boolean disabled(IdScopeToken.Kind kind) {
        return kind == IdScopeToken.Kind.ENTITY ? entityDisabled : blockDisabled;
    }

    private void setDisabled(IdScopeToken.Kind kind, boolean value) {
        if (kind == IdScopeToken.Kind.ENTITY) {
            entityDisabled = value;
        } else {
            blockDisabled = value;
        }
    }

    private void updateSink(IdScopeToken.Kind kind, int value) {
        if (kind == IdScopeToken.Kind.ENTITY) {
            sink.updateEntityId(value);
        } else {
            sink.updateBlockEntityId(value);
        }
    }
}
