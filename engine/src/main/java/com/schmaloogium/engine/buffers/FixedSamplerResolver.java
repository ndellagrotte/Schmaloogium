// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.registry.ProgramSamplerLayout;

/** Resolves the complete fixed-sampler plan for a program layout at a stage and band. */
public interface FixedSamplerResolver {
    FixedSamplerPlanResult resolve(ProgramSamplerLayout layout, StageId stage, StageBand band);
}
