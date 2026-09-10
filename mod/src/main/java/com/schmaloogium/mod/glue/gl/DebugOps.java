// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.gl;

/**
 * Native KHR_debug verbs (PHASE_14_DOC §4.5). The seam that keeps
 * {@link KhrDebugBackend} headlessly testable: the LWJGL3 adapter implements it with
 * {@code KHRDebug}/{@code GL43} entry points, while the mod test source set (no LWJGL
 * on its classpath) supplies recording fakes.
 *
 * <p>Render-thread only; render-thread/context guards and error attribution stay with
 * the installing adapter ([D-P1-30]). Each mutating implementation polls its own GL
 * error after the call and returns {@code false} on failure, so the backend's balance
 * counters move only with the native group stack.
 */
interface DebugOps {

    /** {@code GL_MAX_LABEL_LENGTH}, probed once; {@code <= 0} when unavailable. */
    int maxLabelLength();

    /** {@code GL_MAX_DEBUG_GROUP_STACK_DEPTH}, probed once; {@code <= 0} when
     *  unavailable (capacity 0: every push goes virtual). */
    int maxGroupStackDepth();

    /** {@code glPushDebugGroup(GL_DEBUG_SOURCE_APPLICATION, 0, label)}. */
    boolean pushGroup(String label);

    /** {@code glPopDebugGroup()}. */
    boolean popGroup();

    /** {@code glObjectLabel(identifier, name, label)} — {@code label} is already
     *  clamped to the encoded byte budget by the backend (D-P14-36). */
    boolean label(int identifier, int name, String label);
}
