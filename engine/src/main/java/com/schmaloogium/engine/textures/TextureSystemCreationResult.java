// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import java.util.Objects;

/** Factory result (§2.2): an inactive owner or a typed failure. */
public sealed interface TextureSystemCreationResult {
    record Created(TextureSystem system) implements TextureSystemCreationResult {
        public Created {
            Objects.requireNonNull(system, "system");
        }
    }

    record Failed(TextureFailure failure) implements TextureSystemCreationResult {
        public Failed {
            Objects.requireNonNull(failure, "failure");
        }
    }
}
