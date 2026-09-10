// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import java.util.List;

/**
 * Result of {@code generateMainMipmaps} (PHASE_5_DOC §2.2).
 */
public sealed interface MainMipmapResult {

    /**
     * Exactly one outcome per requested buffer, in request order.
     */
    record Completed(List<MainMipmapOutcome> outcomes) implements MainMipmapResult {
        public Completed {
            outcomes = List.copyOf(outcomes);
        }
    }

    record Rejected(FrameProtocolRejection reason) implements MainMipmapResult {
    }

    record Failed(BufferFailure failure, String diagnosticId, boolean frameAborted)
            implements MainMipmapResult {
    }
}
