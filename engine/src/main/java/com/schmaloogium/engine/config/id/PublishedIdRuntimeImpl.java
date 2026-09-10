// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.uniforms.UniformEventSink;

/**
 * One immutable publication (PHASE_9_DOC §2.2/§4.1). Retirement keeps every hot object
 * alive for existing borrows; close is the drain-verified final step owned by Phase 7.
 * The per-draw state machine is created once, on first use at the render thread.
 */
final class PublishedIdRuntimeImpl implements PublishedIdRuntime {

    private final long generation;
    private final IdRuntimeFingerprint fingerprint;
    private final AliasLookupImpl aliases;
    private final RenderLayerLookupImpl renderLayers;
    private final UniformEventSink sink;
    private final DiagnosticReporter diagnostics;
    private PerDrawDynamicsImpl perDraw;
    private boolean retired;
    private boolean closed;

    PublishedIdRuntimeImpl(IdResolutionTables tables, long generation,
            IdRuntimeFingerprint fingerprint, UniformEventSink sink,
            DiagnosticReporter diagnostics) {
        this.generation = generation;
        this.fingerprint = fingerprint;
        this.sink = sink;
        this.diagnostics = diagnostics;
        this.aliases = new AliasLookupImpl(this, generation, tables);
        this.renderLayers = new RenderLayerLookupImpl(this, tables);
    }

    @Override
    public long generation() {
        checkOpen();
        return generation;
    }

    @Override
    public IdRuntimeFingerprint fingerprint() {
        checkOpen();
        return fingerprint;
    }

    @Override
    public AliasLookup aliases() {
        checkOpen();
        return aliases;
    }

    @Override
    public RenderLayerLookup renderLayers() {
        checkOpen();
        return renderLayers;
    }

    @Override
    public PerDrawDynamics perDraw() {
        checkOpen();
        if (perDraw == null) {
            perDraw = new PerDrawDynamicsImpl(generation, aliases, sink, diagnostics);
        }
        return perDraw;
    }

    @Override
    public void close() {
        closed = true;
    }

    /** Marks the runtime retired: no new borrows target it, borrows stay valid. */
    void retire() {
        retired = true;
    }

    boolean isRetired() {
        return retired;
    }

    void checkOpen() {
        if (closed) {
            throw new IllegalStateException("publication " + generation + " is closed");
        }
    }
}
