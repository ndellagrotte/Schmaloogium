// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Closed-value construction contracts the doc places on the facade's pure values:
 * {@link BlitSpec}'s depth-nearest enforcement and mask requirement (§4.7.4),
 * {@link ColorClearValue}'s finite-float and unsigned-range rules (§4.7.4b, D-P1-67),
 * {@link TextureParameters}' closed validation with negative-zero canonicalization
 * (§4.7.7, D-P1-52), the zero-pinned {@link TextureBorderColor}, and
 * {@link TextureSpec}'s mip-count/target rules (§4.7.7a, D-P1-63).
 */
class FacadeValueContractTest {

    private static TextureParameters parameters() {
        return new TextureParameters(
                TextureMinFilter.LINEAR, TextureMagFilter.LINEAR,
                TextureWrap.CLAMP_TO_EDGE, TextureWrap.CLAMP_TO_EDGE, TextureWrap.CLAMP_TO_EDGE,
                TextureCompareMode.NONE, TextureCompareFunction.LEQUAL,
                new TextureBorderColor(0, 0, 0, 0),
                -1000f, 1000f, 0f, 1f, 0, 1000, TextureSwizzle.IDENTITY);
    }

    @Test
    void blitSpecEnforcesDepthNearestAtConstruction() {
        TextureRegion region = new TextureRegion(0, 0, 0, 64, 64, 1);
        assertThrows(IllegalArgumentException.class,
                () -> new BlitSpec(region, region, EnumSet.of(BlitSpec.BlitMask.COLOR, BlitSpec.BlitMask.DEPTH),
                        BlitSpec.BlitFilter.LINEAR),
                "a depth blit must specify NEAREST");
        assertDoesNotThrow(() -> new BlitSpec(region, region,
                EnumSet.of(BlitSpec.BlitMask.COLOR, BlitSpec.BlitMask.DEPTH), BlitSpec.BlitFilter.NEAREST));
        assertDoesNotThrow(() -> new BlitSpec(region, region,
                EnumSet.of(BlitSpec.BlitMask.COLOR), BlitSpec.BlitFilter.LINEAR));
        assertThrows(IllegalArgumentException.class,
                () -> new BlitSpec(region, region, EnumSet.noneOf(BlitSpec.BlitMask.class), BlitSpec.BlitFilter.NEAREST),
                "the mask must be non-empty");
    }

