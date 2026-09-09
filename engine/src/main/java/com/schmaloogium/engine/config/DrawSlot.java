// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** One positional {@code DRAWBUFFERS} slot. */
public sealed interface DrawSlot permits DrawSlot.Attachment, DrawSlot.None {

    /** One attachment slot. */
    record Attachment(ColorAttachmentKey target) implements DrawSlot {

        public Attachment {
            java.util.Objects.requireNonNull(target, "target");
        }
    }

    /** One no-output slot. */
    record None() implements DrawSlot {
    }
}
