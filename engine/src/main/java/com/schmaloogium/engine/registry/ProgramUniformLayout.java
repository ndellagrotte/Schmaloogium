// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors


package com.schmaloogium.engine.registry;
import com.schmaloogium.engine.registry.internal.CanonicalFraming;



import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The handle-free effective uniform layout of one provider (PHASE_4_DOC §2.2/§4.7).
 * {@code declarations} is immutable and iterates by exact uniform name in Unicode
 * code-point order. Fixed-function descriptors use the canonical empty layout. Immutable.
 */
public record ProgramUniformLayout(
        ProgramUniformLayoutFingerprint fingerprint,
        Map<String, ProgramUniformDeclaration> declarations) {

    private static final ProgramUniformLayout EMPTY = new ProgramUniformLayout(
        new ProgramUniformLayoutFingerprint(CanonicalFraming.EMPTY_UNIFORM_LAYOUT_DIGEST),
        Map.of());

    public ProgramUniformLayout {
        java.util.Objects.requireNonNull(fingerprint, "fingerprint");
        SortedMap<String, ProgramUniformDeclaration> ordered = new TreeMap<>();
        ordered.putAll(java.util.Objects.requireNonNull(declarations, "declarations"));
        declarations = java.util.Collections.unmodifiableSortedMap(ordered);
    }

    /** The canonical empty layout used by fixed-function descriptors. */
    public static ProgramUniformLayout empty() {
        return EMPTY;
    }
}
