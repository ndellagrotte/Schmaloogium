// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Render-thread scope with no color draw buffers; {@link #close()} is idempotent and restores
 * the exact prior selection (PHASE_5_DOC §2.2).
 */
public interface DrawBuffersNoneLease {

    DrawBuffersNoneCloseResult close();
}
