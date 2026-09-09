// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.SourceAttribution;

/** A retained unrecognized {@code shaders.properties} key (debug-logged, never executed). */
public record UnknownProperty(String key, String value, SourceAttribution attribution) {

    public UnknownProperty {
        java.util.Objects.requireNonNull(key, "key");
        java.util.Objects.requireNonNull(value, "value");
        java.util.Objects.requireNonNull(attribution, "attribution");
    }
}
