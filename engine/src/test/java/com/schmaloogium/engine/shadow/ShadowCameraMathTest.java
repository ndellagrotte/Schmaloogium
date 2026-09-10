// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.buffers.BufferIndex;
import com.schmaloogium.engine.buffers.Extent2i;
import com.schmaloogium.engine.buffers.LogicalBuffer;
import com.schmaloogium.engine.buffers.ShadowMipmapPolicy;
import com.schmaloogium.engine.frame.CameraSnapshot;
import com.schmaloogium.engine.frame.ShadowFrameView;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.shadow.internal.ShadowMatrixOps;
import com.schmaloogium.engine.shadow.internal.ShadowPlanFactoryImpl;
import com.schmaloogium.engine.uniforms.Double3;
import com.schmaloogium.engine.uniforms.Float3;
import com.schmaloogium.engine.uniforms.Matrix4Value;
import com.schmaloogium.engine.uniforms.OptionalFloat;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

/** §4.5.2/§4.5.3/§4.5.5/§4.6 headless cases: exact matrices, snapping, synthesis. */
class ShadowCameraMathTest {

    private static final ShadowCameraMath MATH = ShadowCameraMath.shared();

    private static final CameraSnapshot CAMERA =
            new CameraSnapshot(Matrix4Value.identity(), Matrix4Value.identity());

    private static ShadowPolicy policy(float fovPresent, float distance, float renderMul,
            float interval) {
        return new ShadowPolicy(
                fovPresent < 0f ? new OptionalFloat.Absent() : new OptionalFloat.Present(fovPresent),
                distance, renderMul, interval, 0f, true, false,
                new ShadowMipmapPolicy(List.of()),
                new ShadowPcfPolicy(Set.of(
                        new LogicalBuffer(BufferDomain.SHADOWTEX, new BufferIndex(0)))));
    }

    private static ShadowPlan plan(ShadowPolicy policy) {
        ShadowHookHealth health = ShadowHookHealth.of(ShadowHookHealth.catalogue().stream()
                .map(id -> new ShadowHookRow(id, 1, 1, HookDisposition.HEALTHY)).toList());
        ShadowPlanResult result = new ShadowPlanFactoryImpl()
                .plan(new ShadowPlanInput(policy, health, true));
        return assertInstanceOf(ShadowPlanResult.Ready.class, result).plan();
    }

    private static ShadowFrameView frame(double camX, double camY, double camZ) {
        return new ShadowFrameView(100L, 7L, 0.5f, 1,
                new Double3(camX, camY, camZ), 0.25f, 0.3f);
    }

    @Test
    void orthoProjectionMatchesDocMatrix() {
        // H = 32: [0]=1/32; [10]=-2/(F-N); [14]=-(F+N)/(F-N); [15]=1; row3 otherwise 0.
        ShadowCameraProjection p = MATH.compute(frame(0, 0, 0), CAMERA,
                plan(policy(-1f, 32f, 1f, 1f)), new Extent2i(16, 16));
        float[] m = p.projection().toColumnMajorArray();
        assertEquals(1f / 32f, m[0], 1.0e-7);
        assertEquals(1f / 32f, m[5], 1.0e-7);
        assertEquals(-2f / 255.95f, m[10], 1.0e-4);
        assertEquals(-256.05f / 255.95f, m[14], 1.0e-4);
        assertEquals(0f, m[11]);
        assertEquals(1f, m[15]);
        assertEquals(0f, m[3]);
        assertEquals(0f, m[7]);
    }

