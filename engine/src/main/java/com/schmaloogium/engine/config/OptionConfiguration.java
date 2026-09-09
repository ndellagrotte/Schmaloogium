// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.OptionCatalog;
import com.schmaloogium.engine.config.OptionState;

import java.util.List;
import java.util.Map;

import com.schmaloogium.engine.diag.DiagnosticReporter;

/** Immutable decorated option configuration bound to one issuing catalog. */
public record OptionConfiguration(
    OptionCatalog catalog,
    OptionState state,
    List<ProfileModel> profiles,
    ScreenModel mainScreen,
    Map<String, ScreenModel> namedScreens,
    SliderSet sliders,
    Map<String, LangDecorations> localizedDecorations) {

    public OptionConfiguration {
        profiles = List.copyOf(profiles);
        namedScreens = Map.copyOf(namedScreens);
        localizedDecorations = Map.copyOf(localizedDecorations);
        if (!(catalog instanceof OptionCatalogValue cv) || !(state instanceof OptionStateValue sv)
                || sv.catalog() != cv) {
            throw new IllegalArgumentException("state/catalog from a foreign issuing catalog");
        }
    }

    public ProfileInferenceResult inferProfile(OptionState candidate) {
        return ProfileEvaluator.infer(this, candidate);
    }
}
