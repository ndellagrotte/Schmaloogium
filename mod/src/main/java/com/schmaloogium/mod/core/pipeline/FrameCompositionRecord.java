// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core.pipeline;

import com.schmaloogium.engine.buffers.PublishedBufferEstate;
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
 * The active pipeline tuple (PHASE_7_DOC §4.1 step 9): every required field is an accepted
 * publication or a P7-owned adapter. Texture lifecycle authority is retained privately by
 * the transaction; frames receive only its restricted lease source and non-owning publication.
 */
public record FrameCompositionRecord(
        PipelineIdentity identity,
        PipelineVersion version,
        PublishedRegistry registry,
        PublishedBufferEstate estate,
        UniformRuntime uniforms,
        FrameRenderPort port,
        com.schmaloogium.engine.config.EngineFlags engineFlags,
        double handDepthMultiplier,
        long resourceReloadEpoch,
        com.schmaloogium.engine.textures.TexturePublication texturePublication,
        com.schmaloogium.engine.textures.TextureLeaseSource textureLeases,
        Optional<ShadowInvocationSlot> shadowSlot,
        Optional<com.schmaloogium.engine.config.id.PublishedIdRuntime> idRuntime,
        Optional<com.schmaloogium.engine.vertex.VertexEpoch> vertexEpoch) implements FrameComposition {

    public FrameCompositionRecord {
        Objects.requireNonNull(identity, "identity");
        Objects.requireNonNull(version, "version");
        Objects.requireNonNull(registry, "registry");
        Objects.requireNonNull(estate, "estate");
        Objects.requireNonNull(uniforms, "uniforms");
        Objects.requireNonNull(port, "port");
        Objects.requireNonNull(engineFlags, "engineFlags");
        Objects.requireNonNull(texturePublication, "texturePublication");
        Objects.requireNonNull(textureLeases, "textureLeases");
        if (texturePublication.id().generation() != estate.generation()
                || texturePublication.registryGeneration() != registry.generation()
                || texturePublication.resourceReloadEpoch() != resourceReloadEpoch
                || !texturePublication.plan().inputs().configuration().fingerprint()
                        .equals(identity.configuration())) {
            throw new IllegalArgumentException("texture publication does not match composition");
        }
        if (!Double.isFinite(handDepthMultiplier) || handDepthMultiplier <= 0d) {
            throw new IllegalArgumentException("handDepthMultiplier must be finite and positive");
        }
        shadowSlot = shadowSlot == null ? Optional.empty() : shadowSlot;
        idRuntime = idRuntime == null ? Optional.empty() : idRuntime;
        vertexEpoch = vertexEpoch == null ? Optional.empty() : vertexEpoch;
    }


    @Override
    public Optional<ShadowInvocationSlot> shadowSlot() {
        return shadowSlot;
    }

    @Override
    public Optional<com.schmaloogium.engine.config.id.PublishedIdRuntime> idRuntime() {
        return idRuntime;
    }

    @Override
    public Optional<com.schmaloogium.engine.vertex.VertexEpoch> vertexEpoch() {
        return vertexEpoch;
    }

    @Override
    public Optional<FrameCompletionObserver> completionObserver() {
        // H-CAPTURE-01: the capture agent's observer while a plan is armed, else absent (no-op).
        return com.schmaloogium.mod.glue.frame.FrameObservers.current();
    }

}
