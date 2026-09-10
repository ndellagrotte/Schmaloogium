// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

/** Build result (§2.2): Ready with the immutable publication, or Failed without one. */
public sealed interface TextureBuildResult {
    record Ready(TexturePublication publication) implements TextureBuildResult {
        public Ready {
            java.util.Objects.requireNonNull(publication, "publication");
        }
    }

    record Failed(TextureFailure failure) implements TextureBuildResult {
        public Failed {
            java.util.Objects.requireNonNull(failure, "failure");
        }
    }
}
