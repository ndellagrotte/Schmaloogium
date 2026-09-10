// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.Objects;

/** One tooltip line with its render severity. */
public record TooltipLine(String text, TooltipSeverity severity) {

    public TooltipLine {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(severity, "severity");
    }
}
