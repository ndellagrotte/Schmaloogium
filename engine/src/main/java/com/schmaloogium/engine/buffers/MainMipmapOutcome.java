// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Per-buffer outcome of a main-mipmap generation request (PHASE_5_DOC §2.2).
 */
public sealed interface MainMipmapOutcome {

    record Generated(LogicalBuffer buffer) implements MainMipmapOutcome {
    }

    record AlreadyFresh(LogicalBuffer buffer) implements MainMipmapOutcome {
    }

    record Degraded(LogicalBuffer buffer, String diagnosticId) implements MainMipmapOutcome {
    }
}
