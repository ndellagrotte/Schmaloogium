// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures.internal;

/**
 * The contract noise recurrence (PHASE_13_DOC §4.2.2, D-P13-3): every shift, XOR, add,
 * subtract and multiply wraps to signed 32 at that operation boundary; right shift is
 * arithmetic {@code >> 17}; signed remainder truncates its quotient toward zero (Java's
 * {@code %}). No absolute value and no unconditional 0–127 byte guarantee.
 *
 * <p>Required vectors: {@code xorshift(-1) = 253983}; at {@code (x,y,c)=(1,1,1)} final state
 * {@code -1828175987}, signed remainder {@code -115}, uploaded byte {@code 141}.
 * Row-major y/x ordering, channels c = 0,1,2 in RGB order.
 */
public final class NoiseGenerator {

    /** The signed-recurrence schema tag participating in generated source identity. */
    public static final String RECURRENCE_SCHEMA_TAG =
        "phase13.noise/xorshift-signed32-v1";

    private NoiseGenerator() {
    }

    /** One signed-32 xorshift step: {@code s ^ (s<<13)}, {@code s ^ (s>>17)}, {@code s ^ (s<<5)}. */
    public static int xorshift(int s) {
        s = s ^ (s << 13);
        s = s ^ (s >> 17);
        s = s ^ (s << 5);
        return s;
    }

    /** The mixed channel seed: {@code (xorshift(x) + xorshift(y*19)) * xorshift(c*23) - c}. */
    public static int channelSeed(int x, int y, int c) {
        return (xorshift(x) + xorshift(y * 19)) * xorshift(c * 23) - c;
    }

    /** Final recurrence state for one channel texel. */
    public static int channelState(int x, int y, int c) {
        return xorshift(channelSeed(x, y, c));
    }

    /** Signed remainder mod 128, truncating toward zero. */
    public static int channelRemainder(int x, int y, int c) {
        return channelState(x, y, c) % 128;
    }

    /** The uploaded unsigned byte: the low eight bits of the signed remainder. */
    public static byte channelByte(int x, int y, int c) {
        return (byte) channelRemainder(x, y, c);
    }

    /**
     * Generates the full RGB byte plane: {@code resolution × resolution × 3} bytes,
     * row-major y/x, channels c = 0,1,2 in RGB order.
     */
    public static byte[] generateRgb(int resolution) {
        if (resolution <= 0) {
            throw new IllegalArgumentException("resolution must be positive: " + resolution);
        }
        byte[] out = new byte[Math.multiplyExact(resolution, resolution) * 3];
        int pos = 0;
        for (int y = 0; y < resolution; y++) {
            for (int x = 0; x < resolution; x++) {
                out[pos++] = channelByte(x, y, 0);
                out[pos++] = channelByte(x, y, 1);
                out[pos++] = channelByte(x, y, 2);
            }
        }
        return out;
    }
}
