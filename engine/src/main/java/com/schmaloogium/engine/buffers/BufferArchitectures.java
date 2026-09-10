// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors
package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.buffers.internal.ArchitectureImpl;

/**
 * Dependency-free public acquisition of the buffer architecture (PHASE_5_DOC §5.1,
 * D-P5-25). The returned architecture is stateless, retains no request or runtime inputs,
 * and performs no GL, context, or provider work at acquisition time.
 */
public final class BufferArchitectures {

    private BufferArchitectures() {
    }

    /** Returns the stateless architecture facade; never null. */
    public static BufferArchitecture create() {
        return ArchitectureImpl.INSTANCE;
    }
}
