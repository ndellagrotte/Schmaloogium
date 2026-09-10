// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.config.TriState;

/**
 * Typed hand-light policy input (PHASE_9_DOC §4.11): Phase 3's decoded pack tri-state and
 * Phase 12's higher-priority decoded user tri-state, plus the raw
 * {@code dynamicHandLight} request. Resolution is user-over-pack with the local
 * backward-compatible {@code true} fallback (D-P9-8) — never macro presence, never an
 * unconditional GUI true. {@code dynamicHandLight} resolves active unless explicitly
 * false, and only ever suppresses an explicitly installed external provider; Phase 9
 * never implements dynamic lights.
 */
public record HandLightPolicy(
        TriState userOldHandLight,
        TriState packOldHandLight,
        TriState dynamicHandLight) {

    public HandLightPolicy {
        java.util.Objects.requireNonNull(userOldHandLight, "userOldHandLight");
        java.util.Objects.requireNonNull(packOldHandLight, "packOldHandLight");
        java.util.Objects.requireNonNull(dynamicHandLight, "dynamicHandLight");
    }

    /** The all-DEFAULT baseline. */
    public static HandLightPolicy allDefault() {
        return new HandLightPolicy(TriState.DEFAULT, TriState.DEFAULT, TriState.DEFAULT);
    }

    /** Resolved old-hand-light mode: explicit user, else explicit pack, else true. */
    public TriState resolvedOldHandLight() {
        if (userOldHandLight != TriState.DEFAULT) {
            return userOldHandLight;
        }
        if (packOldHandLight != TriState.DEFAULT) {
            return packOldHandLight;
        }
        return TriState.TRUE;
    }

    /** Whether the external dynamic-light suppression request is active. */
    public boolean dynamicHandLightActive() {
        return dynamicHandLight != TriState.FALSE;
    }
}
