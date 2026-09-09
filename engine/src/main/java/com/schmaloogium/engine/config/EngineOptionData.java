// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.EngineOptionData;

import java.util.regex.Pattern;

import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.regex.Pattern;
import java.util.TreeMap;

/** The eight-known-setting/unknown-safe global engine option domain (§5.1). */
public record EngineOptionData(Map<String, String> values) {

    private static final Comparator<String> UNSIGNED_UTF8 =
        EngineOptionData::compareUnsignedUtf8;

    public EngineOptionData {
        EngineOptionData.validate(values);
        TreeMap<String, String> ordered = new TreeMap<>(UNSIGNED_UTF8);
        ordered.putAll(values);
        values = java.util.Collections.unmodifiableSortedMap(ordered);
    }

    /** The canonical empty value. */
    public static EngineOptionData empty() {
        return new EngineOptionData(Map.of());
    }

    private static void validate(Map<String, String> values) {
        java.util.Objects.requireNonNull(values, "values");
        for (Map.Entry<String, String> e : values.entrySet()) {
            java.util.Objects.requireNonNull(e.getKey(), "key");
            java.util.Objects.requireNonNull(e.getValue(), "value");
            if (isKnownKey(e.getKey())) {
                if (!isKnownValueValid(e.getKey(), e.getValue())) {
                    throw new IllegalArgumentException(
                        "invalid value for known engine setting: " + e.getKey());
                }
            } else {
                if (!UNKNOWN_KEY.matcher(e.getKey()).matches()) {
                    throw new IllegalArgumentException("invalid engine setting key: " + e.getKey());
                }
                if (!isSafeText(e.getValue())) {
                    throw new IllegalArgumentException("unsafe engine setting value for: " + e.getKey());
                }
            }
        }
    }

    /** The eight known keys in §5.1 order. */
    public static List<String> knownKeys() {
        return List.of(KNOWN_KEYS);
    }

    private static final String[] KNOWN_KEYS = {
        "normalMapEnabled", "specularMapEnabled", "renderResMul", "shadowResMul",
        "handDepthMul", "oldHandLight", "oldLighting", "antialiasingLevel"
    };

    private static final Pattern UNKNOWN_KEY = Pattern.compile("[A-Za-z_][A-Za-z0-9_.-]*");
    private static final Pattern MULTIPLIER = Pattern.compile(
        "[+-]?(?:[0-9]+(?:\\.[0-9]*)?|\\.[0-9]+)(?:[eE][+-]?[0-9]+)?");

    public static boolean isKnownKey(String key) {
        for (String k : KNOWN_KEYS) {
            if (k.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isKnownValueValid(String key, String value) {
        return switch (key) {
            case "normalMapEnabled", "specularMapEnabled" -> value.equals("true") || value.equals("false");
            case "oldHandLight", "oldLighting" -> value.equals("default") || value.equals("true")
                || value.equals("false");
            case "renderResMul", "shadowResMul", "handDepthMul" -> {
                if (!MULTIPLIER.matcher(value).matches()) {
                    yield false;
                }
                try {
                    float f = Float.parseFloat(value);
                    yield Float.isFinite(f) && f > 0.0f;
                } catch (NumberFormatException e) {
                    yield false;
                }
            }
            case "antialiasingLevel" -> value.equals("0");
            default -> false;
        };
    }

    static boolean isSafeText(String value) {
        for (int i = 0; i < value.length(); ) {
            int cp = value.codePointAt(i);
            if (cp <= 0x001F || (cp >= 0x007F && cp <= 0x009F) || cp == 0x2028 || cp == 0x2029) {
                return false;
            }
            i += Character.charCount(cp);
        }
        return Character.isValidCodePoint(0) && isWellFormedUnicode(value);
    }

    private static boolean isWellFormedUnicode(String value) {
        int i = 0;
        while (i < value.length()) {
            int cp = value.codePointAt(i);
            if (cp >= Character.MIN_HIGH_SURROGATE && cp <= Character.MAX_LOW_SURROGATE) {
                return false;
            }
            i += Character.charCount(cp);
        }
        return true;
    }

    public static int compareUnsignedUtf8(String a, String b) {
        byte[] x = a.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] y = b.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        int n = Math.min(x.length, y.length);
        for (int i = 0; i < n; i++) {
            int d = (x[i] & 0xFF) - (y[i] & 0xFF);
            if (d != 0) {
                return d;
            }
        }
        return x.length - y.length;
    }
}
