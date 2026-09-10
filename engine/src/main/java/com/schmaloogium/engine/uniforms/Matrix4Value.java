// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;


/**
 * One 4×4 matrix value in column-major order — the exact element order accepted by
 * {@code UniformService.uploadMatrix4(..., transpose=false)} (PHASE_6_DOC §4.7). Capture
 * copies all sixteen floats immediately: a provider's or caller's later array mutation
 * cannot affect this value. No mutable array is exposed; access is by component or by
 * fresh copy. Immutable.
 */
public record Matrix4Value(float m00, float m01, float m02, float m03,
                           float m10, float m11, float m12, float m13,
                           float m20, float m21, float m22, float m23,
                           float m30, float m31, float m32, float m33) {

    public Matrix4Value {
        requireFinite(m00, m01, m02, m03, m10, m11, m12, m13,
                m20, m21, m22, m23, m30, m31, m32, m33);
    }

    /** Builds from a column-major array of exactly sixteen finite floats; copies on entry. */
    public static Matrix4Value ofColumnMajor(float[] columnMajor) {
        if (columnMajor == null || columnMajor.length != 16) {
            throw new IllegalArgumentException("matrix needs exactly 16 column-major floats");
        }
        return new Matrix4Value(columnMajor[0], columnMajor[1], columnMajor[2], columnMajor[3],
                columnMajor[4], columnMajor[5], columnMajor[6], columnMajor[7],
                columnMajor[8], columnMajor[9], columnMajor[10], columnMajor[11],
                columnMajor[12], columnMajor[13], columnMajor[14], columnMajor[15]);
    }

    /** The identity matrix (the pending-value neutral for shadow matrices). */
    public static Matrix4Value identity() {
        return new Matrix4Value(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f);
    }

    /** A fresh column-major copy of the sixteen floats, in facade upload order. */
    public float[] toColumnMajorArray() {
        return new float[] {m00, m01, m02, m03, m10, m11, m12, m13,
                m20, m21, m22, m23, m30, m31, m32, m33};
    }

    private static void requireFinite(float... values) {
        for (float v : values) {
            if (!Float.isFinite(v)) {
                throw new IllegalArgumentException("matrix components must be finite");
            }
        }
    }
}
