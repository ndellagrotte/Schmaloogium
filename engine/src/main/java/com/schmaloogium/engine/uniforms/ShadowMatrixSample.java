// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

import java.util.Objects;

/**
 * The shadow-camera signal (PHASE_6_DOC §4.2/§4.12). Phase 8's shadow projection and
 * model-view; no {@code previous*} contract. Values are Phase 8's at v0.2; the interface
 * and cells exist at v0.1 with identity-matrix neutrals. Immutable.
 */
public record ShadowMatrixSample(
        long worldEpoch,
        long frameId,
        Matrix4Value projection,
        Matrix4Value modelView) {

    public ShadowMatrixSample {
        Objects.requireNonNull(projection, "projection");
        Objects.requireNonNull(modelView, "modelView");
    }
}
