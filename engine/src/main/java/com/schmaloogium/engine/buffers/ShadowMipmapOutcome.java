// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Per-buffer outcome of shadow mipmap generation.
 */
public sealed interface ShadowMipmapOutcome {

    record Generated(LogicalBuffer buffer) implements ShadowMipmapOutcome {

        public Generated {
            java.util.Objects.requireNonNull(buffer, "buffer");
        }
    }

    record NotAllocated(LogicalBuffer buffer) implements ShadowMipmapOutcome {

        public NotAllocated {
            java.util.Objects.requireNonNull(buffer, "buffer");
        }
    }

    record Degraded(LogicalBuffer buffer, BufferFailure failure, String diagnosticId)
            implements ShadowMipmapOutcome {

        public Degraded {
            java.util.Objects.requireNonNull(buffer, "buffer");
            java.util.Objects.requireNonNull(failure, "failure");
            java.util.Objects.requireNonNull(diagnosticId, "diagnosticId");
        }
    }
}
