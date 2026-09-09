// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.capture;

import com.schmaloogium.conformance.wire.CanonicalText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * In-memory model of one canonical {@code schmaloogium.run-manifest/4} document
 * (PHASE_2_DOC §4.5.4, D-P2-50): the flat sorted key/value space the wire defines,
 * with JSON-string values held decoded and every scalar held typed. T0 (§4.2.1) is
 * decided from this model, so a verdict is re-derivable from artifacts months later.
 *
 * <p>Values are produced only by {@link RunManifestReader} (strict canonical form) or
 * the {@link Builder}; nothing here re-serializes silently — use
 * {@link RunManifestWriter}.
 */
public final class RunManifest {

    /** The current major; there is no compatibility reader (§4.5.2, §4.5.4). */
    public static final String SCHEMA_LINE = "schema = schmaloogium.run-manifest/4";

    /** One wire value, typed. */
    public sealed interface Value {
        record Text(String value) implements Value {}
        record Token(String value) implements Value {}
        record Bool(boolean value) implements Value {}
        record Int(long value) implements Value {}
        record Dec(double value) implements Value {}
    }

    private final SortedMap<String, Value> entries;

    RunManifest(SortedMap<String, Value> entries) {
        this.entries = java.util.Collections.unmodifiableSortedMap(entries);
    }

    public SortedMap<String, Value> entries() {
        return entries;
    }

    // ------------------------------------------------------------------
    // Typed accessors (missing key → IllegalArgumentException: callers
    // validate presence through the reader's required-key check first)
    // ------------------------------------------------------------------

    public String text(String key) {
        return require(key, Value.Text.class).value();
    }

    public String token(String key) {
        return require(key, Value.Token.class).value();
    }

    public boolean bool(String key) {
        return require(key, Value.Bool.class).value();
    }

    public long integer(String key) {
        return require(key, Value.Int.class).value();
    }

    public double decimal(String key) {
        return require(key, Value.Dec.class).value();
    }

    public boolean has(String key) {
        return entries.containsKey(key);
    }

    private <V extends Value> V require(String key, Class<V> type) {
        Value value = entries.get(key);
        if (value == null) {
            throw new IllegalArgumentException("missing key: " + key);
        }
        if (!type.isInstance(value)) {
            throw new IllegalArgumentException("key " + key + " is " + value.getClass().getSimpleName()
                + ", expected " + type.getSimpleName());
        }
        return type.cast(value);
    }

    // ------------------------------------------------------------------
    // Dense families (§4.5.4: count + dense zero-based rows)
    // ------------------------------------------------------------------

    /** One indexed row of a dense family; leaf field names carry the {@code <n>.}
     *  prefix stripped. */
    public record Row(int index, SortedMap<String, Value> fields) {

        public String text(String field) {
            return RunManifest.requireField(this, field, Value.Text.class).value();
        }

        public String token(String field) {
            return RunManifest.requireField(this, field, Value.Token.class).value();
        }

        public boolean bool(String field) {
            return RunManifest.requireField(this, field, Value.Bool.class).value();
        }

        public long integer(String field) {
            return RunManifest.requireField(this, field, Value.Int.class).value();
        }

        public double decimal(String field) {
            return RunManifest.requireField(this, field, Value.Dec.class).value();
        }
    }

    private static <V extends Value> V requireField(Row row, String field, Class<V> type) {
        Value value = row.fields().get(field);
        if (value == null) {
            throw new IllegalArgumentException("row " + row.index() + " missing field: " + field);
        }
        if (!type.isInstance(value)) {
            throw new IllegalArgumentException("row " + row.index() + " field " + field
                + " is " + value.getClass().getSimpleName());
        }
        return type.cast(value);
    }

    /** Count of a dense family ({@code <family>.count}); absent family → 0. */
    public int familyCount(String family) {
        Value count = entries.get(family + ".count");
        if (count == null) {
            return 0;
        }
        if (!(count instanceof Value.Int intCount)) {
            throw new IllegalArgumentException(family + ".count is not an integer");
        }
        return Math.toIntExact(intCount.value());
    }

