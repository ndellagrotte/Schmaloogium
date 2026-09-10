// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

import com.schmaloogium.engine.config.id.BlockStampResult;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T10-LAYOUT byte-oracle vectors (PHASE_10_DOC §4.1, §4.2): decoded CLASSIC_56 bytes
 * match the shader input types — identity bit patterns with signed 0xffff → −1.0
 * behavior, ordered lightmap shorts, color channel order, zero padding — and the
 * item→block semantic copy never depends on the source's own byte offsets.
 */
class ClassicVertexCodecTest {

    private static ByteBuffer record() {
        return ByteBuffer.allocate(Classic56Layout.STRIDE_BYTES)
                .order(ByteOrder.LITTLE_ENDIAN);
    }

    @Test
    void identityExamplePacksTheExactTwoWords() {
        // (alias=31, renderType=3, metadata=2) → P9 supplies 0x0003001f, 2 →
        // little-endian bytes 1f 00 03 00 02 00 00 00, shader observes (31, 3, 2).
        ByteBuffer buffer = record();
        ClassicVertexCodec.beginRecord(buffer, 0);
        ClassicVertexCodec.putIdentity(buffer, 0, new BlockStampResult.Present(0x0003001f, 2));
        assertArrayEquals(new int[] {0x1f, 0x00, 0x03, 0x00, 0x02, 0x00, 0x00, 0x00},
                unsignedBytes(buffer, Classic56Layout.OFFSET_IDENTITY, 8));
        assertArrayEquals(new int[] {31, 3, 2},
                ClassicVertexCodec.getIdentityComponents(buffer, 0));
    }

    @Test
    void identityIsABitPatternSignedShortNotUnsigned() {
        ByteBuffer buffer = record();
        ClassicVertexCodec.beginRecord(buffer, 0);
        // 65535 and −1 both yield 0xffff and are observed as −1 by the signed-short
        // floating attribute; the codec never re-validates the alias range.
        ClassicVertexCodec.putIdentityWords(buffer, 0, 0x1234ffff, 0x7fff8000);
        assertArrayEquals(new int[] {-1, 0x1234, Short.MIN_VALUE},
                ClassicVertexCodec.getIdentityComponents(buffer, 0));
        assertArrayEquals(new int[] {-1, 0x1234, Short.MIN_VALUE},
                ClassicVertexCodec.getIdentityComponents(buffer, 0));
    }

    @Test
    void absentAndUnrepresentableKeepRenderTypeAndMetadataWithZeroLowId() {
        ByteBuffer buffer = record();
        ClassicVertexCodec.beginRecord(buffer, 0);
        ClassicVertexCodec.putIdentity(buffer, 0, new BlockStampResult.Absent(0x0005_0000, 7));
        assertArrayEquals(new int[] {0, 5, 7},
                ClassicVertexCodec.getIdentityComponents(buffer, 0));
        ClassicVertexCodec.putIdentity(buffer, 0,
                new BlockStampResult.Unrepresentable(0x0006_0000, 9));
        assertArrayEquals(new int[] {0, 6, 9},
                ClassicVertexCodec.getIdentityComponents(buffer, 0));
    }

    @Test
    void recordInitializationIsWhiteColorAndZeroElsewhere() {
        ByteBuffer buffer = record();
        ClassicVertexCodec.beginRecord(buffer, 0);
        assertArrayEquals(new int[] {255, 255, 255, 255},
                ClassicVertexCodec.getColorBytes(buffer, 0));
        assertArrayEquals(new int[] {0, 0, 0}, ClassicVertexCodec.getNormalBytes(buffer, 0));
        assertArrayEquals(new float[] {0f, 0f}, ClassicVertexCodec.getUv0(buffer, 0), 0f);
        assertArrayEquals(new int[] {0, 0, 0},
                ClassicVertexCodec.getIdentityComponents(buffer, 0));
        assertTrue(ClassicVertexCodec.paddingIsZero(buffer, 0));
    }

