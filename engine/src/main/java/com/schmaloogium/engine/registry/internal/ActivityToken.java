// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.registry.BoundProgramActivityToken;

/**
 * The epoch-comparing activity token (PHASE_4_DOC §4.10). Pure thread-safe comparison; no GL
 * query or side effect. A stale token never becomes current again.
 */
final class ActivityToken implements BoundProgramActivityToken {

    private final BarrierCore core;
    private final long epoch;

    ActivityToken(BarrierCore core, long epoch) {
        this.core = core;
        this.epoch = epoch;
    }

    @Override
    public boolean isCurrent() {
        return core.isCurrent(this);
    }

    long epoch() {
        return epoch;
    }
}
