// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Clear policy for a color buffer (PHASE_5_DOC §2.2); remains present even when {@code clear=false}. Fog is
 * resolved ONLY from that frame's {@link ClearRequest} during execution.
 */
public sealed interface ResourceClearPolicy permits ResourceClearPolicy.FogRgbAlphaOne, ResourceClearPolicy.Constant {

    /** Clear to the frame's fog color with alpha 1. */
    record FogRgbAlphaOne() implements ResourceClearPolicy {
    }

    /** Clear to a fixed constant color. */
    record Constant(double r, double g, double b, double a) implements ResourceClearPolicy {

        public Constant {
            if (!Double.isFinite(r)) {
                throw new IllegalArgumentException("non-finite clear red component");
            }
            if (!Double.isFinite(g)) {
                throw new IllegalArgumentException("non-finite clear green component");
            }
            if (!Double.isFinite(b)) {
                throw new IllegalArgumentException("non-finite clear blue component");
            }
            if (!Double.isFinite(a)) {
                throw new IllegalArgumentException("non-finite clear alpha component");
            }
        }
    }
}