    @Test
    void orthoSnappingFollowsSignedRemainder() {
        ShadowCameraProjection snapped = MATH.compute(frame(-33.25, 0, 0), CAMERA,
                plan(policy(-1f, 32f, 1f, 16f)), new Extent2i(16, 16));
        ShadowCameraProjection base = MATH.compute(frame(0, 0, 0), CAMERA,
                plan(policy(-1f, 32f, 1f, 16f)), new Extent2i(16, 16));
        assertFalse(Arrays.equals(snapped.modelView().toColumnMajorArray(),
                base.modelView().toColumnMajorArray()));
        // Exact doc rule: M(camera) = M(origin-camera) * T(dOx, dOy, dOz) where
        // o = (float)c % I - I/2 with signed Java float remainder.
        float dOx = rem(-33.25f, 16f) - rem(0f, 16f);
        float dOy = rem(0f, 16f) - rem(0f, 16f);
        float dOz = rem(0f, 16f) - rem(0f, 16f);
        assertEquals(-1.25f, dOx, 1.0e-6);
        float[] expected = ShadowMatrixOps.multiply(
                base.modelView().toColumnMajorArray(),
                ShadowMatrixOps.translation(dOx, dOy, dOz));
        assertArrayEquals(expected, snapped.modelView().toColumnMajorArray(), 1.0e-4f);
    }

    @Test
    void perspectiveBranchUsesFovAndNeverSnaps() {
        ShadowCameraProjection p = MATH.compute(frame(-33.25, 0, 0), CAMERA,
                plan(policy(90f, 32f, 1f, 16f)), new Extent2i(16, 16));
        float[] m = p.projection().toColumnMajorArray();
        double q = 1d / Math.tan(Math.toRadians(90d) / 2d);
        assertEquals((float) q, m[0], 1.0e-6);
        assertEquals((float) q, m[5], 1.0e-6);
        assertEquals(0f, m[15]);
        // Perspective uses M0 without the snap translation.
        ShadowCameraProjection base = MATH.compute(frame(0, 0, 0), CAMERA,
                plan(policy(90f, 32f, 1f, 16f)), new Extent2i(16, 16));
        assertTrue(Arrays.equals(p.modelView().toColumnMajorArray(),
                base.modelView().toColumnMajorArray()));
    }

    @Test
    void lightDirectionFollowsInverseRotationFormula() {
        ShadowPlan plan = plan(policy(-1f, 32f, 1f, 1f));
        ShadowCameraProjection p = MATH.compute(frame(0, 0, 0), CAMERA, plan,
                new Extent2i(16, 16));
        double[] light = ShadowMatrixOps.transform(
                p.modelView().toColumnMajorArray(), 0d, 0d, 1d, 0d);
        // light = normalize(M^T * (0,0,1)) = normalize(third row of M).
        float[] m = p.modelView().toColumnMajorArray();
        Float3 expected = ShadowMatrixOps.normalize(m[8], m[9], m[10]);
        assertEquals(expected.x(), p.lightDirectionWorld().x(), 1.0e-6);
        assertEquals(expected.y(), p.lightDirectionWorld().y(), 1.0e-6);
        assertEquals(expected.z(), p.lightDirectionWorld().z(), 1.0e-6);
        assertEquals(0d, light[3]);
    }

    @Test
    void frustumFromOrthoCameraIsNormalizedAndLightFacing() {
        ShadowCameraProjection p = MATH.compute(frame(0, 0, 0), CAMERA,
                plan(policy(-1f, 32f, 1f, 1f)), new Extent2i(16, 16));
        ShadowFrustum frustum = MATH.frustum(p);
        assertFalse(frustum.cullingDisabled());
        assertTrue(frustum.planes().length >= 6);
        assertTrue(frustum.planes().length <= ShadowFrustum.CAPACITY);
        Float3 light = p.lightDirectionWorld();
        for (double[] plane : frustum.planes()) {
            double normalLength = Math.sqrt(plane[0] * plane[0] + plane[1] * plane[1]
                    + plane[2] * plane[2]);
            assertEquals(1d, normalLength, 1.0e-6);
            assertTrue(plane[0] * light.x() + plane[1] * light.y() + plane[2] * light.z()
                    >= -1.0e-9);
        }
    }

