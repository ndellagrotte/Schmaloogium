// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * KHR_debug object labels and groups (PHASE_1_DOC §4.7.4). Present at v0.1 as a no-op so
 * Phase 4/5's object-creation sites label from day one ("architect now, implement later");
 * the implementation is v0.5 / Phase 14. Since D-P1-71 the owned-texture order is
 * label-then-materialize: pass the label to {@link TextureService#create} at logical
 * creation, or call {@link #label} before the first admitted materialization, where it is
 * recorded-only (D-P1-73) and applied by that materialization.
 */
public interface DebugService {

    void pushGroup(String label);

    void popGroup();

    /** Labels one GL object of any of the four handle categories. */
    void label(GLHandle handle, String label);

    /**
     * The v0.5 activity gate (§4.7.8, D-P1-54): true iff the installed backend has actual
     * GL 4.3 or GL_KHR_debug capability/entry points AND
     * {@code schmaloogium.debug.glLabels} is enabled. A debug context is NOT a
     * prerequisite; before backend installation or on unsupported profiles it is false,
     * and the gate states nothing about object lifetime.
     */
    boolean isActive();
}
