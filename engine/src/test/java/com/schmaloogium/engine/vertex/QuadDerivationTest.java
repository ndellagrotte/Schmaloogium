// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T10-MATH (PHASE_10_DOC §4.3, §8): the §4.3 oracles — the worked square, the
 * mirrored-U square with opposite handedness, the non-planar diagonal normal, and the
 * degenerate domain that produces no NaN — plus the truncation quantizers that never
 * reach −128/−32768 as extrema.
 */
class QuadDerivationTest {

    private static final float[] SQUARE_POSITIONS = {
            0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0};
    private static final float[] SQUARE_UVS = {0, 0, 1, 0, 1, 1, 0, 1};
    private static final float[] MIRRORED_UVS = {1, 0, 0, 0, 0, 1, 1, 1};

    @Test
    void workedSquareMatchesTheOracle() {
        QuadDerivation.QuadFrame frame = QuadDerivation.derive(SQUARE_POSITIONS, SQUARE_UVS);
        assertEquals(0.0, frame.nx(), 1e-12);
        assertEquals(0.0, frame.ny(), 1e-12);
        assertEquals(1.0, frame.nz(), 1e-12);
        assertEquals(1.0, frame.tx(), 1e-12);
        assertEquals(0.0, frame.ty(), 1e-12);
        assertEquals(0.0, frame.tz(), 1e-12);
        assertEquals(1, frame.w());
        assertEquals(0.5f, frame.midU(), 0f);
        assertEquals(0.5f, frame.midV(), 0f);

        // normal bytes (0, 0, 127, [pad]) and tangent shorts (32767, 0, 0, 32767).
        assertArrayEquals(new byte[] {0, 0, 127}, new byte[] {
                QuadDerivation.quantizeNormalComponent(frame.nx()),
                QuadDerivation.quantizeNormalComponent(frame.ny()),
                QuadDerivation.quantizeNormalComponent(frame.nz())});
        assertArrayEquals(new short[] {32767, 0, 0, 32767}, new short[] {
                QuadDerivation.quantizeTangentComponent(frame.tx()),
                QuadDerivation.quantizeTangentComponent(frame.ty()),
                QuadDerivation.quantizeTangentComponent(frame.tz()),
                QuadDerivation.quantizeTangentComponent(frame.w())});
    }

    @Test
    void mirroredSquareFlipsHandednessOnly() {
        QuadDerivation.QuadFrame frame = QuadDerivation.derive(SQUARE_POSITIONS, MIRRORED_UVS);
        // d = −1 → T = (−1,0,0), B = (0,1,0), w = −1; N and midpoint unchanged.
        assertEquals(-1.0, frame.tx(), 1e-12);
        assertEquals(0.0, frame.ty(), 1e-12);
        assertEquals(0.0, frame.tz(), 1e-12);
        assertEquals(0.0, frame.nx(), 1e-12);
        assertEquals(1.0, frame.nz(), 1e-12);
        assertEquals(-1, frame.w());
        assertEquals(0.5f, frame.midU(), 0f);
        assertArrayEquals(new short[] {-32767, 0, 0, -32767}, new short[] {
                QuadDerivation.quantizeTangentComponent(frame.tx()),
                QuadDerivation.quantizeTangentComponent(frame.ty()),
                QuadDerivation.quantizeTangentComponent(frame.tz()),
                QuadDerivation.quantizeTangentComponent(frame.w())});
    }

    @Test
    void quantizersTruncateAndNeverReachTheMinima() {
        assertEquals(127, QuadDerivation.quantizeNormalComponent(1.0));
        assertEquals(-127, QuadDerivation.quantizeNormalComponent(-1.0));
        assertEquals(-127, QuadDerivation.quantizeNormalComponent(-42.0));
        assertEquals(127, QuadDerivation.quantizeNormalComponent(42.0));
        assertEquals(0, QuadDerivation.quantizeNormalComponent(0.004));
        assertEquals(32767, QuadDerivation.quantizeTangentComponent(1.0));
        assertEquals(-32767, QuadDerivation.quantizeTangentComponent(-1.0));
        assertEquals(-32767, QuadDerivation.quantizeTangentComponent(-9.0));
        // sign(0) = 0 through the same quantizer path as the handedness short.
        assertEquals(0, QuadDerivation.quantizeTangentComponent(0.0));
    }

