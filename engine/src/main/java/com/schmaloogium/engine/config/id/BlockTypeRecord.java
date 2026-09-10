// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import java.util.List;

/**
 * One block type of the live registry projection (PHASE_9_DOC §2.3). {@code stateOrdinals}
 * are ascending snapshot-local state ordinals of all valid canonical states; they never
 * cross the seam back to Minecraft objects.
 */
public record BlockTypeRecord(
        int blockOrdinal,
        RegistryName name,
        int liveLegacyNumericId,
        List<Integer> stateOrdinals) {

    public BlockTypeRecord {
        if (blockOrdinal < 0) {
            throw new IllegalArgumentException("blockOrdinal must be >= 0");
        }
        stateOrdinals = List.copyOf(stateOrdinals);
        if (stateOrdinals.isEmpty()) {
            throw new IllegalArgumentException("a block must own at least one state");
        }
        int previous = -1;
        for (int stateOrdinal : stateOrdinals) {
            if (stateOrdinal <= previous) {
                throw new IllegalArgumentException("stateOrdinals must be ascending and distinct");
            }
            previous = stateOrdinal;
        }
    }
}
