// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scans a vertex-stage source for the classic extended-attribute declarations
 * ({@code attribute vec3 mc_Entity}, {@code attribute vec2 mc_midTexCoord},
 * {@code attribute vec4 at_tangent}; {@code in} accepted for later GLSL versions) and
 * projects them onto {@link VertexAttribute} (PHASE_10_DOC §4.6 "shared declaration plan":
 * the program's own declarations decide which generic arrays a draw enables). The scan is
 * textual, CR-tolerant and ignores line comments; a declaration inside an inactive
 * preprocessor branch is still a declaration for planning purposes (the array is enabled
 * only when the field physically exists, so over-declaration is harmless).
 */
public final class VertexAttributeScanner {

    private static final Pattern DECLARATION = Pattern.compile(
        "(?m)^[ \\t]*(?:attribute|in)[ \\t]+(?:(?:highp|mediump|lowp)[ \\t]+)?"
            + "vec[234][ \\t]+(mc_Entity|mc_midTexCoord|at_tangent)[ \\t]*;");

    private static final Pattern LINE_COMMENT = Pattern.compile("//[^\\r\\n]*");

    private VertexAttributeScanner() {
    }

    /** The attributes the text declares, in enum order; empty when it declares none. */
    public static Set<VertexAttribute> scan(String text) {
        Set<VertexAttribute> found = EnumSet.noneOf(VertexAttribute.class);
        Matcher m = DECLARATION.matcher(LINE_COMMENT.matcher(text).replaceAll(""));
        while (m.find()) {
            switch (m.group(1)) {
                case "mc_Entity" -> found.add(VertexAttribute.MC_ENTITY);
                case "mc_midTexCoord" -> found.add(VertexAttribute.MC_MID_TEX_COORD);
                case "at_tangent" -> found.add(VertexAttribute.AT_TANGENT);
                default -> { }
            }
        }
        return found;
    }
}
