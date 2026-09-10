// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.spi;

import com.schmaloogium.engine.uniforms.Double3;
import com.schmaloogium.engine.uniforms.Float3;
import com.schmaloogium.engine.uniforms.Int2;
import com.schmaloogium.engine.uniforms.OptionalFloat;
import com.schmaloogium.engine.uniforms.OptionalValue;
import com.schmaloogium.engine.uniforms.FogSample;

import java.util.Objects;

/**
 * The frame-acquisition sample (PHASE_6_DOC §4.2). Must echo the request's world/frame
 * identity; vectors and floats are finite; {@code eyeBrightness} components are 0…240;
 * {@code isEyeInWater} is 0, 1 or 2; effect strengths and screen brightness are in [0,1];
 * {@code farPlane} is non-negative; {@code sunAngle} and a present {@code shadowAngle}
 * are in [0,1). {@code fogFallback} is absent only when no valid frame fallback exists.
 * Immutable.
 */
public record FrameUniformSample(
        long worldEpoch,
        long frameId,
        Double3 cameraPosition,
        float eyeAltitude,
        Int2 eyeBrightness,
        int isEyeInWater,
        float nightVision,
        float blindness,
        float screenBrightness,
        boolean hideGui,
        float farPlane,
        float sunAngle,
        OptionalFloat shadowAngle,
        Float3 skyColor,
        OptionalValue<FogSample> fogFallback) {

    public FrameUniformSample {
        Objects.requireNonNull(cameraPosition, "cameraPosition");
        Objects.requireNonNull(eyeBrightness, "eyeBrightness");
        Objects.requireNonNull(shadowAngle, "shadowAngle");
        Objects.requireNonNull(skyColor, "skyColor");
        Objects.requireNonNull(fogFallback, "fogFallback");
        if (eyeBrightness.x() < 0 || eyeBrightness.x() > 240
                || eyeBrightness.y() < 0 || eyeBrightness.y() > 240) {
            throw new IllegalArgumentException("eyeBrightness components must be in 0..240: "
                    + eyeBrightness);
        }
        if (isEyeInWater < 0 || isEyeInWater > 2) {
            throw new IllegalArgumentException("isEyeInWater must be 0, 1 or 2: " + isEyeInWater);
        }
        requireUnitInterval(nightVision, "nightVision");
        requireUnitInterval(blindness, "blindness");
        requireUnitInterval(screenBrightness, "screenBrightness");
        if (!Float.isFinite(farPlane) || farPlane < 0f) {
            throw new IllegalArgumentException("farPlane must be finite and >= 0: " + farPlane);
        }
        requireAngle(sunAngle, "sunAngle");
        if (shadowAngle instanceof OptionalFloat.Present present) {
            requireAngle(present.value(), "shadowAngle");
        }
    }

    private static void requireUnitInterval(float value, String name) {
        if (!Float.isFinite(value) || value < 0f || value > 1f) {
            throw new IllegalArgumentException(name + " must be finite in [0,1]: " + value);
        }
    }

    private static void requireAngle(float value, String name) {
        if (!Float.isFinite(value) || value < 0f || value >= 1f) {
            throw new IllegalArgumentException(name + " must be finite in [0,1): " + value);
        }
    }
}
