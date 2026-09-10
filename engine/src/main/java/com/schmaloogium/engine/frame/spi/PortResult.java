// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.spi;

import com.schmaloogium.engine.frame.FailureId;

/**
 * The port outcome (PHASE_7_DOC §5.1): Completed, mutation-free Rejected, or Failed with
 * shaders-off-recovery scheduling by the caller.
 */
public sealed interface PortResult {

    record Completed() implements PortResult {
    }

    record Rejected(PortRejection reason) implements PortResult {
    }

    record Failed(FailureId failure) implements PortResult {
    }
}
