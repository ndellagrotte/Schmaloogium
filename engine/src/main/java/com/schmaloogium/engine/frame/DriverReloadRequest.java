// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

import java.util.Objects;

/**
 * One validated reload request crossing into the render thread (PHASE_7_DOC §5.1).
 * Immutable; empty reason sets and negative sequences are rejected before mutation.
 */
public record DriverReloadRequest(
        ReloadIntent intent,
        ReloadReasons reasons,
        long intentSequence) {

    public DriverReloadRequest {
        Objects.requireNonNull(intent, "intent");
        Objects.requireNonNull(reasons, "reasons");
        if (reasons.isEmpty()) {
            throw new IllegalArgumentException("reasons must be non-empty");
        }
        if (intentSequence < 0) {
            throw new IllegalArgumentException("intentSequence must be non-negative: " + intentSequence);
        }
    }
}
