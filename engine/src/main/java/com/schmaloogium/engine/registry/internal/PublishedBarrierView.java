// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.registry.BarrierContext;
import com.schmaloogium.engine.registry.BarrierResult;
import com.schmaloogium.engine.registry.ProgramSelectionResult;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.PublishedProgramStateBarrier;
import com.schmaloogium.engine.registry.UseProgramRequest;

import java.util.Objects;

/**
 * The non-owning published barrier view (PHASE_4_DOC §2.2). Every method first verifies that
 * both its generation and identity still match the current ready publication; a replaced
 * barrier returns StalePublication without a GL call or barrier-state change.
 */
final class PublishedBarrierView implements PublishedProgramStateBarrier {

    private final long generation;
    private final BarrierCore core;

    PublishedBarrierView(long generation, BarrierCore core) {
        this.generation = generation;
        this.core = Objects.requireNonNull(core, "core");
    }

    void retireCore() {
        core.releaseForReplacement();
    }

    private boolean stale() {
        return core.isRetired() || core.generation() != generation;
    }

    @Override
    public long generation() {
        return generation;
    }

    @Override
    public ProgramSelectionResult select(ProgramSlotId requested, BarrierContext context) {
        if (stale()) {
            return new ProgramSelectionResult.StalePublication(generation, core.liveGeneration());
        }
        return core.select(requested, context);
    }

    @Override
    public BarrierResult activate(UseProgramRequest request) {
        if (stale()) {
            return new BarrierResult.StalePublication(generation, core.liveGeneration());
        }
        return core.activate(request);
    }

    @Override
    public BarrierResult releaseToFixedFunction(BarrierContext context) {
        if (stale()) {
            return new BarrierResult.StalePublication(generation, core.liveGeneration());
        }
        return core.releaseToFixedFunction(context);
    }
}
