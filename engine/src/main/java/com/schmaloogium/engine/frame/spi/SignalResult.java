// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.spi;

import com.schmaloogium.engine.frame.FailureId;
import com.schmaloogium.engine.frame.HookRejection;

/**
 * The shared signal outcome for the uniform-signal and atlas adapters (PHASE_7_DOC §5.1):
 * accepted, mutation-free rejection, or internal failure.
 */
public sealed interface SignalResult {

    record Accepted() implements SignalResult {
    }

    record Rejected(HookRejection reason) implements SignalResult {
    }

    record Failed(FailureId failure) implements SignalResult {
    }
}
