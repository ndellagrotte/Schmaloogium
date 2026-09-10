// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.gl;

import com.schmaloogium.engine.gl.GLHandle;

/**
 * Resolves an engine handle to the native object name a KHR_debug label may target
 * (PHASE_14_DOC §4.5, D-P14-40). Implemented by the installing mod.glue adapter, which
 * owns the concrete handle classes and their authentication; the backend only learns
 * "existing native object" or "none".
 *
 * <p>Return a negative value when the handle is unmaterialized (label stays retained
 * on the handle per P1's D-P1-71/74 discipline — the backend issues no GL), not
 * labelable, foreign, or deleted. Never guess a name.
 */
interface LabelTargetResolver {

    /** The live native name of {@code handle}, or a negative value when absent. */
    int nativeName(GLHandle handle);
}
