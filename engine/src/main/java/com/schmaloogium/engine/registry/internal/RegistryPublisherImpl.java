// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.registry.BarrierContext;
import com.schmaloogium.engine.registry.BarrierPublicationCandidate;
import com.schmaloogium.engine.registry.CompiledRegistryCandidate;
import com.schmaloogium.engine.registry.ProgramRegistryView;
import com.schmaloogium.engine.registry.PublicationFailure;
import com.schmaloogium.engine.registry.PublicationFailureKind;
import com.schmaloogium.engine.registry.PublicationResult;
import com.schmaloogium.engine.registry.PublishedProgramStateBarrier;
import com.schmaloogium.engine.registry.PublishedRegistry;
import com.schmaloogium.engine.registry.RegistryBuildFailure;
import com.schmaloogium.engine.registry.RegistryFailureKind;
import com.schmaloogium.engine.registry.RegistryPublication;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The atomic registry publisher (PHASE_4_DOC §2.2/§4.11). Render-thread-only. Every accepted
 * replacement — ready pair, accepted shaders-off, forced RecoveredOff — increments the
 * generation exactly once; pre-release rejection and failed compilation do not mutate
 * publication state. Acceptance transfers both caller-owned candidates to the publisher;
 * the publisher alone releases the previous registry and barrier during replacement.
 */
public final class RegistryPublisherImpl implements com.schmaloogium.engine.registry.ProgramRegistryPublisher {

    private RegistryContexts contexts;
    private final AtomicLong generation;
    private volatile PublishedRegistry current;
    private volatile CompiledProgramRegistryImpl publishedRegistry;
    private volatile boolean contextsAdopted;

    public RegistryPublisherImpl(RegistryContexts contexts, long initialGeneration) {
        this.contexts = Objects.requireNonNull(contexts, "contexts");
        this.generation = new AtomicLong(initialGeneration);
        this.current = PublishedRegistry.off(initialGeneration, contexts);
    }

    /**
     * Each accepted ready publication migrates the live context source to its product, so
     * barrier liveness checks and the next replacement's release context share one source
     * (the published snapshot's {@code contexts()}).
     */
    private RegistryContexts effectiveContexts(CompiledProgramRegistryImpl accepted) {
        contexts = accepted.contexts();
        contextsAdopted = true;
        return contexts;
    }

    @Override
    public PublishedRegistry current() {
        return current;
    }

    @Override
    public PublicationResult publish(RegistryPublication publication, BarrierContext releaseContext) {
        Objects.requireNonNull(releaseContext, "releaseContext");
        if (publication == null) {
            return reject(PublicationFailureKind.NULL_PUBLICATION);
        }
        // Every publication accepts only a release-kind context from the current
        // source/epoch (§4.10): the one "issued by the old publication's
        // BarrierContextSource", i.e. current().contexts() — the only source a caller
        // outside this package can mint from. A ready pair may equally present a context
        // minted from the incoming product's own source (the pre-2026-09-11 shape); the
        // live source migrates to the accepted product only on acceptance.
        if (!(releaseContext instanceof MintedBarrierContext minted)) {
            return reject(PublicationFailureKind.CONTEXT_SOURCE);
        }
        RegistryContexts validationContexts = contexts;
        if (!minted.source().equals(validationContexts.identity())
                && publication instanceof RegistryPublication.Ready early
                && early.registry() != null
                && minted.source().equals(early.registry().registry().contexts().identity())) {
            validationContexts = early.registry().registry().contexts();
        }
        if (!minted.source().equals(validationContexts.identity())) {
            return reject(PublicationFailureKind.CONTEXT_SOURCE);
        }
        if (!validationContexts.isCurrent(minted)) {
            return reject(PublicationFailureKind.CONTEXT_EPOCH);
        }
        if (minted.kind() != MintedBarrierContext.ContextKind.RELEASE) {
            return reject(PublicationFailureKind.CONTEXT_KIND);
        }
        if (publication instanceof RegistryPublication.ShadersOff off) {
            return acceptOff(off.cause(), null, false);
        }
        RegistryPublication.Ready ready = (RegistryPublication.Ready) publication;
        CompiledRegistryCandidate candidate = ready.registry();
        BarrierPublicationCandidate barrierCandidate = ready.barrier();
        if (candidate == null || barrierCandidate == null) {
            return reject(PublicationFailureKind.NULL_PUBLICATION);
        }
        if (candidate.isClosed()) {
            return reject(PublicationFailureKind.REGISTRY_STATE);
        }
        if (barrierCandidate.isClosed()) {
            return reject(PublicationFailureKind.BARRIER_STATE);
        }
        BarrierBundle bundle = barrierCandidate.bundle();
        CompiledProgramRegistryImpl registry = candidate.registry();
        if (bundle.isReleased()) {
            return reject(PublicationFailureKind.BARRIER_STATE);
        }
        if (!bundle.fingerprint().equals(registry.fingerprint())) {
            return reject(PublicationFailureKind.PRODUCT_IDENTITY);
        }
        if (bundle.registryIdentity() != registry.identity()) {
            return reject(PublicationFailureKind.REGISTRY_IDENTITY);
        }
        if (registry.ownershipTransferred()) {
            return reject(PublicationFailureKind.OWNERSHIP);
        }
        if (bundle.isBootstrapOnly()) {
            return reject(PublicationFailureKind.BOOTSTRAP_CANDIDATE);
        }
        // Accept: transfer ownership, retire the previous barrier/registry, advance once.
        registry.markOwnershipTransferred();
        barrierCandidate.markTransferred();
        retirePrevious();
        long next = generation.incrementAndGet();
        RegistryContexts snapshotContexts = effectiveContexts(registry);
        snapshotContexts.publishStages(registry.stages());
        bundle.barrier().adoptContextIdentity(snapshotContexts.identity());
        ProgramRegistryView view = registry.detachedView();
        PublishedBarrierView barrierView =
            new PublishedBarrierView(next, bundle.barrier());
        PublishedRegistry snapshot = new PublishedRegistry(
            next, Optional.of(view), Optional.of(barrierView), snapshotContexts);
        bundle.barrier().attachGeneration(next);
        publishedRegistry = registry;
        current = snapshot;
        return new PublicationResult.Accepted(snapshot);
    }

    /** Accepted shaders-off / forced recovery: off snapshot, generation advanced once. */
    public PublicationResult acceptOff(
            RegistryBuildFailure cause,
            PublicationFailureKind recoveryKind,
            boolean forced) {
        retirePrevious();
        long next = generation.incrementAndGet();
        PublishedRegistry snapshot = PublishedRegistry.off(next, contexts);
        current = snapshot;
        if (forced) {
            return new PublicationResult.RecoveredOff(snapshot, new PublicationFailure(
                recoveryKind, diagnosticOf(cause), "recovered to shaders-off"));
        }
        return new PublicationResult.Accepted(snapshot);
    }

    private void retirePrevious() {
        PublishedRegistry previous = current;
        previous.barrier().ifPresent(view -> {
            if (view instanceof PublishedBarrierView barrierView) {
                barrierView.retireCore();
            }
        });
        CompiledProgramRegistryImpl previousRegistry = publishedRegistry;
        if (previousRegistry != null) {
            previousRegistry.close();
        }
        publishedRegistry = null;
    }

    private PublicationResult.Rejected reject(PublicationFailureKind kind) {
        return new PublicationResult.Rejected(current, new PublicationFailure(
            kind, kind.name(), "publication rejected before release"));
    }

    private static String diagnosticOf(RegistryBuildFailure cause) {
        return cause == null ? "SHADERS_OFF" : cause.diagnosticId();
    }
}
