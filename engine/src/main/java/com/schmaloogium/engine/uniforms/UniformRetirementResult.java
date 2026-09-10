// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

/**
 * Closed terminal-retirement outcome (PHASE_6_DOC §4.14.2). {@link Retired} and
 * {@link AlreadyRetired} authorize the caller to release the runtime's borrowed services;
 * {@link Rejected} does not — admission stays closed and retirement must be retried on the
 * render thread after the outer callback returns.
 */
public sealed interface UniformRetirementResult {

    record Retired() implements UniformRetirementResult {
    }

    record AlreadyRetired() implements UniformRetirementResult {
    }

    record Rejected(UniformRetirementRejection reason) implements UniformRetirementResult {
        public Rejected {
            java.util.Objects.requireNonNull(reason, "reason");
        }
    }
}