    @Test
    void aabbTestRejectsOnlyOutsideBoxes() {
        ShadowCameraProjection p = MATH.compute(frame(0, 0, 0), CAMERA,
                plan(policy(-1f, 32f, 1f, 1f)), new Extent2i(16, 16));
        ShadowFrustum frustum = MATH.frustum(p);
        // The clip-space center maps back into the frustum interior through M^-1.
        float[] invF = toFloat(invert(p.modelView().toColumnMajorArray()));
        double[] center = ShadowMatrixOps.transform(invF, 0d, 0d, 0d, 1d);
        ShadowAabb around = new ShadowAabb(
                center[0] - 2, center[1] - 2, center[2] - 2,
                center[0] + 2, center[1] + 2, center[2] + 2);
        assertTrue(frustum.intersects(around));
        // A box displaced far toward the shadow camera (past its near plane) is out.
        Float3 light = p.lightDirectionWorld();
        ShadowAabb behind = new ShadowAabb(
                center[0] - light.x() * 10_000f - 2, center[1] - light.y() * 10_000f - 2,
                center[2] - light.z() * 10_000f - 2,
                center[0] - light.x() * 10_000f + 2, center[1] - light.y() * 10_000f + 2,
                center[2] - light.z() * 10_000f + 2);
        assertFalse(frustum.intersects(behind));
    }

    @Test
    void degenerateClipDisablesCulling() {
        // Projection collapsing every row onto w keeps all base planes zero-length:
        // the conservative fallback must disable culling entirely.
        float[] collapsed = new float[16];
        collapsed[15] = 1f;
        ShadowCameraProjection degenerate = new ShadowCameraProjection(
                Matrix4Value.ofColumnMajor(collapsed), Matrix4Value.identity(),
                new Float3(0f, 1f, 0f), 0d);
        ShadowFrustum frustum = MATH.frustum(degenerate);
        assertTrue(frustum.cullingDisabled());
        assertEquals(0, frustum.planes().length);
        assertTrue(frustum.intersects(new ShadowAabb(-1, -1, -1, 1, 1, 1)));
    }

    @Test
    void computeRejectsInvalidInputs() {
        ShadowPlan plan = plan(policy(-1f, 32f, 1f, 1f));
        assertThrows(IllegalArgumentException.class,
                () -> MATH.compute(null, CAMERA, plan, new Extent2i(16, 16)));
        assertThrows(IllegalArgumentException.class,
                () -> MATH.compute(frame(0, 0, 0), null, plan, new Extent2i(16, 16)));
        assertThrows(IllegalArgumentException.class,
                () -> MATH.compute(frame(0, 0, 0), CAMERA, null, new Extent2i(16, 16)));
        assertThrows(IllegalArgumentException.class,
                () -> MATH.compute(frame(0, 0, 0), CAMERA, plan, new Extent2i(0, 16)));
    }

    /** Java signed float remainder, mirroring the implementation's snap arithmetic. */
    private static float rem(float a, float b) {
        return a % b;
    }

    private static float[] toFloat(double[] v) {
        float[] out = new float[v.length];
        for (int i = 0; i < v.length; i++) {
            out[i] = (float) v[i];
        }
        return out;
    }

    /** Inverts a 4x4 column-major matrix by Gauss-Jordan (test helper). */
    private static double[] invert(float[] m) {
        double[][] a = new double[4][8];
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 4; row++) {
                a[row][col] = m[col * 4 + row];
                a[row][4 + col] = row == col ? 1 : 0;
            }
        }
        for (int pivot = 0; pivot < 4; pivot++) {
            int best = pivot;
            for (int r = pivot + 1; r < 4; r++) {
                if (Math.abs(a[r][pivot]) > Math.abs(a[best][pivot])) {
                    best = r;
                }
            }
            double[] tmp = a[pivot];
            a[pivot] = a[best];
            a[best] = tmp;
            double d = a[pivot][pivot];
            for (int c = 0; c < 8; c++) {
                a[pivot][c] /= d;
            }
            for (int r = 0; r < 4; r++) {
                if (r == pivot) {
                    continue;
                }
                double f = a[r][pivot];
                for (int c = 0; c < 8; c++) {
                    a[r][c] -= f * a[pivot][c];
                }
            }
        }
        double[] inv = new double[16];
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 4; row++) {
                inv[col * 4 + row] = a[row][4 + col];
            }
        }
        return inv;
    }
}
