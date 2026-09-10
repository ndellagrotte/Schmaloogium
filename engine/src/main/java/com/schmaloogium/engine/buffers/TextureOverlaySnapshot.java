// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.pack.ConfigurationFingerprint;
import com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint;
import com.schmaloogium.engine.registry.RegistryFingerprint;

/** Immutable snapshot view of a published texture overlay. */
public interface TextureOverlaySnapshot {
    TextureOverlayPublicationId id();

    RegistryFingerprint registryFingerprint();

    long registryGeneration();

    long resourceReloadEpoch();

    ConfigurationFingerprint configurationFingerprint();

    FixedSamplerPolicyFingerprint policyFingerprint();

    TextureCandidateTable candidates();
}
