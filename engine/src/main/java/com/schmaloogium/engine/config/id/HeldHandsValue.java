// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/**
 * One immutable both-hands sample emitted after Phase 6 accepts the frame identity and
 * before the first shader draw (PHASE_9_DOC §4.11). The world/tick identity must echo the
 * authenticated frame so the Phase 6 sink can replace the value for its logical tick.
 */
public record HeldHandsValue(
        long worldEpoch,
        long logicalTick,
        HeldStackValue main,
        HeldStackValue off) {

    public HeldHandsValue {
        java.util.Objects.requireNonNull(main, "main");
        java.util.Objects.requireNonNull(off, "off");
    }
}
