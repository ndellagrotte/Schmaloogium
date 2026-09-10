// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.schmaloogium.engine.diag.EngineDiagnostic;

/**
 * The whole resolved options view-model for one configuration and frozen locale
 * (PHASE_12_DOC §2.2): root screen, declared subscreens, build diagnostics, the
 * optional pending-profile summary and the session availability block. Deeply
 * immutable; deterministic across builds of one configuration and locale (I-4).
 */
public record OptionPresentationModel(
        PresentationScreen mainScreen,
        Map<ScreenId, PresentationScreen> subScreens,
        List<EngineDiagnostic> diagnostics,
        Optional<PendingProfileSummary> pendingProfileSummary,
        OptionSessionAvailability availability) {

    public OptionPresentationModel {
        Objects.requireNonNull(mainScreen, "mainScreen");
        subScreens = Map.copyOf(subScreens);
        diagnostics = List.copyOf(diagnostics);
        pendingProfileSummary = pendingProfileSummary == null
                ? Optional.empty() : pendingProfileSummary;
        Objects.requireNonNull(availability, "availability");
    }
}
