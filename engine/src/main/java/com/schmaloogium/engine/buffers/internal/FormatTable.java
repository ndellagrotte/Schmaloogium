// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import com.schmaloogium.engine.buffers.ClearRequest;
import com.schmaloogium.engine.buffers.ResourceClearPolicy;
import com.schmaloogium.engine.gl.ColorInternalFormat;
import com.schmaloogium.engine.gl.PixelFormat;
import com.schmaloogium.engine.gl.PixelLayout;
import com.schmaloogium.engine.gl.PixelType;
import com.schmaloogium.engine.gl.TextureMagFilter;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * The single Phase-5 format legality table (PHASE_5_DOC §4.2, D-P5-42, D-P5-46): for every
 * {@link ColorInternalFormat} — the 37 pack-facing values plus private {@code RGBA_COMPAT} —
 * this closes the synchronous allocation transfer layout, the numeric class, the base
 * filter, and the typed clear-value conversion. P13/P1 consume this policy rather than
 * implementing a second parser.
 */
public final class FormatTable {

    /** Numeric class of one color storage format (D-P5-46 conversion rows). */
    enum NumericClass {
        NORMALIZED_FIXED_POINT,
        SIGNED_FLOAT,
        UNSIGNED_PACKED_FLOAT,
        SHARED_EXPONENT,
        SIGNED_INTEGER,
        UNSIGNED_INTEGER
    }

    /** One format's complete policy row. */
    record FormatRow(PixelLayout.Color allocationLayout, NumericClass numericClass,
            TextureMagFilter baseFilter) {
    }

    /** Converted typed clear payload, class-tagged so signed/unsigned/floating never alias. */
    sealed interface ConvertedClear {
        record Floating(float r, float g, float b, float a) implements ConvertedClear {
        }

        record Signed(int r, int g, int b, int a) implements ConvertedClear {
        }

        record Unsigned(long r, long g, long b, long a) implements ConvertedClear {
        }
    }

    private static final Map<ColorInternalFormat, FormatRow> ROWS = build();

    private FormatTable() {
    }

    static FormatRow row(ColorInternalFormat format) {
        return Objects.requireNonNull(ROWS.get(format), () -> "no row for " + format);
    }

    static boolean isIntegerStorage(ColorInternalFormat format) {
        NumericClass numericClass = row(format).numericClass();
        return numericClass == NumericClass.SIGNED_INTEGER
            || numericClass == NumericClass.UNSIGNED_INTEGER;
    }

    /**
     * The whole-estate fallback allocation: plain unsized RGBA, normalized fixed point
     * (§4.7; private {@code RGBA_COMPAT} representation).
     */
    static ColorInternalFormat fallbackFormat() {
        return ColorInternalFormat.RGBA_COMPAT;
    }

