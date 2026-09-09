// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.Optional;

import com.schmaloogium.engine.config.OptionState;

/** Exact profile inference over one validated state of the configuration's own catalog. */
final class ProfileEvaluator {

    private ProfileEvaluator() {
    }

    static ProfileInferenceResult infer(OptionConfiguration configuration, OptionState candidate) {
        if (candidate == null) {
            return new ProfileInferenceResult.InvalidState(OptionStateFailure.NULL_INPUT);
        }
        if (!(configuration.catalog() instanceof OptionCatalogValue catalog)
                || !(candidate instanceof OptionStateValue state)
                || state.catalog() != catalog) {
            return new ProfileInferenceResult.InvalidState(OptionStateFailure.FOREIGN_CATALOG);
        }
        if (!catalog.validate(candidate).valid()) {
            return new ProfileInferenceResult.InvalidState(OptionStateFailure.FOREIGN_CATALOG);
        }
        ProfileModel best = null;
        for (ProfileModel profile : configuration.profiles()) {
            if (matches(profile, state)) {
                if (best == null || profile.constraints().size() > best.constraints().size()) {
                    best = profile;
                }
                // ties keep the earlier (source-order) profile
            }
        }
        if (best == null) {
            return new ProfileInferenceResult.Inferred(new ProfileInference(Optional.empty(), true));
        }
        return new ProfileInferenceResult.Inferred(
            new ProfileInference(Optional.of(best.name()), false));
    }

    private static boolean matches(ProfileModel profile, OptionStateValue state) {
        for (ProfileConstraint constraint : profile.constraints()) {
            OptionValue current = state.values().get(constraint.optionName());
            if (current == null || !current.equals(constraint.requiredValue())) {
                return false;
            }
        }
        return true;
    }
}
