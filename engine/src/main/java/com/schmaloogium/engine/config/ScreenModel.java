// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.List;
import java.util.OptionalInt;

/** One screen model with the D-P3-55 expanded-slot column floor. */
public record ScreenModel(OptionalInt explicitColumns, List<ScreenEntry> entries) {

    public ScreenModel {
        entries = List.copyOf(entries);
    }

    public int resolvedColumns(int expandedSlotCount) {
        if (expandedSlotCount < 0) throw new IllegalArgumentException("negative slot count");
        int configuredColumns = explicitColumns.orElse(2);
        return Math.max(configuredColumns, Math.ceilDiv(expandedSlotCount, 9));
    }
}
