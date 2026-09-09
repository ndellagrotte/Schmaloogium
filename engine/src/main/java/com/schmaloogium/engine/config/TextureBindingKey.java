// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.OptionalInt;

/** (stage, sampler, optional numeric duplicate discriminator) texture binding key. */
public record TextureBindingKey(
        TexturePropertyStage stage, String sampler, OptionalInt duplicateDiscriminator) {

    public TextureBindingKey {
        java.util.Objects.requireNonNull(stage, "stage");
        java.util.Objects.requireNonNull(sampler, "sampler");
        if (sampler.isEmpty()) {
            throw new IllegalArgumentException("sampler must be non-empty");
        }
    }
}
