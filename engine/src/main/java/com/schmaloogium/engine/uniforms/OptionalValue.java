// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

/**
 * Closed present/absent value container — never a nullable field and never
 * {@code java.util.Optional} (PHASE_6_DOC §4.2). Used for the frame provider's optional
 * fog fallback.
 */
public sealed interface OptionalValue<T> {

    T value();

    boolean isPresent();

    record Present<T>(T value) implements OptionalValue<T> {
        public Present {
            if (value == null) {
                throw new IllegalArgumentException("value must not be null");
            }
        }

        @Override
        public boolean isPresent() {
            return true;
        }
    }

    record Absent<T>() implements OptionalValue<T> {
        @Override
        public T value() {
            throw new IllegalStateException("absent");
        }

        @Override
        public boolean isPresent() {
            return false;
        }
    }
}
