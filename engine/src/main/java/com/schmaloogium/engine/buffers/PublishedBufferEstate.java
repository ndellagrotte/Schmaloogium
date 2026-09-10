// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import java.util.Objects;
import java.util.Optional;

/**
 * Published estate generation: the optional non-owning view plus the owner-defined resource
 * snapshot (PHASE_5_DOC §2.2). Ready publication retains the candidate's exact {@code Available}
 * value; off publication carries only {@code Unavailable(SHADERS_OFF)}.
 */
public record PublishedBufferEstate(
        long generation,
        Optional<BufferEstateView> estate,
        BufferResourceSnapshot resources) {

    public PublishedBufferEstate {
        estate = Objects.requireNonNullElse(estate, Optional.empty());
        resources = Objects.requireNonNull(resources, "resources");
    }
}
