// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/** Index of a buffer within its domain (PHASE_5_DOC §2.2). */
public record BufferIndex(int value) {

    public BufferIndex {
        if (value < 0) {
            throw new IllegalArgumentException("negative buffer index");
        }
    }
}
