// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

import java.nio.ByteBuffer;

import com.schmaloogium.engine.config.id.BlockStampResult;
/**
 * Endian-explicit CLASSIC_56 encoders and decoders (PHASE_10_DOC §4.1, §4.2). Native
 * ByteBuffer order is established once by the buffer owner; every store below is an
 * ordered element store: multi-component identity is written as three ordered shorts —
 * never a host-endian {@code putLong} that can exchange components — and color keeps
 * its byte channel order instead of a packed-RGBA-int reinterpretation. Normal and
 * tangent components likewise use ordered byte/short stores through the §4.3
 * quantizers.
 *
 * <p>Record initialization (§4.2): a new destination record starts white (color), zero
 * elsewhere — lightmap, normal, UV storage, padding and neutral extension fields.
 * These white/zero source-absent COLOR/UV1 bytes are storage hygiene only; they never
 * add conventional participation or authorize a reset.
 */
public final class ClassicVertexCodec {

    private ClassicVertexCodec() {
    }

    /**
     * Initializes one new destination record before its first scalar write: white
     * color, zero lightmap/normal/UV/padding and neutral extension fields.
     */
    public static void beginRecord(ByteBuffer buffer, int base) {
        for (int i = 0; i < Classic56Layout.STRIDE_BYTES; i++) {
            buffer.put(base + i, (byte) 0);
        }
        buffer.put(base + Classic56Layout.OFFSET_COLOR, (byte) 0xff);
        buffer.put(base + Classic56Layout.OFFSET_COLOR + 1, (byte) 0xff);
        buffer.put(base + Classic56Layout.OFFSET_COLOR + 2, (byte) 0xff);
        buffer.put(base + Classic56Layout.OFFSET_COLOR + 3, (byte) 0xff);
    }

    public static void putPosition(ByteBuffer buffer, int base, float x, float y, float z) {
        buffer.putFloat(base + ClassicSemantic.POSITION.byteOffset(), x);
        buffer.putFloat(base + ClassicSemantic.POSITION.byteOffset() + 4, y);
        buffer.putFloat(base + ClassicSemantic.POSITION.byteOffset() + 8, z);
    }

    /** Writes color as four ordered channel bytes — never a packed-RGBA int. */
    public static void putColorBytes(ByteBuffer buffer, int base, int r, int g, int b, int a) {
        buffer.put(base + ClassicSemantic.COLOR.byteOffset(), (byte) r);
        buffer.put(base + ClassicSemantic.COLOR.byteOffset() + 1, (byte) g);
        buffer.put(base + ClassicSemantic.COLOR.byteOffset() + 2, (byte) b);
        buffer.put(base + ClassicSemantic.COLOR.byteOffset() + 3, (byte) a);
    }

    public static void putUv0(ByteBuffer buffer, int base, float u, float v) {
        buffer.putFloat(base + ClassicSemantic.UV0.byteOffset(), u);
        buffer.putFloat(base + ClassicSemantic.UV0.byteOffset() + 4, v);
    }

    /** Writes the two lightmap shorts in component order (unnormalized FF set 1). */
    public static void putLightmap(ByteBuffer buffer, int base, int u1, int v1) {
        buffer.putShort(base + ClassicSemantic.UV1.byteOffset(), (short) u1);
        buffer.putShort(base + ClassicSemantic.UV1.byteOffset() + 2, (short) v1);
    }

    /** Writes three ordered signed source-normal bytes. */
    public static void putNormalBytes(ByteBuffer buffer, int base, byte x, byte y, byte z) {
        buffer.put(base + ClassicSemantic.NORMAL.byteOffset(), x);
        buffer.put(base + ClassicSemantic.NORMAL.byteOffset() + 1, y);
        buffer.put(base + ClassicSemantic.NORMAL.byteOffset() + 2, z);
    }

    /** Writes a quad-derived normal through the §4.3 quantizer. */
    public static void putNormalQuantized(ByteBuffer buffer, int base,
                                          double x, double y, double z) {
        putNormalBytes(buffer, base,
                QuadDerivation.quantizeNormalComponent(x),
                QuadDerivation.quantizeNormalComponent(y),
                QuadDerivation.quantizeNormalComponent(z));
    }

    public static void putMidTexCoord(ByteBuffer buffer, int base, float u, float v) {
        buffer.putFloat(base + Classic56Layout.OFFSET_MID_TEX_COORD, u);
        buffer.putFloat(base + Classic56Layout.OFFSET_MID_TEX_COORD + 4, v);
    }

    /** Writes the four ordered tangent shorts (xyz quantized, handedness last). */
    public static void putTangent(ByteBuffer buffer, int base,
                                  double tx, double ty, double tz, int w) {
        buffer.putShort(base + Classic56Layout.OFFSET_TANGENT,
                QuadDerivation.quantizeTangentComponent(tx));
        buffer.putShort(base + Classic56Layout.OFFSET_TANGENT + 2,
                QuadDerivation.quantizeTangentComponent(ty));
        buffer.putShort(base + Classic56Layout.OFFSET_TANGENT + 4,
                QuadDerivation.quantizeTangentComponent(tz));
        buffer.putShort(base + Classic56Layout.OFFSET_TANGENT + 6,
                QuadDerivation.quantizeTangentComponent(w));
    }

