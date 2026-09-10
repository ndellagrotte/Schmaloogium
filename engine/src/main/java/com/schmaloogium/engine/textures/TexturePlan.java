// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import java.util.List;
import java.util.Objects;

/**
 * The pure planning output (§2.3): companions, noise, custom entries, unsupported
 * diagnostics, the retained macro provenance and the memory estimate. Metadata only — no
 * bytes, cursors or handles.
 */
public record TexturePlan(
        TexturePlanRequest inputs,
        List<CompanionAtlasPlan> companions,
        NoisePlan noise,
        List<CustomTexturePlanEntry> customTextures,
        List<UnsupportedBinding> unsupported,
        CompanionMacroState macroState,
        TextureMemoryEstimate memory) {
    public TexturePlan {
        Objects.requireNonNull(inputs, "inputs");
        Objects.requireNonNull(companions, "companions");
        companions.forEach(Objects::requireNonNull);
        companions = List.copyOf(companions);
        Objects.requireNonNull(noise, "noise");
        Objects.requireNonNull(customTextures, "customTextures");
        customTextures.forEach(Objects::requireNonNull);
        customTextures = List.copyOf(customTextures);
        Objects.requireNonNull(unsupported, "unsupported");
        unsupported.forEach(Objects::requireNonNull);
        unsupported = List.copyOf(unsupported);
        Objects.requireNonNull(macroState, "macroState");
        Objects.requireNonNull(memory, "memory");
    }
}
