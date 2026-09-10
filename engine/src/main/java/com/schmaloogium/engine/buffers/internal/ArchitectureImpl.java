// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors
package com.schmaloogium.engine.buffers.internal;

import com.schmaloogium.engine.buffers.BufferArchitecture;
import com.schmaloogium.engine.buffers.BufferBuildRequest;
import com.schmaloogium.engine.buffers.BufferBuildResult;
import com.schmaloogium.engine.buffers.BufferPlanRequest;
import com.schmaloogium.engine.buffers.BufferPlanResult;

/**
 * The stateless architecture facade (PHASE_5_DOC §5.1/D-P5-25): pure planning reruns
 * identically from its request, render-thread construction is a separate requirement.
 */
public final class ArchitectureImpl implements BufferArchitecture {

    /** Stateless singleton handed out by {@code BufferArchitectures.create()}. */
    public static final ArchitectureImpl INSTANCE = new ArchitectureImpl();

    private ArchitectureImpl() {
    }

    @Override
    public BufferPlanResult plan(BufferPlanRequest request) {
        return BufferPlanner.plan(request);
    }

    @Override
    public BufferBuildResult create(BufferBuildRequest request) {
        return CandidateBuilder.build(request);
    }
}
