// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/**
 * Render-thread safe-boundary publication of one candidate (PHASE_9_DOC §2.2/§4.1,
 * §5.3). Phase 7 validates matching identities and calls {@code publish} before a new
 * frame may open; a successful call assigns the next monotonically increasing positive
 * generation, atomically replaces the current publication and retires (never silently
 * frees) the previous one. Rejected publications leave the candidate caller-owned and
 * the current runtime untouched. Generation overflow is terminal shaders-off, never
 * wraparound.
 */
public interface IdRuntimePublisher {

    /** Publishes the candidate at the safe boundary described by the context. */
    IdPublishResult publish(IdRuntimeCandidate candidate, IdPublishContext context);

    /** The borrowed current publication, or empty while inactive. */
    java.util.Optional<PublishedIdRuntime> current();

    /** Retires the current publication (shaders-off / coordinated teardown path). */
    IdDeactivateResult deactivate(IdPublishContext context);

    /**
     * Creates a publisher wired to the Phase 6 event sink and the diagnostic reporter.
     * The sink is the only upload path for held/entity/TE values; scope protocol
     * failures report once on the frame channel.
     */
    static IdRuntimePublisher create(
            com.schmaloogium.engine.uniforms.UniformEventSink sink,
            com.schmaloogium.engine.diag.DiagnosticReporter diagnostics) {
        return new IdRuntimePublisherImpl(sink, diagnostics);
    }
}
