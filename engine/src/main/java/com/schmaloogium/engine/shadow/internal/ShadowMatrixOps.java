// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow.internal;

import com.schmaloogium.engine.uniforms.Float3;
import com.schmaloogium.engine.uniforms.Matrix4Value;

/**
 * Column-major 4×4 helpers for the shadow camera math (PHASE_8_DOC §4.5). Conventional
 * mathematical rows acting on column vectors; arrays are exactly sixteen floats in
 * column-major order — the same order as {@link Matrix4Value} and the facade's
 * {@code transpose=false} uploads. All products allocate one fresh result array; callers
 * on the frame path call each helper once per frame.
 */
public final class ShadowMatrixOps {

    private ShadowMatrixOps() {
    }

    /** Translation T(x,y,z). */
    public static float[] translation(double x, double y, double z) {
        return new float[] {
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                (float) x, (float) y, (float) z, 1f};
    }

    /** Rotation about X by radians. */
    public static float[] rotationX(double radians) {
        double c = Math.cos(radians);
        double s = Math.sin(radians);
        return new float[] {
                1f, 0f, 0f, 0f,
                0f, (float) c, (float) s, 0f,
                0f, (float) -s, (float) c, 0f,
                0f, 0f, 0f, 1f};
    }

    /** Rotation about Y by radians. */
    public static float[] rotationY(double radians) {
        double c = Math.cos(radians);
        double s = Math.sin(radians);
        return new float[] {
                (float) c, 0f, (float) -s, 0f,
                0f, 1f, 0f, 0f,
                (float) s, 0f, (float) c, 0f,
                0f, 0f, 0f, 1f};
    }

    /** Rotation about Z by radians. */
    public static float[] rotationZ(double radians) {
        double c = Math.cos(radians);
        double s = Math.sin(radians);
        return new float[] {
                (float) c, (float) s, 0f, 0f,
                (float) -s, (float) c, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f};
    }

    /** Degrees to radians. */
    public static double radians(double degrees) {
        return Math.toRadians(degrees);
    }

    /** The product a·b (apply b first to a column vector); fresh result. */
    public static float[] multiply(float[] a, float[] b) {
        float[] out = new float[16];
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 4; row++) {
                double sum = 0d;
                for (int k = 0; k < 4; k++) {
                    sum += (double) a[k * 4 + row] * (double) b[col * 4 + k];
                }
                out[col * 4 + row] = (float) sum;
            }
        }
        return out;
    }

    /** Transforms the xyzw direction/point; returns the transformed components. */
    public static double[] transform(float[] m, double x, double y, double z, double w) {
        double[] out = new double[4];
        for (int row = 0; row < 4; row++) {
            out[row] = (double) m[row] * x + (double) m[4 + row] * y
                    + (double) m[8 + row] * z + (double) m[12 + row] * w;
        }
        return out;
    }

    /** Wraps a column-major array into the immutable Matrix4Value. */
    public static Matrix4Value toValue(float[] columnMajor) {
        return Matrix4Value.ofColumnMajor(columnMajor);
    }

    /** Normalizes a direction; a zero-length input returns zero. */
    public static Float3 normalize(double x, double y, double z) {
        double length = Math.sqrt(x * x + y * y + z * z);
        if (length == 0d || !Double.isFinite(length)) {
            return new Float3(0f, 0f, 0f);
        }
        return new Float3((float) (x / length), (float) (y / length), (float) (z / length));
    }
}
