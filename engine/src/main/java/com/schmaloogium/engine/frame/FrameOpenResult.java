// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

import java.util.Optional;

/**
 * The frame-open outcome (PHASE_7_DOC §5.1). {@code Opened} is the only result creating a
 * live token; every other variant leaves the driver IDLE and forbids later calls with a
 * fabricated token. All rejections and failures are mutation-free.
 */
public sealed interface FrameOpenResult {

    /** A live frame token; the only authorization for the frame's step calls. */
    record Opened(FrameToken token) implements FrameOpenResult {
    }

    /** The frame renders vanilla only; no engine state changed. */
    record VanillaOnly(FrameOpenRejection reason) implements FrameOpenResult {
    }

    /** An internal failure; the driver schedules shaders-off recovery. */
    record Failed(FailureId failure) implements FrameOpenResult {
    }

    /** Convenience view of an opened token. */
    static Optional<FrameToken> token(FrameOpenResult result) {
        return result instanceof Opened opened ? Optional.of(opened.token()) : Optional.empty();
    }
}
