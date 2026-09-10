// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.Objects;

/** Identity of one option: the pack-declared option name, verbatim. */
public record OptionId(String name) {

    public OptionId {
        Objects.requireNonNull(name, "name");
        if (name.isEmpty()) {
            throw new IllegalArgumentException("option id must be non-empty");
        }
    }
}
