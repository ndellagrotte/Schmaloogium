// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

public sealed interface ArrayExtent {
    record Sized(int positiveConstant) implements ArrayExtent {

        public Sized {
            if (positiveConstant <= 0) {
                throw new IllegalArgumentException("array extent must be positive");
            }
        }
    }

    record Unsized() implements ArrayExtent {
    }
}
