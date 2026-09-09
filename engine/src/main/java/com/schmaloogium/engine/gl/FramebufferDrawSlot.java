// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Positional draw-route slot (PHASE_1_DOC §4.7.4): the output locations for
 * {@link FramebufferService#drawBuffers}. {@link None} consumes a location but names no
 * attachment (repeated {@code None} is legal); empty list means no writes, never
 * preserve-current; no raw sentinel is accepted. The backend alone encodes
 * {@link Attachment} as {@code COLOR_ATTACHMENT0+index} and {@link None} as {@code GL_NONE}.
 */
public sealed interface FramebufferDrawSlot {

    /** Exact color attachment selected through its positional draw-route index. */
    record Attachment(int index) implements FramebufferDrawSlot {
    }

    /** Consumes a location but names no attachment. */
    record None() implements FramebufferDrawSlot {
    }
}
