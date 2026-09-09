// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.registry.BarrierContext;
import com.schmaloogium.engine.registry.BarrierContextSource;
import com.schmaloogium.engine.registry.FrameBarrierContexts;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.registry.StageRegistry;
import com.schmaloogium.engine.registry.StageStep;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The Phase-4-owned context source (PHASE_4_DOC §4.10). Render-thread-only. One instance is
 * carried by every PublishedRegistry snapshot so epochs advance across replacements.
 * {@code beginFrame} monotonically advances the private epoch and permanently retires the
 * prior frame contexts. {@code activation} accepts only a StageStep of the current published
 * StageRegistry and requires the shadow relation; {@code release} issues the canonical
 * release-kind context copying the last activation pair (FINAL/SCREEN when none) with
 * shadowPass always false.
 */
public final class RegistryContexts implements BarrierContextSource {

    private final Credentials.SourceIdentity identity;
    private final AtomicLong epoch = new AtomicLong();
    private volatile FrameContexts current;
    private volatile StageRegistry publishedStages;

    public RegistryContexts() {
        this.identity = new Credentials.SourceIdentity(SYSTEM_IDENTITY.incrementAndGet());
    }

    private static final AtomicLong SYSTEM_IDENTITY = new AtomicLong();

    @Override
    public FrameBarrierContexts beginFrame() {
        long frame = epoch.incrementAndGet();
        FrameContexts frameContexts = new FrameContexts(frame);
        current = frameContexts;
        return frameContexts;
    }

    /** The publisher installs the current stage registry so steps can be authenticated. */
    public void publishStages(StageRegistry stages) {
        this.publishedStages = Objects.requireNonNull(stages, "stages");
    }

    /** True when this minted context belongs to the live source and current epoch. */
    boolean isCurrent(MintedBarrierContext context) {
        return context.source().equals(identity)
            && current != null
            && context.epoch() == current.frame;
    }

    Credentials.SourceIdentity identity() {
        return identity;
    }

    private final class FrameContexts implements FrameBarrierContexts {

        private final long frame;
        private volatile MintedBarrierContext lastActivation;

        FrameContexts(long frame) {
            this.frame = frame;
        }

        @Override
        public BarrierContext activation(StageStep step, boolean shadowPass) {
            Objects.requireNonNull(step, "step");
            StageRegistry stages = publishedStages;
            if (stages == null || !stages.stepExists(step)) {
                throw new IllegalArgumentException("step is not in the published stage registry");
            }
            boolean expectedShadow = step.stage() == StageId.SHADOW
                && step.band() == StageBand.SHADOW;
            if (shadowPass != expectedShadow) {
                throw new IllegalArgumentException(
                    "shadowPass must equal the SHADOW/SHADOW step relation");
            }
            MintedBarrierContext context = new MintedBarrierContext(
                identity, frame, MintedBarrierContext.ContextKind.ACTIVATION,
                step.stage(), step.band(), shadowPass);
            lastActivation = context;
            return context;
        }

        @Override
        public BarrierContext release() {
            MintedBarrierContext last = lastActivation;
            StageId stage = last != null ? last.stage() : StageId.FINAL;
            StageBand band = last != null ? last.band() : StageBand.SCREEN;
            return new MintedBarrierContext(
                identity, frame, MintedBarrierContext.ContextKind.RELEASE, stage, band, false);
        }
    }
}
