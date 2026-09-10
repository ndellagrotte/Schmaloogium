// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

/** Closed per-sprite companion origin: discovered resource or the contract default fill. */
public sealed interface CompanionOrigin {

    /** A discovered pack companion resource for this sprite/kind. */
    record Resource(String resourceIdentity) implements CompanionOrigin {
        public Resource {
            java.util.Objects.requireNonNull(resourceIdentity, "resourceIdentity");
            if (resourceIdentity.isEmpty()) {
                throw new IllegalArgumentException("resourceIdentity must be non-empty");
            }
        }
    }

    /** The missing-sprite default fill (§4.1.4). */
    record DefaultFill(int packedRgba) implements CompanionOrigin {
    }
}
