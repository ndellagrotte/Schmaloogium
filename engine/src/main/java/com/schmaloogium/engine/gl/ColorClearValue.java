// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Typed color-clear payload for {@link FramebufferService#clearColorAttachment}
 * (PHASE_1_DOC §4.7.4b, D-P1-67): exactly three immutable four-component records.
 * {@link Floating} serves normalized/floating formats only; {@link Signed} and
 * {@link Unsigned} serve matching integer formats only — a mismatched type is rejected
 * even if native GL would silently accept it. Unsigned components are mathematical
 * integers in {@code 0..4294967295}, never signed-int reinterpretations in public data
 * or logs.
 */
public sealed interface ColorClearValue {

    /** Finite float components for normalized/floating attachments. */
    record Floating(float r, float g, float b, float a) implements ColorClearValue {

        public Floating {
            requireFinite(r, "r");
            requireFinite(g, "g");
            requireFinite(b, "b");
            requireFinite(a, "a");
        }
    }

    /** Signed integer components for signed-integer attachments. */
    record Signed(int r, int g, int b, int a) implements ColorClearValue {
    }

    /** Unsigned components in {@code 0..4294967295} for unsigned-integer attachments. */
    record Unsigned(long r, long g, long b, long a) implements ColorClearValue {

        private static final long MAX = 0xFFFFFFFFL;

        public Unsigned {
            requireRange(r, "r");
            requireRange(g, "g");
            requireRange(b, "b");
            requireRange(a, "a");
        }

        private static void requireRange(long v, String name) {
            if (v < 0 || v > MAX) {
                throw new IllegalArgumentException(
                        "unsigned color-clear component " + name + " must be in 0..4294967295: " + v);
            }
        }
    }

    private static void requireFinite(float v, String name) {
        if (!Float.isFinite(v)) {
            throw new IllegalArgumentException("floating color-clear component " + name + " must be finite: " + v);
        }
    }
}
