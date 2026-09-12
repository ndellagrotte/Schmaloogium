// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.wire;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * One canonical flat document (PHASE_2_DOC §4.1, §4.5.2): a schema first line, then
 * strictly ascending {@code key = value} lines with LF endings and a final newline. Values
 * are held as their exact wire text; typed accessors decode through {@link CanonicalText}.
 * Used by the capture plan, the world-generation descriptor, the world receipt, the
 * comparison artifacts and the baseline manifests — every text artifact that is hashed or
 * compared byte-wise.
 */
public final class FlatDocument {

    private final String schemaLine;
    private final SortedMap<String, String> entries;

    private FlatDocument(String schemaLine, SortedMap<String, String> entries) {
        this.schemaLine = schemaLine;
        this.entries = Collections.unmodifiableSortedMap(entries);
    }

    public String schemaLine() {
        return schemaLine;
    }

    public SortedMap<String, String> entries() {
        return entries;
    }

    public boolean has(String key) {
        return entries.containsKey(key);
    }

    public String raw(String key) {
        String raw = entries.get(key);
        if (raw == null) {
            throw new IllegalArgumentException("missing key: " + key);
        }
        return raw;
    }

    public String text(String key) {
        String raw = raw(key);
        CanonicalText.requireCanonicalJson(raw);
        return CanonicalText.decodeJson(raw);
    }

    public String token(String key) {
        String raw = raw(key);
        if (raw.isEmpty()) {
            throw new IllegalArgumentException("empty token: " + key);
        }
        return raw;
    }

    public boolean bool(String key) {
        return CanonicalText.parseBoolean(raw(key));
    }

    public long integer(String key) {
        return CanonicalText.parseInt(raw(key));
    }

    public double decimal(String key) {
        return CanonicalText.parseDouble(raw(key));
    }

    public int count(String family) {
        return Math.toIntExact(integer(family + ".count"));
    }

    /** Renders the canonical bytes: schema line, sorted entries, LF endings. */
    public String render() {
        StringBuilder out = new StringBuilder(schemaLine).append('\n');
        for (Map.Entry<String, String> e : entries.entrySet()) {
            out.append(e.getKey()).append(" = ").append(e.getValue()).append('\n');
        }
        return out.toString();
    }

    public byte[] bytes() {
        return render().getBytes(StandardCharsets.UTF_8);
    }

    public String sha256() {
        return Hashes.sha256Hex(bytes());
    }

    /** Strict parse: exact schema line, no CR, single {@code " = "} split, ascending unique keys. */
    public static FlatDocument parse(String text, String expectedSchemaLine) {
        try (BufferedReader lines = new BufferedReader(new StringReader(text))) {
            String first = lines.readLine();
            if (!expectedSchemaLine.equals(first)) {
                throw new IllegalArgumentException("first line must be exactly '"
                    + expectedSchemaLine + "': " + first);
            }
            TreeMap<String, String> entries = new TreeMap<>();
            String previous = null;
            String line;
            while ((line = lines.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                if (line.endsWith("\r")) {
                    throw new IllegalArgumentException("CR byte in line: " + line);
                }
                int split = line.indexOf(" = ");
                if (split <= 0) {
                    throw new IllegalArgumentException("expected 'key = value': " + line);
                }
                String key = line.substring(0, split);
                CanonicalText.validateKey(key);
                if (previous != null && key.compareTo(previous) <= 0) {
                    throw new IllegalArgumentException("keys must be strictly ascending: "
                        + previous + " then " + key);
                }
                previous = key;
                entries.put(key, line.substring(split + 3));
            }
            if (!text.endsWith("\n")) {
                throw new IllegalArgumentException("document must end with a newline");
            }
            return new FlatDocument(expectedSchemaLine, entries);
        } catch (IOException impossible) {
            throw new IllegalStateException(impossible);
        }
    }

    public static Builder builder(String schemaLine) {
        return new Builder(schemaLine);
    }

    /** Typed, duplicate-rejecting builder. */
    public static final class Builder {

        private final String schemaLine;
        private final TreeMap<String, String> entries = new TreeMap<>();

        private Builder(String schemaLine) {
            this.schemaLine = schemaLine;
        }

        public Builder text(String key, String value) {
            return put(key, CanonicalText.encodeJson(value));
        }

        public Builder token(String key, String value) {
            if (value == null || value.isEmpty() || value.strip().length() != value.length()
                    || value.contains("\n")) {
                throw new IllegalArgumentException("token must be non-empty and unpadded: " + key);
            }
            return put(key, value);
        }

        public Builder bool(String key, boolean value) {
            return put(key, CanonicalText.formatBoolean(value));
        }

        public Builder integer(String key, long value) {
            return put(key, CanonicalText.formatInt(value));
        }

        public Builder decimal(String key, double value) {
            return put(key, CanonicalText.formatDouble(value));
        }

        /** Copies an already-canonical raw value (validated only as a key). */
        public Builder raw(String key, String rawValue) {
            return put(key, rawValue);
        }

        public boolean has(String key) {
            return entries.containsKey(key);
        }

        private Builder put(String key, String value) {
            CanonicalText.validateKey(key);
            if (entries.putIfAbsent(key, value) != null) {
                throw new IllegalArgumentException("duplicate key: " + key);
            }
            return this;
        }

        public FlatDocument build() {
            return new FlatDocument(schemaLine, new TreeMap<>(entries));
        }
    }
}
