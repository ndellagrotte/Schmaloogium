// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import java.util.OptionalInt;

/**
 * One item type of the live registry projection (PHASE_9_DOC §2.3). The optional placed
 * default state is the captured {@code ItemBlock} default-state relation that feeds
 * static held light (§4.11).
 */
public record ItemTypeRecord(
        int itemOrdinal,
        RegistryName name,
        OptionalInt placedBlockDefaultStateOrdinal) {

    public ItemTypeRecord {
        if (itemOrdinal < 0) {
            throw new IllegalArgumentException("itemOrdinal must be >= 0");
        }
        if (placedBlockDefaultStateOrdinal == null) {
            placedBlockDefaultStateOrdinal = OptionalInt.empty();
        }
        if (placedBlockDefaultStateOrdinal.isPresent() && placedBlockDefaultStateOrdinal.getAsInt() < 0) {
            throw new IllegalArgumentException("placed state ordinal must be >= 0");
        }
    }
}
