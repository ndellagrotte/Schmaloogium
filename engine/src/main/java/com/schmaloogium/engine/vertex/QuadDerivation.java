// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

/**
 * The exact per-quad normal/tangent/midpoint derivation (PHASE_10_DOC §4.3). Given the
 * four positions and UVs in emitted order, evaluated from stored floats before
 * quantization:
 *
 * <pre>{@code
 * a = p2 - p0                 b = p3 - p1
 * N = normalize(a × b)
 * e1 = p1 - p0                e2 = p2 - p0
 * du1 = u1-u0  dv1 = v1-v0    du2 = u2-u0  dv2 = v2-v0
 * d = du1*dv2 - du2*dv1
 * T = normalize((dv2*e1 - dv1*e2) / d)
 * B = normalize((-du2*e1 + du1*e2) / d)
 * w = sign(dot(B, N × T))
 * midU = (u0+u1+u2+u3)*0.25   midV = (v0+v1+v2+v3)*0.25
 * }</pre>
 *
 * <p>All four vertices of a quad receive the same N, {@code (T, w)} and midpoint. There
 * is no Gram–Schmidt, triangle averaging, sprite-bound midpoint or cross-face
 * smoothing. Handedness is {@code N×T} (RESEARCH App C.2; Pintonium's {@code T×N} is
 * rejected, D-P10-2) and is computed with unquantized N/T/B. Degenerate domain: a
 * zero-length diagonal cross yields zero N; a zero UV determinant, zero-length T or B,
 * or a non-finite intermediate yields zero tangent and {@code w = 0} — never NaN/Inf —
 * while a finite valid normal and midpoint are retained independently. Non-finite input
 * positions/UVs reject the product (thrown). No epsilon collapses small nonzero
 * determinants.
 */
public final class QuadDerivation {

    /**
     * One derived quad frame: unquantized normal {@code (nx, ny, nz)}, tangent
     * {@code (tx, ty, tz)}, handedness {@code w ∈ {-1, 0, +1}}, and the UV
     * quarter-average midpoint.
     */
    public record QuadFrame(double nx, double ny, double nz,
                            double tx, double ty, double tz,
                            int w, float midU, float midV) {

        public QuadFrame {
            if (w != -1 && w != 0 && w != 1) {
                throw new IllegalArgumentException("handedness must be -1, 0 or +1: " + w);
            }
        }
    }

    private QuadDerivation() {
    }

    /**
     * Derives the quad frame. {@code positions} holds xyz for vertices 0..3 (12 floats),
     * {@code uvs} holds uv for vertices 0..3 (8 floats), both in emitted order.
     *
     * @throws IllegalArgumentException when any input position or UV is non-finite
     */
    public static QuadFrame derive(float[] positions, float[] uvs) {
        if (positions == null || positions.length != 12) {
            throw new IllegalArgumentException("positions must hold 12 floats");
        }
        if (uvs == null || uvs.length != 8) {
            throw new IllegalArgumentException("uvs must hold 8 floats");
        }
        for (float p : positions) {
            requireFinite(p, "position");
        }
        for (float u : uvs) {
            requireFinite(u, "uv");
        }

        double ax = positions[6] - positions[0];
        double ay = positions[7] - positions[1];
        double az = positions[8] - positions[2];
        double bx = positions[9] - positions[3];
        double by = positions[10] - positions[4];
        double bz = positions[11] - positions[5];

        double[] n = cross(ax, ay, az, bx, by, bz);
        double nLen = length(n);
        double[] normal = nLen == 0.0 ? new double[] {0.0, 0.0, 0.0} : scale(n, 1.0 / nLen);

        double e1x = positions[3] - positions[0];
        double e1y = positions[4] - positions[1];
        double e1z = positions[5] - positions[2];
        double e2x = positions[6] - positions[0];
        double e2y = positions[7] - positions[1];
        double e2z = positions[8] - positions[2];

        double du1 = uvs[2] - uvs[0];
        double dv1 = uvs[3] - uvs[1];
        double du2 = uvs[4] - uvs[0];
        double dv2 = uvs[5] - uvs[1];
        double d = du1 * dv2 - du2 * dv1;

        double[] tangent = {0.0, 0.0, 0.0};
        double[] bitangent = {0.0, 0.0, 0.0};
        int w = 0;
        if (d != 0.0) {
            double[] t = {(dv2 * e1x - dv1 * e2x) / d,
                    (dv2 * e1y - dv1 * e2y) / d,
                    (dv2 * e1z - dv1 * e2z) / d};
            double[] b = {(-du2 * e1x + du1 * e2x) / d,
                    (-du2 * e1y + du1 * e2y) / d,
                    (-du2 * e1z + du1 * e2z) / d};
            double tLen = length(t);
            double bLen = length(b);
            if (tLen != 0.0 && bLen != 0.0 && finite(t) && finite(b)) {
                tangent = scale(t, 1.0 / tLen);
                bitangent = scale(b, 1.0 / bLen);
                double[] crossNT = cross(normal[0], normal[1], normal[2],
                        tangent[0], tangent[1], tangent[2]);
                double dot = dot(bitangent, crossNT);
                if (Double.isFinite(dot)) {
                    w = dot > 0.0 ? 1 : (dot < 0.0 ? -1 : 0);
                }
            }
        }

        float midU = (float) ((uvs[0] + uvs[2] + uvs[4] + uvs[6]) * 0.25);
        float midV = (float) ((uvs[1] + uvs[3] + uvs[5] + uvs[7]) * 0.25);
        return new QuadFrame(normal[0], normal[1], normal[2],
                tangent[0], tangent[1], tangent[2], w, midU, midV);
    }

    /** Quantizes one normal component: {@code (byte) trunc(clamp(x, -1, 1) * 127)} — never −128. */
    public static byte quantizeNormalComponent(double component) {
        double clamped = Math.clamp(component, -1.0, 1.0);
        return (byte) (int) (clamped * 127.0);
    }

    /** Quantizes one tangent component: {@code (short) trunc(clamp(x, -1, 1) * 32767)} — never −32768. */
    public static short quantizeTangentComponent(double component) {
        double clamped = Math.clamp(component, -1.0, 1.0);
        return (short) (int) (clamped * 32767.0);
    }

    private static void requireFinite(float value, String what) {
        if (!Float.isFinite(value)) {
            throw new IllegalArgumentException(
                    "non-finite " + what + " rejects the product before GL: " + value);
        }
    }

    private static double[] cross(double ax, double ay, double az,
                                  double bx, double by, double bz) {
        return new double[] {ay * bz - az * by, az * bx - ax * bz, ax * by - ay * bx};
    }

    private static double length(double[] v) {
        double len = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        return Double.isFinite(len) ? len : 0.0;
    }

    private static double[] scale(double[] v, double factor) {
        return new double[] {v[0] * factor, v[1] * factor, v[2] * factor};
    }

    private static double dot(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    private static boolean finite(double[] v) {
        return Double.isFinite(v[0]) && Double.isFinite(v[1]) && Double.isFinite(v[2]);
    }
}
