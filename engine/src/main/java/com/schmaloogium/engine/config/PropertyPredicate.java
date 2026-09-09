// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.List;

/** One source-ordered {@code property=alternatives} constraint. */
public record PropertyPredicate(
        String propertyName, List<PropertyValueConstraint> acceptedValues) {

    public PropertyPredicate {
        java.util.Objects.requireNonNull(propertyName, "propertyName");
        acceptedValues = List.copyOf(java.util.Objects.requireNonNull(acceptedValues, "acceptedValues"));
        if (acceptedValues.isEmpty()) {
            throw new IllegalArgumentException("acceptedValues must be non-empty");
        }
    }
}
