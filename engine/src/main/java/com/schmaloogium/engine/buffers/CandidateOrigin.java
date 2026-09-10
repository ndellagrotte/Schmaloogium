// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.config.TextureBindingKey;

/** Closed provenance domain for texture binding candidates (Phase 5 Doc §2.4). */
public sealed interface CandidateOrigin {
    record Custom(TextureBindingKey key, int phase3Ordinal) implements CandidateOrigin {
    }

    record Companion(AtlasId atlas, CompanionKind kind) implements CandidateOrigin {
    }

    record DefaultFill(CompanionKind kind) implements CandidateOrigin {
    }

    record Noise() implements CandidateOrigin {
    }
}
