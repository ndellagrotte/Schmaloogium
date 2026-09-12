// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.gl.AlphaBlendOverride;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.gl.ProgramHandle;
import com.schmaloogium.engine.registry.BarrierContext;
import com.schmaloogium.engine.registry.BarrierParticipantResult;
import com.schmaloogium.engine.registry.BarrierContext;
import com.schmaloogium.engine.registry.BarrierResult;
import com.schmaloogium.engine.registry.UseProgramRequest;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import com.schmaloogium.engine.registry.ProgramResolutionStatus;
import com.schmaloogium.engine.registry.ProgramSamplerLayout;
import com.schmaloogium.engine.registry.ProgramUniformCacheKey;
import com.schmaloogium.engine.registry.ProgramSelectionResult;
import com.schmaloogium.engine.registry.ProgramSelectionValidation;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.ProgramStateBundle;
import com.schmaloogium.engine.registry.ResolvedProgramDescriptor;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The private use-program state barrier (PHASE_4_DOC §4.10). One core per accepted
 * publication; the published view checks staleness before delegating here. Selection
 * authenticates, applies force-shadow before the sole fallback resolution, and privately
 * retains the resolved binding; activation authenticates the identical retained
 * selection/context, invalidates the predecessor activity, closes the predecessor lease,
 * binds without resolution, acquires the provider lock, and runs the positions in
 * sampler/built-in/custom order.
 */
final class BarrierCore {

    private volatile long generation;
    private final com.schmaloogium.engine.registry.RegistryFingerprint fingerprint;
    private final CompiledProgramRegistryImpl registry;
    private final Credentials.SourceIdentity contextIdentity;
    private volatile java.util.function.Supplier<Credentials.SourceIdentity> contextIdentityRef;
    private final ProgramBindingParticipantRef samplers;
    private final ProgramBindingParticipantRef builtIns;
    private final ProgramBindingParticipantRef customs;
    private final boolean bootstrapOnly;
    private final java.util.function.LongSupplier currentGeneration;

    private final java.util.concurrent.atomic.AtomicLong activityEpoch =
        new java.util.concurrent.atomic.AtomicLong();
    private volatile boolean callbackOpen;
    private volatile AlphaBlendOverride held;
    private volatile boolean dead;

    BarrierCore(
            long generation,
            com.schmaloogium.engine.registry.RegistryFingerprint fingerprint,
            CompiledProgramRegistryImpl registry,
            Credentials.SourceIdentity contextIdentity,
            ProgramBindingParticipantRef samplers,
            ProgramBindingParticipantRef builtIns,
            ProgramBindingParticipantRef customs,
            java.util.function.LongSupplier currentGeneration,
            boolean bootstrapOnly) {
        this.generation = generation;
        this.fingerprint = Objects.requireNonNull(fingerprint, "fingerprint");
        this.registry = Objects.requireNonNull(registry, "registry");
        this.contextIdentity = Objects.requireNonNull(contextIdentity, "contextIdentity");
        this.contextIdentityRef = () -> contextIdentity;
        this.samplers = Objects.requireNonNull(samplers, "samplers");
        this.builtIns = Objects.requireNonNull(builtIns, "builtIns");
        this.customs = Objects.requireNonNull(customs, "customs");
        this.currentGeneration = Objects.requireNonNull(currentGeneration, "currentGeneration");
        this.bootstrapOnly = bootstrapOnly;
    }

    long generation() {
        return generation;
    }

    /** The publisher stamps the accepted publication generation before first use. */
    void attachGeneration(long publicationGeneration) {
        this.generation = publicationGeneration;
    }

    /**
     * Adopted when the publisher reuses the registry's context source across generations;
     * re-points every context-liveness check without invalidating issued contexts.
     */
    void adoptContextIdentity(Credentials.SourceIdentity identity) {
        this.contextIdentityRef = () -> identity;
    }

    private boolean identityMatches(MintedBarrierContext minted) {
        return minted.source().equals(contextIdentityRef.get());
    }

    com.schmaloogium.engine.registry.RegistryFingerprint fingerprint() {
        return fingerprint;
    }

    boolean isBootstrapOnly() {
        return bootstrapOnly;
    }

