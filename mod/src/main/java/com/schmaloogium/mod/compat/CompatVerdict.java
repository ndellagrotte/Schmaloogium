// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.compat;

import java.util.List;

/** The verdict a {@link CompatCheck} returns (PHASE_1_DOC §4.10). */
public sealed interface CompatVerdict {

    /** No conflict. */
    record Ok() implements CompatVerdict {
    }

    /** Continue with a named degradation (warning to log and GUI; the engine continues). */
    record Degrade(String reasonKey, List<Object> args) implements CompatVerdict {
        public Degrade {
            args = List.copyOf(args);
        }
    }

    /** Force shaders off for the session — a supported terminal state, not an error. */
    record Bail(String reasonKey, List<Object> args) implements CompatVerdict {
        public Bail {
            args = List.copyOf(args);
        }
    }
}
