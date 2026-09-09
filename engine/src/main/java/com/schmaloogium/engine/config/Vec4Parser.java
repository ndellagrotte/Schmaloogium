// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Parses {@code vec4(r,g,b,a)} clear-color initializer text. */
public final class Vec4Parser {

    private Vec4Parser() {
    }

    public static Vec4f parse(String text) {
        String t = text.trim();
        if (!t.startsWith("vec4(") || !t.endsWith(")")) {
            return null;
        }
        String[] parts = t.substring("vec4(".length(), t.length() - 1).split(",");
        if (parts.length != 4) {
            return null;
        }
        try {
            return new Vec4f(Float.parseFloat(parts[0].trim()), Float.parseFloat(parts[1].trim()),
                Float.parseFloat(parts[2].trim()), Float.parseFloat(parts[3].trim()));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
