// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.vertex;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.vertex.Classic56Layout;
import com.schmaloogium.engine.vertex.ClassicVertexCodec;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.junit.jupiter.api.Test;

/** Quad finalization over a fake sidecar: complete quads only, once, from vertex zero. */
class VertexIngressTest {

    /** A builder stand-in: a native-order buffer of extended records and the counters. */
    private static final class FakeBuilder implements BuilderSidecar {
        final ByteBuffer bytes = ByteBuffer.allocateDirect(16 * Classic56Layout.STRIDE_BYTES)
            .order(ByteOrder.nativeOrder());
        boolean extended = true;
        int vertices;
        int finalizedThrough;

        @Override public boolean schmaloogium$extended() { return extended; }
        @Override public void schmaloogium$setExtended(boolean e, long serial) { extended = e; }
        @Override public long schmaloogium$serial() { return 1; }
        @Override public int schmaloogium$finalizedThrough() { return finalizedThrough; }
        @Override public void schmaloogium$setFinalizedThrough(int v) { finalizedThrough = v; }
        @Override public ByteBuffer schmaloogium$byteBuffer() { return bytes; }
        @Override public int schmaloogium$vertexCount() { return vertices; }
        @Override public int schmaloogium$drawMode() { return 7; }
        @Override public void schmaloogium$selectElement(int elementIndex) { }

        void vertex(float x, float y, float z, float u, float v) {
            int base = vertices * Classic56Layout.STRIDE_BYTES;
            ClassicVertexCodec.beginRecord(bytes, base);
            ClassicVertexCodec.putPosition(bytes, base, x, y, z);
            ClassicVertexCodec.putUv0(bytes, base, u, v);
            vertices++;
        }
    }

    @Test
    void completeQuadsAreFinalizedOnceAndPartialOnesWait() {
        FakeBuilder b = new FakeBuilder();
        b.vertex(0, 0, 0, 0, 0);
        b.vertex(1, 0, 0, 1, 0);
        b.vertex(1, 1, 0, 1, 1);
        VertexIngress.finalizeCompleteQuads(b);
        assertEquals(0, b.finalizedThrough, "three vertices are not a quad");
        b.vertex(0, 1, 0, 0, 1);
        VertexIngress.finalizeCompleteQuads(b);
        assertEquals(4, b.finalizedThrough);
        for (int v = 0; v < 4; v++) {
            int base = v * Classic56Layout.STRIDE_BYTES;
            assertArrayEquals(new float[] {0.5f, 0.5f}, ClassicVertexCodec.getMidTexCoord(b.bytes, base), 0f);
            assertArrayEquals(new int[] {32767, 0, 0, 32767}, ClassicVertexCodec.getTangentShorts(b.bytes, base));
            assertEquals(127, ClassicVertexCodec.getNormalBytes(b.bytes, base)[2]);
            assertEquals(0, ClassicVertexCodec.getIdentityComponents(b.bytes, base)[0],
                "no task context: neutral identity");
            assertTrue(ClassicVertexCodec.paddingIsZero(b.bytes, base));
        }
        // A second call over the same count does nothing (watermark).
        VertexIngress.finalizeCompleteQuads(b);
        assertEquals(4, b.finalizedThrough);
    }

    @Test
    void inactiveBuildersDoNoExtendedWork() {
        FakeBuilder b = new FakeBuilder();
        b.extended = false;
        b.vertex(0, 0, 0, 0, 0);
        b.vertex(1, 0, 0, 1, 0);
        b.vertex(1, 1, 0, 1, 1);
        b.vertex(0, 1, 0, 0, 1);
        VertexIngress.finalizeCompleteQuads(b);
        assertEquals(0, b.finalizedThrough);
        assertArrayEquals(new float[] {0f, 0f}, ClassicVertexCodec.getMidTexCoord(b.bytes, 0), 0f);
    }
}