    boolean isRetired() {
        return dead;
    }

    void retire() {
        dead = true;
        activityEpoch.incrementAndGet();
    }

    long liveGeneration() {
        return currentGeneration.getAsLong();
    }

    boolean isCurrent(ActivityToken token) {
        return !dead && token.epoch() == activityEpoch.get();
    }

    boolean callbackWindowOpen() {
        return callbackOpen;
    }

    ProgramHandle handleOf(ProgramSlotId provider) {
        return registry.handleOf(provider);
    }

    com.schmaloogium.engine.gl.UniformService uniforms() {
        return registry.device().uniforms();
    }

    /** Release all retained locks when the publisher replaces or recovers this barrier. */
    void releaseForReplacement() {
        dead = true;
        activityEpoch.incrementAndGet();
        AlphaBlendOverride lease = held;
        held = null;
        if (lease != null) {
            try {
                lease.close();
            } catch (RuntimeException ignored) {
                // Replacement proceeds; the diagnostic was recorded at activation time.
            }
        }
    }

    // ------------------------------------------------------------------ select

    ProgramSelectionResult select(ProgramSlotId requested, BarrierContext context) {
        Objects.requireNonNull(requested, "requested");
        MintedBarrierContext minted = asMinted(context);
        if (minted == null || !identityMatches(minted)
            || minted.kind() != MintedBarrierContext.ContextKind.ACTIVATION) {
            return new ProgramSelectionResult.ShadersOff("SELECTION_CONTEXT_INVALID");
        }
        // §4.10: an absent deferred/composite indexed pass is Skipped (absence is normal),
        // never resolved down its fallback chain to the fixed-function terminal - that
        // would draw a passthrough quad and flip the written buffers for every empty
        // index (Task B fix-up 2026-09-11; PHASE_4_DOC.md:1786, :2417).
        if (!minted.shadowPass() && registry.isAbsentIndexedPass(requested)) {
            return new ProgramSelectionResult.Skipped(requested);
        }
        ResolvedCompiledProgramBinding resolution = resolveForSelection(requested, minted);
        if (resolution == null) {
            if (registry.isAbsentIndexedPass(requested)) {
                return new ProgramSelectionResult.Skipped(requested);
            }
            return new ProgramSelectionResult.ShadersOff("UNKNOWN_REQUESTED_SLOT");
        }
        ProgramBindingSelection selection = new ProgramBindingSelection(
            generation,
            fingerprint,
            resolution.requested(),
            registry.descriptorOf(resolution),
            stageOf(resolution.effective()),
            minted.band(),
            minted,
            new SelectionCredential(new Credentials.Selection(this)));
        return new ProgramSelectionResult.Selected(selection);
    }

    /** Force-shadow before the sole fallback resolution (§4.10 step 1). */
    private ResolvedCompiledProgramBinding resolveForSelection(
            ProgramSlotId requested, MintedBarrierContext context) {
        if (context.shadowPass() && requested.packName().startsWith("gbuffers_")) {
            ProgramSlotId shadowRoot = registry.shadowRootId();
            ResolvedCompiledProgramBinding forced = registry.resolveBinding(shadowRoot);
            if (forced == null) {
                return forced;
            }
            List<ProgramSlotId> path = new ArrayList<>(forced.fallbackPath());
            if (!path.contains(shadowRoot)) {
                path.add(0, shadowRoot);
            }
            return new ResolvedCompiledProgramBinding(
                requested, forced.effective(), List.copyOf(path));
        }
        return registry.resolveBinding(requested);
    }

    private StageId stageOf(CompiledProgramBinding binding) {
        return registry.stageOf(binding.provider());
    }

    // ---------------------------------------------------------------- validate

