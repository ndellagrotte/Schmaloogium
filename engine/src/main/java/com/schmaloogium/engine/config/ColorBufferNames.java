// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** The one case-sensitive pack-facing color-buffer spelling normalizer (section 4.7). */
public final class ColorBufferNames {

    private ColorBufferNames() {
    }

    private static final String[][] ALIASES = {
        {"colortex0", "gcolor"}, {"colortex1", "gdepth"}, {"colortex2", "gnormal"},
        {"colortex3", "composite"}, {"colortex4", "gaux1"}, {"colortex5", "gaux2"},
        {"colortex6", "gaux3"}, {"colortex7", "gaux4"}
    };

    /** Returns the attachment index 0..7, or null for every other spelling. */
    public static Integer normalize(String name) {
        for (int i = 0; i < ALIASES.length; i++) {
            for (String alias : ALIASES[i]) {
                if (alias.equals(name)) {
                    return i;
                }
            }
        }
        return null;
    }
}
