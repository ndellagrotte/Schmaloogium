// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.List;

/** Deeply immutable, already-split tooltip. */
public record Tooltip(List<TooltipLine> lines) {

    public static final Tooltip EMPTY = new Tooltip(List.of());

    public Tooltip {
        lines = List.copyOf(lines);
    }

    public static Tooltip ofLines(List<TooltipLine> lines) {
        return lines.isEmpty() ? EMPTY : new Tooltip(lines);
    }
}