    /** The pure private-credential validation (§4.10); first failure wins. */
    ProgramSelectionValidation validate(
            ProgramBindingSelection selection,
            BarrierContext context) {
        Objects.requireNonNull(selection, "selection");
        Objects.requireNonNull(context, "context");
        Credentials.Selection credential = selection.credential().inner();
        if (credential.issuer() != this) {
            return new ProgramSelectionValidation.Rejected(
                com.schmaloogium.engine.registry.ProgramSelectionRejection.INVALID_ISSUER);
        }
        if (dead || selection.registryGeneration() != generation
            || !selection.registryFingerprint().equals(fingerprint)) {
            return new ProgramSelectionValidation.Rejected(
                com.schmaloogium.engine.registry.ProgramSelectionRejection.STALE_GENERATION);
        }
        if (!(selection.originatingContext() instanceof MintedBarrierContext origin)
            || !(context instanceof MintedBarrierContext supplied)
            || !origin.equals(supplied)
            || !identityMatches(origin)
            || !isContextLive(origin)) {
            return new ProgramSelectionValidation.Rejected(
                com.schmaloogium.engine.registry.ProgramSelectionRejection.STALE_CONTEXT);
        }
        ResolvedCompiledProgramBinding current = registry.resolveBinding(selection.requested());
        if (current == null || !current.effective().provider()
            .equals(selection.effectiveDescriptor().effective())
            || stageOf(current.effective()) != selection.effectiveStage()
            || !descriptorMatches(current, selection.effectiveDescriptor())) {
            return new ProgramSelectionValidation.Rejected(
                com.schmaloogium.engine.registry.ProgramSelectionRejection.PROVIDER_LAYOUT_MISMATCH);
        }
        if (origin.band() != selection.actualBand()
            || stageOf(current.effective()) != origin.stage()
            && !forcedShadowSelection(origin, selection)) {
            return new ProgramSelectionValidation.Rejected(
                com.schmaloogium.engine.registry.ProgramSelectionRejection.WRONG_STAGE_BAND);
        }
        return new ProgramSelectionValidation.Valid();
    }

    private boolean isContextLive(MintedBarrierContext context) {
        return registry.contexts().isCurrent(context);
    }

    /** A valid shadow context may force a gbuffers hook to shadow; the band stays actual. */
    private boolean forcedShadowSelection(MintedBarrierContext origin,
            ProgramBindingSelection selection) {
        return origin.shadowPass()
            && selection.requested().packName().startsWith("gbuffers_")
            && selection.effectiveStage() == StageId.SHADOW;
    }

    private boolean descriptorMatches(
            ResolvedCompiledProgramBinding resolution,
            ResolvedProgramDescriptor descriptor) {
        ResolvedProgramDescriptor projected = registry.descriptorOf(resolution);
        return projected.equals(descriptor);
    }

    // ---------------------------------------------------------------- activate

    BarrierResult activate(UseProgramRequest request) {
        Objects.requireNonNull(request, "request");
        ProgramBindingSelection selection = request.selection();
        BarrierResult authentication = authenticate(selection, request.context());
        if (authentication != null) {
            return authentication;
        }
        ResolvedCompiledProgramBinding resolution = registry.resolveBinding(selection.requested());
        // Invalidate predecessor activity before any mutation (§4.10 step 5).
        activityEpoch.incrementAndGet();
        AlphaBlendOverride previous = held;
        held = null;
        if (previous != null) {
            try {
                previous.close();
            } catch (RuntimeException failure) {
                return new BarrierResult.FailedSafe("PREVIOUS_LEASE_CLOSE_FAILED");
            }
        }
        CompiledProgramBinding binding = resolution.effective();
        GLDevice device = registry.device();
        boolean shader;
        try {
            if (binding instanceof CompiledProgramBinding.ShaderProgram program) {
                shader = true;
                device.shaders().use(program.handle());
            } else {
                shader = false;
                device.shaders().useFixedFunction();
            }
        } catch (RuntimeException failure) {
            return new BarrierResult.FailedSafe("BIND_FAILED");
        }
        if (shader) {
            ProgramStateBundle state = binding.state();
            try {
                held = device.state().lockAlphaBlend(
                    StateAdapters.alpha(state.alphaTest()),
                    StateAdapters.blend(state.blend()));
            } catch (RuntimeException failure) {
                return new BarrierResult.FailedSafe("LOCK_ACQUISITION_FAILED");
            }
            try {
                device.state().effectiveBlend();
            } catch (RuntimeException failure) {
                closeQuietly(held);
                held = null;
                return new BarrierResult.FailedSafe("EFFECTIVE_BLEND_SAMPLING_FAILED");
            }
        }
        if (!shader) {
            // A fixed terminal binds fixed function with no lease and no participants;
            // locks (already dropped above) stay restored.
            return new BarrierResult.FixedFunction(resolution.fallbackPath());
        }
        long epoch = activityEpoch.get();
        ActivityToken token = new ActivityToken(this, epoch);
        List<BarrierParticipantResult.Degraded> degradations = new ArrayList<>();
        if (shader) {
            ResolvedProgramDescriptor descriptor =
                registry.descriptorOf(registry.resolveBinding(selection.requested()));
            ProgramUniformCacheKey cacheKey = new ProgramUniformCacheKey(
                generation, binding.provider(), registry.uniformLayoutOf(binding).fingerprint());
            callbackOpen = true;
            try {
                degradations.addAll(runPositions(selection, descriptor, cacheKey, token, binding));
            } finally {
                callbackOpen = false;
            }
            if (token.epoch() != activityEpoch.get()) {
                return new BarrierResult.FailedSafe("ACTIVITY_EPOCH_RACED");
            }
        }
        return new BarrierResult.Activated(
            registry.descriptorOf(registry.resolveBinding(selection.requested())),
            List.copyOf(degradations));
    }

