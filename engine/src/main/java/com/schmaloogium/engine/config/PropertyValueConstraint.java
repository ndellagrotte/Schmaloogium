// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** One typed property alternative: literal text or integer interval. */
public sealed interface PropertyValueConstraint {
    record Literal(String value) implements PropertyValueConstraint {

        public Literal {
            java.util.Objects.requireNonNull(value, "value");
        }
    }

    record IntegerInterval(IntegerRange range) implements PropertyValueConstraint {

        public IntegerInterval {
            java.util.Objects.requireNonNull(range, "range");
        }
    }
}