    @Test
    void colorKeepsByteChannelOrderAndLightmapKeepsComponentOrder() {
        ByteBuffer buffer = record();
        ClassicVertexCodec.beginRecord(buffer, 0);
        ClassicVertexCodec.putColorBytes(buffer, 0, 0x12, 0x34, 0x56, 0x78);
        assertArrayEquals(new int[] {0x12, 0x34, 0x56, 0x78},
                unsignedBytes(buffer, ClassicSemantic.COLOR.byteOffset(), 4));
        ClassicVertexCodec.putLightmap(buffer, 0, 0x1122, 0x3344);
        assertArrayEquals(new int[] {0x22, 0x11, 0x44, 0x33},
                unsignedBytes(buffer, ClassicSemantic.UV1.byteOffset(), 4));
        assertArrayEquals(new int[] {0x1122, 0x3344}, ClassicVertexCodec.getLightmap(buffer, 0));
    }

    @Test
    void itemNormalAtADifferentSourceOffsetCopiesBySemantic() {
        // T10-LAYOUT: the ITEM source stores its normal at a different vanilla offset;
        // the mapping copies by semantic usage, so a blind prefix copy of source bytes
        // would land the normal at the wrong destination offset.
        ByteBuffer itemSource = record();
        byte[] itemNormal = {0, 0, 127};
        // Deliberately offset from the classic destination: this source's normal sits at 26.
        itemSource.put(26, itemNormal[0]).put(27, itemNormal[1]).put(28, itemNormal[2]);

        ByteBuffer destination = record();
        ClassicVertexCodec.beginRecord(destination, 0);
        ClassicSemantic destinationSemantic =
                VertexProducer.itemToBlockIngress().get(ClassicSemantic.NORMAL);
        assertEquals(ClassicSemantic.NORMAL, destinationSemantic);
        assertEquals(28, destinationSemantic.byteOffset());
        ClassicVertexCodec.putNormalBytes(destination, 0,
                itemSource.get(26), itemSource.get(27), itemSource.get(28));
        assertArrayEquals(new int[] {0, 0, 127},
                ClassicVertexCodec.getNormalBytes(destination, 0));
    }

    @Test
    void paddingIsAlwaysZeroAfterInitAndWrites() {
        ByteBuffer buffer = record();
        ClassicVertexCodec.beginRecord(buffer, 0);
        ClassicVertexCodec.putPosition(buffer, 0, 1f, 2f, 3f);
        ClassicVertexCodec.putTangent(buffer, 0, 1.0, 0.0, 0.0, 1);
        ClassicVertexCodec.putIdentityWords(buffer, 0, 1, 2);
        assertTrue(ClassicVertexCodec.paddingIsZero(buffer, 0));
        buffer.put(31, (byte) 1);
        assertFalse(ClassicVertexCodec.paddingIsZero(buffer, 0));
    }

    @Test
    void floatAndShortReadsRoundTrip() {
        ByteBuffer buffer = record();
        ClassicVertexCodec.beginRecord(buffer, 0);
        ClassicVertexCodec.putPosition(buffer, 0, 0.25f, -1.5f, 64f);
        ClassicVertexCodec.putUv0(buffer, 0, 0.125f, 0.875f);
        ClassicVertexCodec.putMidTexCoord(buffer, 0, 0.5f, 0.5f);
        assertArrayEquals(new float[] {0.25f, -1.5f, 64f}, ClassicVertexCodec.getPosition(buffer, 0), 0f);
        assertArrayEquals(new float[] {0.125f, 0.875f}, ClassicVertexCodec.getUv0(buffer, 0), 0f);
        assertArrayEquals(new float[] {0.5f, 0.5f}, ClassicVertexCodec.getMidTexCoord(buffer, 0), 0f);
    }

    private static int[] unsignedBytes(ByteBuffer buffer, int offset, int count) {
        int[] bytes = new int[count];
        for (int i = 0; i < count; i++) {
            bytes[i] = buffer.get(offset + i) & 0xff;
        }
        return bytes;
    }
}
