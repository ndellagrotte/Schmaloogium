// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors
package com.schmaloogium.engine.buffers;

/**
 * The Phase 5 architecture facade (PHASE_5_DOC §5.1): pure planning is available before any
 * GL context work, render-thread construction is separate.
 */
public interface BufferArchitecture {

    /** Pure planning; no GL handles. */
    BufferPlanResult plan(BufferPlanRequest request);

    /** Render-thread estate construction. */
    BufferBuildResult create(BufferBuildRequest request);
}
