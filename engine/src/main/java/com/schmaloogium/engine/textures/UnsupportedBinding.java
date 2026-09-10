// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.config.TextureBindingKey;
import com.schmaloogium.engine.registry.StageId;

import java.util.Objects;

/**
 * Closed unsupported diagnostic (§4.3.6): retains the original key, discriminator and
 * expanded stage; participates in canonical fingerprinting.
 */
public record UnsupportedBinding(TextureBindingKey key, StageId expandedStage,
                                 RequestedTextureTarget requestedTarget,
                                 UnsupportedReason reason) {
    public UnsupportedBinding {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(expandedStage, "expandedStage");
        Objects.requireNonNull(requestedTarget, "requestedTarget");
        Objects.requireNonNull(reason, "reason");
        boolean unknown = requestedTarget instanceof RequestedTextureTarget.UnknownSampler;
        if (reason == UnsupportedReason.KEY_DOMAIN && !unknown) {
            throw new IllegalArgumentException("KEY_DOMAIN requires UnknownSampler");
        }
        if (reason == UnsupportedReason.STAGE_COLUMN
                && !(requestedTarget instanceof RequestedTextureTarget.KnownSampler)) {
            throw new IllegalArgumentException("STAGE_COLUMN requires KnownSampler");
        }
    }
}
