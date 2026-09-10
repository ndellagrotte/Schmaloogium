// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

import com.schmaloogium.engine.buffers.PublishedBufferEstate;
import com.schmaloogium.engine.registry.BarrierContext;
import com.schmaloogium.engine.registry.FrameBarrierContexts;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import com.schmaloogium.engine.registry.PublishedRegistry;

import java.util.Objects;

/**
 * The borrowed invocation values handed to a real shadow slot (PHASE_7_DOC §5.1). The exact
 * §2 field order is (frame, shadowFrame, camera, registry, buffers, barrierContexts,
 * execution, selection, activationContext, texturePublication, textureLeases); the trailing
 * R7-12 texture pair joins this record together with the Phase-13 lease source itself — at
 * v0.1 the slot is NotInstalled and the pair has no owner to borrow from. All values are
 * borrowed for the one invocation; the context retains nothing.
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
        BarrierContext activationContext) {

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
    }
}
