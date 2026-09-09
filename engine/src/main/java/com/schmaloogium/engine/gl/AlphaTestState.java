// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import java.util.Objects;

/**
 * An enabled alpha-test configuration (PHASE_1_DOC §4.7.4): function and reference
 * value. Absence (null) is the disabled state, per {@link StateService#alphaTest}; a
 * present value is always an enabled alpha test. No GL constants appear in any signature.
 */
public record AlphaTestState(AlphaTestFunction function, float reference) {

    public AlphaTestState {
        Objects.requireNonNull(function, "function");
    }

    /** Closed engine alpha-test function vocabulary; the backend maps these to native
     *  values. */
    public enum AlphaTestFunction {
        NEVER,
        LESS,
        EQUAL,
        LEQUAL,
        GREATER,
        NOTEQUAL,
        GEQUAL,
        ALWAYS
    }
}
