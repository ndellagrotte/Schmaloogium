// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

/**
 * The apply outcome ladder (PHASE_12_DOC §2.2): {@code UNCHANGED} clean apply;
 * {@code PERSISTED} filesystem commit; {@code SESSION_ACCEPTED} Internal acceptance
 * (deliberately not disk durability); {@code REJECTED} preflight refusal preserving all
 * state; {@code FAILED} post-write failure retaining the pending preview.
 */
public enum OptionApplyStatus {
    UNCHANGED, PERSISTED, SESSION_ACCEPTED, REJECTED, FAILED
}