    @Test
    void floatingClearComponentsMustBeFinite() {
        assertDoesNotThrow(() -> new ColorClearValue.Floating(0f, 1f, -1f, 0.5f));
        assertThrows(IllegalArgumentException.class, () -> new ColorClearValue.Floating(Float.NaN, 0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new ColorClearValue.Floating(0, Float.POSITIVE_INFINITY, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new ColorClearValue.Floating(0, 0, 0, Float.NEGATIVE_INFINITY));
    }

    @Test
    void unsignedClearComponentsAreMathematicalIntegers() {
        assertDoesNotThrow(() -> new ColorClearValue.Unsigned(0, 1, 0xFFFFFFFFL, 42));
        assertThrows(IllegalArgumentException.class, () -> new ColorClearValue.Unsigned(-1, 0, 0, 0),
                "no signed-int reinterpretations in public data");
        assertThrows(IllegalArgumentException.class, () -> new ColorClearValue.Unsigned(0, 0, 0x1_0000_0000L, 0));
    }

    @Test
    void textureParametersValidateTheirClosedDomain() {
        assertDoesNotThrow(FacadeValueContractTest::parameters);
        assertThrows(IllegalArgumentException.class, () -> new TextureParameters(
                null, TextureMagFilter.LINEAR, TextureWrap.REPEAT, TextureWrap.REPEAT, TextureWrap.REPEAT,
                TextureCompareMode.NONE, TextureCompareFunction.LEQUAL, new TextureBorderColor(0, 0, 0, 0),
                0, 0, 0, 1, 0, 0, TextureSwizzle.IDENTITY),
                "fields are nonnull");
        assertThrows(IllegalArgumentException.class,
                () -> new TextureParameters(
                        TextureMinFilter.LINEAR, TextureMagFilter.LINEAR, TextureWrap.REPEAT,
                        TextureWrap.REPEAT, TextureWrap.REPEAT, TextureCompareMode.NONE,
                        TextureCompareFunction.LEQUAL, new TextureBorderColor(0, 0, 0, 0),
                        Float.NaN, 1, 0, 1, 0, 0, TextureSwizzle.IDENTITY),
                "floats must be finite");
        assertThrows(IllegalArgumentException.class, () -> new TextureParameters(
                TextureMinFilter.LINEAR, TextureMagFilter.LINEAR, TextureWrap.REPEAT, TextureWrap.REPEAT,
                TextureWrap.REPEAT, TextureCompareMode.NONE, TextureCompareFunction.LEQUAL,
                new TextureBorderColor(0, 0, 0, 0), 1f, 0f, 0f, 1f, 0, 0, TextureSwizzle.IDENTITY),
                "minLod <= maxLod");
        assertThrows(IllegalArgumentException.class, () -> new TextureParameters(
                TextureMinFilter.LINEAR, TextureMagFilter.LINEAR, TextureWrap.REPEAT, TextureWrap.REPEAT,
                TextureWrap.REPEAT, TextureCompareMode.NONE, TextureCompareFunction.LEQUAL,
                new TextureBorderColor(0, 0, 0, 0), 0f, 0f, 0f, 1f, -1, 0, TextureSwizzle.IDENTITY),
                "baseLevel >= 0");
        assertThrows(IllegalArgumentException.class, () -> new TextureParameters(
                TextureMinFilter.LINEAR, TextureMagFilter.LINEAR, TextureWrap.REPEAT, TextureWrap.REPEAT,
                TextureWrap.REPEAT, TextureCompareMode.NONE, TextureCompareFunction.LEQUAL,
                new TextureBorderColor(0, 0, 0, 0), 0f, 0f, 0f, 1f, 5, 4, TextureSwizzle.IDENTITY),
                "baseLevel <= maxLevel");
        assertThrows(IllegalArgumentException.class, () -> new TextureParameters(
                TextureMinFilter.LINEAR, TextureMagFilter.LINEAR, TextureWrap.REPEAT, TextureWrap.REPEAT,
                TextureWrap.REPEAT, TextureCompareMode.NONE, TextureCompareFunction.LEQUAL,
                new TextureBorderColor(0, 0, 0, 0), 0f, 0f, 0f, 2f, 0, 0, TextureSwizzle.IDENTITY),
                "this scope permits maxAnisotropy = 1 only");
        assertThrows(IllegalArgumentException.class, () -> new TextureBorderColor(0, 0, 1, 0),
                "the border is exactly (0,0,0,0)");
    }

    @Test
    void negativeZeroIsCanonicalizedToPositiveZero() {
        TextureParameters p = new TextureParameters(
                TextureMinFilter.NEAREST, TextureMagFilter.NEAREST,
                TextureWrap.CLAMP_TO_EDGE, TextureWrap.CLAMP_TO_EDGE, TextureWrap.CLAMP_TO_EDGE,
                TextureCompareMode.NONE, TextureCompareFunction.ALWAYS,
                new TextureBorderColor(-0.0f, 0.0f, -0.0f, 0.0f),
                -0.0f, -0.0f, -0.0f, 1.0f, 0, 0, TextureSwizzle.IDENTITY);
        assertAllRawZeroBits(p.minLod(), p.maxLod(), p.lodBias());
        assertEquals(Float.floatToRawIntBits(1.0f), Float.floatToRawIntBits(p.maxAnisotropy()));
        assertEquals(0, Float.floatToRawIntBits(p.borderColor().red()),
                "border -0.0 canonicalizes to +0.0");
    }

    private static void assertAllRawZeroBits(float... values) {
        for (float v : values) {
            assertEquals(0, Float.floatToRawIntBits(v), "expected bitwise +0.0, got " + v);
        }
    }

    @Test
    void samplerKeyCopiesTheSamplerPrefixLosslessly() {
        TextureParameters p = parameters();
        SamplerKey key = SamplerKey.of(p);
        assertAll(
                () -> assertEquals(p.minFilter(), key.minFilter()),
                () -> assertEquals(p.magFilter(), key.magFilter()),
                () -> assertEquals(p.wrapS(), key.wrapS()),
                () -> assertEquals(p.wrapT(), key.wrapT()),
                () -> assertEquals(p.wrapR(), key.wrapR()),
                () -> assertEquals(p.compareMode(), key.compareMode()),
                () -> assertEquals(p.compareFunction(), key.compareFunction()),
                () -> assertEquals(p.borderColor(), key.borderColor()),
                () -> assertEquals(p.minLod(), key.minLod()),
                () -> assertEquals(p.maxLod(), key.maxLod()),
                () -> assertEquals(p.lodBias(), key.lodBias()),
                () -> assertEquals(p.maxAnisotropy(), key.maxAnisotropy()));
        // Object-only state (baseLevel/maxLevel/swizzle) stays off the key: a value
        // differing only there maps to the SAME key, so interning stays deterministic.
        TextureParameters samePrefixOtherObjectState = new TextureParameters(
                p.minFilter(), p.magFilter(), p.wrapS(), p.wrapT(), p.wrapR(),
                p.compareMode(), p.compareFunction(), p.borderColor(),
                p.minLod(), p.maxLod(), p.lodBias(), p.maxAnisotropy(),
                1, 2, TextureSwizzle.LEGACY_DEPTH_LUMINANCE);
        assertEquals(key, SamplerKey.of(samePrefixOtherObjectState));
        assertNotEquals(p, samePrefixOtherObjectState);
        assertThrows(IllegalArgumentException.class, () -> SamplerKey.of(null));
    }

    @Test
    void textureSpecEnforcesTargetAndMipCountRules() {
        assertDoesNotThrow(() -> new TextureSpec.ColorTextureSpec(
                TextureAllocationTarget.TEXTURE_2D, ColorInternalFormat.RGBA8,
                new PixelLayout.Color(PixelFormat.BGRA, PixelType.UNSIGNED_INT_8_8_8_8_REV),
                new TextureExtent(1024, 1024, 1), 11));
        assertThrows(IllegalArgumentException.class, () -> new TextureSpec.ColorTextureSpec(
                TextureAllocationTarget.TEXTURE_2D, ColorInternalFormat.RGBA8,
                new PixelLayout.Color(PixelFormat.BGRA, PixelType.UNSIGNED_INT_8_8_8_8_REV),
                new TextureExtent(1024, 1024, 1), 12),
                "1+floor(log2(1024)) = 11 is the ceiling");
        assertThrows(IllegalArgumentException.class, () -> new TextureSpec.ColorTextureSpec(
                TextureAllocationTarget.RECTANGLE, ColorInternalFormat.RGBA8,
                new PixelLayout.Color(PixelFormat.BGRA, PixelType.UNSIGNED_INT_8_8_8_8_REV),
                new TextureExtent(1024, 1024, 1), 2),
                "RECTANGLE admits exactly one mip level");
        assertThrows(IllegalArgumentException.class, () -> new TextureSpec.DepthTextureSpec(
                TextureAllocationTarget.TEXTURE_3D, DepthAttachmentFormat.DEPTH_COMPONENT,
                new PixelLayout.Depth(DepthTransferLayout.DEPTH_COMPONENT_FLOAT),
                new TextureExtent(64, 64, 8), 1),
                "depth specs admit only TEXTURE_2D");
        assertDoesNotThrow(() -> new TextureSpec.DepthTextureSpec(
                TextureAllocationTarget.TEXTURE_2D, DepthAttachmentFormat.DEPTH24_STENCIL8,
                new PixelLayout.Depth(DepthTransferLayout.DEPTH_STENCIL_UNSIGNED_INT_24_8),
                new TextureExtent(1024, 1024, 1), 1));
        assertThrows(IllegalArgumentException.class, () -> new TextureExtent(0, 10, 1),
                "extent dimensions are positive");
        assertThrows(IllegalArgumentException.class, () -> new TextureRegion(-1, 0, 0, 8, 8, 1),
                "region origins are nonnegative");
        assertThrows(IllegalArgumentException.class, () -> new TextureData(
                TextureAllocationTarget.TEXTURE_2D, new TextureRegion(0, 0, 0, 8, 8, 1),
                -1, new PixelLayout.Color(PixelFormat.RGBA, PixelType.UNSIGNED_BYTE),
                ByteBuffer.allocate(8 * 8 * 4)),
                "the mip index is nonnegative");
        assertTrue(new TextureSpec.ColorTextureSpec(
                        TextureAllocationTarget.TEXTURE_1D, ColorInternalFormat.R16F,
                        new PixelLayout.Color(PixelFormat.RED, PixelType.HALF_FLOAT),
                        new TextureExtent(64, 1, 1), 7)
                instanceof TextureSpec, "1D color allocation is admitted");
    }
}
