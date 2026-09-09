// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scans directive-active GLSL text for const declarations with initializers:
 * the App F.3 exact-whitelist family plus the resource-requirement consts.
 */
public final class ConstScanner {

    private ConstScanner() {
    }

    private static final Pattern CONST = Pattern.compile(
        "^\\s*const\\s+(int|float|bool|vec2|vec3|vec4|ivec2)\\s+([A-Za-z_][A-Za-z0-9_]*)"
            + "\\s*=\\s*([^;]+);.*$");

    /** One const finding: declared type, raw initializer text, and 1-based line. */
    public record Finding(String type, String name, String value, int line) {
    }

    public static Map<String, Finding> scan(String text) {
        Map<String, Finding> out = new LinkedHashMap<>();
        String[] lines = text.split("\\n", -1);
        boolean blockComment = false;
        for (int i = 0; i < lines.length; i++) {
            String line = stripComments(lines[i]);
            boolean[] after = {blockComment};
            line = stripComments(lines[i]);
            Matcher m = CONST.matcher(line);
            if (m.matches()) {
                out.putIfAbsent(m.group(2),
                    new Finding(m.group(1), m.group(2), m.group(3).trim(), i + 1));
            }
        }
        return out;
    }

    /** Removes line comments and whole block comments from one line. */
    static String stripComments(String line) {
        StringBuilder sb = new StringBuilder(line.length());
        boolean inBlock = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (!inBlock && c == '/' && i + 1 < line.length() && line.charAt(i + 1) == '/') {
                break;
            }
            if (!inBlock && c == '/' && i + 1 < line.length() && line.charAt(i + 1) == '*') {
                inBlock = true;
                i++;
                continue;
            }
            if (inBlock && c == '*' && i + 1 < line.length() && line.charAt(i + 1) == '/') {
                inBlock = false;
                i++;
                continue;
            }
            if (!inBlock) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
