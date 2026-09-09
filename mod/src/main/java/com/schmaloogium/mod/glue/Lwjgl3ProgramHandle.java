// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.ProgramHandle;

/**
 * The LWJGL3 backend's program handle (PHASE_1_DOC §4.7.3): opaque to {@code :engine},
 * carrying no accessor the engine can reach. Package-private {@code glName()} and the
 * device identity are backend-private; subject labels are synthetic sequence names,
 * replaceable through {@link com.schmaloogium.engine.gl.DebugService#label}.
 */
final class Lwjgl3ProgramHandle implements ProgramHandle, Lwjgl3Handle {

    private final Lwjgl3GLDevice device;
    private int glName;
    private String subjectLabel;
    private boolean deleted;

    Lwjgl3ProgramHandle(Lwjgl3GLDevice owner, int glName, String subjectLabel) {
        this.device = owner;
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
