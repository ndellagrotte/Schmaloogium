// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import java.util.Optional;
import java.util.OptionalInt;

public record GeometryLayoutDeclaration(
        Optional<GeometryInputPrimitive> input, Optional<GeometryOutputPrimitive> output,
        OptionalInt maxVertices, AttributedSourceLocation location) {

    public GeometryLayoutDeclaration {
        input = input == null ? Optional.empty() : input;
        output = output == null ? Optional.empty() : output;
        java.util.Objects.requireNonNull(location, "location");
    }
}
