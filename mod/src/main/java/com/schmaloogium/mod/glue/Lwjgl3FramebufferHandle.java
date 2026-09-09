// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.FramebufferDrawSlot;
import com.schmaloogium.engine.gl.FramebufferHandle;

import java.util.ArrayList;
import java.util.List;

/**
 * The LWJGL3 backend's framebuffer handle (PHASE_1_DOC §4.7.4). The backend retains the
 * attachment ledger and the established positional draw route: typed-clear and
 * depth-copy admission read them instead of querying GL state.
 */
final class Lwjgl3FramebufferHandle implements FramebufferHandle, Lwjgl3Handle {

    private final Lwjgl3GLDevice device;
    private String subjectLabel;
    private boolean deleted;
    private int glName;

    /** Attachments, indexed by positional color attachment index; null slots are holes. */
    final List<Lwjgl3OwnedTexture> colorAttachments = new ArrayList<>();

    Lwjgl3OwnedTexture depthAttachment;
    boolean stencilAttached;

    /** The established positional draw route; null until drawBuffers first sets one. */
    List<FramebufferDrawSlot> drawRoute;

    Lwjgl3FramebufferHandle(Lwjgl3GLDevice owner, int glName, String debugLabel) {
        this.device = owner;
        this.glName = glName;
        this.subjectLabel = debugLabel;
    }

    @Override
    public Lwjgl3GLDevice owner() {
        return device;
    }

    @Override
    public boolean deleted() {
        return deleted;
    }

    @Override
    public String subjectLabel() {
        return subjectLabel;
    }

    int glName() {
        return glName;
    }

    void relabel(String label) {
        this.subjectLabel = label;
    }

    void markDeleted() {
        this.deleted = true;
    }
}
