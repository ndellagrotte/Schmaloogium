// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Color attachment index 0..15. */
public record ColorAttachmentKey(int colortexIndex) {

    public ColorAttachmentKey {
        if (colortexIndex < 0 || colortexIndex > 15) {
            throw new IllegalArgumentException("colortexIndex outside 0..15");
        }
    }
}
