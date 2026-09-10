// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.List;

/**
 * The seven-entry global engine-settings view-model (PHASE_12_DOC §4.6.2), always
 * delivered complete and in the canonical table order (D-P12-32). {@code
 * antialiasingLevel} has no entry — reserved exact {@code 0}, no GUI surface
 * (D-P12-35) — and there is no anisotropic-filtering control at all.
 */
public record EngineSettingsModel(List<EngineSettingEntry> entries) {

    public EngineSettingsModel {
        entries = List.copyOf(entries);
    }
}
