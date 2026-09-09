// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.ProgramKey;

import java.util.Optional;

/** One evaluated program state over the finalized option state. */
public record EvaluatedProgramState(
        ProgramKey key,
        Optional<AlphaTestSpec> alphaTest,
        Optional<BlendSpec> blend,
        Optional<ViewportScale> scale,
        boolean propertyEnabled,
        boolean profileDisabled,
        boolean finalEnabled) {

    public EvaluatedProgramState {
        java.util.Objects.requireNonNull(key, "key");
        alphaTest = alphaTest == null ? Optional.empty() : alphaTest;
        blend = blend == null ? Optional.empty() : blend;
        scale = scale == null ? Optional.empty() : scale;
    }
}
