// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;

/** Result of resolving one fixed sampler name against a layout, stage and band. */
public sealed interface FixedSamplerResolution {
    record Resolved(FixedSamplerName name, int unit) implements FixedSamplerResolution {
    }

    record UnsupportedDomain(FixedSamplerName name, StageId stage, StageBand band)
        implements FixedSamplerResolution {
    }
}
