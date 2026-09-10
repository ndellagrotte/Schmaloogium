// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import java.util.Objects;

/** Outcome of estate building (PHASE_5_DOC §2.2). */
public sealed interface BufferBuildResult
        permits BufferBuildResult.Ready, BufferBuildResult.AwaitingMainDepth, BufferBuildResult.ShadersOff {

    /** Estate candidate is ready for publication. */
    record Ready(BufferEstateCandidate candidate) implements BufferBuildResult {

        public Ready {
            Objects.requireNonNull(candidate, "candidate");
        }
    }

    /** Build waits for the main depth source to reach the expected version. */
    record AwaitingMainDepth(long expectedVersion) implements BufferBuildResult {
    }

    /** Shaders are off; build refused with the given failure. */
    record ShadersOff(BufferFailure failure) implements BufferBuildResult {

        public ShadersOff {
            Objects.requireNonNull(failure, "failure");
        }
    }
}
