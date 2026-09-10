// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/**
 * One full-int alias lookup result (PHASE_9_DOC §2.2). {@code present=false} always
 * carries {@code shaderId=0}; shader ID zero may also be explicitly mapped, so presence
 * is never inferred from the integer.
 */
public record AliasValue(boolean present, int shaderId) {

    public AliasValue {
        if (!present) {
            shaderId = 0;
        }
    }

    /** The canonical absent value. */
    public static AliasValue absent() {
        return new AliasValue(false, 0);
    }
}
