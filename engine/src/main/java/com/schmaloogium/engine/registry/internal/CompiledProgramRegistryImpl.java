// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.gl.ProgramHandle;
import com.schmaloogium.engine.registry.CompiledProgramRegistry;
import com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint;
import com.schmaloogium.engine.registry.ProgramOwnBuildDisposition;
import com.schmaloogium.engine.registry.ProgramResolutionProjection;
import com.schmaloogium.engine.registry.ProgramRegistryView;
import com.schmaloogium.engine.registry.ProgramSamplerLayout;
import com.schmaloogium.engine.registry.ProgramSamplerLayoutFingerprint;
import com.schmaloogium.engine.registry.ProgramSlotDescriptor;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.ProgramSlotKind;
import com.schmaloogium.engine.registry.ProgramUniformLayout;
import com.schmaloogium.engine.registry.RegistryFingerprint;
import com.schmaloogium.engine.registry.ResolvedProgramDescriptor;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.registry.StageRegistry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The owning private registry (PHASE_4_DOC §2.2). Immutable after assembly except for the
 * close idempotence latch. Owns exactly the Phase-4-issued program handles; close deletes
 * them through the ShaderService delete-and-report stage and leaves detached views
 * unchanged. Only this implementation and, after acceptance, the publisher can reach it.
 */
public final class CompiledProgramRegistryImpl implements CompiledProgramRegistry {

    private static final java.util.concurrent.atomic.AtomicLong IDENTITY =
        new java.util.concurrent.atomic.AtomicLong();

    private final StageRegistry stages;
    private final Map<ProgramSlotId, ProgramSlotDescriptor> catalog;
    private final Map<ProgramSlotId, ResolvedCompiledProgramBinding> resolutions;
    private final List<ProgramResolutionProjection> projections;
    private final RegistryFingerprint fingerprint;
    private final FixedSamplerPolicyFingerprint policyFingerprint;
    private final GLDevice device;
    private final List<ProgramHandle> ownedHandles;
    private final RegistryContexts contexts;
    private final ProgramSlotId shadowRoot;
    private final long identity = IDENTITY.incrementAndGet();
    private final java.util.concurrent.atomic.AtomicBoolean compositionClaimed =
        new java.util.concurrent.atomic.AtomicBoolean();
    private volatile boolean closed;
    private volatile boolean ownershipTransferred;
    private final ProgramRegistryView detachedView;

    CompiledProgramRegistryImpl(
            StageRegistry stages,
            List<ProgramSlotDescriptor> catalogOrder,
            Map<ProgramSlotId, ResolvedCompiledProgramBinding> resolutions,
            List<ProgramResolutionProjection> projections,
            RegistryFingerprint fingerprint,
            FixedSamplerPolicyFingerprint policyFingerprint,
            GLDevice device,
            List<ProgramHandle> ownedHandles,
            RegistryContexts contexts,
            ProgramSlotId shadowRoot) {
        this.stages = stages;
        Map<ProgramSlotId, ProgramSlotDescriptor> byId = new LinkedHashMap<>();
        for (ProgramSlotDescriptor descriptor : catalogOrder) {
            byId.put(descriptor.id(), descriptor);
        }
        this.catalog = Map.copyOf(byId);
        this.resolutions = Map.copyOf(resolutions);
        this.projections = List.copyOf(projections);
        this.fingerprint = fingerprint;
        this.policyFingerprint = policyFingerprint;
        this.device = device;
        this.ownedHandles = List.copyOf(ownedHandles);
        this.contexts = contexts;
        this.shadowRoot = shadowRoot;
        this.detachedView = new DetachedView(this);
    }

    GLDevice device() {
        return device;
    }

    long identity() {
        return identity;
    }

    boolean claimComposition() {
        return compositionClaimed.compareAndSet(false, true);
    }

    void releaseCompositionClaim() {
        compositionClaimed.set(false);
    }

    void sealComposition() {
        compositionClaimed.set(true);
    }

    RegistryContexts contexts() {
        return contexts;
    }

    ProgramSlotId shadowRootId() {
        return shadowRoot;
    }

    public boolean ownershipTransferred() {
        return ownershipTransferred;
    }

    public void markOwnershipTransferred() {
        this.ownershipTransferred = true;
    }

    /** The resolved walk for a catalog raster slot; null for unknown or virtual slots. */
    ResolvedCompiledProgramBinding resolveBinding(ProgramSlotId requested) {
        return resolutions.get(requested);
    }

