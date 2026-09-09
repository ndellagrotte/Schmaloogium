// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.ProfileName;

import java.util.List;

/** One immutable expanded profile model (Appendix F.4). */
public record ProfileModel(
        ProfileName name,
        List<ProfileConstraint> constraints,
        List<ProgramDisable> disabledPrograms) {

    public ProfileModel {
        java.util.Objects.requireNonNull(name, "name");
        constraints = List.copyOf(constraints);
        disabledPrograms = List.copyOf(disabledPrograms);
    }
}
