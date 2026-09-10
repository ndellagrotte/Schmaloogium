// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

/**
 * The effective blend-state signal (PHASE_6_DOC §4.12, D-P6-32): reported by Phase 7
 * after a successful effective GlStateManager/backend change, including Phase 1 override
 * acquisition/release. Suppressed attempts emit no event. Disabled blending carries zero
 * factors. Immutable.
 */
public record BlendSample(
        long worldEpoch,
        long frameId,
        boolean enabled,
        int srcRgb,
        int dstRgb,
        int srcAlpha,
        int dstAlpha) {
}
