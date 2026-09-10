// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors
package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.buffers.internal.BufferPublisherImpl;

/**
 * Dependency-free public acquisition of the estate publisher (PHASE_5_DOC §4.11). The
 * publisher is render-thread-only from construction onward; Phase 7 owns the instance.
 */
public final class BufferEstatePublishers {

    private BufferEstatePublishers() {
    }

    /** Returns a fresh publisher whose initial publication is shaders-off generation 0. */
    public static BufferEstatePublisher create() {
        return new BufferPublisherImpl();
    }
}
