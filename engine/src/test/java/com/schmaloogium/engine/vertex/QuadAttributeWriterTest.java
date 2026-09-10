// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

import com.schmaloogium.engine.config.id.BlockStampResult;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The per-quad attribute writer (PHASE_10_DOC §4.2 endVertex stamp, §4.3): all four
 * vertices of a quad receive the same quantized normal/tangent/midpoint and the exact
 * identity words; ordinary bytes (position, color, UV0, lightmap) are preserved.
 */
class QuadAttributeWriterTest {

    private static final int QUADS = 2;

    private static ByteBuffer quadBuffer(int quads) {
        return ByteBuffer.allocate(quads * 4 * Classic56Layout.STRIDE_BYTES)
                .order(ByteOrder.LITTLE_ENDIAN);
    }

    private static void writeSquare(ByteBuffer buffer, int quadIndex) {
        float[][] positions = {{0, 0, 0}, {1, 0, 0}, {1, 1, 0}, {0, 1, 0}};
        float[][] uvs = {{0, 0}, {1, 0}, {1, 1}, {0, 1}};
        for (int vertex = 0; vertex < 4; vertex++) {
            int base = (quadIndex * 4 + vertex) * Classic56Layout.STRIDE_BYTES;
            ClassicVertexCodec.beginRecord(buffer, base);
            ClassicVertexCodec.putPosition(buffer, base,
                    positions[vertex][0], positions[vertex][1], positions[vertex][2]);
            ClassicVertexCodec.putUv0(buffer, base, uvs[vertex][0], uvs[vertex][1]);
            ClassicVertexCodec.putColorBytes(buffer, base, 0x40, 0x50, 0x60, 0x70);
            ClassicVertexCodec.putLightmap(buffer, base, 0x0101, 0x0202);
        }
    }

    @Test
    void everyVertexOfTheQuadReceivesTheSameFrame() {
        ByteBuffer buffer = quadBuffer(1);
        writeSquare(buffer, 0);
        QuadAttributeWriter.finalizeQuad(buffer, 0, Classic56Layout.STRIDE_BYTES,
                new BlockStampResult.Present(0x0003001f, 2));
        for (int vertex = 0; vertex < 4; vertex++) {
            int base = vertex * Classic56Layout.STRIDE_BYTES;
            assertArrayEquals(new int[] {0, 0, 127},
                    ClassicVertexCodec.getNormalBytes(buffer, base));
            assertArrayEquals(new int[] {32767, 0, 0, 32767},
                    ClassicVertexCodec.getTangentShorts(buffer, base));
            assertArrayEquals(new float[] {0.5f, 0.5f},
                    ClassicVertexCodec.getMidTexCoord(buffer, base), 0f);
            assertArrayEquals(new int[] {31, 3, 2},
                    ClassicVertexCodec.getIdentityComponents(buffer, base));
        }
    }

    @Test
    void ordinaryBytesArePreservedWhilePaddingStaysZero() {
        ByteBuffer buffer = quadBuffer(1);
        writeSquare(buffer, 0);
        QuadAttributeWriter.finalizeQuad(buffer, 0, Classic56Layout.STRIDE_BYTES,
                new BlockStampResult.Present(0x0003001f, 2));
        for (int vertex = 0; vertex < 4; vertex++) {
            int base = vertex * Classic56Layout.STRIDE_BYTES;
            assertArrayEquals(new float[] {(vertex == 0 || vertex == 3) ? 0f : 1f,
                            (vertex < 2) ? 0f : 1f, 0f},
                    ClassicVertexCodec.getPosition(buffer, base), 0f);
            assertArrayEquals(new int[] {0x40, 0x50, 0x60, 0x70},
                    ClassicVertexCodec.getColorBytes(buffer, base));
            assertArrayEquals(new int[] {0x0101, 0x0202},
                    ClassicVertexCodec.getLightmap(buffer, base));
            assertTrue(ClassicVertexCodec.paddingIsZero(buffer, base));
        }
    }

