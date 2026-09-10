// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/**
 * One sampled hand stack value (PHASE_9_DOC §4.11). {@code itemOrdinal} is the
 * snapshot-local item ordinal from glue; {@code staticLight} is the captured
 * {@code ItemBlock} default state's validated 0–15 emitted light. An empty hand carries
 * ordinal 0 and light 0.
 */
public record HeldStackValue(boolean empty, int itemOrdinal, int staticLight) {

    public HeldStackValue {
        if (itemOrdinal < 0) {
            throw new IllegalArgumentException("itemOrdinal must be >= 0");
        }
        if (staticLight < 0 || staticLight > 15) {
            throw new IllegalArgumentException("staticLight must be in 0..15: " + staticLight);
        }
    }

    /** The canonical empty-hand value. */
    public static HeldStackValue emptyHand() {
        return new HeldStackValue(true, 0, 0);
    }
}
