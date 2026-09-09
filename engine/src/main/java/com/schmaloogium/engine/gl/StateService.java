// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import java.util.EnumSet;
import java.util.Optional;

/**
 * The deliberately narrow state service (PHASE_1_DOC §4.7.4, D-P1-31): a verb exists when
 * the reference pass structure requires the ENGINE itself to perturb that state —
 * viewport, clears, depth mask, depth test, blend, alpha test and fog (the composite/final
 * block of RESEARCH.md §4.4 plus per-program alpha/blend from App F.7). Colour mask and
 * face culling are out, each with a deferred-table row; no matrix state exists by design.
 *
 * <p>Every verb whose GL state {@code GlStateManager} caches must be issued THROUGH it by
 * the backend ([D-P1-29]) — a rule about implementation, not about which verbs exist.
 * {@code null}/{@code absent} disables where noted; present values are always enabled
 * configurations.
 */
public interface StateService {

    void viewport(int x, int y, int w, int h);

    void clearColor(float r, float g, float b, float a);

    void clear(EnumSet<ClearTarget> targets);

    /** Depth WRITES. */
    void depthMask(boolean enabled);

    /** Depth TEST — a different bit of state. */
    void depthTest(boolean enabled);

    /** {@code null}/absent = disabled. */
    void blend(BlendState state);

    /** {@code null}/absent = disabled. */
    void alphaTest(AlphaTestState state);

    /**
     * The per-program duration lock (D-P1-57, R30-1): requires non-null optionals; empty
     * leaves that aspect ordinary, present disabled state locks OFF. One lease per
     * device, including an empty lease; nested acquisition rejects before mutation. The
     * returned {@link AlphaBlendOverride} is opaque and backend-implemented; close
     * restores only the held aspects to the exact pre-lock snapshot.
     */
    AlphaBlendOverride lockAlphaBlend(
            Optional<AlphaTestState> alpha, Optional<BlendState> blend);

    /** The complete effective cached/native-coherent blend value, including disabled
     *  factors (D-P1-57). */
    BlendState effectiveBlend();

    /** {@code null}/absent = disabled. */
    void fog(FogState state);

    /** Snapshot the state we are about to perturb, for the §G4.6 restore discipline.
     *  {@link StateAspect} is an engine enum with one constant per verb above. */
    StateSnapshot snapshot(EnumSet<StateAspect> aspects);

    void restore(StateSnapshot snapshot);
}
