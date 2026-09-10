// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.buffers.TextureCandidateTable;
import com.schmaloogium.engine.buffers.TextureOverlayPublicationId;
import com.schmaloogium.engine.registry.RegistryFingerprint;

import java.util.Objects;

/**
 * The non-owning immutable publication value (§4.5.1): id.generation is the accepted estate
 * generation; registry generation is separate. Candidates may carry opaque handles; the plan
 * never does.
 */
public record TexturePublication(
        TextureOverlayPublicationId id,
        RegistryFingerprint registryFingerprint,
        long registryGeneration,
        long resourceReloadEpoch,
        TexturePlan plan,
        TextureCandidateTable candidates) {
    public TexturePublication {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(registryFingerprint, "registryFingerprint");
        Objects.requireNonNull(plan, "plan");
        Objects.requireNonNull(candidates, "candidates");
    }
}
