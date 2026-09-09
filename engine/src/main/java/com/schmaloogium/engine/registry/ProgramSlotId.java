// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * A program slot identity: the exact pack program name (PHASE_4_DOC §2.2). Case
 * significant everywhere.
 */
public record ProgramSlotId(String packName) {

    public ProgramSlotId {
        java.util.Objects.requireNonNull(packName, "packName");
        if (packName.isEmpty()) {
            throw new IllegalArgumentException("program slot name must be non-empty");
        }
    }

    @Override
    public String toString() {
        return packName;
    }
}
