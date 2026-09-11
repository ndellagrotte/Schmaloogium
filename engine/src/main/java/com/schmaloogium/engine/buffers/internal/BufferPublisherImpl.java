// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors
package com.schmaloogium.engine.buffers.internal;

import com.schmaloogium.engine.buffers.BufferEstateCandidate;
import com.schmaloogium.engine.buffers.BufferFailure;
import com.schmaloogium.engine.buffers.BufferEstatePublisher;
import com.schmaloogium.engine.buffers.BufferPublicationResult;
import com.schmaloogium.engine.buffers.BufferResizeConsumer;
import com.schmaloogium.engine.buffers.BufferResizeNotice;
import com.schmaloogium.engine.buffers.BufferResizeReason;
import com.schmaloogium.engine.buffers.BufferResizeRegistration;
import com.schmaloogium.engine.buffers.BufferResizeRegistrationRejection;
import com.schmaloogium.engine.buffers.BufferResizeRegistrationResult;
import com.schmaloogium.engine.buffers.BufferResourceSnapshot;
import com.schmaloogium.engine.buffers.BufferSizing;
import com.schmaloogium.engine.buffers.PublishedBufferEstate;
import com.schmaloogium.engine.buffers.ResourceProjectionUnavailableReason;
import com.schmaloogium.engine.buffers.ResizeConsumerResult;
import com.schmaloogium.engine.registry.RegistryFingerprint;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The Phase 5 estate publisher (PHASE_5_DOC §4.11): render-thread-only installation of
 * ready estates, deterministic first-applicable resize-reason derivation, registration
 * validation against the generation-to-sizing ledger, and in-order consumer dispatch whose
 * first failure replaces the estate with a shaders-off publication at the next integer
 * generation. Off and never-opened generations stay absent from the ledger, so consumer
 * acknowledgements can only reference successfully installed ready estates.
 */
public final class BufferPublisherImpl implements BufferEstatePublisher {

    /** Outcome of one resize dispatch: the first failing consumer, if any. */
    private record DispatchOutcome(String failedConsumerId, int deliveredCount) {
    }

    /** A live resize registration with its last acknowledged baseline (§4.11). */
    private static final class Registration {
        final String consumerId;
        final BufferResizeConsumer consumer;
        BufferSizing acknowledgedSizing;

        Registration(String consumerId, BufferResizeConsumer consumer,
                BufferSizing acknowledgedSizing) {
            this.consumerId = consumerId;
            this.consumer = consumer;
            this.acknowledgedSizing = acknowledgedSizing;
        }
    }

    private long lastGeneration;
    private PublishedBufferEstate current;
    private CandidateImpl installed;
    private final Map<Long, BufferSizing> generationSizings = new LinkedHashMap<>();
    private final Map<String, Registration> consumers = new LinkedHashMap<>();

    /** Dependency-free construction; Phase 7 owns the instance from here on. */
    public BufferPublisherImpl() {
        lastGeneration = 0L;
        current = new PublishedBufferEstate(lastGeneration, Optional.empty(),
            new BufferResourceSnapshot.Unavailable(
                ResourceProjectionUnavailableReason.SHADERS_OFF));
    }

    @Override
    public PublishedBufferEstate current() {
        return current;
    }

    @Override
    public BufferPublicationResult publish(BufferEstateCandidate candidate,
            RegistryFingerprint acceptedRegistry) {
        Objects.requireNonNull(candidate, "candidate");
        Objects.requireNonNull(acceptedRegistry, "acceptedRegistry");
        CandidateImpl impl = candidate.internal();
        if (!impl.registryFingerprint().equals(acceptedRegistry)) {
            // Provenance mismatch rejects before any mutation; the candidate stays
            // caller-owned for diagnosis or close.
            return new BufferPublicationResult.ProvenanceRejected(
                impl.registryFingerprint(), acceptedRegistry);
        }
        long generation = lastGeneration + 1;
        CandidateImpl previous = installed;
        BufferSizing previousSizing = previous == null ? null : previous.sizing();
        PlanningArtifacts previousArtifacts = previous == null ? null : previous.core().plan;
        RegistryFingerprint previousRegistry = previous == null ? null
            : previous.registryFingerprint();
        installed = impl;
        lastGeneration = generation;
        // Acceptance issues the sole estate generation (§5.1): the view, its shadow
        // disposition and every per-frame protocol check key on this stamp.
        impl.core().generation = generation;
        generationSizings.put(generation, impl.sizing());
        current = new PublishedBufferEstate(generation,
            Optional.of(impl.view()), impl.resources());
        if (previous != null && previous != impl) {
            // The publication owns the superseded estate from here; the candidate handle
            // Phase 7 still holds is an idempotent backstop.
            previous.closeOwned();
        }
        DispatchOutcome outcome = dispatchResizes(impl.sizing(), generation, previousSizing,
            previousArtifacts, previousRegistry);
        if (outcome != null) {
            return new BufferPublicationResult.ConsumerFailed(generation, current,
                outcome.failedConsumerId(), outcome.deliveredCount());
        }
        return new BufferPublicationResult.Published(current);
    }

