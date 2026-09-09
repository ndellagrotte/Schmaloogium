// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

public sealed interface GeometrySourceRequest {
    record None() implements GeometrySourceRequest {}
    record PreserveNative(LegacyGeometryConfig expected) implements GeometrySourceRequest {

        public PreserveNative {
            java.util.Objects.requireNonNull(expected, "expected");
        }
    }
}
