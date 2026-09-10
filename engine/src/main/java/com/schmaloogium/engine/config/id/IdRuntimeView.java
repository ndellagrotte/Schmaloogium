// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.diag.EngineDiagnostic;

import java.util.List;

/**
 * Operation-free prepublication inspection (PHASE_9_DOC §2.2/§5.1). The view never
 * transfers ownership and exposes only resolved-table summaries, complete identity and
 * the buffered diagnostics in deterministic source order — the evidence surface for
 * Phase 7 composition and headless tests.
 */
public interface IdRuntimeView {

    /** The complete derived runtime identity. */
    IdRuntimeFingerprint fingerprint();

    /** The mapping-side identity (schema, selections, sources, catalog versions). */
    IdSourceFingerprint sourceFingerprint();

    /** The registry generation the snapshot was captured at. */
    long registryGeneration();

    /** The live registry projection fingerprint. */
    IdRegistryFingerprint registryFingerprint();

    int blockStateCount();

    int itemOrdinalCount();

    int entityTypeCount();

    /** States holding an explicit or fallback block alias. */
    int blockAliasAssignments();

    /** Items holding an explicit item alias. */
    int itemAliasAssignments();

    /** Entity types holding an explicit entity alias. */
    int entityAliasAssignments();

    /** States moved to a resolved custom render layer. */
    int layerAssignments();

    /** Every diagnostic buffered during the build, in deterministic source order. */
    List<EngineDiagnostic> diagnostics();
}
