// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core.pipeline;

import com.schmaloogium.engine.buffers.PublishedBufferEstate;
import com.schmaloogium.engine.buffers.TextureOverlayPublicationId;
import com.schmaloogium.engine.frame.PipelineIdentity;
import com.schmaloogium.engine.frame.PipelineVersion;
import com.schmaloogium.engine.frame.ShadowInvocationSlot;
import com.schmaloogium.engine.frame.lifecycle.FrameComposition;
import com.schmaloogium.engine.frame.spi.FrameCompletionObserver;
import com.schmaloogium.engine.frame.spi.FrameRenderPort;
import com.schmaloogium.engine.registry.PublishedRegistry;
import com.schmaloogium.engine.uniforms.UniformRuntime;

import java.util.Objects;
import java.util.Optional;

/**
 * The v0.1 active pipeline tuple (PHASE_7_DOC §4.1 step 9): every field is an accepted
 * publication or a P7-owned adapter. The shadow slot, completion observer and texture
 * publication are the explicit empty values the driver treats as no-ops.
 */
public record FrameCompositionRecord(
        PipelineIdentity identity,
        PipelineVersion version,
        PublishedRegistry registry,
        PublishedBufferEstate estate,
        UniformRuntime uniforms,
        FrameRenderPort port,
        long resourceReloadEpoch) implements FrameComposition {

    public FrameCompositionRecord {
        Objects.requireNonNull(identity, "identity");
        Objects.requireNonNull(version, "version");
        Objects.requireNonNull(registry, "registry");
        Objects.requireNonNull(estate, "estate");
        Objects.requireNonNull(uniforms, "uniforms");
        Objects.requireNonNull(port, "port");
    }

    @Override
    public Optional<ShadowInvocationSlot> shadowSlot() {
        return Optional.empty();
    }

    @Override
    public Optional<FrameCompletionObserver> completionObserver() {
        return Optional.empty();
    }

    @Override
    public Optional<TextureOverlayPublicationId> texturePublication() {
        return Optional.empty();
    }
}
