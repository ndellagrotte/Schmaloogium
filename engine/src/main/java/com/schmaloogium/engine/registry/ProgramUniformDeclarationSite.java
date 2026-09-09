// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import com.schmaloogium.engine.preprocess.AttributedSourceLocation;
import com.schmaloogium.engine.preprocess.MaterializationFingerprint;
import com.schmaloogium.engine.preprocess.ShaderSourceStage;

/**
 * One attributed declaration site (PHASE_4_DOC §2.2): the declaring linked stage, the
 * Phase 3 attributed source location and the containing materialization fingerprint.
 * Phase 3 owns these value types; Phase 4 never reparses source. Immutable.
 */
public record ProgramUniformDeclarationSite(
        ShaderSourceStage declaringStage,
        AttributedSourceLocation location,
        MaterializationFingerprint materialization) {

    public ProgramUniformDeclarationSite {
        java.util.Objects.requireNonNull(declaringStage, "declaringStage");
        java.util.Objects.requireNonNull(location, "location");
        java.util.Objects.requireNonNull(materialization, "materialization");
    }
}
