// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

/**
 * Closed present/absent float value — never a nullable field (PHASE_6_DOC §4.2). Used for
 * the frame provider's optional {@code shadowAngle}.
 */
public sealed interface OptionalFloat {

    float value();

    boolean isPresent();

    record Present(float value) implements OptionalFloat {
        public Present {
            if (!Float.isFinite(value)) {
                throw new IllegalArgumentException("value must be finite");
            }
        }

        @Override
        public boolean isPresent() {
            return true;
        }
    }

    record Absent() implements OptionalFloat {
        @Override
        public float value() {
            throw new IllegalStateException("absent");
        }

        @Override
        public boolean isPresent() {
            return false;
        }
    }
}
