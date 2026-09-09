// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import java.util.List;
import java.util.Map;

public sealed interface DecisionValue {
    record Bool(boolean value) implements DecisionValue {}
    record IntegerValue(long value) implements DecisionValue {}
    record FloatBits(int bits) implements DecisionValue {}
    record Token(String value) implements DecisionValue {

        public Token {
            java.util.Objects.requireNonNull(value, "value");
        }
    }

    record TextHash(String sha256) implements DecisionValue {

        public TextHash {
            java.util.Objects.requireNonNull(sha256, "sha256");
        }
    }

    record Absent() implements DecisionValue {}

    record Sequence(List<DecisionValue> values) implements DecisionValue {

        public Sequence {
            values = List.copyOf(values);
        }
    }

    record Fields(Map<String, DecisionValue> values) implements DecisionValue {

        public Fields {
            values = Map.copyOf(values);
        }
    }
}
