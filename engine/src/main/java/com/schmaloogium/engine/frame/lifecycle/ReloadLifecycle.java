// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.lifecycle;

import java.util.Objects;

/**
 * The reload effect algebra (PHASE_7_DOC §4.8): {@code NONE < REPUBLISH < FULL}.
 * FULL rediscovers then loads; REPUBLISH loads the unchanged current selection without
 * discovery; NONE retains the exact configuration (resource refresh only).
 */
public enum ReloadLifecycle {
    NONE,
    REPUBLISH,
    FULL;

    /** The stronger of two effects; the merge law is max, never accumulation. */
    public static ReloadLifecycle max(ReloadLifecycle a, ReloadLifecycle b) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");
        return a.ordinal() >= b.ordinal() ? a : b;
    }
}
