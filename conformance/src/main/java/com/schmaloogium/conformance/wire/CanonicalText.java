// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.wire;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * The one scalar grammar every Phase 2 wire document shares (PHASE_2_DOC §4.1, §4.5.2):
 * ASCII dotted keys, JSON string escaping, {@code true|false} booleans, base-10 integers
 * and §4.1's fixed decimal form for finite doubles. [D-P2-4] makes every text artifact
 * UTF-8, LF-only, {@code Locale.ROOT}, sorted and free of platform-dependent formatting,
 * so every encoder here is a total function of its input and nothing consults the
 * default locale.
 *
 * <p>This class owns grammar only. Which keys exist, which are JSON strings and what a
 * document's first line says is the owning document format's business.
 */
public final class CanonicalText {

    /** Unsigned UTF-8 byte order (PHASE_2_DOC §4.1); identical to code-point order for
     *  the ASCII keys this grammar admits, which {@link #validateKey} enforces. */
    public static final Comparator<String> ORDER = Comparator.naturalOrder();

    private CanonicalText() {
    }

    // ------------------------------------------------------------------
    // Keys
    // ------------------------------------------------------------------

    /** Dotted ASCII identifier segments admitting {@code _} and digits (§4.5.2). */
    public static void validateKey(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("empty key");
        }
        boolean segmentStart = true;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (c == '.') {
                if (segmentStart) {
                    throw new IllegalArgumentException("empty key segment in: " + key);
                }
                segmentStart = true;
                continue;
            }
            boolean ok = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
                || (c >= '0' && c <= '9') || c == '_';
            if (!ok) {
                throw new IllegalArgumentException("illegal character in key: " + key);
            }
            segmentStart = false;
        }
        if (segmentStart) {
            throw new IllegalArgumentException("key ends in a dot: " + key);
        }
    }

    // ------------------------------------------------------------------
    // JSON strings (§4.5.2, §4.5.4)
    // ------------------------------------------------------------------

    /** Encodes one complete JSON string literal, quoting included. Escapes the two
     *  mandatory characters, the five named control escapes, and every other control
     *  character as a backslash-u four-hex-digit escape; passes all other code points
     *  through as UTF-8 text. */
    public static String encodeJson(String value) {
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

    /** Decodes one complete JSON string literal. Rejects anything but one string: no
     *  surrounding whitespace, no trailing tokens, invalid escapes, raw control
     *  characters or unpaired surrogates (§4.5.4). */
    public static String decodeJson(String literal) {
        if (literal == null || literal.length() < 2 || literal.charAt(0) != '"'
                || literal.charAt(literal.length() - 1) != '"') {
            throw new IllegalArgumentException("not one JSON string literal");
        }
        StringBuilder out = new StringBuilder(literal.length());
        int i = 1;
        int end = literal.length() - 1;
        while (i < end) {
            char c = literal.charAt(i);
            if (c < 0x20) {
                throw new IllegalArgumentException("raw control character in JSON string");
            }
            if (c != '"') {
                if (c == '\\') {
                    i = readEscape(literal, i, out);
                } else {
                    if (Character.isHighSurrogate(c)) {
                        if (i + 1 >= end || !Character.isLowSurrogate(literal.charAt(i + 1))) {
                            throw new IllegalArgumentException("unpaired surrogate in JSON string");
                        }
                        out.append(c).append(literal.charAt(i + 1));
                        i += 2;
                        continue;
                    }
                    if (Character.isLowSurrogate(c)) {
                        throw new IllegalArgumentException("unpaired surrogate in JSON string");
                    }
                    out.append(c);
                    i++;
                }
                continue;
            }
            throw new IllegalArgumentException("unescaped quote inside JSON string");
        }
        return out.toString();
    }

    private static int readEscape(String literal, int backslashIndex, StringBuilder out) {
        int i = backslashIndex + 1;
        if (i >= literal.length() - 1) {
            throw new IllegalArgumentException("truncated escape in JSON string");
        }
        char e = literal.charAt(i);
        switch (e) {
            case '"' -> out.append('"');
            case '\\' -> out.append('\\');
            case 'n' -> out.append('\n');
            case 'r' -> out.append('\r');
            case 't' -> out.append('\t');
            case 'u' -> {
                if (i + 4 >= literal.length()) {
                    throw new IllegalArgumentException("truncated \\u escape in JSON string");
                }
                try {
                    int cp = Integer.parseInt(literal.substring(i + 1, i + 5), 16);
                    out.append((char) cp);
                } catch (NumberFormatException bad) {
                    throw new IllegalArgumentException("malformed \\u escape in JSON string");
                }
                return i + 5;
            }
            default -> throw new IllegalArgumentException("invalid escape \\" + e + " in JSON string");
        }
        return i + 1;
    }

    /** Round-trips {@link #decodeJson} through {@link #encodeJson} and requires byte
     *  equality, so a stored document is rejected unless it is already canonical. */
    public static void requireCanonicalJson(String literal) {
        if (!encodeJson(decodeJson(literal)).equals(literal)) {
            throw new IllegalArgumentException("noncanonical JSON string literal: " + literal);
        }
    }

    // ------------------------------------------------------------------
    // Scalars
    // ------------------------------------------------------------------

    public static String formatBoolean(boolean value) {
        return value ? "true" : "false";
    }

    public static boolean parseBoolean(String raw) {
        if ("true".equals(raw)) {
            return true;
        }
        if ("false".equals(raw)) {
            return false;
        }
        throw new IllegalArgumentException("not a boolean: " + raw);
    }

    public static String formatInt(long value) {
        return Long.toString(value);
    }

    public static long parseInt(String raw) {
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("not a base-10 integer: " + raw);
        }
    }

    /** §4.1 fixed decimal form: the shortest decimal that round-trips to the same
     *  double, always with at least one fraction digit, always plain (values whose
     *  shortest form is scientific expand to their exact plain decimal instead).
     *  Finite inputs only. */
    public static String formatDouble(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("non-finite double");
        }
        String plain = Double.toString(value);
        if (plain.indexOf('E') >= 0 || plain.indexOf('e') >= 0) {
            plain = new BigDecimal(value).toPlainString();
        }
        return plain.indexOf('.') < 0 ? plain + ".0" : plain;
    }

    public static double parseDouble(String raw) {
        try {
            double d = Double.parseDouble(raw);
            if (!Double.isFinite(d)) {
                throw new IllegalArgumentException("non-finite double: " + raw);
            }
            // Canonical re-render must reproduce the stored bytes exactly.
            if (!formatDouble(d).equals(raw)) {
                throw new IllegalArgumentException("noncanonical double: " + raw);
            }
            return d;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("not a fixed-decimal double: " + raw);
        }
    }

    /** Exact decimal form of the float carrying {@code bits} (IEEE 754 as stored by
     *  {@code DecisionValue.FloatBits}); determinism never depends on {@code toString}. */
    public static String formatFloatBits(int bits) {
        float f = Float.intBitsToFloat(bits);
        if (!Float.isFinite(f)) {
            throw new IllegalArgumentException("non-finite float bits: " + bits);
        }
        return new BigDecimal(f).stripTrailingZeros().toPlainString();
    }
}
