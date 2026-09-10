// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;


/**
 * The {@code atlasSize} value source result (PHASE_13_DOC §4.4): Known means size available,
 * never evidence that the atlas is currently bound. Phase 6 owns the uniform upload.
 */
public sealed interface AtlasSizeResult {

    record Known(int width, int height) implements AtlasSizeResult {
        public Known {
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("atlas extent must be positive: "
                    + width + "x" + height);
            }
        }
    }

    record Unknown() implements AtlasSizeResult {
        public static final Unknown INSTANCE = new Unknown();
    }

    static AtlasSizeResult known(int width, int height) {
        return new Known(width, height);
    }
}
