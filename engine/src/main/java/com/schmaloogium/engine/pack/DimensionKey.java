// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.pack.DimensionKey;

import java.util.OptionalInt;

/** Closed engine dimension value: empty base or one legacy world ID in [-128,128]. */
public record DimensionKey(OptionalInt legacyId) implements Comparable<DimensionKey> {

    /** The base source set. */
    public static final DimensionKey BASE = new DimensionKey(OptionalInt.empty());

    public DimensionKey {
        java.util.Objects.requireNonNull(legacyId, "legacyId");
        if (legacyId.isPresent() && (legacyId.getAsInt() < -128 || legacyId.getAsInt() > 128)) {
            throw new IllegalArgumentException("legacy dimension ID outside [-128,128]");
        }
    }

    /** A present legacy world key. */
    public static DimensionKey world(int id) {
        return new DimensionKey(OptionalInt.of(id));
    }

    @Override
    public int compareTo(DimensionKey o) {
        if (legacyId.isEmpty() && o.legacyId.isEmpty()) {
            return 0;
        }
        if (legacyId.isEmpty()) {
            return -1;
        }
        if (o.legacyId.isEmpty()) {
            return 1;
        }
        return Integer.compare(legacyId.getAsInt(), o.legacyId.getAsInt());
    }
}
