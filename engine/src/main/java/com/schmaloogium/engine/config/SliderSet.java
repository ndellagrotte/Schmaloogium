// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.List;

/** Ordered slider option names; unknown names diagnose without invalidating other entries. */
public record SliderSet(List<String> optionNames) {

    public SliderSet {
        optionNames = List.copyOf(optionNames);
    }
}
