// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.List;
import java.util.Objects;

/** One fully resolved screen of the options tree. */
public record PresentationScreen(
        ScreenId id,
        String title,
        int resolvedColumns,
        List<PresentationEntry> entries) {

    public PresentationScreen {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(title, "title");
        entries = List.copyOf(entries);
    }
}
