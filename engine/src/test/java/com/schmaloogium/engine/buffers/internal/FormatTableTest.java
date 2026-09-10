// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.buffers.ClearRequest;
import com.schmaloogium.engine.buffers.ResourceClearPolicy;
import com.schmaloogium.engine.gl.ColorInternalFormat;
import com.schmaloogium.engine.gl.PixelFormat;
import com.schmaloogium.engine.gl.PixelLayout;
import com.schmaloogium.engine.gl.PixelType;
import com.schmaloogium.engine.gl.TextureMagFilter;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * Phase-5 format legality table (PHASE_5_DOC §4.2, D-P5-42, D-P5-46): every pack-facing
 * value plus the private RGBA_COMPAT fallback has a row, numeric classes and transfer
 * layouts agree per family, the D-P5-46 conversion rows clamp/saturate/truncate as
 * documented, non-finite payloads are rejectable, and clear-payload resolution prefers
 * the pack constant over frame fog. Pure policy; no GL anywhere.
 */
class FormatTableTest {

    @Test
    void everyFormatHasARowWithClassConsistentTransferAndFilter() {
        long integerRows = 0;
        for (ColorInternalFormat format : ColorInternalFormat.values()) {
            FormatTable.FormatRow row = FormatTable.row(format);
            if (FormatTable.isIntegerStorage(format)) {
                integerRows++;
                assertEquals(TextureMagFilter.NEAREST, row.baseFilter(), format::toString);
                assertEquals(new PixelLayout.Color(PixelFormat.RGBA_INTEGER,
                    PixelType.UNSIGNED_INT_8_8_8_8_REV), row.allocationLayout(), format::toString);
                assertTrue(row.numericClass() == FormatTable.NumericClass.SIGNED_INTEGER
                    || row.numericClass() == FormatTable.NumericClass.UNSIGNED_INTEGER,
                    format::toString);
            } else {
                assertEquals(TextureMagFilter.LINEAR, row.baseFilter(), format::toString);
                assertEquals(new PixelLayout.Color(PixelFormat.BGRA,
                    PixelType.UNSIGNED_INT_8_8_8_8_REV), row.allocationLayout(), format::toString);
                assertFalse(row.numericClass() == FormatTable.NumericClass.SIGNED_INTEGER
                    || row.numericClass() == FormatTable.NumericClass.UNSIGNED_INTEGER,
                    format::toString);
            }
        }
        assertEquals(8, integerRows, "exactly the eight RGBA32{I,UI} rows are integer storage");
    }

    @Test
    void numericClassesFollowTheDocumentedFormatFamilies() {
        assertEquals(FormatTable.NumericClass.NORMALIZED_FIXED_POINT,
            FormatTable.row(ColorInternalFormat.RGBA8).numericClass());
        assertEquals(FormatTable.NumericClass.NORMALIZED_FIXED_POINT,
            FormatTable.row(ColorInternalFormat.RGBA_COMPAT).numericClass());
        assertEquals(FormatTable.NumericClass.SIGNED_FLOAT,
            FormatTable.row(ColorInternalFormat.RGBA16F).numericClass());
        assertEquals(FormatTable.NumericClass.UNSIGNED_PACKED_FLOAT,
            FormatTable.row(ColorInternalFormat.R11F_G11F_B10F).numericClass());
        assertEquals(FormatTable.NumericClass.SHARED_EXPONENT,
            FormatTable.row(ColorInternalFormat.RGB9_E5).numericClass());
        assertEquals(FormatTable.NumericClass.SIGNED_INTEGER,
            FormatTable.row(ColorInternalFormat.RGBA32I).numericClass());
        assertEquals(FormatTable.NumericClass.UNSIGNED_INTEGER,
            FormatTable.row(ColorInternalFormat.RGBA32UI).numericClass());
        assertSame(ColorInternalFormat.RGBA_COMPAT, FormatTable.fallbackFormat());
    }

    @Test
    void normalizedConversionClampsToUnitRangeAndNormalizesNegativeZero() {
        FormatTable.ConvertedClear.Floating floating = assertInstanceOf(
            FormatTable.ConvertedClear.Floating.class,
            FormatTable.convert(ColorInternalFormat.RGBA8, -1.0, 0.5, 2.0, -0.0));
        assertEquals(0.0f, floating.r());
        assertEquals(0.5f, floating.g());
        assertEquals(1.0f, floating.b());
        assertEquals(0.0f, floating.a());
        assertTrue(1.0f / floating.a() > 0.0f, "-0.0 must normalize to +0.0, not stay negative");
    }