    boolean isAbsentIndexedPass(ProgramSlotId requested) {
        ProgramSlotDescriptor descriptor = catalog.get(requested);
        if (descriptor == null || descriptor.kind() != ProgramSlotKind.RASTER) {
            return false;
        }
        boolean indexed = false;
        for (com.schmaloogium.engine.registry.StageStep step : stages.schedule()) {
            for (com.schmaloogium.engine.registry.PassDescriptor pass : stages.passes(step)) {
                if (pass.slot().equals(requested) && pass.index().isPresent()) {
                    indexed = true;
                    break;
                }
            }
        }
        if (!indexed) {
            return false;
        }
        ProgramResolutionProjection row = projectionOf(requested);
        return row != null
            && (row.ownBuild() == ProgramOwnBuildDisposition.NO_SOURCE
                || row.ownBuild() == ProgramOwnBuildDisposition.DISABLED);
    }

    ProgramResolutionProjection projectionOf(ProgramSlotId slot) {
        for (ProgramResolutionProjection row : projections) {
            if (row.slot().equals(slot)) {
                return row;
            }
        }
        return null;
    }

    /** The handle of a provider's own shader binding; null when none exists. */
    public ProgramHandle handleOf(ProgramSlotId provider) {
        for (ResolvedCompiledProgramBinding resolution : resolutions.values()) {
            if (resolution.effective() instanceof CompiledProgramBinding.ShaderProgram shader
                && shader.provider().equals(provider)) {
                return shader.handle();
            }
        }
        return null;
    }

    StageId stageOf(ProgramSlotId slot) {
        ProgramSlotDescriptor descriptor = catalog.get(slot);
        if (descriptor == null) {
            throw new IllegalArgumentException("unknown slot: " + slot);
        }
        return descriptor.stage();
    }

    ProgramUniformLayout uniformLayoutOf(CompiledProgramBinding binding) {
        if (binding instanceof CompiledProgramBinding.ShaderProgram shader) {
            return shader.uniformLayout();
        }
        return ProgramUniformLayout.empty();
    }

    /** The published descriptor projection of one resolved binding (handle-free). */
    ResolvedProgramDescriptor descriptorOf(ResolvedCompiledProgramBinding resolution) {
        CompiledProgramBinding effective = resolution.effective();
        List<ProgramSlotId> fallbackPath = effective.provider().equals(resolution.requested())
            ? List.of()
            : resolution.fallbackPath();
        if (effective instanceof CompiledProgramBinding.ShaderProgram shader) {
            return new ResolvedProgramDescriptor(
                resolution.requested(),
                shader.provider(),
                shader.state(),
                shader.uniformLayout(),
                shader.samplerLayout(),
                shader.sources(),
                fallbackPath);
        }
        CompiledProgramBinding.FixedFunction fixed = (CompiledProgramBinding.FixedFunction) effective;
        return new ResolvedProgramDescriptor(
            resolution.requested(),
            fixed.provider(),
            fixed.state(),
            ProgramUniformLayout.empty(),
            new ProgramSamplerLayout.FixedFunctionEmpty(
                new ProgramSamplerLayoutFingerprint(
                    UniformLayouts.emptyLayoutDigest("FIXED_FUNCTION", policyFingerprint)),
                policyFingerprint),
            List.of(),
            fallbackPath);
    }

    public ProgramRegistryView detachedView() {
        return detachedView;
    }

    @Override
    public StageRegistry stages() {
        return stages;
    }

    @Override
    public Optional<ResolvedProgramDescriptor> resolve(ProgramSlotId requested) {
        ResolvedCompiledProgramBinding resolution = resolutions.get(requested);
        if (resolution == null || resolution.effective() == null) {
            return Optional.empty();
        }
        return Optional.of(descriptorOf(resolution));
    }

    @Override
    public List<ProgramResolutionProjection> resolutions() {
        return projections;
    }

    @Override
    public RegistryFingerprint fingerprint() {
        return fingerprint;
    }

    @Override
    public FixedSamplerPolicyFingerprint samplerPolicyFingerprint() {
        return policyFingerprint;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        for (ProgramHandle handle : ownedHandles) {
            device.shaders().delete(handle);
        }
    }

    /** The immutable detached metadata snapshot (PHASE_4_DOC §2.2). */
    private record DetachedView(CompiledProgramRegistryImpl registry)
            implements ProgramRegistryView {

        @Override
        public StageRegistry stages() {
            return registry.stages;
        }

        @Override
        public Optional<ResolvedProgramDescriptor> resolve(ProgramSlotId requested) {
            return registry.resolve(requested);
        }

        @Override
        public List<ProgramResolutionProjection> resolutions() {
            return registry.projections;
        }

        @Override
        public RegistryFingerprint fingerprint() {
            return registry.fingerprint;
        }

        @Override
        public FixedSamplerPolicyFingerprint samplerPolicyFingerprint() {
            return registry.policyFingerprint;
        }
    }
}
