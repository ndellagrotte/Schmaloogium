// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Live resize-consumer registration; {@link #close()} performs idempotent render-thread removal
 * at the same safe point (PHASE_5_DOC §2.2).
 */
public interface BufferResizeRegistration extends AutoCloseable {

    @Override
    void close();
}
