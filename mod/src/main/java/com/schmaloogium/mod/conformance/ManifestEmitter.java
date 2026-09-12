// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.conformance;

import java.util.Map;
import java.util.TreeMap;

/**
 * Emits one {@code schmaloogium.run-manifest/4} document (PHASE_2_DOC §4.5.4): the schema
 * line, then strictly ascending {@code key = value} lines with LF endings and a final
 * newline. Typed puts; a duplicate key is a programming error and throws. Dense families
 * are written through {@link #row}, which keeps the {@code <family>.count} scalar current.
 */
final class ManifestEmitter {

    static final String SCHEMA_LINE = "schema = schmaloogium.run-manifest/4";

    private final TreeMap<String, String> entries = new TreeMap<>();
    private final TreeMap<String, Integer> counts = new TreeMap<>();

    ManifestEmitter text(String key, String value) {
        return put(key, CanonicalScalars.encodeJson(value));
    }

    ManifestEmitter token(String key, String value) {
        if (value == null || value.isEmpty() || value.contains("\n") || value.strip().length() != value.length()) {
            throw new IllegalArgumentException("token must be non-empty and unpadded: " + key + "=" + value);
        }
        return put(key, value);
    }

    ManifestEmitter bool(String key, boolean value) {
        return put(key, CanonicalScalars.formatBoolean(value));
    }

    ManifestEmitter integer(String key, long value) {
        return put(key, CanonicalScalars.formatInt(value));
    }

    ManifestEmitter decimal(String key, double value) {
        return put(key, CanonicalScalars.formatDouble(value));
    }

    /** Starts a dense row; returns its key prefix {@code family.<n>.} and bumps the count. */
    String row(String family) {
        int index = counts.merge(family, 1, Integer::sum) - 1;
        entries.put(family + ".count", CanonicalScalars.formatInt(index + 1));
        return family + "." + index + ".";
    }

    /** Declares an empty family (count zero) unless rows were added. */
    ManifestEmitter family(String family) {
        entries.putIfAbsent(family + ".count", "0");
        counts.putIfAbsent(family, 0);
        return this;
    }

    boolean has(String key) {
        return entries.containsKey(key);
    }

    private ManifestEmitter put(String key, String value) {
        if (entries.putIfAbsent(key, value) != null) {
            throw new IllegalArgumentException("duplicate manifest key " + key);
        }
        return this;
    }

    String render() {
        StringBuilder sb = new StringBuilder(SCHEMA_LINE).append('\n');
        for (Map.Entry<String, String> e : entries.entrySet()) {
            sb.append(e.getKey()).append(" = ").append(e.getValue()).append('\n');
        }
        return sb.toString();
    }
}
