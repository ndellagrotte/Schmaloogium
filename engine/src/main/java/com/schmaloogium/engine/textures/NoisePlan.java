// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.config.TextureSidecarRef;

import com.schmaloogium.engine.pack.NormalizedPackPath;

import java.util.Optional;

/** Closed noise-plan sum (§4.2): generated, pack override, or disabled. */
public sealed interface NoisePlan {

    /** The contract generated noise at resolution² RGB. */
    record Generated(int resolution) implements NoisePlan {
        public Generated {
            if (resolution <= 0) {
                throw new IllegalArgumentException("resolution must be positive: " + resolution);
            }
        }
    }

    /** A {@code texture.noise} pack image override; the image's own dimensions win. */
    record FromPack(NormalizedPackPath image, Optional<TextureSidecarRef> sidecar,
                    int declaredResolution) implements NoisePlan {
        public FromPack {
            java.util.Objects.requireNonNull(image, "image");
            sidecar = sidecar == null ? Optional.empty() : sidecar;
            if (declaredResolution < 0) {
                throw new IllegalArgumentException("declaredResolution must be nonnegative: "
                    + declaredResolution);
            }
        }
    }

    /** No noise requirement: no noise allocation, cell Absent(NOT_CONFIGURED). */
    record Disabled() implements NoisePlan {
        public static final Disabled INSTANCE = new Disabled();
    }

    static NoisePlan disabled() {
        return Disabled.INSTANCE;
    }
}
