// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.matrix;

import com.schmaloogium.engine.uniforms.support.UniformFixture;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pure math verification for the engine-side matrix helpers: the inverse is exact for
 * the translation term, A·A⁻¹ ≈ I for general invertible input, singular matrices
 * yield an empty optional, and the length-16 column-major contract is enforced.
 */
class MatrixInversionTest {

    private static final float EPSILON = 1e-5f;

    private static float[] translation(float tx, float ty, float tz) {
        // Column-major identity with the translation in elements 12..14.
        return new float[] {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                tx, ty, tz, 1,
        };
    }

    private static float[] multiply(float[] a, float[] b) {
        return com.schmaloogium.engine.uniforms.matrix.Matrix4.multiplyColumnMajor(a, b);
    }

    @Test
    void identityInvertsToIdentity() {
        float[] identity = translation(0, 0, 0);
        assertArrayEquals(identity, Matrix4.invertColumnMajor(identity).orElseThrow(),
                1e-7f);
    }

    @Test
    void translationInvertsToNegatedTranslation() {
        float[] inverse = Matrix4.invertColumnMajor(translation(3, -4, 5)).orElseThrow();
        assertArrayEquals(translation(-3, 4, -5), inverse, 1e-6f);
    }

    @Test
    void generalInvertibleMatrixComposesToIdentity() {
        // Rotation about Z by 90 degrees times a scale of 2 (column-major).
        float[] m = new float[] {
                0, 2, 0, 0,
                -2, 0, 0, 0,
                0, 0, 2, 0,
                1, 2, 3, 1,
        };
        Optional<float[]> inverse = Matrix4.invertColumnMajor(m);
        assertTrue(inverse.isPresent());
        float[] product = multiply(m, inverse.orElseThrow());
        float[] identity = translation(0, 0, 0);
        assertArrayEquals(identity, product, 1e-4f);
    }

    @Test
    void singularMatrixYieldsEmpty() {
        // Rank 3: last row collapses (determinant 0).
        float[] singular = new float[] {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 0,
        };
        assertFalse(Matrix4.invertColumnMajor(singular).isPresent());
        float[] zero = new float[16];
        assertFalse(Matrix4.invertColumnMajor(zero).isPresent());
    }

    @Test
    void lengthSixteenContractIsEnforced() {
        assertThrows(IllegalArgumentException.class,
                () -> Matrix4.invertColumnMajor(new float[15]));
        assertThrows(IllegalArgumentException.class,
                () -> Matrix4.multiplyColumnMajor(new float[16], new float[12]));
    }

    @Test
    void multiplyComposesTranslations() {
        float[] product = multiply(translation(1, 0, 0), translation(0, 2, 0));
        assertArrayEquals(translation(1, 2, 0), product, 1e-6f);
        assertEquals(16, product.length);
    }
}
