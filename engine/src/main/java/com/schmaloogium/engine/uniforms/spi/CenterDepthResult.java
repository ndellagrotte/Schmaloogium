// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.spi;

import java.util.Objects;

/** Closed center-depth read result (PHASE_6_DOC §4.2). */
public sealed interface CenterDepthResult {

    /** One depth sample; the value is finite in [0,1] (validated by the runtime). */
    record Sample(float depth) implements CenterDepthResult {
    }

    /** No valid sample this frame; the diagnostic key is non-empty and stable. */
    record Unavailable(String diagnosticId) implements CenterDepthResult {
        public Unavailable {
            Objects.requireNonNull(diagnosticId, "diagnosticId");
            if (diagnosticId.isEmpty()) {
                throw new IllegalArgumentException("diagnosticId must be non-empty");
            }
        }
    }
}
