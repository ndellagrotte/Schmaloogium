// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

public record DeclaredUniform(
        String exactName,
        DeclaredGlslType type,
        ShaderSourceStage declaringStage,
        AttributedSourceLocation location) {

    public DeclaredUniform {
        java.util.Objects.requireNonNull(exactName, "exactName");
        java.util.Objects.requireNonNull(type, "type");
        java.util.Objects.requireNonNull(declaringStage, "declaringStage");
        java.util.Objects.requireNonNull(location, "location");
    }
}
