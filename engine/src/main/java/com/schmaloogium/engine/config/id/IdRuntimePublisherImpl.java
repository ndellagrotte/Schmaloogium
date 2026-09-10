// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.uniforms.UniformEventSink;

import java.util.List;
import java.util.Optional;

/**
 * Render-thread publication owner (PHASE_9_DOC §4.1, §5.3). Generations are monotonically
 * increasing positive longs; overflow is terminal shaders-off. Replacement retires (never
 * silently frees) the previous publication; rejected publications leave the candidate
 * caller-owned and the current runtime untouched. Lifecycle notices report on the Phase 9
 * IDS channel.
 */
final class IdRuntimePublisherImpl implements IdRuntimePublisher {

    private final UniformEventSink sink;
    private final DiagnosticReporter diagnostics;
    private long nextGeneration = 1;
    private PublishedIdRuntimeImpl current;
    private boolean terminal;

    IdRuntimePublisherImpl(UniformEventSink sink, DiagnosticReporter diagnostics) {
        this.sink = java.util.Objects.requireNonNull(sink, "sink");
        this.diagnostics = java.util.Objects.requireNonNull(diagnostics, "diagnostics");
    }

    @Override
    public IdPublishResult publish(IdRuntimeCandidate candidate, IdPublishContext context) {
        if (!(candidate instanceof IdRuntimeCandidateImpl candidateImpl)) {
            return new IdPublishResult.Rejected(IdPublishResult.Rejection.CANDIDATE_CLOSED);
        }
        if (terminal) {
            return new IdPublishResult.Rejected(IdPublishResult.Rejection.PUBLISHER_TERMINAL);
        }
        if (candidateImpl.isClosed()) {
            return new IdPublishResult.Rejected(IdPublishResult.Rejection.CANDIDATE_CLOSED);
        }
        if (candidateImpl.isTransferred()) {
            return new IdPublishResult.Rejected(
                    IdPublishResult.Rejection.CANDIDATE_ALREADY_PUBLISHED);
        }
        if (nextGeneration == Long.MAX_VALUE) {
            // Overflow is terminal shaders-off, never wraparound (§4.1).
            terminal = true;
            return new IdPublishResult.Rejected(
                    IdPublishResult.Rejection.GENERATION_OVERFLOW);
        }
        long generation = nextGeneration++;
        PublishedIdRuntimeImpl runtime = new PublishedIdRuntimeImpl(candidateImpl.tables(),
                generation, candidateImpl.view().fingerprint(), sink, diagnostics);
        candidateImpl.markTransferred();
        PublishedIdRuntimeImpl previous = current;
        current = runtime;
        if (previous != null) {
            previous.retire();
            notice("schmaloogium.info.ids.retired", previous.generation(), context);
        }
        notice("schmaloogium.info.ids.published", generation, context);
        return new IdPublishResult.Published(runtime);
    }

    @Override
    public Optional<PublishedIdRuntime> current() {
        return Optional.ofNullable(current);
    }

    @Override
    public IdDeactivateResult deactivate(IdPublishContext context) {
        if (current == null) {
            return IdDeactivateResult.ALREADY_INACTIVE;
        }
        PublishedIdRuntimeImpl previous = current;
        current = null;
        previous.retire();
        notice("schmaloogium.info.ids.retired", previous.generation(), context);
        return IdDeactivateResult.DEACTIVATED;
    }

    private void notice(String key, long generation, IdPublishContext context) {
        diagnostics.report(new EngineDiagnostic(DiagnosticSeverity.INFO, UserChannel.LOG_ONLY,
                key, List.of(generation, context.stage()), context.stage(), LogChannels.IDS));
    }
}
