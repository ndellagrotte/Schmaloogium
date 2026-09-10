// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow.internal;

import com.schmaloogium.engine.buffers.Extent2i;
import com.schmaloogium.engine.frame.CameraSnapshot;
import com.schmaloogium.engine.frame.ShadowFrameView;
import com.schmaloogium.engine.shadow.CelestialMath;
import com.schmaloogium.engine.shadow.ShadowAabb;
import com.schmaloogium.engine.shadow.ShadowCameraMath;
import com.schmaloogium.engine.shadow.ShadowCameraProjection;
import com.schmaloogium.engine.shadow.ShadowFrustum;
import com.schmaloogium.engine.shadow.ShadowFrustumBuilder;
import com.schmaloogium.engine.shadow.ShadowPlan;
import com.schmaloogium.engine.uniforms.Float3;
import com.schmaloogium.engine.uniforms.Matrix4Value;

/**
 * The sole §4.5/§4.6 implementation. Pure and allocation-frugal: per compute, one
 * perspective/ortho projection, one model-view chain, one snap translation on the ortho
 * branch only; per frustum, the plane work above one fixed-capacity plane array. The sfb
 * extent is validated and otherwise influences nothing. Invalid/nonfinite input fails
 * through {@code IllegalArgumentException} before any result exists.
 */
public final class ShadowCameraMathImpl implements ShadowCameraMath {

    /** §4.5.2: ortho near plane. */
    public static final float NEAR = 0.05f;
    /** §4.5.2: ortho far plane. */
    public static final float FAR = 256.0f;
    /** §4.5.2 model-view camera stand-off. */
    private static final double STANDOFF = -100.0d;
    private static final double HALF_PI = Math.PI / 2.0d;

    private static final double DEDUPE_COS_EPSILON = 1.0e-9d;
    private static final double DEDUPE_D_EPSILON = 1.0e-6d;
    private static final double NORMAL_EPSILON = 1.0e-12d;

    @Override
    public ShadowCameraProjection compute(ShadowFrameView frame, CameraSnapshot camera,
            ShadowPlan plan, Extent2i shadowExtent) {
        if (frame == null || camera == null || plan == null || shadowExtent == null) {
            throw new IllegalArgumentException("null compute input");
        }
        if (shadowExtent.width() <= 0 || shadowExtent.height() <= 0) {
            throw new IllegalArgumentException("shadow extent must be positive");
        }
        double skyAngle = frame.skyAngle();
        if (!Double.isFinite(skyAngle) || skyAngle < 0d || skyAngle >= 1d) {
            throw new IllegalArgumentException("skyAngle must be finite in [0,1)");
        }
        com.schmaloogium.engine.shadow.ShadowCelestialAngles angles =
                plan.celestialPolicy().sample(frame.sunAngle());

        boolean perspective = plan.policy().shadowMapFov().isPresent();
        float[] projection = perspective
                ? perspectiveProjection(plan.policy().shadowMapFov().value(), NEAR, FAR)
                : orthographicProjection(plan.policy().shadowDistance());

        float[] base = ShadowMatrixOps.multiply(
                ShadowMatrixOps.translation(0d, 0d, STANDOFF),
                ShadowMatrixOps.multiply(
                        ShadowMatrixOps.rotationX(HALF_PI),
                        ShadowMatrixOps.multiply(
                                ShadowMatrixOps.rotationZ(angles.thetaRadians()),
                                ShadowMatrixOps.rotationX(
                                        ShadowMatrixOps.radians(
                                                plan.policy().sunPathRotationDegrees())))));
        float[] modelView = base;
        if (!perspective) {
            float interval = plan.policy().shadowIntervalSize();
            if (interval != 0.0f) {
                double cx = frame.cameraPosition().x();
                double cy = frame.cameraPosition().y();
                double cz = frame.cameraPosition().z();
                float ox = (float) cx % interval - interval / 2f;
                float oy = (float) cy % interval - interval / 2f;
                float oz = (float) cz % interval - interval / 2f;
                modelView = ShadowMatrixOps.multiply(base,
                        ShadowMatrixOps.translation(ox, oy, oz));
            }
        }
        Float3 light = lightDirectionWorld(base);
        return new ShadowCameraProjection(ShadowMatrixOps.toValue(projection),
                ShadowMatrixOps.toValue(modelView), light, angles.thetaRadians());
    }