    @Override
    public BufferPublicationResult publishOff(BufferFailure cause) {
        Objects.requireNonNull(cause, "cause");
        lastGeneration = lastGeneration + 1;
        current = new PublishedBufferEstate(lastGeneration, Optional.empty(),
            new BufferResourceSnapshot.Unavailable(
                ResourceProjectionUnavailableReason.SHADERS_OFF));
        return new BufferPublicationResult.Published(current);
    }

    @Override
    public BufferResizeRegistrationResult addResizeConsumer(String consumerId,
            BufferResizeConsumer consumer, BufferSizing acknowledgedSizing,
            long acknowledgedGeneration) {
        if (consumerId == null || consumerId.isBlank()) {
            return rejected(BufferResizeRegistrationRejection.BLANK_CONSUMER_ID);
        }
        Objects.requireNonNull(consumer, "consumer");
        Objects.requireNonNull(acknowledgedSizing, "acknowledgedSizing");
        if (consumers.containsKey(consumerId)) {
            return rejected(BufferResizeRegistrationRejection.DUPLICATE_LIVE_CONSUMER_ID);
        }
        if (acknowledgedGeneration > current.generation()) {
            return rejected(BufferResizeRegistrationRejection.FUTURE_ACKNOWLEDGED_GENERATION);
        }
        BufferSizing recorded = generationSizings.get(acknowledgedGeneration);
        if (recorded == null) {
            // Off and never-installed generations are unknown for acknowledgement (§4.11).
            return rejected(BufferResizeRegistrationRejection.UNKNOWN_ACKNOWLEDGED_GENERATION);
        }
        if (!recorded.equals(acknowledgedSizing)) {
            return rejected(BufferResizeRegistrationRejection.ACKNOWLEDGED_SIZING_MISMATCH);
        }
        Registration registration = new Registration(consumerId, consumer, acknowledgedSizing);
        consumers.put(consumerId, registration);
        return new BufferResizeRegistrationResult.Registered(new BufferResizeRegistration() {
            @Override
            public void close() {
                consumers.remove(consumerId);
            }
        });
    }

    /**
     * Invokes live consumers in registration order; stops at the first failure or thrown
     * callback, replaces the installed estate with a shaders-off publication at the next
     * integer generation, and retires the failed estate. Returns the failure outcome, or
     * null when every consumer succeeded.
     */
    private DispatchOutcome dispatchResizes(BufferSizing newSizing, long newGeneration,
            BufferSizing previousSizing, PlanningArtifacts previousArtifacts,
            RegistryFingerprint previousRegistry) {
        if (consumers.isEmpty()) {
            return null;
        }
        BufferResizeReason reason = resizeReason(previousSizing, previousArtifacts,
            previousRegistry, newSizing, installed.core().plan,
            installed.registryFingerprint());
        int delivered = 0;
        for (Registration registration : consumers.values()) {
            BufferResizeNotice notice = new BufferResizeNotice(registration.acknowledgedSizing,
                newSizing, newGeneration, reason);
            boolean advanced;
            try {
                advanced = registration.consumer.resize(notice) == ResizeConsumerResult.SUCCESS;
            } catch (RuntimeException | LinkageError e) {
                advanced = false;
            }
            if (advanced) {
                registration.acknowledgedSizing = newSizing;
                delivered++;
                continue;
            }
            // The failing generation never opens for drawing; its ledger fact is removed
            // so acknowledgements cannot reference it (§4.11 "never-installed").
            generationSizings.remove(newGeneration);
            CandidateImpl replaced = installed;
            installed = null;
            replaced.closeOwned();
            lastGeneration = newGeneration + 1;
            current = new PublishedBufferEstate(lastGeneration, Optional.empty(),
                new BufferResourceSnapshot.Unavailable(
                    ResourceProjectionUnavailableReason.SHADERS_OFF));
            return new DispatchOutcome(registration.consumerId, delivered);
        }
        return null;
    }

    /**
     * First applicable reason in declaration order derivable from installed candidate
     * facts (§4.11). Render-quality and shadow-quality fold into their extents during
     * planning (D-P5-27 two-field sizing), and main-depth extent changes route through
     * {@code refreshMainDepth} rather than publication, so the corresponding enum values
     * are never first-applicable here.
     */
    private static BufferResizeReason resizeReason(BufferSizing previousSizing,
            PlanningArtifacts previousArtifacts, RegistryFingerprint previousRegistry,
            BufferSizing newSizing, PlanningArtifacts newArtifacts,
            RegistryFingerprint newRegistry) {
        if (!previousSizing.mainExtent().equals(newSizing.mainExtent())) {
            return BufferResizeReason.DISPLAY_EXTENT;
        }
        if (!previousSizing.shadowExtent().equals(newSizing.shadowExtent())) {
            return BufferResizeReason.SHADOW_RESOLUTION;
        }
        if (!previousRegistry.equals(newRegistry)) {
            return BufferResizeReason.REGISTRY_PLAN;
        }
        if (!previousArtifacts.equals(newArtifacts)) {
            return BufferResizeReason.COLOR_INVENTORY_OR_FORMAT;
        }
        return BufferResizeReason.PACK_CONFIGURATION;
    }

    private static BufferResizeRegistrationResult rejected(
            BufferResizeRegistrationRejection reason) {
        return new BufferResizeRegistrationResult.Rejected(reason);
    }
}
