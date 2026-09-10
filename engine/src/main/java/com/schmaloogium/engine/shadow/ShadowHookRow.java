// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import java.util.Objects;

/**
 * One scalar hook observation (PHASE_8_DOC §4.13.1): {@code expected} is 1 for every row of
 * the canonical flattened-v3 catalogue; {@code actual} is the audit observation count for the
 * named target only. Resolution is not a runtime invocation count, and GET/SET observations
 * are independent of the corresponding RESOLVE observation.
 */
public record ShadowHookRow(String hookId, int expected, int actual, HookDisposition disposition) {

    public ShadowHookRow {
        Objects.requireNonNull(hookId, "hookId");
        Objects.requireNonNull(disposition, "disposition");
        if (expected < 0) {
            throw new IllegalArgumentException("negative expected count: " + hookId);
        }
        if (actual < 0) {
            throw new IllegalArgumentException("negative actual count: " + hookId);
        }
    }
}
