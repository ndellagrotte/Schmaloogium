// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The opaque authenticated selection (PHASE_4_DOC §2.2). Minted only by a published barrier
 * around its private binding, publication identity and issued context; exposes handle-free
 * immutable metadata only. Public-record equality never authenticates.
 */
public final class ProgramBindingSelection {

    private final long registryGeneration;
    private final RegistryFingerprint registryFingerprint;
    private final ProgramSlotId requested;
    private final ResolvedProgramDescriptor effectiveDescriptor;
    private final StageId effectiveStage;
    private final StageBand actualBand;
    private final BarrierContext originatingContext;
    private final com.schmaloogium.engine.registry.internal.SelectionCredential credential;

    public ProgramBindingSelection(
            long registryGeneration,
            RegistryFingerprint registryFingerprint,
            ProgramSlotId requested,
            ResolvedProgramDescriptor effectiveDescriptor,
            StageId effectiveStage,
            StageBand actualBand,
            BarrierContext originatingContext,
            com.schmaloogium.engine.registry.internal.SelectionCredential credential) {
        this.registryGeneration = registryGeneration;
        this.registryFingerprint = java.util.Objects.requireNonNull(registryFingerprint, "registryFingerprint");
        this.requested = java.util.Objects.requireNonNull(requested, "requested");
        this.effectiveDescriptor = java.util.Objects.requireNonNull(effectiveDescriptor, "effectiveDescriptor");
        this.effectiveStage = java.util.Objects.requireNonNull(effectiveStage, "effectiveStage");
        this.actualBand = java.util.Objects.requireNonNull(actualBand, "actualBand");
        this.originatingContext = java.util.Objects.requireNonNull(originatingContext, "originatingContext");
        this.credential = java.util.Objects.requireNonNull(credential, "credential");
    }

    public long registryGeneration() {
        return registryGeneration;
    }

    public RegistryFingerprint registryFingerprint() {
        return registryFingerprint;
    }

    public ProgramSlotId requested() {
        return requested;
    }

    public ResolvedProgramDescriptor effectiveDescriptor() {
        return effectiveDescriptor;
    }

    public StageId effectiveStage() {
        return effectiveStage;
    }

    public StageBand actualBand() {
        return actualBand;
    }

    public BarrierContext originatingContext() {
        return originatingContext;
    }

    public com.schmaloogium.engine.registry.internal.SelectionCredential credential() {
        return credential;
    }

    @Override
    public String toString() {
        return "ProgramBindingSelection[generation=" + registryGeneration
            + ", requested=" + requested + ", effective=" + effectiveDescriptor.effective() + "]";
    }
}
