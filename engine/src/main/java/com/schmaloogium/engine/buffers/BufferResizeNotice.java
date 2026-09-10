// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import java.util.Objects;

/**
 * Resize publication (PHASE_5_DOC §2.2), issued only after the new estate is ready. Carries no registry
 * fingerprint by design.
 */
public record BufferResizeNotice(BufferSizing oldSizing, BufferSizing newSizing, long newGeneration,
        BufferResizeReason reason) {

    public BufferResizeNotice {
        Objects.requireNonNull(oldSizing, "oldSizing");
        Objects.requireNonNull(newSizing, "newSizing");
        Objects.requireNonNull(reason, "reason");
    }
}
