// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** {@code alphaTest} state; {@code Off} differs from absence. */
public sealed interface AlphaTestSpec {
    record Off() implements AlphaTestSpec {}
    record Enabled(AlphaFunction function, float reference) implements AlphaTestSpec {

        public Enabled {
            java.util.Objects.requireNonNull(function, "function");
            if (!Float.isFinite(reference)) {
                throw new IllegalArgumentException("alpha test reference must be finite");
            }
        }
    }
}