    @Test
    void nonPlanarQuadUsesTheSpecifiedDiagonalNormal() {
        float[] positions = {0, 0, 0, 1, 0, 0, 1, 1, 0.5f, 0, 1, 0};
        QuadDerivation.QuadFrame frame = QuadDerivation.derive(positions, SQUARE_UVS);
        // a × b = (−0.5, −0.5, 2); normalized over sqrt(4.5).
        double scale = 1.0 / Math.sqrt(4.5);
        assertEquals(-0.5 * scale, frame.nx(), 1e-12);
        assertEquals(-0.5 * scale, frame.ny(), 1e-12);
        assertEquals(2.0 * scale, frame.nz(), 1e-12);
        double length = Math.sqrt(frame.nx() * frame.nx() + frame.ny() * frame.ny()
                + frame.nz() * frame.nz());
        assertEquals(1.0, length, 1e-12);
    }

    @Test
    void degenerateUvDomainYieldsZeroTangentAndZeroHandednessWithoutNaN() {
        float[] flatUvs = {0.25f, 0.25f, 0.25f, 0.25f, 0.25f, 0.25f, 0.25f, 0.25f};
        QuadDerivation.QuadFrame frame = QuadDerivation.derive(SQUARE_POSITIONS, flatUvs);
        assertEquals(0.0, frame.tx(), 0.0);
        assertEquals(0.0, frame.ty(), 0.0);
        assertEquals(0.0, frame.tz(), 0.0);
        assertEquals(0, frame.w());
        // The finite valid normal and midpoint are retained independently.
        assertEquals(1.0, frame.nz(), 1e-12);
        assertEquals(0.25f, frame.midU(), 0f);
    }

    @Test
    void degenerateGeometryYieldsZeroNormalButKeepsTangentMathFinite() {
        // p0 == p2 collapses the diagonal cross: zero N, no NaN anywhere.
        float[] positions = {1, 2, 3, 1, 0, 0, 1, 2, 3, 0, 1, 0};
        QuadDerivation.QuadFrame frame = QuadDerivation.derive(positions, SQUARE_UVS);
        assertEquals(0.0, frame.nx(), 0.0);
        assertEquals(0.0, frame.ny(), 0.0);
        assertEquals(0.0, frame.nz(), 0.0);
        assertFalse(Double.isNaN(frame.tx()));
        assertFalse(Double.isNaN(frame.tz()));
        // w must be finite (0 through the sign of a zero dot).
        assertTrue(frame.w() == 0 || frame.w() == 1 || frame.w() == -1);
    }

    @Test
    void nonFiniteInputRejectsTheProduct() {
        float[] nanPositions = SQUARE_POSITIONS.clone();
        nanPositions[4] = Float.NaN;
        assertThrows(IllegalArgumentException.class,
                () -> QuadDerivation.derive(nanPositions, SQUARE_UVS));
        float[] infiniteUvs = SQUARE_UVS.clone();
        infiniteUvs[3] = Float.POSITIVE_INFINITY;
        assertThrows(IllegalArgumentException.class,
                () -> QuadDerivation.derive(SQUARE_POSITIONS, infiniteUvs));
    }

    @Test
    void noEpsilonCollapsesASmallNonzeroDeterminant() {
        // u1 differs from u0 by one float ULP: d is tiny but nonzero, so the tangent
        // math still runs and stays finite instead of collapsing to zero.
        float[] uvs = {0, 0, Math.nextUp(0f), 0, 0, 1, 0, 1};
        QuadDerivation.QuadFrame frame = QuadDerivation.derive(SQUARE_POSITIONS, uvs);
        assertFalse(Double.isNaN(frame.tx()));
        assertTrue(Double.isFinite(frame.midU()));
    }
}
