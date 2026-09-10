// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.eval;

/** Fixed-size scratch cell stack for one evaluation. Cells hold a boolean, a binary32
 * float, or a float vector; vectors store their lanes at {@code cell * 4}. Grown lazily
 * but reused across nodes and refreshes. */
final class Cells {

    static final byte KBOOL = 0;
    static final byte KFLOAT = 1;
    static final byte KVEC2 = 2;
    static final byte KVEC3 = 3;
    static final byte KVEC4 = 4;

    private static final int INITIAL = 64;

    private byte[] kind = new byte[INITIAL];
    private float[] f = new float[4 * INITIAL];
    private boolean[] b = new boolean[INITIAL];
    private int top = -1;

    int push(byte k) {
        top++;
        if (top >= kind.length) {
            int capacity = kind.length * 2;
            kind = java.util.Arrays.copyOf(kind, capacity);
            f = java.util.Arrays.copyOf(f, 4 * capacity);
            b = java.util.Arrays.copyOf(b, capacity);
        }
        kind[top] = k;
        return top;
    }

    void pop() {
        top--;
    }

    int top() {
        return top;
    }

    byte kind(int cell) {
        return kind[cell];
    }

    float floatAt(int cell, int lane) {
        return f[4 * cell + lane];
    }

    void setFloatAt(int cell, int lane, float value) {
        f[4 * cell + lane] = value;
    }

    boolean boolAt(int cell) {
        return b[cell];
    }

    void setBoolAt(int cell, boolean value) {
        b[cell] = value;
    }

    int vectorWidth(byte k) {
        return switch (k) {
            case KVEC2 -> 2;
            case KVEC3 -> 3;
            case KVEC4 -> 4;
            default -> -1;
        };
    }
}
