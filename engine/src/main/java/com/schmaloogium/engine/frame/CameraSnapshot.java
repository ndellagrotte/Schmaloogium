// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

import com.schmaloogium.engine.uniforms.Matrix4Value;

/**
 * The exactly-two-matrix camera payload (PHASE_7_DOC §5.1). Exactly two matrices: callers
 * cannot authenticate a substituted snapshot by value equality — only the bridge's copy at
 * the H-FRAME-04 post-camera point is trusted, because the driver authenticates the token,
 * not the payload.
 */
public record CameraSnapshot(Matrix4Value modelView, Matrix4Value projection) {

    public CameraSnapshot {
        java.util.Objects.requireNonNull(modelView, "modelView");
        java.util.Objects.requireNonNull(projection, "projection");
    }
}
