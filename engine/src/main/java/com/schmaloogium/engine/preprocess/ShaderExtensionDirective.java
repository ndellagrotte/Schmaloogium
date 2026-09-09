// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

public record ShaderExtensionDirective(
        String name, ExtensionBehavior behavior, AttributedSourceLocation location) {

    public ShaderExtensionDirective {
        java.util.Objects.requireNonNull(name, "name");
        java.util.Objects.requireNonNull(behavior, "behavior");
        java.util.Objects.requireNonNull(location, "location");
    }
}
