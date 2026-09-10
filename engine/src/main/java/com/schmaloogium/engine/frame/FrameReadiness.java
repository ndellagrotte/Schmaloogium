// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

import com.schmaloogium.engine.buffers.TextureOverlayPublicationId;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;

/**
 * The readiness summary carried by every finalized frame (PHASE_7_DOC §5.1). Identities are
 * the exact accepted publication's, never inferred; {@code texturePublication} is the
 * explicit empty publication at v0.1 (P13 arrives later).
 */
public record FrameReadiness(
        PipelineIdentity identity,
        long registryGeneration,
        long bufferEstateGeneration,
        OptionalLong idRuntimeGeneration,
        Optional<TextureOverlayPublicationId> texturePublication,
        long resourceReloadEpoch,
        int consecutiveFinalizedFrames,
        Optional<FailureId> failure) {

    public FrameReadiness {
        Objects.requireNonNull(identity, "identity");
        idRuntimeGeneration = idRuntimeGeneration == null
                ? OptionalLong.empty() : idRuntimeGeneration;
        texturePublication = texturePublication == null
                ? Optional.empty() : texturePublication;
        Objects.requireNonNull(failure, "failure");
        if (consecutiveFinalizedFrames < 0) {
            throw new IllegalArgumentException(
                    "consecutiveFinalizedFrames must be non-negative: " + consecutiveFinalizedFrames);
        }
    }
}
