// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.Objects;

/**
 * Identity of one options screen: the reserved {@code MAIN} root or a declared
 * subscreen name taken verbatim from the pack's screen model (PHASE_12_DOC §4.3.1;
 * §G4.1 forbids renaming). Pure value; no Minecraft type may ever appear here.
 */
public record ScreenId(String value) {

    /** The reserved root screen id. */
    public static final ScreenId MAIN = new ScreenId("MAIN");

    public ScreenId {
        Objects.requireNonNull(value, "value");
        if (value.isEmpty()) {
            throw new IllegalArgumentException("screen id must be non-empty");
        }
    }

    /** The declared subscreen id with the given verbatim name. */
    public static ScreenId declared(String name) {
        return new ScreenId(name);
    }

    /** True when this is the reserved root. */
    public boolean isMain() {
        return MAIN.value.equals(value);
    }
}
