// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.textures.internal.NoiseGenerator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The §4.2.2 signed-recurrence contract (§8 noise_signedRecurrence): arithmetic vectors,
 * and proof that the signedness of the shift and the absence of abs() are load-bearing —
 * locally computed wrong recurrences diverge from the contract vectors.
 */
class NoiseGeneratorTest {

    /** The corrupted variant: the right shift is logical {@code >>>} instead of arithmetic. */
    private static int xorshiftUnsignedShift(int s) {
        s = s ^ (s << 13);
        s = s ^ (s >>> 17);
        s = s ^ (s << 5);
        return s;
    }

    private static int wrongStateUnsignedShift(int x, int y, int c) {
        int seed = (xorshiftUnsignedShift(x) + xorshiftUnsignedShift(y * 19))
            * xorshiftUnsignedShift(c * 23) - c;
        return xorshiftUnsignedShift(seed);
    }

    private static byte wrongByteUnsignedShift(int x, int y, int c) {
        return (byte) (wrongStateUnsignedShift(x, y, c) % 128);
    }

    @Test
    void contractVectors() {
        assertEquals(253983, NoiseGenerator.xorshift(-1));
        assertEquals(-1828175987, NoiseGenerator.channelState(1, 1, 1));
        assertEquals(-115, NoiseGenerator.channelRemainder(1, 1, 1));
        assertEquals((byte) 141, NoiseGenerator.channelByte(1, 1, 1));
    }

    @Test
    void unsignedShiftDivergesFromContractVectors() {
        // the corrupted recurrence agrees on small positive inputs, so the seed is equal,
        // but the final xorshift over the large signed seed diverges: the required state
        // vector -1828175987 is never reproduced. The mutation only perturbs bits 22..31,
        // so remainder (% 128) and uploaded byte are structurally insensitive to it —
        // which is exactly why the state vector is the pinned evidence.
        assertNotEquals(-1828175987, wrongStateUnsignedShift(1, 1, 1));
        assertNotEquals(NoiseGenerator.channelState(1, 1, 1), wrongStateUnsignedShift(1, 1, 1));
        int stateDifferences = 0;
        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 32; x++) {
                for (int c = 0; c < 3; c++) {
                    if (NoiseGenerator.channelState(x, y, c) != wrongStateUnsignedShift(x, y, c)) {
                        stateDifferences++;
                    }
                }
            }
        }
        assertTrue(stateDifferences > 0,
            "unsigned-shift recurrence must not reproduce the signed states");
    }

    @Test
    void absoluteValueDivergesFromContractVectors() {
        // abs() before the upload step: |-115| = 115, never the required byte 141
        int remainder = NoiseGenerator.channelRemainder(1, 1, 1);
        byte absByte = (byte) Math.abs(remainder);
        assertNotEquals((byte) 141, absByte);
        assertEquals((byte) 115, absByte);
        assertNotEquals(NoiseGenerator.channelByte(1, 1, 1), absByte);
    }

    @Test
    void generateRgbShapeAndRowMajorSampling() {
        int res = 256;
        byte[] plane = NoiseGenerator.generateRgb(res);
        assertEquals(res * res * 3, plane.length);
        assertEquals(196608, plane.length);
        // row-major y then x, channels c = 0,1,2 in RGB order at (y*res + x)*3 + c
        int[][] cells = {{0, 0}, {255, 255}, {100, 33}, {7, 128}, {1, 1}};
        for (int[] cell : cells) {
            int x = cell[0];
            int y = cell[1];
            for (int c = 0; c < 3; c++) {
                assertEquals(NoiseGenerator.channelByte(x, y, c),
                    plane[(y * res + x) * 3 + c],
                    "cell (" + x + "," + y + ") channel " + c);
            }
        }
    }

    @Test
    void deterministicAcrossCalls() {
        assertTrue(Arrays.equals(NoiseGenerator.generateRgb(256),
            NoiseGenerator.generateRgb(256)));
        assertTrue(Arrays.equals(NoiseGenerator.generateRgb(4),
            NoiseGenerator.generateRgb(4)));
        assertEquals(NoiseGenerator.xorshift(-1), NoiseGenerator.xorshift(-1));
    }
}
