// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.Map;

final class InternalOptionSnapshotValue implements InternalOptionSnapshot {

    private final Object domain;
    private final Map<String, OptionValue> values;

    InternalOptionSnapshotValue(Object domain, Map<String, OptionValue> values) {
        this.domain = domain;
        this.values = Map.copyOf(values);
    }

    Object domain() {
        return domain;
    }

    Map<String, OptionValue> values() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof InternalOptionSnapshotValue other
            && this.domain == other.domain
            && this.values.equals(other.values);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(System.identityHashCode(domain), values);
    }
}
