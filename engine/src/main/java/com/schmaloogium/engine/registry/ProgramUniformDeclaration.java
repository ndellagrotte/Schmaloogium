// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import com.schmaloogium.engine.preprocess.DeclaredGlslType;

import java.util.List;

/**
 * One merged uniform declaration (PHASE_4_DOC §2.2/§4.7). Repeated exact names with
 * structurally equal Phase 3 types merge into one declaration retaining every attributed
 * site; the same exact name with unequal structural types is a source-attributed conflict
 * that fails the program before any GL object is created. Case is significant.
 * {@code sites} preserves linked-stage order VERTEX, GEOMETRY, FRAGMENT, then COMPUTE, and
 * source token order within a stage. Immutable.
 */
public record ProgramUniformDeclaration(
        String exactName,
        DeclaredGlslType type,
        List<ProgramUniformDeclarationSite> sites) {

    public ProgramUniformDeclaration {
        java.util.Objects.requireNonNull(exactName, "exactName");
        java.util.Objects.requireNonNull(type, "type");
        sites = List.copyOf(sites);
    }
}
