// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow.internal;

import com.schmaloogium.engine.frame.ShadowSlotEpoch;

/**
 * The opaque lifecycle epoch minted by the pass factory (PHASE_7_DOC §5.1): identity
 * equals reference identity, so a stale copy can never match the live credential.
 */
public final class ShadowSlotEpochImpl implements ShadowSlotEpoch {

    public static final ShadowSlotEpochImpl INSTANCE = new ShadowSlotEpochImpl();

    private ShadowSlotEpochImpl() {
    }

    @Override
    public String toString() {
        return "ShadowSlotEpoch@" + Integer.toHexString(System.identityHashCode(this));
    }
}
