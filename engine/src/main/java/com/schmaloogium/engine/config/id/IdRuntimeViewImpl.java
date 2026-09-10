// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.diag.EngineDiagnostic;

import java.util.List;

/** Operation-free inspection of one resolved candidate (PHASE_9_DOC §5.1 row 4). */
final class IdRuntimeViewImpl implements IdRuntimeView {

    private final IdResolutionTables tables;
    private final IdRuntimeFingerprint fingerprint;
    private final IdSourceFingerprint sourceFingerprint;
    private final long registryGeneration;
    private final IdRegistryFingerprint registryFingerprint;
    private final List<EngineDiagnostic> diagnostics;

    IdRuntimeViewImpl(IdResolutionTables tables, IdRuntimeFingerprint fingerprint,
            IdSourceFingerprint sourceFingerprint, long registryGeneration,
            IdRegistryFingerprint registryFingerprint, List<EngineDiagnostic> diagnostics) {
        this.tables = tables;
        this.fingerprint = fingerprint;
        this.sourceFingerprint = sourceFingerprint;
        this.registryGeneration = registryGeneration;
        this.registryFingerprint = registryFingerprint;
        this.diagnostics = List.copyOf(diagnostics);
    }

    @Override
    public IdRuntimeFingerprint fingerprint() {
        return fingerprint;
    }

    @Override
    public IdSourceFingerprint sourceFingerprint() {
        return sourceFingerprint;
    }

    @Override
    public long registryGeneration() {
        return registryGeneration;
    }

    @Override
    public IdRegistryFingerprint registryFingerprint() {
        return registryFingerprint;
    }

    @Override
    public int blockStateCount() {
        return tables.stateCount;
    }

    @Override
    public int itemOrdinalCount() {
        return tables.itemCount;
    }

    @Override
    public int entityTypeCount() {
        return tables.entityCount;
    }

    @Override
    public int blockAliasAssignments() {
        return tables.blockAssignments;
    }

    @Override
    public int itemAliasAssignments() {
        return tables.itemAssignments;
    }

    @Override
    public int entityAliasAssignments() {
        return tables.entityAssignments;
    }

    @Override
    public int layerAssignments() {
        return tables.layerAssignments;
    }

    @Override
    public List<EngineDiagnostic> diagnostics() {
        return diagnostics;
    }
}
