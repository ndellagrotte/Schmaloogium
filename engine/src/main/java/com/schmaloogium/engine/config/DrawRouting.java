// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.List;

/** Per-program draw routing. */
public sealed interface DrawRouting permits DrawRouting.AllUsed, DrawRouting.Explicit {

    /** No active valid routing directive; the program writes every color buffer it uses. */
    record AllUsed() implements DrawRouting {
    }

    /** Non-empty ordered slot list preserving character order and duplicates. */
    record Explicit(List<DrawSlot> slots) implements DrawRouting {

        public Explicit {
            slots = List.copyOf(slots);
            if (slots.isEmpty()) {
                throw new IllegalArgumentException("explicit routing must be non-empty");
            }
        }
    }
}
