// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.spi;

import com.schmaloogium.engine.frame.FailureId;

/**
 * The fullscreen execution outcome (PHASE_7_DOC §5.1). {@code Completed} alone advances
 * flips; Rejected is mutation-free; Failed has already contained the frame.
 */
public sealed interface FullscreenExecutionResult {

    record Completed() implements FullscreenExecutionResult {
    }

    record Rejected(PortRejection reason) implements FullscreenExecutionResult {
    }

    record Failed(FailureId failure) implements FullscreenExecutionResult {
    }
}
