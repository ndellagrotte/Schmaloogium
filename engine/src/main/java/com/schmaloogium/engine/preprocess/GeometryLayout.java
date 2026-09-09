// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

public record GeometryLayout(
        GeometryInputPrimitive input, GeometryOutputPrimitive output, int maxVertices) {

    public GeometryLayout {
        java.util.Objects.requireNonNull(input, "input");
        java.util.Objects.requireNonNull(output, "output");
    }
}
