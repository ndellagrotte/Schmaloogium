// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import com.schmaloogium.engine.uniforms.Matrix4Value;

import java.util.Objects;

/**
 * The complete §4.5.2 projection result, immutable. {@code projection} is the perspective
 * matrix (FOV from the pack, absent → 110°); {@code modelView} is
 * {@code T(0,0,-100) · Rx(90°) · Rz(θ)} — no snap translation, which Phase 6 composes
 * itself as {@code M0 = T(snap) · modelView}; {@code lightDirectionWorld} is the
 * normalized direction from the camera/world toward the active light, ready for the
 * §4.7 traversal prism; {@code thetaRadians} is the celestial angle actually used.
 */
public record ShadowCameraProjection(
        Matrix4Value projection,
        Matrix4Value modelView,
        com.schmaloogium.engine.uniforms.Float3 lightDirectionWorld,
        double thetaRadians) {

    public ShadowCameraProjection {
        Objects.requireNonNull(projection, "projection");
        Objects.requireNonNull(modelView, "modelView");
        Objects.requireNonNull(lightDirectionWorld, "lightDirectionWorld");
    }
}
