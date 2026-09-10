// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

/**
 * The accepted-frame timing report (PHASE_6_DOC §2.2/§4.6, D-P6-25). Copies the exact
 * accepted input identity/tick/seconds plus the actual post-update cadence-engine
 * {@code frameCounter}/{@code frameTimeCounter} cells — never recomputed from the frame
 * id, capture ordinal or wall time. Immutable historical evidence: it survives
 * invalidation, cannot authorize a later runtime or frame, and Phase 7 authenticates its
 * epoch against its live frame.
 */
public record UniformFrameTiming(
        long registryGeneration,
        long frameId,
        long worldEpoch,
        long logicalTick,
        double smoothingTimeTicks,
        float frameTimeSeconds,
        int frameCounter,
        float frameTimeCounter) {
}
