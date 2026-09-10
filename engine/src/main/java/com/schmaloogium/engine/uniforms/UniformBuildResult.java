// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

/**
 * The closed factory result (PHASE_6_DOC §2.2). {@link Success} contains the only
 * operational runtime and transfers its lifecycle to the caller; the factory retains
 * nothing. {@link Failure} carries one non-empty stable diagnostic ID, no runtime, and
 * has performed no GL work; every supplied service remains caller-owned.
 */
public sealed interface UniformBuildResult {

    record Success(UniformRuntime runtime) implements UniformBuildResult {
        public Success {
            java.util.Objects.requireNonNull(runtime, "runtime");
        }
    }

    record Failure(String diagnosticId) implements UniformBuildResult {
        public Failure {
            java.util.Objects.requireNonNull(diagnosticId, "diagnosticId");
            if (diagnosticId.isEmpty()) {
                throw new IllegalArgumentException("diagnosticId must be non-empty");
            }
        }
    }
}