    private BarrierResult authenticate(ProgramBindingSelection selection, BarrierContext context) {
        ProgramSelectionValidation validation = validate(selection, context);
        if (validation instanceof ProgramSelectionValidation.Valid) {
            return null;
        }
        var rejection =
            ((ProgramSelectionValidation.Rejected) validation).reason();
        return new BarrierResult.ShadersOff(rejection.name());
    }

    private List<BarrierParticipantResult.Degraded> runPositions(
            ProgramBindingSelection selection,
            com.schmaloogium.engine.registry.ResolvedProgramDescriptor descriptor,
            ProgramUniformCacheKey cacheKey,
            ActivityToken token,
            CompiledProgramBinding binding) {
        List<BarrierParticipantResult.Degraded> degradations = new ArrayList<>();
        CompiledProgramBinding.ShaderProgram shader = (CompiledProgramBinding.ShaderProgram) binding;
        BoundUniformAccess access = new BoundUniformAccess(
            this, cacheKey, binding.provider(),
            registry.uniformLayoutOf(binding), token);
        int position = 0;
        for (ProgramBindingParticipantRef participant
                : List.of(samplers, builtIns, customs)) {
            position++;
            try {
                BarrierParticipantResult result = participant.invoke(descriptor, selection, access);
                if (result instanceof BarrierParticipantResult.Degraded degraded) {
                    degradations.add(degraded);
                }
            } catch (RuntimeException failure) {
                degradations.add(new BarrierParticipantResult.Degraded(
                    "PARTICIPANT_FAILED_POSITION_" + position,
                    String.valueOf(failure.getMessage())));
            }
        }
        return degradations;
    }

    private static void closeQuietly(AlphaBlendOverride override) {
        try {
            override.close();
        } catch (RuntimeException ignored) {
            // Best-effort containment on the failure path.
        }
    }

    // ----------------------------------------------------------------- release

    BarrierResult releaseToFixedFunction(BarrierContext context) {
        MintedBarrierContext minted = asMinted(context);
        if (minted == null || !identityMatches(minted)
            || minted.kind() != MintedBarrierContext.ContextKind.RELEASE
            || !isContextLive(minted)) {
            return new BarrierResult.ShadersOff("RELEASE_CONTEXT_INVALID");
        }
        activityEpoch.incrementAndGet();
        AlphaBlendOverride lease = held;
        held = null;
        if (lease != null) {
            try {
                lease.close();
            } catch (RuntimeException failure) {
                return new BarrierResult.FailedSafe("LEASE_RESTORE_FAILED");
            }
        }
        try {
            registry.device().shaders().useFixedFunction();
        } catch (RuntimeException failure) {
            return new BarrierResult.FailedSafe("FIXED_FUNCTION_BIND_FAILED");
        }
        List<ProgramSlotId> path = List.of();
        return new BarrierResult.FixedFunction(path);
    }

    private static MintedBarrierContext asMinted(BarrierContext context) {
        return context instanceof MintedBarrierContext minted ? minted : null;
    }
}
