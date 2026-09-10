// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import com.schmaloogium.engine.frame.CameraSnapshot;
import com.schmaloogium.engine.frame.ShadowFrameView;
import com.schmaloogium.engine.uniforms.CelestialSample;
import com.schmaloogium.engine.uniforms.Float3;
import com.schmaloogium.engine.shadow.internal.ShadowMatrixOps;

import java.util.Objects;

/**
 * The public pure celestial producer (PHASE_8_DOC §4.5.1/§4.5.4, D-P8-22/D-P8-38):
 * plan-independent, shadow-availability-independent, and the sole owner of both
 * formulas. Phase 7's pre-camera provider and main H-SKY-02 route and Phase 8's camera
 * compute all call these statics; neither requires an instance, a ready plan, request or
 * enablement state, an estate, an extent, hook health, GL or a retained world.
 *
 * <p>{@link #angles} is total over the validated domain {@code sunAngle ∈ [0,1)} and
 * rejects everything else with {@code IllegalArgumentException} before returning — no
 * clamp, wrap or substituted previous value. {@link #sample} owns the unchanged Phase 6
 * celestial-vector event construction from the actual post-camera main model-view.
 */
public final class CelestialMath {

    private CelestialMath() {
    }

    /**
     * §4.5.1: day includes the boundary ({@code s == 0.5} is day with
     * {@code shadowAngle == 0.5}); {@code theta = -2π·a} with
     * {@code a = shadowAngle < 0.25 ? shadowAngle + 0.75 : shadowAngle - 0.25}.
     */
    public static ShadowCelestialAngles angles(float sunAngle) {
        if (!Float.isFinite(sunAngle) || sunAngle < 0f || sunAngle >= 1f) {
            throw new IllegalArgumentException(
                    "sunAngle must be finite in [0,1): " + sunAngle);
        }
        boolean day = sunAngle <= 0.5f;
        float shadowAngle = day ? sunAngle : sunAngle - 0.5f;
        float a = shadowAngle < 0.25f ? shadowAngle + 0.75f : shadowAngle - 0.25f;
        double theta = -2.0d * Math.PI * (double) a;
        return new ShadowCelestialAngles(day, shadowAngle, theta);
    }

    /**
     * §4.5.4: the four w=0 eye vectors from the supplied main model-view — never the
     * shadow model-view, an identity placeholder or a current GL query. Only xyz are
     * submitted; the main translation contributes nothing through w=0 while its rotation
     * still affects the result.
     */
    public static CelestialSample sample(
            ShadowFrameView frame, CameraSnapshot mainCamera, float sunPathRotationDegrees) {
        requireNonNull(frame, "frame");
        requireNonNull(mainCamera, "mainCamera");
        if (!Float.isFinite(sunPathRotationDegrees)) {
            throw new IllegalArgumentException("sunPathRotationDegrees must be finite");
        }
        double skyAngle = frame.skyAngle();
        if (!Double.isFinite(skyAngle) || skyAngle < 0d || skyAngle >= 1d) {
            throw new IllegalArgumentException("skyAngle must be finite in [0,1)");
        }
        ShadowCelestialAngles angles = angles(frame.sunAngle());

        float[] sunWorld = ShadowMatrixOps.multiply(
                ShadowMatrixOps.rotationY(ShadowMatrixOps.radians(-90.0d)),
                ShadowMatrixOps.multiply(
                        ShadowMatrixOps.rotationZ(ShadowMatrixOps.radians(sunPathRotationDegrees)),
                        ShadowMatrixOps.rotationX(ShadowMatrixOps.radians(skyAngle * 360.0d))));
        double[] sun = ShadowMatrixOps.transform(sunWorld, 0d, 100d, 0d, 0d);
        double[] mainRotation = rotationOf(mainCamera.modelView());
        double[] sunPosition = transformDirection(mainRotation, sun);
        double[] moonPosition = new double[] {-sunPosition[0], -sunPosition[1], -sunPosition[2]};
        double[] up = transformDirection(mainRotation,
                ShadowMatrixOps.transform(ShadowMatrixOps.rotationY(ShadowMatrixOps.radians(-90.0d)),
                        0d, 100d, 0d, 0d));

        Float3 sunPositionV = requireFinite(sunPosition, "sunPosition");
        Float3 moonPositionV = requireFinite(moonPosition, "moonPosition");
        Float3 shadowLight = angles.day() ? sunPositionV : moonPositionV;
        Float3 upPosition = requireFinite(up, "upPosition");
        return new CelestialSample(frame.worldEpoch(), frame.frameId(),
                sunPositionV, moonPositionV, shadowLight, upPosition);
    }

    private static void requireNonNull(Object value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
    }

    private static double[] rotationOf(com.schmaloogium.engine.uniforms.Matrix4Value matrix) {
        // The w=0 direction transform needs only the linear part; translation columns
        // cannot contribute.
        float[] m = matrix.toColumnMajorArray();
        return new double[] {
                m[0], m[4], m[8],
                m[1], m[5], m[9],
                m[2], m[6], m[10]};
    }

    private static double[] transformDirection(double[] r3, double[] v) {
        return new double[] {
                r3[0] * v[0] + r3[1] * v[1] + r3[2] * v[2],
                r3[3] * v[0] + r3[4] * v[1] + r3[5] * v[2],
                r3[6] * v[0] + r3[7] * v[1] + r3[8] * v[2]};
    }

    private static Float3 requireFinite(double[] v, String name) {
        for (double component : v) {
            if (!Double.isFinite(component)) {
                throw new IllegalArgumentException(name + " must be finite");
            }
        }
        return new Float3((float) v[0], (float) v[1], (float) v[2]);
    }
}
