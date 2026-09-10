// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

/**
 * Ordered reload lifecycles (PHASE_12_DOC §4.7.3): {@code NONE} runs additive work
 * only; {@code REPUBLISH} reloads the unchanged selection with new engine inputs;
 * {@code FULL} re-discovers then loads. The ordinal order is the merge algebra's max.
 */
public enum ReloadLifecycle {
    NONE, REPUBLISH, FULL;

    /** The stronger of two effects; the merge law is max, never accumulation. */
    public static ReloadLifecycle max(ReloadLifecycle a, ReloadLifecycle b) {
        return a.ordinal() >= b.ordinal() ? a : b;
    }
}
