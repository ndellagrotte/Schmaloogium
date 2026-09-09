// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.nio.charset.StandardCharsets;

/** Canonical Properties escaping for persistence writes. */
public final class PropertiesEscaping {

    private PropertiesEscaping() {
    }

    public static String escapeKey(String key) {
        return escape(key, true);
    }

    public static String escapeValue(String value) {
        return escape(value, false);
    }

    private static String escape(String s, boolean isKey) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\t' -> sb.append("\\t");
                case '\r' -> sb.append("\\r");
                case '\f' -> sb.append("\\f");
                case '=' , ':' , '#', '!' -> {
                    if (isKey) {
                        sb.append('\\').append(c);
                    } else {
                        sb.append(c);
                    }
                }
                default -> {
                    if (c < 0x20 || c > 0x7e) {
                        byte[] bytes = String.valueOf(c).getBytes(StandardCharsets.UTF_8);
                        for (byte b : bytes) {
                            sb.append(String.format("\\u%04x", b & 0xFF));
                        }
                    } else {
                        if (isKey && i == 0 && c == ' ') {
                            sb.append('\\');
                        }
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }
}