    /** §4.5.4: normalize(inverse(rotation(M0)) · (0,0,1,0)); toward the active light. */
    private static Float3 lightDirectionWorld(float[] modelView) {
        // inverse(rotation) = transpose for the orthonormal rotation part.
        return ShadowMatrixOps.normalize(modelView[8], modelView[9], modelView[10]);
    }

    private static float[] orthographicProjection(float halfPlane) {
        if (!Float.isFinite(halfPlane) || halfPlane <= 0f) {
            throw new IllegalArgumentException("shadowDistance must be finite positive");
        }
        double n = NEAR;
        double f = FAR;
        return new float[] {
                1f / halfPlane, 0f, 0f, 0f,
                0f, 1f / halfPlane, 0f, 0f,
                0f, 0f, (float) (-2d / (f - n)), 0f,
                0f, 0f, (float) (-(f + n) / (f - n)), 1f};
    }

    private static float[] perspectiveProjection(float fov, double near, double far) {
        if (!Float.isFinite(fov) || fov <= 0f || fov >= 180f) {
            throw new IllegalArgumentException("shadowMapFov must be finite in (0,180)");
        }
        double q = 1d / Math.tan(Math.toRadians(fov) / 2.0d);
        return new float[] {
                (float) q, 0f, 0f, 0f,
                0f, (float) q, 0f, 0f,
                0f, 0f, (float) ((far + near) / (near - far)), -1f,
                0f, 0f, (float) (2d * far * near / (near - far)), 0f};
    }

    @Override
    public ShadowFrustum frustum(ShadowCameraProjection camera) {
        if (camera == null) {
            throw new IllegalArgumentException("null camera");
        }
        float[] clip = ShadowMatrixOps.multiply(
                camera.projection().toColumnMajorArray(),
                camera.modelView().toColumnMajorArray());
        double[][] base = basePlanes(clip);
        if (base == null) {
            return ShadowFrustum.disabledCulling();
        }
        double[] center = clipCenter(clip);
        if (center == null) {
            return ShadowFrustum.disabledCulling();
        }
        double lx = camera.lightDirectionWorld().x();
        double ly = camera.lightDirectionWorld().y();
        double lz = camera.lightDirectionWorld().z();
        return synthesize(base, center, lx, ly, lz);
    }

    @Override
    public ShadowFrustumBuilder frustumBuilder() {
        return new ShadowFrustumBuilder() {
            @Override
            public ShadowFrustum build(ShadowCameraProjection camera) {
                return frustum(camera);
            }
        };
    }

    /** §4.6 rows: [left,right,bottom,top,near,far], normalized; null on degeneracy. */
    private static double[][] basePlanes(float[] clip) {
        double[][] rows = new double[][] {
                row(clip, 0), row(clip, 1), row(clip, 2), row(clip, 3)};
        double[][] planes = new double[6][4];
        int[][] pairs = {{3, 0}, {3, 0}, {3, 1}, {3, 1}, {3, 2}, {3, 2}};
        int[] signs = {+1, -1, +1, -1, +1, -1};
        for (int i = 0; i < 6; i++) {
            for (int k = 0; k < 4; k++) {
                planes[i][k] = rows[pairs[i][0]][k] + signs[i] * rows[pairs[i][1]][k];
            }
            double length = Math.sqrt(planes[i][0] * planes[i][0]
                    + planes[i][1] * planes[i][1] + planes[i][2] * planes[i][2]);
            if (!Double.isFinite(length) || length <= NORMAL_EPSILON) {
                return null;
            }
            for (int k = 0; k < 4; k++) {
                planes[i][k] /= length;
                if (!Double.isFinite(planes[i][k])) {
                    return null;
                }
            }
        }
        return planes;
    }

    private static double[] row(float[] clip, int row) {
        return new double[] {clip[row], clip[4 + row], clip[8 + row], clip[12 + row]};
    }

