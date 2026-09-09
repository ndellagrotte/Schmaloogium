// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.Map;

/** Aggregate §4.7 resource requirements with every baseline-valued record present. */
public record ResourceRequirements(
    BufferMinima minima,
    Map<ColorAttachmentKey, ColorAttachmentRequirement> colorAttachments,
    ShadowRequirements shadow,
    CenterDepthRequirements centerDepth,
    Map<ProgramRequirementKey, ProgramRequirements> programs,
    SmoothingConstants smoothing,
    WorldRenderConstants world,
    NoiseRequirement noise) {

    public ResourceRequirements {
        colorAttachments = colorAttachments == null ? Map.of() : Map.copyOf(colorAttachments);
        programs = programs == null ? Map.of() : Map.copyOf(programs);
    }
}
