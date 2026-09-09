// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.ShaderHandle;
import com.schmaloogium.engine.gl.ShaderStage;

/** The LWJGL3 backend's shader handle (PHASE_1_DOC §4.7.3) — opaque, device-scoped. */
final class Lwjgl3ShaderHandle implements ShaderHandle, Lwjgl3Handle {

    private final Lwjgl3GLDevice device;
    private final ShaderStage stage;
    private int glName;
    private String subjectLabel;
    private boolean deleted;

    Lwjgl3ShaderHandle(Lwjgl3GLDevice owner, ShaderStage stage, int glName, String subjectLabel) {
        this.device = owner;
        this.stage = stage;
        this.glName = glName;
        this.subjectLabel = subjectLabel;
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

    ShaderStage stage() {
        return stage;
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
