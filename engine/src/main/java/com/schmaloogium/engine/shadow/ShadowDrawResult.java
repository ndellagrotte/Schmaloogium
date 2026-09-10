// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

/**
 * Typed draw/leaf outcome from a {@link ShadowWorldPort} call.
 */
public sealed interface ShadowDrawResult {

    record Succeeded() implements ShadowDrawResult {
    }

    record Rejected(ShadowWorldPort.Failure reason) implements ShadowDrawResult {

        public Rejected {
            if (reason == null) {
                throw new IllegalArgumentException("reason");
            }
        }
    }

    record Failed(ShadowWorldPort.Failure reason, String diagnosticId)
            implements ShadowDrawResult {

        public Failed {
            if (reason == null) {
                throw new IllegalArgumentException("reason");
            }
            if (diagnosticId == null) {
                throw new IllegalArgumentException("diagnosticId");
            }
        }
    }
}