    /**
     * D-P5-46 conversion table. The payload is resolved from the frame's {@link ClearRequest}
     * (default colors) or the pack's explicit override BEFORE this call; conversion is by
     * numeric class:
     * <ul>
     * <li>normalized fixed point: clamp to [0,1] per channel (-0.0 to +0.0);</li>
     * <li>floating: signed clamp per channel — 16F channels saturate to ±65504, absent
     * channels and all 32F channels to ±Float.MAX_VALUE; packed/shared-exponent RGB uses its
     * own representable maxima;</li>
     * <li>signed integer: promote to double, truncate toward zero, saturate to
     * [-2147483648, 2147483647];</li>
     * <li>unsigned integer: truncate toward zero, saturate to [0, 4294967295].</li>
     * </ul>
     */
    static ConvertedClear convert(ColorInternalFormat realizedFormat, double r, double g,
            double b, double a) {
        FormatRow formatRow = row(realizedFormat);
        return switch (formatRow.numericClass()) {
            case NORMALIZED_FIXED_POINT -> new ConvertedClear.Floating(
                clampNormalized(r), clampNormalized(g), clampNormalized(b), clampNormalized(a));
            case SIGNED_FLOAT -> {
                // D-P5-46: clamp each PRESENT channel to the destination's largest finite
                // magnitude (65504 for 16F, Float.MAX_VALUE for 32F); absent channels and
                // the 32F rule coincide at Float.MAX_VALUE.
                float magnitude = realizedFormat.name().endsWith("16F")
                    ? 65504.0f : Float.MAX_VALUE;
                float alphaMagnitude = realizedFormat.name().startsWith("RGBA")
                    ? magnitude : Float.MAX_VALUE;
                yield new ConvertedClear.Floating(
                    clampFloat(r, magnitude), clampFloat(g, magnitude),
                    clampFloat(b, magnitude), clampFloat(a, alphaMagnitude));
            }
            case UNSIGNED_PACKED_FLOAT -> new ConvertedClear.Floating(
                clampUnsignedFloat(r, 65024.0f), clampUnsignedFloat(g, 65024.0f),
                clampUnsignedFloat(b, 64512.0f), clampFloat(a, Float.MAX_VALUE));
            case SHARED_EXPONENT -> new ConvertedClear.Floating(
                clampUnsignedFloat(r, 65408.0f), clampUnsignedFloat(g, 65408.0f),
                clampUnsignedFloat(b, 65408.0f), clampFloat(a, Float.MAX_VALUE));
            case SIGNED_INTEGER -> new ConvertedClear.Signed(
                clampSigned(r), clampSigned(g), clampSigned(b), clampSigned(a));
            case UNSIGNED_INTEGER -> new ConvertedClear.Unsigned(
                clampUnsigned(r), clampUnsigned(g), clampUnsigned(b), clampUnsigned(a));
        };
    }

    /** Rejection when any payload component is NaN or infinite (§4.6). */
    static boolean rejectable(double r, double g, double b, double a) {
        return !(Double.isFinite(r) && Double.isFinite(g) && Double.isFinite(b)
            && Double.isFinite(a));
    }

    /**
     * Resolves the four payload components for one buffer: the pack's explicit constant
     * override when present, else the frame default colors per §4.6 — the FogRgbAlphaOne
     * policy is default colortex0 only, so fog RGB with alpha 1.0.
     */
    static double[] resolvePayload(ResourceClearPolicy policy, ClearRequest request) {
        if (policy instanceof ResourceClearPolicy.Constant constant) {
            return new double[] {constant.r(), constant.g(), constant.b(), constant.a()};
        }
        return new double[] {request.fogRed(), request.fogGreen(), request.fogBlue(), 1.0};
    }

    private static float clampNormalized(double value) {
        float clamped = (float) Math.min(1.0, Math.max(0.0, value));
        return clamped == 0.0f ? 0.0f : clamped;
    }

    private static float clampFloat(double value, float limit) {
        double bounded = Math.min(limit, Math.max(-limit, value));
        float clamped = (float) bounded;
        return clamped == 0.0f ? 0.0f : clamped;
    }

    private static float clampUnsignedFloat(double value, float limit) {
        double bounded = Math.min(limit, Math.max(0.0, value));
        float clamped = (float) bounded;
        return clamped == 0.0f ? 0.0f : clamped;
    }

    private static int clampSigned(double value) {
        if (Double.isNaN(value)) {
            return 0;
        }
        if (value >= 2147483647.0) {
            return Integer.MAX_VALUE;
        }
        if (value <= -2147483648.0) {
            return Integer.MIN_VALUE;
        }
        return (int) value; // truncation toward zero
    }

    private static long clampUnsigned(double value) {
        if (Double.isNaN(value)) {
            return 0L;
        }
        if (value >= 4294967295.0) {
            return 4294967295L;
        }
        if (value <= 0.0) {
            return 0L;
        }
        return (long) value; // truncation toward zero
    }

