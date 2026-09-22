// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

import com.schmaloogium.engine.buffers.PublishedBufferEstate;
import com.schmaloogium.engine.registry.BarrierContext;
import com.schmaloogium.engine.registry.FrameBarrierContexts;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import com.schmaloogium.engine.registry.PublishedRegistry;
import com.schmaloogium.engine.frame.spi.AtlasBindingEvidence;
import com.schmaloogium.engine.textures.TexturePublication;
import com.schmaloogium.engine.textures.TextureLeaseSource;

import java.util.Objects;

/**
 * The borrowed invocation values handed to a real shadow slot (PHASE_7_DOC §5.1).
 * All values, including the accepted texture publication, its lease source and the
 * authenticated root base observation, are borrowed for one invocation. The context
 * retains no lease; each physical snapshot acquires its own from the source.
 */
public record ShadowInvocationContext(
        FrameToken frame,
        ShadowFrameView shadowFrame,
        CameraSnapshot camera,
        PublishedRegistry registry,
        PublishedBufferEstate buffers,
        FrameBarrierContexts barrierContexts,
        ShadowExecutionView execution,
        ProgramBindingSelection selection,
        BarrierContext activationContext,
        TexturePublication texturePublication,
        TextureLeaseSource textureLeases,
        AtlasBindingEvidence baseBinding) {

    public ShadowInvocationContext {
        Objects.requireNonNull(frame, "frame");
        Objects.requireNonNull(shadowFrame, "shadowFrame");
        Objects.requireNonNull(camera, "camera");
        Objects.requireNonNull(registry, "registry");
        Objects.requireNonNull(buffers, "buffers");
        Objects.requireNonNull(barrierContexts, "barrierContexts");
        Objects.requireNonNull(execution, "execution");
        Objects.requireNonNull(selection, "selection");
        Objects.requireNonNull(activationContext, "activationContext");
        Objects.requireNonNull(texturePublication, "texturePublication");
        Objects.requireNonNull(textureLeases, "textureLeases");
        Objects.requireNonNull(baseBinding, "baseBinding");
    }
}
