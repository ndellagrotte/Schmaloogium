// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import java.util.Objects;

/** One inventory row: a logical buffer and its resolved format (PHASE_5_DOC §2.2). */
public record BufferInventoryEntry(LogicalBuffer buffer, ResolvedBufferFormat format) {

    public BufferInventoryEntry {
        Objects.requireNonNull(buffer, "buffer");
        Objects.requireNonNull(format, "format");
    }
}
