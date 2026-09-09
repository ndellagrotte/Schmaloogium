// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.config.OptionState;

import java.util.Map;
import java.util.Optional;

final class OptionStateValue implements OptionState {

    final OptionCatalogValue catalog;
    final java.util.SortedMap<String, OptionValue> values;

    OptionStateValue(OptionCatalogValue catalog, java.util.Map<String, OptionValue> values) {
        this.catalog = java.util.Objects.requireNonNull(catalog, "catalog");
        java.util.SortedMap<String, OptionValue> ordered =
            new java.util.TreeMap<>(EngineOptionData::compareUnsignedUtf8);
        ordered.putAll(java.util.Objects.requireNonNull(values, "values"));
        this.values = java.util.Collections.unmodifiableSortedMap(ordered);
    }

    OptionCatalogValue catalog() {
        return catalog;
    }

    @Override
    public Map<String, OptionValue> values() {
        return values;
    }

    @Override
    public Optional<OptionValue> value(String name) {
        java.util.Objects.requireNonNull(name, "name");
        return Optional.ofNullable(values.get(name));
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof OptionStateValue other
            && this.catalog == other.catalog
            && this.values.equals(other.values);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(System.identityHashCode(catalog), values);
    }

    @Override
    public String toString() {
        return "OptionState" + values;
    }
}
