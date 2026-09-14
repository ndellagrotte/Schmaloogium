// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.Set;

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

    /**
     * One attachment-scoped directive name split into the attachment it selects and the
     * directive suffix, through the same normalizer. {@code canonical} is true for the
     * {@code colortexN} spelling and false for a legacy alias, so a pack that declares
     * both spellings resolves to the canonical one.
     */
    public record ScopedDirective(int index, String suffix, boolean canonical) {
    }

    /**
     * Splits {@code gaux3Format} / {@code colortex6MipmapEnabled} and friends, or returns
     * null when the name is not one of {@code suffixes} scoped to an attachment. Only the
     * caller's known suffixes are accepted, so an unrelated const that merely begins with
     * a buffer name (a pack's own {@code compositeStrength}, say) is never misread as a
     * directive.
     */
    public static ScopedDirective scoped(String name, Set<String> suffixes) {
        for (int i = 0; i < ALIASES.length; i++) {
            for (int alias = 0; alias < ALIASES[i].length; alias++) {
                String prefix = ALIASES[i][alias];
                if (!name.startsWith(prefix) || name.length() == prefix.length()) {
                    continue;
                }
                String suffix = name.substring(prefix.length());
                if (suffixes.contains(suffix)) {
                    return new ScopedDirective(i, suffix, alias == 0);
                }
            }
        }
        return null;
    }
}
