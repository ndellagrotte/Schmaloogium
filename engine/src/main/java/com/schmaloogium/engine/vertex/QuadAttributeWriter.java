// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

import com.schmaloogium.engine.config.id.BlockStampResult;

import java.nio.ByteBuffer;

/**
 * Writes the per-quad derived attributes into the four CLASSIC_56 records of one
 * completed quad (PHASE_10_DOC §2.1, §4.2, §4.3): the §4.3 frame is evaluated from the
 * stored positions/UVs before quantization, then all four vertices receive the same
 * quantized normal, tangent (with handedness), midpoint and the exact Phase 9 identity
 * words. Position, color, UV0 and lightmap bytes are preserved untouched.
 *
 * <p>A non-finite stored input rejects the product before GL by propagating the
 * derivation rejection; the builder-side wrapper policy turns any such failure into
 * product invalidation, never a sealed prefix.
 */
public final class QuadAttributeWriter {

    private QuadAttributeWriter() {
    }

    /**
     * Finalizes the quad whose first record starts at {@code quadBaseOffset}. Reads the
     * four records' positions and UVs, derives the §4.3 frame, and stamps normal,
     * midpoint, tangent and identity into every record of the quad.
     */
    public static void finalizeQuad(ByteBuffer buffer, int quadBaseOffset, int strideBytes,
                                    BlockStampResult stamp) {
        if (strideBytes < Classic56Layout.STRIDE_BYTES) {
            throw new IllegalArgumentException(
                    "stride below the classic floor: " + strideBytes);
        }
        float[] positions = new float[12];
        float[] uvs = new float[8];
        for (int vertex = 0; vertex < 4; vertex++) {
            int base = quadBaseOffset + vertex * strideBytes;
            float[] position = ClassicVertexCodec.getPosition(buffer, base);
            float[] uv = ClassicVertexCodec.getUv0(buffer, base);
            System.arraycopy(position, 0, positions, vertex * 3, 3);
            System.arraycopy(uv, 0, uvs, vertex * 2, 2);
        }
        QuadDerivation.QuadFrame frame = QuadDerivation.derive(positions, uvs);
        for (int vertex = 0; vertex < 4; vertex++) {
            int base = quadBaseOffset + vertex * strideBytes;
            ClassicVertexCodec.putNormalQuantized(buffer, base, frame.nx(), frame.ny(), frame.nz());
            ClassicVertexCodec.putMidTexCoord(buffer, base, frame.midU(), frame.midV());
            ClassicVertexCodec.putTangent(buffer, base, frame.tx(), frame.ty(), frame.tz(), frame.w());
            ClassicVertexCodec.putIdentity(buffer, base, stamp);
        }
    }

    /**
     * Re-stamps only the identity words of every record of the quad, leaving the
     * derived frame and all ordinary bytes untouched.
     */
    public static void restampIdentity(ByteBuffer buffer, int quadBaseOffset, int strideBytes,
                                       BlockStampResult stamp) {
        if (strideBytes < Classic56Layout.STRIDE_BYTES) {
            throw new IllegalArgumentException(
                    "stride below the classic floor: " + strideBytes);
        }
        for (int vertex = 0; vertex < 4; vertex++) {
            ClassicVertexCodec.putIdentity(buffer, quadBaseOffset + vertex * strideBytes, stamp);
        }
    }
}
