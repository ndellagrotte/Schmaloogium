// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.scene;

import com.schmaloogium.conformance.wire.CanonicalText;
import com.schmaloogium.conformance.wire.Hashes;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * [D-P2-41]: the stable effective-option-state identity. SHA-256 over the UTF-8 canonical
 * text {@code schema = schmaloogium.option-state/1\n} followed by the sorted exact lines for
 * {@code pack.options.count}, {@code pack.options.<n>.{name,value}},
 * {@code pack.engineOptions.count} and {@code pack.engineOptions.<n>.{name,value}}, JSON-string
 * values, final newline. Both maps are the complete resolved effective state (defaults
 * included), sorted by name in UTF-8 byte order with unique non-empty names.
 */
public final class OptionStateDigest {

    public static final String SCHEMA_LINE = "schema = schmaloogium.option-state/1";

    private OptionStateDigest() {
    }

    public static String canonicalText(Map<String, String> packOptions, Map<String, String> engineOptions) {
        TreeMap<String, String> lines = new TreeMap<>(CanonicalText.ORDER);
        emit(lines, "pack.options", packOptions);
        emit(lines, "pack.engineOptions", engineOptions);
        StringBuilder sb = new StringBuilder(SCHEMA_LINE).append('\n');
        for (Map.Entry<String, String> e : lines.entrySet()) {
            sb.append(e.getKey()).append(" = ").append(e.getValue()).append('\n');
        }
        return sb.toString();
    }

    public static String sha256(Map<String, String> packOptions, Map<String, String> engineOptions) {
        return Hashes.sha256HexOf(canonicalText(packOptions, engineOptions));
    }

    private static void emit(TreeMap<String, String> lines, String family, Map<String, String> map) {
        SortedMap<String, String> sorted = new TreeMap<>(CanonicalText.ORDER);
        for (Map.Entry<String, String> e : map.entrySet()) {
            if (e.getKey() == null || e.getKey().isEmpty()) {
                throw new IllegalArgumentException("empty option name in " + family);
            }
            if (sorted.putIfAbsent(e.getKey(), e.getValue()) != null) {
                throw new IllegalArgumentException("duplicate option name in " + family + ": " + e.getKey());
            }
        }
        lines.put(family + ".count", Long.toString(sorted.size()));
        int i = 0;
        for (Map.Entry<String, String> e : sorted.entrySet()) {
            lines.put(family + "." + i + ".name", CanonicalText.encodeJson(e.getKey()));
            lines.put(family + "." + i + ".value", CanonicalText.encodeJson(e.getValue()));
            i++;
        }
    }
}
