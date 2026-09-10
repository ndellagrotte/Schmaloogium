// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.pack.PackConfiguration;
import com.schmaloogium.engine.registry.ProgramRegistryView;
import com.schmaloogium.engine.registry.RegistryFingerprint;

import java.util.Objects;

/**
 * The frozen planning inputs (§2.3/§5.1): the exact handle-free detached registry view whose
 * fingerprint must equal {@code registryFingerprint}; {@code estateGeneration} is the actual
 * accepted Phase 5 generation, never the registry generation; {@code registryGeneration} is
 * the actual accepted publisher generation, not a guessed successor. No handle enters a plan.
 */
public record TexturePlanRequest(
        PackConfiguration configuration,
        ProgramRegistryView registry,
        AtlasCatalog atlases,
        TextureSourceCatalog sources,
        CompanionPolicy companionPolicy,
        CompanionMacroState macroState,
        GLCapabilityProfile capabilities,
        RegistryFingerprint registryFingerprint,
        long estateGeneration,
        long registryGeneration,
        long resourceReloadEpoch) {
    public TexturePlanRequest {
        Objects.requireNonNull(configuration, "configuration");
        Objects.requireNonNull(registry, "registry");
        Objects.requireNonNull(atlases, "atlases");
        Objects.requireNonNull(sources, "sources");
        Objects.requireNonNull(companionPolicy, "companionPolicy");
        Objects.requireNonNull(macroState, "macroState");
        Objects.requireNonNull(capabilities, "capabilities");
        Objects.requireNonNull(registryFingerprint, "registryFingerprint");
    }
}