    /**
     * Stamps the exact Phase 9 two-word identity: {@code mc_Entity.x} = low 16 bits of
     * the packed word, {@code mc_Entity.y} = high 16 bits, {@code mc_Entity.z} = the
     * metadata word — three ordered shorts as bit patterns, never re-validated as an
     * unsigned range (PHASE_10_DOC §4.1).
     */
    public static void putIdentity(ByteBuffer buffer, int base, BlockStampResult stamp) {
        if (stamp instanceof BlockStampResult.Present present) {
            putIdentityWords(buffer, base, present.packedRenderTypeAndId(), present.metadata());
        } else if (stamp instanceof BlockStampResult.Absent absent) {
            putIdentityWords(buffer, base, absent.packedRenderTypeAndZero(), absent.metadata());
        } else if (stamp instanceof BlockStampResult.Unrepresentable unrepresentable) {
            putIdentityWords(buffer, base, unrepresentable.packedRenderTypeAndZero(),
                    unrepresentable.metadata());
        } else {
            throw new IllegalArgumentException("unknown BlockStampResult family: " + stamp);
        }
    }

    /** The raw two-word form of {@link #putIdentity}. */
    public static void putIdentityWords(ByteBuffer buffer, int base, int word0, int word1) {
        buffer.putShort(base + Classic56Layout.OFFSET_IDENTITY, (short) (word0 & 0xffff));
        buffer.putShort(base + Classic56Layout.OFFSET_IDENTITY + 2, (short) (word0 >>> 16));
        buffer.putShort(base + Classic56Layout.OFFSET_IDENTITY + 4, (short) (word1 & 0xffff));
    }

    public static float[] getPosition(ByteBuffer buffer, int base) {
        int offset = ClassicSemantic.POSITION.byteOffset();
        return new float[] {buffer.getFloat(base + offset),
                buffer.getFloat(base + offset + 4), buffer.getFloat(base + offset + 8)};
    }

    /** Reads the four color channel bytes in channel order (unsigned 0..255). */
    public static int[] getColorBytes(ByteBuffer buffer, int base) {
        int offset = ClassicSemantic.COLOR.byteOffset();
        return new int[] {buffer.get(base + offset) & 0xff,
                buffer.get(base + offset + 1) & 0xff,
                buffer.get(base + offset + 2) & 0xff,
                buffer.get(base + offset + 3) & 0xff};
    }

    public static float[] getUv0(ByteBuffer buffer, int base) {
        int offset = ClassicSemantic.UV0.byteOffset();
        return new float[] {buffer.getFloat(base + offset), buffer.getFloat(base + offset + 4)};
    }

    /** Reads the two signed lightmap shorts in component order. */
    public static int[] getLightmap(ByteBuffer buffer, int base) {
        int offset = ClassicSemantic.UV1.byteOffset();
        return new int[] {buffer.getShort(base + offset), buffer.getShort(base + offset + 2)};
    }

    /** Reads the three signed source-normal bytes. */
    public static int[] getNormalBytes(ByteBuffer buffer, int base) {
        int offset = ClassicSemantic.NORMAL.byteOffset();
        return new int[] {buffer.get(base + offset), buffer.get(base + offset + 1),
                buffer.get(base + offset + 2)};
    }

    public static float[] getMidTexCoord(ByteBuffer buffer, int base) {
        return new float[] {buffer.getFloat(base + Classic56Layout.OFFSET_MID_TEX_COORD),
                buffer.getFloat(base + Classic56Layout.OFFSET_MID_TEX_COORD + 4)};
    }

    /** Reads the four signed tangent shorts; 0xffff is observed as −1, not 65535. */
    public static int[] getTangentShorts(ByteBuffer buffer, int base) {
        int offset = Classic56Layout.OFFSET_TANGENT;
        return new int[] {buffer.getShort(base + offset), buffer.getShort(base + offset + 2),
                buffer.getShort(base + offset + 4), buffer.getShort(base + offset + 6)};
    }

    /** Reads the identity bit patterns as the signed shorts the shader observes. */
    public static int[] getIdentityComponents(ByteBuffer buffer, int base) {
        int offset = Classic56Layout.OFFSET_IDENTITY;
        return new int[] {buffer.getShort(base + offset), buffer.getShort(base + offset + 2),
                buffer.getShort(base + offset + 4)};
    }

    /** True when both classic padding runs are zero in this record. */
    public static boolean paddingIsZero(ByteBuffer buffer, int base) {
        if (buffer.get(base + 31) != 0) {
            return false;
        }
        return buffer.getShort(base + 54) == 0;
    }
}
