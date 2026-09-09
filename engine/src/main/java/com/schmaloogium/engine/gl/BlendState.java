// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import java.util.Objects;

/**
 * An enabled blend configuration: separate RGB/alpha source and destination factors
 * (PHASE_1_DOC §4.7.4). Absence (null) is the disabled state, per
 * {@link StateService#blend}; a present value is always an enabled blend.
 * {@link #effectiveBlend()} returns the complete effective value, including disabled
 * factors. No GL constants appear in any signature.
 */
public record BlendState(BlendFactor srcRgb, BlendFactor dstRgb, BlendFactor srcAlpha, BlendFactor dstAlpha) {

    public BlendState {
        Objects.requireNonNull(srcRgb, "srcRgb");
        Objects.requireNonNull(dstRgb, "dstRgb");
        Objects.requireNonNull(srcAlpha, "srcAlpha");
        Objects.requireNonNull(dstAlpha, "dstAlpha");
    }

    /** Closed engine blend-factor vocabulary; the backend maps these to native values. */
    public enum BlendFactor {
        ZERO,
        ONE,
        SRC_COLOR,
        ONE_MINUS_SRC_COLOR,
        DST_COLOR,
        ONE_MINUS_DST_COLOR,
        SRC_ALPHA,
        ONE_MINUS_SRC_ALPHA,
        DST_ALPHA,
        ONE_MINUS_DST_ALPHA,
        CONSTANT_COLOR,
        ONE_MINUS_CONSTANT_COLOR,
        CONSTANT_ALPHA,
        ONE_MINUS_CONSTANT_ALPHA,
        SRC_ALPHA_SATURATE
    }
}
