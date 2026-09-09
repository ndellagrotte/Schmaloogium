// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

/** Provider-only checked signal; never published in a {@link PackLoadFailure}. */
public final class InternalPackReadException extends Exception {

    public InternalPackReadException(String message) {
        super(message);
    }
}
