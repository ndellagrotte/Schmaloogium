// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.gl;

import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.gl.DebugService;
import com.schmaloogium.engine.gl.FramebufferHandle;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.GLHandle;
import com.schmaloogium.engine.gl.ProgramHandle;
import com.schmaloogium.engine.gl.ShaderHandle;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.log.LogChannels;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * A5's KHR_debug backend — Phase 1's {@link DebugService} implementation
 * (PHASE_14_DOC §4.5; helper home mod.glue.gl per P1 D-P1-54). Balance-safe by
 * construction (D-P14-13, the PD §17 B7 do-not-inherit): the backend owns
 * {@code realDepth}/{@code virtualDepth}; capacity overflow goes virtual with no GL,
 * virtual pops issue nothing, underflow is a no-op with one rate-limited diagnostic,
 * and the frame-boundary drain ({@link #drainAtBoundary}, D-P14-37) removes virtual
 * groups without GL, then issues exactly the outstanding real pops — the driver's
 * default group can never be popped for a caller's leaked push.
 *
 * <p>Activity gate (D-P14-14, P1 D-P1-54): {@code (GL 4.3 || GL_KHR_debug) &&
 * -Dschmaloogium.debug.glLabels} — a debug context is an enhancement, never a
 * precondition. Under {@code DebugTier.NONE} the whole service is free: zero ops
 * calls, zero counters.
 *
 * <p>Labels (D-P14-36): the payload budget is {@code GL_MAX_LABEL_LENGTH - 1} encoded
 * UTF-8 bytes (probed once), truncation lands only on a complete code-point boundary,
 * and emission happens only for an existing native object — an unmaterialized texture
 * keeps its label retained on the handle (D-P1-71/74) and the backend issues no GL.
 *
 * <p>Render-thread confined (PHASE_14_DOC §7.1); the installing adapter owns the
 * render-thread/context guards, exactly as for every facade verb.
 */
public final class KhrDebugBackend implements DebugService {

    // KHR_debug identifier values, pinned from the GL specification (stable forever).
    private static final int GL_TEXTURE = 0x1706;
    private static final int GL_SHADER = 0x82E0;
    private static final int GL_PROGRAM = 0x82E2;
    private static final int GL_FRAMEBUFFER = 0x82D8;

    private static final String KHR_DEBUG = "GL_KHR_debug";

    private final GLCapabilityProfile profile;
    private final BooleanSupplier glLabels;
    private final LabelTargetResolver targets;
    private final DebugOps ops;
    private final DiagnosticReporter diagnostics;

    /** Probed once at construction (D-P14-36); capacity 0 when the probe fails. */
    private final int maxGroupDepth;
    /** {@code GL_MAX_LABEL_LENGTH - 1} encoded bytes; negative disables labeling. */
    private final int labelBudget;

    private int realDepth;
    private int virtualDepth;
    private boolean underflowDiagnosed;
    private boolean imbalanceDiagnosed;

    public KhrDebugBackend(GLCapabilityProfile profile,
                           BooleanSupplier glLabels,
                           LabelTargetResolver targets,
                           DebugOps ops,
                           DiagnosticReporter diagnostics) {
        if (profile == null || glLabels == null || targets == null || ops == null
                || diagnostics == null) {
            throw new IllegalArgumentException("all backend collaborators must not be null");
        }
        this.profile = profile;
        this.glLabels = glLabels;
        this.targets = targets;
        this.ops = ops;
        this.diagnostics = diagnostics;
        this.maxGroupDepth = Math.max(0, ops.maxGroupStackDepth());
        this.labelBudget = ops.maxLabelLength() - 1;
    }

    @Override
    public boolean isActive() {
        return (profile.atLeast(4, 3) || profile.hasExtension(KHR_DEBUG))
                && glLabels.getAsBoolean();
    }

    // ------------------------------------------------------------------ groups (D-P14-13)

    @Override
    public void pushGroup(String labelGroup) {
        if (!isActive() || labelGroup == null) {
            return;
        }
        if (realDepth < maxGroupDepth - 1) {
            if (ops.pushGroup(labelGroup)) {
                realDepth++;
            }
            return;
        }
        virtualDepth++; // at capacity: no GL, the drain removes these without popping
    }

    @Override
    public void popGroup() {
        if (!isActive()) {
            return;
        }
        if (virtualDepth > 0) {
            virtualDepth--;
            return;
        }
        if (realDepth > 0) {
            if (ops.popGroup()) {
                realDepth--;
            }
            return;
        }
        // Underflow: no-op, one rate-limited diagnostic per session naming the case.
        if (!underflowDiagnosed) {
            underflowDiagnosed = true;
            diagnostics.report(new EngineDiagnostic(DiagnosticSeverity.ERROR,
                    UserChannel.LOG_ONLY, "schmaloogium.gl.debug.groupUnderflow", List.of(),
                    "debug.popGroup underflow: no group is open; no GL was issued", LogChannels.GL));
        }
    }

    /**
     * D-P14-37 frame-boundary assertion (P7 finish/abort in the deferred wiring): both
     * counters must be zero. Virtual entries are removed with no GL, then exactly the
     * outstanding real groups are popped — one rate-limited recovery diagnostic, never
     * one per leaked group.
     */
    public void drainAtBoundary() {
        if (virtualDepth == 0 && realDepth == 0) {
            return;
        }
        virtualDepth = 0;
        while (realDepth > 0) {
            if (!ops.popGroup()) {
                break; // a failed pop leaves the count honest; a later drain retries
            }
            realDepth--;
        }
        if (!imbalanceDiagnosed) {
            imbalanceDiagnosed = true;
            diagnostics.report(new EngineDiagnostic(DiagnosticSeverity.ERROR,
                    UserChannel.LOG_ONLY, "schmaloogium.gl.debug.groupImbalance", List.of(),
                    "unbalanced debug group stack drained at the frame boundary "
                            + "(virtual entries dropped without GL, real groups popped)", LogChannels.GL));
        }
    }

    // ------------------------------------------------------------------ labels (D-P14-36/40)

    @Override
    public void label(GLHandle handle, String label) {
        if (!isActive() || handle == null || label == null || labelBudget < 0) {
            return;
        }
        int name = targets.nativeName(handle);
        if (name < 0) {
            return; // unmaterialized/foreign: the handle retains the label, no GL (D-P14-40)
        }
        Integer identifier = identifierFor(handle);
        if (identifier == null) {
            return; // outside the four categories: never guess an identifier
        }
        ops.label(identifier, name, clampToUtf8Budget(label, labelBudget));
    }

    /** Truncates to at most {@code budgetBytes} encoded UTF-8 bytes, cutting only at a
     *  complete code-point boundary (D-P14-36). Package-private for the coverage test. */
    static String clampToUtf8Budget(String label, int budgetBytes) {
        byte[] bytes = label.getBytes(StandardCharsets.UTF_8);
        if (bytes.length <= budgetBytes) {
            return label;
        }
        int cut = budgetBytes;
        while (cut > 0 && (bytes[cut] & 0xC0) == 0x80) {
            cut--; // step back over continuation bytes to the next complete boundary
        }
        return new String(bytes, 0, cut, StandardCharsets.UTF_8);
    }

    private static Integer identifierFor(GLHandle handle) {
        if (handle instanceof TextureHandle) {
            return GL_TEXTURE;
        }
        if (handle instanceof ShaderHandle) {
            return GL_SHADER;
        }
        if (handle instanceof ProgramHandle) {
            return GL_PROGRAM;
        }
        if (handle instanceof FramebufferHandle) {
            return GL_FRAMEBUFFER;
        }
        return null;
    }

    // test observation (same package)
    int realDepth() {
        return realDepth;
    }

    int virtualDepth() {
        return virtualDepth;
    }
}
