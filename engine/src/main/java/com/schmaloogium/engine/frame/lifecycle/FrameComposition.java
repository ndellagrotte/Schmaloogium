// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.lifecycle;

import com.schmaloogium.engine.buffers.PublishedBufferEstate;
import com.schmaloogium.engine.buffers.TextureOverlayPublicationId;
import com.schmaloogium.engine.frame.PipelineVersion;
import com.schmaloogium.engine.frame.PipelineIdentity;
import com.schmaloogium.engine.frame.ShadowInvocationSlot;
import com.schmaloogium.engine.frame.spi.FrameCompletionObserver;
import com.schmaloogium.engine.frame.spi.FrameRenderPort;
import com.schmaloogium.engine.registry.PublishedRegistry;
import com.schmaloogium.engine.uniforms.UniformRuntime;

import java.util.Objects;
import java.util.Optional;

/**
 * One coherent active pipeline tuple as the frame driver consumes it (PHASE_7_DOC §4.1
 * step 9): the accepted registry, estate, runtime, identity, version, resource epoch,
 * optional shadow slot and optional completion observer. Only a fully accepted publication
 * is installed; no partial tuple ever reaches the driver.
 */
public interface FrameComposition {

    PipelineIdentity identity();

    PipelineVersion version();

    PublishedRegistry registry();

    PublishedBufferEstate estate();

    UniformRuntime uniforms();

    FrameRenderPort port();

    /** Empty until Phase 8 publishes a slot; empty keeps SHADOW_DONE a no-op rebind. */
    Optional<ShadowInvocationSlot> shadowSlot();

    /** Empty disables the H-CAPTURE-01 observation. */
    Optional<FrameCompletionObserver> completionObserver();

    /** The explicit empty texture publication at v0.1; P13 fills it later. */
    Optional<TextureOverlayPublicationId> texturePublication();

    long resourceReloadEpoch();

    /** The Phase 9 id runtime published with this composition (v0.3; empty = IDs off). */
    default Optional<com.schmaloogium.engine.config.id.PublishedIdRuntime> idRuntime() {
        return Optional.empty();
    }

    /** The Phase 10 vertex epoch this composition admits (v0.3; empty = vanilla formats). */
    default Optional<com.schmaloogium.engine.vertex.VertexEpoch> vertexEpoch() {
        return Optional.empty();
    }
}
