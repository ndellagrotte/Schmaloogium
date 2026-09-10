// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/**
 * One entity type of the live registry projection (PHASE_9_DOC §2.3). The exact
 * registration class maps to this ordinal in glue; unregistered runtime subclasses stay
 * absent rather than attributed to a guessed superclass (§4.2).
 */
public record EntityTypeRecord(int entityTypeOrdinal, RegistryName name) {

    public EntityTypeRecord {
        if (entityTypeOrdinal < 0) {
            throw new IllegalArgumentException("entityTypeOrdinal must be >= 0");
        }
    }
}
