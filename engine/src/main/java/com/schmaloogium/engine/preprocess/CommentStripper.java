// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

/** Line-scoped GLSL comment removal for text scanners (never used for output text). */
public final class CommentStripper {

    private CommentStripper() {
    }

    /** Removes C-style blocks and line comments from one logical line. */
    public static String stripComments(String line) {
        StringBuilder out = new StringBuilder(line.length());
        boolean inString = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"' && (i == 0 || line.charAt(i - 1) != '\\')) {
                inString = !inString;
            }
            if (!inString && c == '/' && i + 1 < line.length() && line.charAt(i + 1) == '/') {
                break;
            }
            if (!inString && c == '/' && i + 1 < line.length() && line.charAt(i + 1) == '*') {
                int end = line.indexOf("*/", i + 2);
                if (end < 0) {
                    break;
                }
                i = end + 1;
                continue;
            }
            out.append(c);
        }
        return out.toString();
    }
}
