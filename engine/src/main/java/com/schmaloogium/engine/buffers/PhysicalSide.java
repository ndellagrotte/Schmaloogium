// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/** Ping-pong side of the double-buffered main color chain (PHASE_5_DOC §2.2). */
public enum PhysicalSide {
    A,
    B;

    /** The other physical side. */
    public PhysicalSide opposite() {
        return this == A ? B : A;
    }
}