    private static Map<ColorInternalFormat, FormatRow> build() {
        Map<ColorInternalFormat, FormatRow> rows = new EnumMap<>(ColorInternalFormat.class);
        PixelLayout.Color normalized = new PixelLayout.Color(PixelFormat.BGRA,
            PixelType.UNSIGNED_INT_8_8_8_8_REV);
        PixelLayout.Color integer = new PixelLayout.Color(PixelFormat.RGBA_INTEGER,
            PixelType.UNSIGNED_INT_8_8_8_8_REV);

        // Normalized fixed point: the 8/16-bit un/snorm families, the mixed normalized
        // formats, and private RGBA_COMPAT (D-P5-46).
        for (ColorInternalFormat format : new ColorInternalFormat[] {
            ColorInternalFormat.R8, ColorInternalFormat.RG8, ColorInternalFormat.RGB8,
            ColorInternalFormat.RGBA8, ColorInternalFormat.R8_SNORM,
            ColorInternalFormat.RG8_SNORM, ColorInternalFormat.RGB8_SNORM,
            ColorInternalFormat.RGBA8_SNORM, ColorInternalFormat.R16,
            ColorInternalFormat.RG16, ColorInternalFormat.RGB16, ColorInternalFormat.RGBA16,
            ColorInternalFormat.R16_SNORM, ColorInternalFormat.RG16_SNORM,
            ColorInternalFormat.RGB16_SNORM, ColorInternalFormat.RGBA16_SNORM,
            ColorInternalFormat.R3_G3_B2, ColorInternalFormat.RGB5_A1,
            ColorInternalFormat.RGB10_A2, ColorInternalFormat.RGBA_COMPAT}) {
            rows.put(format, new FormatRow(normalized, NumericClass.NORMALIZED_FIXED_POINT,
                TextureMagFilter.LINEAR));
        }

        // Signed float families.
        for (ColorInternalFormat format : new ColorInternalFormat[] {
            ColorInternalFormat.R16F, ColorInternalFormat.RG16F,
            ColorInternalFormat.RGB16F, ColorInternalFormat.RGBA16F,
            ColorInternalFormat.R32F, ColorInternalFormat.RG32F,
            ColorInternalFormat.RGB32F, ColorInternalFormat.RGBA32F}) {
            rows.put(format, new FormatRow(normalized, NumericClass.SIGNED_FLOAT,
                TextureMagFilter.LINEAR));
        }

        // Integer storage: RGBA_INTEGER transfer + UNSIGNED_INT_8_8_8_8_REV scalar words,
        // NEAREST filter (D-P5-42).
        for (ColorInternalFormat format : new ColorInternalFormat[] {
            ColorInternalFormat.R32I, ColorInternalFormat.RG32I,
            ColorInternalFormat.RGB32I, ColorInternalFormat.RGBA32I,
            ColorInternalFormat.R32UI, ColorInternalFormat.RG32UI,
            ColorInternalFormat.RGB32UI, ColorInternalFormat.RGBA32UI}) {
            rows.put(format, new FormatRow(integer, integerStorageClass(format),
                TextureMagFilter.NEAREST));
        }

        // Mixed packed float / shared exponent.
        rows.put(ColorInternalFormat.R11F_G11F_B10F,
            new FormatRow(normalized, NumericClass.UNSIGNED_PACKED_FLOAT,
                TextureMagFilter.LINEAR));
        rows.put(ColorInternalFormat.RGB9_E5,
            new FormatRow(normalized, NumericClass.SHARED_EXPONENT, TextureMagFilter.LINEAR));
        return java.util.Collections.unmodifiableMap(rows);
    }

    private static NumericClass integerStorageClass(ColorInternalFormat format) {
        // Only the *32I family is signed; *32UI must not match a bare endsWith("I").
        return format.name().endsWith("32I")
            ? NumericClass.SIGNED_INTEGER
            : NumericClass.UNSIGNED_INTEGER;
    }
}
