// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.diag.EngineDiagnostic;

import java.util.List;

/**
 * The sole prepublication owner (PHASE_9_DOC §2.2/§4.1). Closing before publication
 * discards the candidate; publication transfers it exactly once, after which caller
 * close is a no-op because the publisher owns retirement.
 */
final class IdRuntimeCandidateImpl implements IdRuntimeCandidate {

    private final IdResolutionTables tables;
    private final IdRuntimeFingerprint fingerprint;
    private final IdSourceFingerprint sourceFingerprint;
    private final long registryGeneration;
    private final IdRegistryFingerprint registryFingerprint;
    private final List<EngineDiagnostic> diagnostics;
    private IdRuntimeView view;
    private boolean closed;
    private boolean transferred;

    IdRuntimeCandidateImpl(IdResolutionTables tables, IdRuntimeFingerprint fingerprint,
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
    public IdRuntimeView view() {
        if (closed) {
            throw new IllegalStateException("candidate is closed");
        }
        if (view == null) {
            view = new IdRuntimeViewImpl(tables, fingerprint, sourceFingerprint,
                    registryGeneration, registryFingerprint, diagnostics);
        }
        return view;
    }

    @Override
    public void close() {
        if (!transferred) {
            closed = true;
        }
    }

    IdResolutionTables tables() {
        if (closed) {
            throw new IllegalStateException("candidate is closed");
        }
        return tables;
    }

    void markTransferred() {
        transferred = true;
    }

    boolean isClosed() {
        return closed;
    }

    boolean isTransferred() {
        return transferred;
    }
}
