// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.registry.StageId;

/** Immutable per-cell lookup over expanded texture binding candidates. */
public interface TextureCandidateTable {
    TextureCandidateEntry entry(StageId expandedStage, FixedSamplerName name);
}