    @Test
    void floatingConversionSaturates16BitChannelsAnd32BitChannelsToTheirMaxima() {
        FormatTable.ConvertedClear.Floating f16 = assertInstanceOf(
            FormatTable.ConvertedClear.Floating.class,
            FormatTable.convert(ColorInternalFormat.RGBA16F, -1.0e9, 1.0e9, 0.25, -3.5));
        assertEquals(-65504.0f, f16.r());
        assertEquals(65504.0f, f16.g());
        assertEquals(0.25f, f16.b());
        assertEquals(-3.5f, f16.a());
        FormatTable.ConvertedClear.Floating f32 = assertInstanceOf(
            FormatTable.ConvertedClear.Floating.class,
            FormatTable.convert(ColorInternalFormat.RGBA32F, -1.0e40, 1.0e40, 1.5, -1.5));
        assertEquals(-Float.MAX_VALUE, f32.r());
        assertEquals(Float.MAX_VALUE, f32.g());
        assertEquals(1.5f, f32.b());
        assertEquals(-1.5f, f32.a());
    }

    @Test
    void packedAndSharedExponentConversionUsesRepresentableChannelMaxima() {
        FormatTable.ConvertedClear.Floating packed = assertInstanceOf(
            FormatTable.ConvertedClear.Floating.class,
            FormatTable.convert(ColorInternalFormat.R11F_G11F_B10F, 1.0e9, -1.0, 1.0e9, 1.0e40));
        assertEquals(65024.0f, packed.r());
        assertEquals(0.0f, packed.g());
        assertEquals(64512.0f, packed.b());
        assertEquals(Float.MAX_VALUE, packed.a());

        FormatTable.ConvertedClear.Floating shared = assertInstanceOf(
            FormatTable.ConvertedClear.Floating.class,
            FormatTable.convert(ColorInternalFormat.RGB9_E5, 1.0e9, 0.5, 1.0e9, 7.0));
        assertEquals(65408.0f, shared.r());
        assertEquals(0.5f, shared.g());
        assertEquals(65408.0f, shared.b());
        assertEquals(7.0f, shared.a());
    }

    @Test
    void integerConversionTruncatesTowardZeroAndSaturatesTheRange() {
        FormatTable.ConvertedClear.Signed signed = assertInstanceOf(
            FormatTable.ConvertedClear.Signed.class,
            FormatTable.convert(ColorInternalFormat.RGBA32I, 1.9, -1.9, 3.0e9, -3.0e9));
        assertEquals(1, signed.r());
        assertEquals(-1, signed.g());
        assertEquals(Integer.MAX_VALUE, signed.b());
        assertEquals(Integer.MIN_VALUE, signed.a());

        FormatTable.ConvertedClear.Unsigned unsigned = assertInstanceOf(
            FormatTable.ConvertedClear.Unsigned.class,
            FormatTable.convert(ColorInternalFormat.RGBA32UI, 7.9, -1.0, 5.0e9, 0.5));
        assertEquals(7L, unsigned.r());
        assertEquals(0L, unsigned.g());
        assertEquals(4294967295L, unsigned.b());
        assertEquals(0L, unsigned.a());
    }

    @Test
    void nonFinitePayloadsAreRejectableAndFiniteOnesAreNot() {
        assertTrue(FormatTable.rejectable(Double.NaN, 0, 0, 0));
        assertTrue(FormatTable.rejectable(0, Double.POSITIVE_INFINITY, 0, 0));
        assertTrue(FormatTable.rejectable(0, 0, 0, Double.NEGATIVE_INFINITY));
        assertFalse(FormatTable.rejectable(0.0, 1.0, 2.0, 3.0));
    }

    @Test
    void payloadResolutionPrefersThePackConstantOverFrameFog() {
        ClearRequest request = new ClearRequest(7L, 0.1f, 0.2f, 0.3f, false);
        assertArrayEquals(new double[] {0.25, 0.5, 0.75, 1.0}, FormatTable.resolvePayload(
            new ResourceClearPolicy.Constant(0.25, 0.5, 0.75, 1.0), request));
        // D-P5-46 promotes the request's float components EXACTLY to double, so the
        // payload must stay float-identical (0.1f widens to 0.10000000149011612).
        assertArrayEquals(new double[] {(double) request.fogRed(), (double) request.fogGreen(),
            (double) request.fogBlue(), 1.0}, FormatTable.resolvePayload(
            new ResourceClearPolicy.FogRgbAlphaOne(), request));
    }

    @Test
    void nullFormatLookupIsRejectedLoudly() {
        assertThrows(NullPointerException.class, () -> FormatTable.row(null));
    }
}
