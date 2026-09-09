// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import java.util.List;

/**
 * The five tiers actually selected, plus why (R-P14→P1-2, PHASE_1_DOC §4.7.8;
 * PHASE_14_DOC §2.1): an immutable record of {@link SamplerTier}, {@link DsaTier},
 * {@link DebugTier}, {@link AsyncCompileTier} and {@link AsyncReadbackTier}, plus an
 * immutable ordered list of rationale strings. Derived by a PURE function of
 * {@link GLCapabilityProfile} + {@link GlModernizationPolicy}, once, at bring-up, and
 * never mutated for the life of the GL context; the derivation itself and the
 * {@code mod.glue.gl} helpers are Phase 14's (D-P1-54). Consumers: diagnostics, Phase 12
 * for display, Phase 2's run manifest (proposed only).
 */
public record GlModernizationPlan(SamplerTier sampler, DsaTier dsa, DebugTier debug,
                                  AsyncCompileTier compile, AsyncReadbackTier readback,
                                  List<String> rationale) {

    public GlModernizationPlan {
        if (sampler == null || dsa == null || debug == null || compile == null || readback == null) {
            throw new IllegalArgumentException("every tier must be selected");
        }
        if (rationale == null || rationale.isEmpty()) {
            throw new IllegalArgumentException("rationale must name the deciding capability for each row");
        }
        rationale = List.copyOf(rationale);
    }
}
