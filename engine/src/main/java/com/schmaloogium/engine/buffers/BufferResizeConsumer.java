// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Render-thread resize observer invoked at the estate replacement safe point;
 * implementations must not throw (PHASE_5_DOC §2.2).
 */
public interface BufferResizeConsumer {

    ResizeConsumerResult resize(BufferResizeNotice notice);
}
