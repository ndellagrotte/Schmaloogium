// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.conformance;

import java.math.BigDecimal;

/**
 * The client-side copy of Phase 2's canonical scalar rules (PHASE_2_DOC §4.1, §4.5.2): JSON
 * string encoding/decoding, {@code true|false}, base-10 integers and the fixed decimal form.
 * {@code :mod} cannot depend on {@code :conformance} (the boundary is a file, §2.2), so this
 * small mirror exists; the runner's strict reader is the arbiter — any drift rejects the run.
 */
final class CanonicalScalars {

    private CanonicalScalars() {
    }

    static String encodeJson(String value) {
        StringBuilder out = new StringBuilder(value.length() + 2);
        out.append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\b' -> out.append("\\b");
                case '\f' -> out.append("\\f");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default -> {
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
                }
            }
        }
        out.append('"');
        return out.toString();
    }

    static String decodeJson(String literal) {
        if (literal == null || literal.length() < 2 || literal.charAt(0) != '"'
                || literal.charAt(literal.length() - 1) != '"') {
            throw new IllegalArgumentException("not one JSON string literal: " + literal);
        }
        StringBuilder out = new StringBuilder(literal.length());
        int end = literal.length() - 1;
        int i = 1;
        while (i < end) {
            char c = literal.charAt(i);
            if (c == '\\') {
                if (i + 1 >= end) {
                    throw new IllegalArgumentException("truncated escape");
                }
                char e = literal.charAt(i + 1);
                switch (e) {
                    case '"' -> out.append('"');
                    case '\\' -> out.append('\\');
                    case 'n' -> out.append('\n');
                    case 'r' -> out.append('\r');
                    case 't' -> out.append('\t');
                    case 'b' -> out.append('\b');
                    case 'f' -> out.append('\f');
                    case 'u' -> {
                        if (i + 5 >= literal.length()) {
                            throw new IllegalArgumentException("truncated \\u escape");
                        }
                        out.append((char) Integer.parseInt(literal.substring(i + 2, i + 6), 16));
                        i += 6;
                        continue;
                    }
                    default -> throw new IllegalArgumentException("invalid escape \\" + e);
                }
                i += 2;
            } else if (c == '"' || c < 0x20) {
                throw new IllegalArgumentException("raw quote or control character in JSON string");
            } else {
                out.append(c);
                i++;
            }
        }
        return out.toString();
    }

    static String formatBoolean(boolean value) {
        return value ? "true" : "false";
    }

    static boolean parseBoolean(String raw) {
        if ("true".equals(raw)) {
            return true;
        }
        if ("false".equals(raw)) {
            return false;
        }
        throw new IllegalArgumentException("not a boolean: " + raw);
    }

    static String formatInt(long value) {
        return Long.toString(value);
    }

    static long parseInt(String raw) {
        return Long.parseLong(raw);
    }

    /** §4.1 fixed decimal form: shortest round-tripping decimal, ≥1 fraction digit, never scientific. */
    static String formatDouble(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("non-finite double");
        }
        String plain = Double.toString(value);
        if (plain.indexOf('E') >= 0 || plain.indexOf('e') >= 0) {
            plain = new BigDecimal(value).toPlainString();
        }
        return plain.indexOf('.') < 0 ? plain + ".0" : plain;
    }

    static double parseDouble(String raw) {
        double d = Double.parseDouble(raw);
        if (!Double.isFinite(d) || !formatDouble(d).equals(raw)) {
            throw new IllegalArgumentException("not a canonical fixed-decimal double: " + raw);
        }
        return d;
    }
}
