// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.spi;

import com.schmaloogium.engine.uniforms.Float3;

import java.util.Objects;

/**
 * The typed uniform signal set (PHASE_7_DOC §5.1). Each variant carries exactly the
 * non-identity fields of its Phase-6 binding sample; the bridge copies worldEpoch/frameId
 * from the accepted signal and forwards the payload verbatim — no resampling.
 */
public sealed interface UniformSignal {

    /** The celestial rotation moment (H-SKY-02): four direction vectors. */
    record Celestial(Float3 sunPosition, Float3 moonPosition, Float3 shadowLightPosition,
                     Float3 upPosition) implements UniformSignal {

        public Celestial {
            Objects.requireNonNull(sunPosition, "sunPosition");
            Objects.requireNonNull(moonPosition, "moonPosition");
            Objects.requireNonNull(shadowLightPosition, "shadowLightPosition");
            Objects.requireNonNull(upPosition, "upPosition");
        }
    }

    /** One fog observation: mode, density, and the exact RGB color. */
    record Fog(int fogMode, float density, Float3 color) implements UniformSignal {

        public Fog {
            Objects.requireNonNull(color, "color");
        }
    }

    /** One effective blend observation (D-P6-32 route). */
    record Blend(BlendStateValue value) implements UniformSignal {

        public Blend {
            Objects.requireNonNull(value, "value");
        }
    }
}
