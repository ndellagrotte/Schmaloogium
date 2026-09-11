// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core.pipeline;

import com.schmaloogium.engine.pack.PackConfiguration;
import com.schmaloogium.engine.uniforms.UniformRuntime;

import java.util.Objects;

/** What the transaction retains about the installed tuple beyond the driver's view. */
public record ActivePipeline(
        FrameCompositionRecord composition,
        PackConfiguration configuration,
        UniformRuntime uniforms,
        ReplayErrorCollector replay) {

    public ActivePipeline {
        Objects.requireNonNull(composition, "composition");
        Objects.requireNonNull(configuration, "configuration");
        Objects.requireNonNull(uniforms, "uniforms");
        Objects.requireNonNull(replay, "replay");
    }
}