    /** Rows of a dense family, ascending index, each row's fields stripped of the
     *  {@code family.<n>.} prefix. */
    public List<Row> family(String family) {
        int count = familyCount(family);
        List<SortedMap<String, Value>> byIndex = new ArrayList<>(count);
        String prefix = family + ".";
        for (Map.Entry<String, Value> e : entries.entrySet()) {
            String key = e.getKey();
            if (!key.startsWith(prefix) || key.equals(prefix + "count")) {
                continue;
            }
            String rest = key.substring(prefix.length());
            int dot = rest.indexOf('.');
            if (dot <= 0) {
                continue;
            }
            int index;
            try {
                index = Integer.parseInt(rest.substring(0, dot));
            } catch (NumberFormatException notAnIndex) {
                continue;
            }
            while (byIndex.size() <= index) {
                byIndex.add(null);
            }
            SortedMap<String, Value> row = byIndex.get(index);
            if (row == null) {
                row = new TreeMap<>();
                byIndex.set(index, row);
            }
            row.put(rest.substring(dot + 1), e.getValue());
        }
        List<Row> rows = new ArrayList<>(count);
        for (int i = 0; i < byIndex.size(); i++) {
            SortedMap<String, Value> fields = byIndex.get(i);
            if (fields == null) {
                throw new IllegalArgumentException("dense family " + family
                    + " is missing index " + i);
            }
            rows.add(new Row(i, fields));
        }
        if (rows.size() != count) {
            throw new IllegalArgumentException("dense family " + family + " has " + rows.size()
                + " rows but count=" + count);
        }
        return rows;
    }

    // ------------------------------------------------------------------
    // Builder
    // ------------------------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final SortedMap<String, Value> entries = new TreeMap<>();

        public Builder text(String key, String value) {
            CanonicalText.validateKey(key);
            return put(key, new Value.Text(java.util.Objects.requireNonNull(value, key)));
        }

        public Builder token(String key, String value) {
            CanonicalText.validateKey(key);
            if (value == null || value.isEmpty() || value.strip().length() != value.length()) {
                throw new IllegalArgumentException("token must be non-empty and unpadded: " + key);
            }
            return put(key, new Value.Token(value));
        }

        public Builder bool(String key, boolean value) {
            CanonicalText.validateKey(key);
            return put(key, new Value.Bool(value));
        }

        public Builder integer(String key, long value) {
            CanonicalText.validateKey(key);
            return put(key, new Value.Int(value));
        }

        public Builder decimal(String key, double value) {
            CanonicalText.validateKey(key);
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("non-finite decimal: " + key);
            }
            return put(key, new Value.Dec(value));
        }

        /** Adds one row of a dense family at the given index. */
        public Builder row(String family, int index, Map<String, Value> fields) {
            CanonicalText.validateKey(family);
            if (index < 0) {
                throw new IllegalArgumentException("negative row index");
            }
            fields.forEach((field, value) -> put(family + "." + index + "." + field, value));
            return set(family + ".count", new Value.Int(Math.max(familyCount(family), index + 1)));
        }

        public Builder count(String family, int count) {
            if (count < 0) {
                throw new IllegalArgumentException("negative count");
            }
            return integer(family + ".count", count);
        }

        private Builder put(String key, Value value) {
            Value previous = entries.put(key, value);
            if (previous != null) {
                throw new IllegalArgumentException("duplicate key: " + key);
            }
            return this;
        }

        /** Upsert for derivations: replaces an existing key's value. */
        public Builder set(String key, Value value) {
            CanonicalText.validateKey(key);
            entries.put(key, java.util.Objects.requireNonNull(value, key));
            return this;
        }

        /** Removes a key (conditional-presence derivations). */
        public Builder unset(String key) {
            CanonicalText.validateKey(key);
            entries.remove(key);
            return this;
        }

        private int familyCount(String family) {
            Value count = entries.get(family + ".count");
            return count instanceof Value.Int intCount ? Math.toIntExact(intCount.value()) : 0;
        }

        public RunManifest build() {
            return new RunManifest(new TreeMap<>(entries));
        }
    }
}