    /** The point mapping to the clip-space center: solve C·x = (0,0,0,1). */
    private static double[] clipCenter(float[] clip) {
        double[][] a = new double[4][5];
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                a[r][c] = clip[c * 4 + r];
            }
            a[r][4] = r == 3 ? 1d : 0d;
        }
        return solve(a);
    }

    /** Gaussian elimination with partial pivoting; null when singular. */
    private static double[] solve(double[][] augmented) {
        int n = 4;
        for (int col = 0; col < n; col++) {
            int pivot = col;
            for (int r = col + 1; r < n; r++) {
                if (Math.abs(augmented[r][col]) > Math.abs(augmented[pivot][col])) {
                    pivot = r;
                }
            }
            if (Math.abs(augmented[pivot][col]) < 1.0e-12d) {
                return null;
            }
            double[] tmp = augmented[col];
            augmented[col] = augmented[pivot];
            augmented[pivot] = tmp;
            for (int r = 0; r < n; r++) {
                if (r == col) {
                    continue;
                }
                double factor = augmented[r][col] / augmented[col][col];
                for (int c = col; c <= n; c++) {
                    augmented[r][c] -= factor * augmented[col][c];
                }
            }
        }
        double[] x = new double[n];
        for (int r = 0; r < n; r++) {
            x[r] = augmented[r][n] / augmented[r][r];
            if (!Double.isFinite(x[r])) {
                return null;
            }
        }
        return x;
    }

    /** §4.6 synthesis: light-facing base planes plus deduplicated silhouette planes. */
    private static ShadowFrustum synthesize(double[][] base, double[] center,
            double lx, double ly, double lz) {
        double[][] kept = new double[ShadowFrustum.CAPACITY][4];
        int keptCount = 0;
        double[] dots = new double[base.length];
        for (int i = 0; i < base.length; i++) {
            dots[i] = base[i][0] * lx + base[i][1] * ly + base[i][2] * lz;
            if (dots[i] >= 0d) {
                copyInto(base[i], kept, keptCount++);
            }
        }
        for (int i = 0; i < base.length; i++) {
            for (int j = i + 1; j < base.length; j++) {
                boolean adjacent = !(i == 0 && j == 1) && !(i == 2 && j == 3)
                        && !(i == 4 && j == 5);
                if (!adjacent) {
                    continue;
                }
                boolean opposite = (dots[i] >= 0d) != (dots[j] >= 0d);
                if (!opposite) {
                    continue;
                }
                double[] plus = dots[i] >= 0d ? base[i] : base[j];
                double[] minus = dots[i] >= 0d ? base[j] : base[i];
                double u = plus[0] * lx + plus[1] * ly + plus[2] * lz;
                double v = minus[0] * lx + minus[1] * ly + minus[2] * lz;
                double[] q = new double[4];
                for (int k = 0; k < 4; k++) {
                    q[k] = (-v) * plus[k] + u * minus[k];
                }
                double qLength = Math.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2]);
                if (!Double.isFinite(qLength) || qLength <= NORMAL_EPSILON) {
                    continue;
                }
                for (int k = 0; k < 4; k++) {
                    q[k] /= qLength;
                }
                orientToward(q, center);
                if (isDuplicate(q, kept, keptCount)) {
                    continue;
                }
                if (keptCount >= ShadowFrustum.CAPACITY) {
                    return ShadowFrustum.disabledCulling();
                }
                copyInto(q, kept, keptCount++);
            }
        }
        double[][] planes = new double[keptCount][];
        for (int i = 0; i < keptCount; i++) {
            planes[i] = kept[i];
        }
        return new ShadowFrustum(planes, false);
    }

    /** Flips the plane so the frustum center is on the inside. */
    private static void orientToward(double[] plane, double[] center) {
        double side = plane[0] * center[0] + plane[1] * center[1]
                + plane[2] * center[2] + plane[3];
        if (side < 0d) {
            for (int k = 0; k < 4; k++) {
                plane[k] = -plane[k];
            }
        }
    }

    /** Parallel-equal dedupe within the fixed epsilon. */
    private static boolean isDuplicate(double[] plane, double[][] kept, int count) {
        for (int i = 0; i < count; i++) {
            double dot = kept[i][0] * plane[0] + kept[i][1] * plane[1] + kept[i][2] * plane[2];
            if (dot > 1d - DEDUPE_COS_EPSILON) {
                double dScale = Math.max(1d, Math.max(Math.abs(kept[i][3]), Math.abs(plane[3])));
                if (Math.abs(kept[i][3] - plane[3]) <= DEDUPE_D_EPSILON * dScale) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void copyInto(double[] plane, double[][] target, int index) {
        target[index][0] = plane[0];
        target[index][1] = plane[1];
        target[index][2] = plane[2];
        target[index][3] = plane[3];
    }
}