    @Test
    void restampReplacesOnlyTheIdentityWords() {
        ByteBuffer buffer = quadBuffer(1);
        writeSquare(buffer, 0);
        QuadAttributeWriter.finalizeQuad(buffer, 0, Classic56Layout.STRIDE_BYTES,
                new BlockStampResult.Present(0x0003001f, 2));
        QuadAttributeWriter.restampIdentity(buffer, 0, Classic56Layout.STRIDE_BYTES,
                new BlockStampResult.Absent(0x0009_0000, 4));
        for (int vertex = 0; vertex < 4; vertex++) {
            int base = vertex * Classic56Layout.STRIDE_BYTES;
            assertArrayEquals(new int[] {0, 9, 4},
                    ClassicVertexCodec.getIdentityComponents(buffer, base));
            // The derived frame survives the restamp.
            assertArrayEquals(new int[] {32767, 0, 0, 32767},
                    ClassicVertexCodec.getTangentShorts(buffer, base));
        }
    }

    @Test
    void consecutiveQuadsKeepTheirOwnFrames() {
        ByteBuffer buffer = quadBuffer(QUADS);
        writeSquare(buffer, 0);
        // Second quad: translated square with mirrored UVs → w = −1.
        for (int vertex = 0; vertex < 4; vertex++) {
            int base = (4 + vertex) * Classic56Layout.STRIDE_BYTES;
            ClassicVertexCodec.beginRecord(buffer, base);
            ClassicVertexCodec.putPosition(buffer, base,
                    5 + (vertex == 1 || vertex == 2 ? 1 : 0), vertex >= 2 ? 1 : 0, 0);
            ClassicVertexCodec.putUv0(buffer, base,
                    vertex == 0 || vertex == 3 ? 1 : 0, vertex >= 2 ? 1 : 0);
        }
        QuadAttributeWriter.finalizeQuad(buffer, 0, Classic56Layout.STRIDE_BYTES,
                new BlockStampResult.Present(1, 0));
        QuadAttributeWriter.finalizeQuad(buffer, 4 * Classic56Layout.STRIDE_BYTES,
                Classic56Layout.STRIDE_BYTES, new BlockStampResult.Present(2, 0));
        assertArrayEquals(new int[] {32767, 0, 0, 32767},
                ClassicVertexCodec.getTangentShorts(buffer, 0));
        assertArrayEquals(new int[] {-32767, 0, 0, -32767},
                ClassicVertexCodec.getTangentShorts(buffer, 4 * Classic56Layout.STRIDE_BYTES));
        assertArrayEquals(new int[] {1, 0, 0}, ClassicVertexCodec.getIdentityComponents(buffer, 0));
        assertArrayEquals(new int[] {2, 0, 0},
                ClassicVertexCodec.getIdentityComponents(buffer, 4 * Classic56Layout.STRIDE_BYTES));
    }

    @Test
    void nonFiniteStoredPositionPropagatesRejection() {
        ByteBuffer buffer = quadBuffer(1);
        writeSquare(buffer, 0);
        buffer.putFloat(4, Float.NaN);
        assertThrows(IllegalArgumentException.class,
                () -> QuadAttributeWriter.finalizeQuad(buffer, 0,
                        Classic56Layout.STRIDE_BYTES, new BlockStampResult.Present(1, 0)));
    }

    @Test
    void grownStrideIsAcceptedWhileClassicOffsetsHold() {
        VertexLayout grown = Classic56Layout.grow()
                .append("at_midBlock", 3, StorageType.INT8, Delivery.FLOAT_VALUE)
                .reserve("reserved", 1)
                .build("CLASSIC_56_GROWN");
        assertEquals(60, grown.strideBytes());
        ByteBuffer buffer = ByteBuffer.allocate(4 * 60).order(ByteOrder.LITTLE_ENDIAN);
        float[][] positions = {{0, 0, 0}, {1, 0, 0}, {1, 1, 0}, {0, 1, 0}};
        float[][] uvs = {{0, 0}, {1, 0}, {1, 1}, {0, 1}};
        for (int vertex = 0; vertex < 4; vertex++) {
            int base = vertex * 60;
            ClassicVertexCodec.beginRecord(buffer, base);
            ClassicVertexCodec.putPosition(buffer, base,
                    positions[vertex][0], positions[vertex][1], positions[vertex][2]);
            ClassicVertexCodec.putUv0(buffer, base, uvs[vertex][0], uvs[vertex][1]);
        }
        QuadAttributeWriter.finalizeQuad(buffer, 0, 60, new BlockStampResult.Present(1, 0));
        assertArrayEquals(new float[] {0.5f, 0.5f},
                ClassicVertexCodec.getMidTexCoord(buffer, 0), 0f);
        assertArrayEquals(new int[] {1, 0, 0},
                ClassicVertexCodec.getIdentityComponents(buffer, 3 * 60));
    }
}
