// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.Map;
import java.util.Optional;

/** Raw per-program property state of one declared program key. */
public record ProgramState(
    Optional<AlphaTestSpec> alphaTest,
    Optional<BlendSpec> blend,
    Optional<ViewportScale> scale,
    Map<FlipBufferKey, FlipOverride> flips,
    Optional<ProgramEnabledExpression> enabledExpression) {

    public ProgramState {
        flips = flips == null ? Map.of() : Map.copyOf(flips);
    }
}
