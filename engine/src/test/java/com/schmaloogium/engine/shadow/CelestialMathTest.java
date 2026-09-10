// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.frame.CameraSnapshot;
import com.schmaloogium.engine.frame.ShadowFrameView;
import com.schmaloogium.engine.uniforms.CelestialSample;
import com.schmaloogium.engine.uniforms.Double3;
import com.schmaloogium.engine.uniforms.Float3;
import com.schmaloogium.engine.uniforms.Matrix4Value;
import com.schmaloogium.engine.shadow.internal.ShadowMatrixOps;

import org.junit.jupiter.api.Test;

/** §4.5.1/§4.5.4 headless cases: boundaries, rejection, and the single vector formula. */
class CelestialMathTest {

    @Test
    void dayBoundariesFollowDoc() {
        ShadowCelestialAngles zero = CelestialMath.angles(0f);
        assertTrue(zero.day());
        assertEquals(0f, zero.shadowAngle());
        // a = 0.75, theta = -2pi*0.75
        assertEquals(-1.5d * Math.PI, zero.thetaRadians(), 1.0e-9);

        ShadowCelestialAngles quarter = CelestialMath.angles(0.25f);
        assertTrue(quarter.day());
        assertEquals(0.25f, quarter.shadowAngle());
        assertEquals(0d, quarter.thetaRadians(), 1.0e-9);

        // Exactly 0.5 is day with shadowAngle 0.5, not night/zero.
        ShadowCelestialAngles half = CelestialMath.angles(0.5f);
        assertTrue(half.day());
        assertEquals(0.5f, half.shadowAngle());
    }

    @Test
    void nightStartsAboveHalf() {
        float justAboveHalf = Math.nextUp(0.5f);
        ShadowCelestialAngles night = CelestialMath.angles(justAboveHalf);
        assertEquals(false, night.day());
        assertEquals(0f, night.shadowAngle(), 1.0e-7);

        ShadowCelestialAngles nearOne = CelestialMath.angles(0.999f);
        assertEquals(false, nearOne.day());
        assertEquals(0.499f, nearOne.shadowAngle(), 1.0e-6);
    }

    @Test
    void rejectsOutOfRangeSunAngle() {
        assertThrows(IllegalArgumentException.class, () -> CelestialMath.angles(-0.001f));
        assertThrows(IllegalArgumentException.class, () -> CelestialMath.angles(1.0f));
        assertThrows(IllegalArgumentException.class, () -> CelestialMath.angles(Float.NaN));
        assertThrows(IllegalArgumentException.class,
                () -> CelestialMath.angles(Float.POSITIVE_INFINITY));
    }

    @Test
    void sampleUsesMainRotationAndOwnFormula() {
        ShadowFrameView frame = new ShadowFrameView(100L, 7L, 0.5f, 1,
                new Double3(0, 0, 0), 0.25f, 0.3f);
        // A pure Rz(90°) main rotation: rotates world directions into eye space.
        float[] mainRotation = ShadowMatrixOps.rotationZ(Math.PI / 2.0d);
        CameraSnapshot camera = new CameraSnapshot(
                Matrix4Value.ofColumnMajor(mainRotation), Matrix4Value.identity());

        CelestialSample sample = CelestialMath.sample(frame, camera, 0.0f);

        // sunWorld = Ry(-90)*Rz(0)*Rx(sky*360)*(0,100,0); sky=0.25 -> Rx(90°).
        float[] sunWorld = ShadowMatrixOps.multiply(
                ShadowMatrixOps.rotationY(ShadowMatrixOps.radians(-90.0d)),
                ShadowMatrixOps.multiply(
                        ShadowMatrixOps.rotationZ(0.0d),
                        ShadowMatrixOps.rotationX(ShadowMatrixOps.radians(90.0d))));
        double[] sun = ShadowMatrixOps.transform(sunWorld, 0d, 100d, 0d, 0d);
        double[] rotated = ShadowMatrixOps.transform(mainRotation, sun[0], sun[1], sun[2], 0d);

        assertEquals((float) rotated[0], sample.sunPosition().x(), 1.0e-4);
        assertEquals((float) rotated[1], sample.sunPosition().y(), 1.0e-4);
        assertEquals((float) rotated[2], sample.sunPosition().z(), 1.0e-4);

        // Moon mirrors the sun; day selects the sun as the shadow light.
        assertEquals(-sample.sunPosition().x(), sample.moonPosition().x(), 1.0e-5);
        assertEquals(-sample.sunPosition().y(), sample.moonPosition().y(), 1.0e-5);
        assertEquals(-sample.sunPosition().z(), sample.moonPosition().z(), 1.0e-5);
        assertEquals(sample.sunPosition(), sample.shadowLightPosition());

        // Frame identity carried unchanged; w=0 never mixes in translation.
        assertEquals(100L, sample.worldEpoch());
        assertEquals(7L, sample.frameId());
    }

    @Test
    void nightSelectsMoonLight() {
        ShadowFrameView nightFrame = new ShadowFrameView(100L, 7L, 0.5f, 1,
                new Double3(0, 0, 0), 0.25f, 0.75f);
        CameraSnapshot camera = new CameraSnapshot(
                Matrix4Value.identity(), Matrix4Value.identity());
        CelestialSample sample = CelestialMath.sample(nightFrame, camera, 0.0f);
        assertEquals(sample.moonPosition(), sample.shadowLightPosition());
    }

    @Test
    void distinctRotationsGiveDistinctVectorsAtEqualTime() {
        ShadowFrameView frame = new ShadowFrameView(100L, 7L, 0.5f, 1,
                new Double3(0, 0, 0), 0.25f, 0.3f);
        CameraSnapshot identity = new CameraSnapshot(
                Matrix4Value.identity(), Matrix4Value.identity());
        CameraSnapshot rotated = new CameraSnapshot(
                Matrix4Value.ofColumnMajor(ShadowMatrixOps.rotationZ(1.234d)),
                Matrix4Value.identity());
        CelestialSample a = CelestialMath.sample(frame, identity, 0.0f);
        CelestialSample b = CelestialMath.sample(frame, rotated, 0.0f);
        assertTrue(distance(a.sunPosition(), b.sunPosition()) > 1.0);
    }

    @Test
    void rejectsNullAndInvalidInputs() {
        ShadowFrameView frame = new ShadowFrameView(100L, 7L, 0.5f, 1,
                new Double3(0, 0, 0), 0.25f, 0.3f);
        CameraSnapshot camera = new CameraSnapshot(
                Matrix4Value.identity(), Matrix4Value.identity());
        assertThrows(IllegalArgumentException.class, () -> CelestialMath.sample(null, camera, 0f));
        assertThrows(IllegalArgumentException.class, () -> CelestialMath.sample(frame, null, 0f));
        assertThrows(IllegalArgumentException.class,
                () -> CelestialMath.sample(frame, camera, Float.NaN));
    }

    private static double distance(Float3 a, Float3 b) {
        double dx = a.x() - b.x();
        double dy = a.y() - b.y();
        double dz = a.z() - b.z();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
