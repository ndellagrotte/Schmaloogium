// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * One positional draw-routing slot (PHASE_4_DOC §4.9): an attachment or a hole. {@code None}
 * consumes output capacity but is not a resource.
 */
public sealed interface DrawRoutingSlot {

    /** One attachment slot; the referenced buffer is the logical target. */
    record Attachment(BufferRef buffer) implements DrawRoutingSlot {

        public Attachment {
            java.util.Objects.requireNonNull(buffer, "buffer");
        }
    }

    /** One no-output slot. */
    record None() implements DrawRoutingSlot {
    }
}
