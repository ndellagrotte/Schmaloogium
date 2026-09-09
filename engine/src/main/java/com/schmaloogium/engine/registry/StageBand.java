// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * Where the executor can place a pass inside the frame (PHASE_4_DOC §2.2). The band
 * answers "when", the {@link StageId} answers "which pack stage owns this" — the pair is
 * load-bearing because one {@code gbuffers} program family runs on both sides of deferred.
 */
public enum StageBand {
    LOAD_OR_RESIZE,
    FRAME_BEGIN,
    SHADOW,
    AFTER_SHADOW,
    BEFORE_GBUFFERS,
    GBUFFERS_OPAQUE,
    BETWEEN_GBUFFERS,
    GBUFFERS_TRANSLUCENT,
    FRAME_END,
    SCREEN
}
