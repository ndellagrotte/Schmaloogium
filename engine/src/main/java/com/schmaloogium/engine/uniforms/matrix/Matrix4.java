// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.matrix;

import java.util.Optional;

/**
 * The deterministic pure-Java 4×4 inversion (PHASE_6_DOC §4.7). Computes in
 * {@code double}, returns finite floats, and reports a singular/non-finite result
 * instead of manufacturing an inverse. Column-major element order — the same order as
 * {@code Matrix4Value} and {@code UniformService.uploadMatrix4(..., transpose=false)}.
 */
public final class Matrix4 {

    private Matrix4() {
    }

    /**
     * Inverts one column-major matrix via the adjugate over the cofactor expansion;
     * empty when the matrix is singular or produces a non-finite inverse. The input is
     * read only; the returned array is fresh. Runs once per current-matrix signal, never
     * in the activation loop.
     */
    public static Optional<float[]> invertColumnMajor(float[] m) {
        if (m == null || m.length != 16) {
            throw new IllegalArgumentException("matrix needs exactly 16 column-major floats");
        }
        double[][] a = fromColumnMajor(m);
        double[][] cofactors = new double[4][4];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                cofactors[row][col] = cofactor(a, row, col);
            }
        }
        double determinant = 0d;
        for (int col = 0; col < 4; col++) {
            determinant += a[0][col] * cofactors[0][col];
        }
        if (determinant == 0d || !Double.isFinite(determinant)) {
            return Optional.empty();
        }
        // inverse = transpose(cofactor matrix) / determinant
        float[] out = new float[16];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                double v = cofactors[col][row] / determinant;
                if (!Double.isFinite(v)) {
                    return Optional.empty();
                }
                out[col * 4 + row] = (float) v;
            }
        }
        return Optional.of(out);
    }

    /** Multiplies two column-major matrices (a·b); verification helper. */
    public static float[] multiplyColumnMajor(float[] a, float[] b) {
        if (a == null || b == null || a.length != 16 || b.length != 16) {
            throw new IllegalArgumentException("matrices need exactly 16 column-major floats");
        }
        double[][] left = fromColumnMajor(a);
        double[][] right = fromColumnMajor(b);
        float[] out = new float[16];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                double sum = 0d;
                for (int k = 0; k < 4; k++) {
                    sum += left[row][k] * right[k][col];
                }
                out[col * 4 + row] = (float) sum;
            }
        }
        return out;
    }

    private static double[][] fromColumnMajor(float[] m) {
        double[][] a = new double[4][4];
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 4; row++) {
                a[row][col] = m[col * 4 + row];
            }
        }
        return a;
    }

    private static double cofactor(double[][] a, int row, int col) {
        double[][] minor = new double[3][3];
        int mr = 0;
        for (int r = 0; r < 4; r++) {
            if (r == row) {
                continue;
            }
            int mc = 0;
            for (int c = 0; c < 4; c++) {
                if (c == col) {
                    continue;
                }
                minor[mr][mc++] = a[r][c];
            }
            mr++;
        }
        double det = minor[0][0] * (minor[1][1] * minor[2][2] - minor[1][2] * minor[2][1])
                - minor[0][1] * (minor[1][0] * minor[2][2] - minor[1][2] * minor[2][0])
                + minor[0][2] * (minor[1][0] * minor[2][1] - minor[1][1] * minor[2][0]);
        return ((row + col) % 2 == 0) ? det : -det;
    }
}
