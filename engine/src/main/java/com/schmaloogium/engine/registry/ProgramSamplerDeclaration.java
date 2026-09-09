// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import com.schmaloogium.engine.preprocess.DeclaredGlslType;

import java.util.List;

/**
 * One projected sampler declaration (PHASE_4_DOC §2.2/§4.7). The projection excludes
 * nonsamplers, retains direct {@code DeclaredGlslType.Sampler} values and losslessly retains
 * sampler-containing arrays/structs as unsupported shapes — never a cast to sampler2D.
 * {@code declarationOrder} is the zero-based first-occurrence ordinal (VERTEX, GEOMETRY,
 * FRAGMENT, COMPUTE, then token order); equal name/type repetitions coalesce with all sites
 * retained. Immutable.
 */
public record ProgramSamplerDeclaration(
        String exactName,
        DeclaredGlslType type,
        int declarationOrder,
        List<ProgramUniformDeclarationSite> sites) {

    public ProgramSamplerDeclaration {
        java.util.Objects.requireNonNull(exactName, "exactName");
        java.util.Objects.requireNonNull(type, "type");
        sites = List.copyOf(sites);
    }
}
