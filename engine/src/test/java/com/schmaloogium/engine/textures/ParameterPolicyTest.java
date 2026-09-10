// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.buffers.TextureParameterSpec;
import com.schmaloogium.engine.gl.ColorInternalFormat;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.gl.TextureBorderColor;
import com.schmaloogium.engine.gl.TextureCompareFunction;
import com.schmaloogium.engine.gl.TextureCompareMode;
import com.schmaloogium.engine.gl.TextureMagFilter;
import com.schmaloogium.engine.gl.TextureMinFilter;
import com.schmaloogium.engine.gl.TextureParameters;
import com.schmaloogium.engine.gl.TextureSwizzle;
import com.schmaloogium.engine.gl.TextureWrap;
import com.schmaloogium.engine.textures.internal.ParameterPolicy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Effective-parameter law: target/format legality before allocation (§4.3.5), the D-P13-42
 * companion/default policies and the complete D-P13-29 P1 facade mapping (§5.5).
 */
class ParameterPolicyTest {

    private static TextureParameterSpec spec(TextureMinFilter min, TextureMagFilter mag,
                                             TextureWrap wrap) {
        return new TextureParameterSpec(min, mag, wrap);
    }

    @Test
    void rectangleRejectsRepeatAndMipmapMinification() {
        // RECTANGLE rejects REPEAT and mipmap minification, even combined with CLAMP
        assertFalse(ParameterPolicy.isLegal(TextureAllocationTarget.RECTANGLE,
            ColorInternalFormat.RGBA8, spec(TextureMinFilter.NEAREST,
                TextureMagFilter.NEAREST, TextureWrap.REPEAT)));
        assertFalse(ParameterPolicy.isLegal(TextureAllocationTarget.RECTANGLE,
            ColorInternalFormat.RGBA8, spec(TextureMinFilter.LINEAR_MIPMAP_LINEAR,
                TextureMagFilter.LINEAR, TextureWrap.CLAMP_TO_EDGE)));
        // a legal RECTANGLE combination
        assertTrue(ParameterPolicy.isLegal(TextureAllocationTarget.RECTANGLE,
            ColorInternalFormat.RGBA8, spec(TextureMinFilter.LINEAR, TextureMagFilter.LINEAR,
                TextureWrap.CLAMP_TO_EDGE)));
        // integer RECTANGLE still nearest-only
        assertTrue(ParameterPolicy.isLegal(TextureAllocationTarget.RECTANGLE,
            ColorInternalFormat.R32I, spec(TextureMinFilter.NEAREST, TextureMagFilter.NEAREST,
                TextureWrap.CLAMP_TO_EDGE)));
        assertFalse(ParameterPolicy.isLegal(TextureAllocationTarget.RECTANGLE,
            ColorInternalFormat.R32I, spec(TextureMinFilter.NEAREST, TextureMagFilter.NEAREST,
                TextureWrap.REPEAT)));
    }

    @Test
    void integerStorageRequiresNearestOnly() {
        // R32I with LINEAR mag or LINEAR min is illegal even though that is the RAW default
        assertFalse(ParameterPolicy.isLegal(TextureAllocationTarget.TEXTURE_2D,
            ColorInternalFormat.R32I, spec(TextureMinFilter.LINEAR, TextureMagFilter.LINEAR,
                TextureWrap.CLAMP_TO_EDGE)));
        assertFalse(ParameterPolicy.isLegal(TextureAllocationTarget.TEXTURE_2D,
            ColorInternalFormat.R32I, spec(TextureMinFilter.LINEAR, TextureMagFilter.NEAREST,
                TextureWrap.REPEAT)));
        assertFalse(ParameterPolicy.isLegal(TextureAllocationTarget.TEXTURE_2D,
            ColorInternalFormat.R32I, spec(TextureMinFilter.LINEAR_MIPMAP_LINEAR,
                TextureMagFilter.LINEAR, TextureWrap.REPEAT)));
        assertTrue(ParameterPolicy.isLegal(TextureAllocationTarget.TEXTURE_2D,
            ColorInternalFormat.R32I, spec(TextureMinFilter.NEAREST_MIPMAP_NEAREST,
                TextureMagFilter.NEAREST, TextureWrap.REPEAT)));
        assertTrue(ParameterPolicy.isLegal(TextureAllocationTarget.TEXTURE_2D,
            ColorInternalFormat.R32I, spec(TextureMinFilter.NEAREST,
                TextureMagFilter.NEAREST, TextureWrap.CLAMP_TO_EDGE)));
    }
    @Test
    void nonIntegerFormatsAcceptAnyGrantedCombination() {
        assertTrue(ParameterPolicy.isLegal(TextureAllocationTarget.TEXTURE_2D,
            ColorInternalFormat.RGBA8, spec(TextureMinFilter.LINEAR_MIPMAP_LINEAR,
                TextureMagFilter.LINEAR, TextureWrap.REPEAT)));
        assertTrue(ParameterPolicy.isLegal(TextureAllocationTarget.TEXTURE_2D,
            ColorInternalFormat.RGBA16F, spec(TextureMinFilter.NEAREST,
                TextureMagFilter.NEAREST, TextureWrap.CLAMP_TO_EDGE)));
        assertTrue(ParameterPolicy.isLegal(TextureAllocationTarget.TEXTURE_1D,
            ColorInternalFormat.R32F, spec(TextureMinFilter.NEAREST_MIPMAP_NEAREST,
                TextureMagFilter.LINEAR, TextureWrap.REPEAT)));
        assertTrue(ParameterPolicy.isLegal(TextureAllocationTarget.TEXTURE_3D,
            ColorInternalFormat.RGB32F, spec(TextureMinFilter.LINEAR,
                TextureMagFilter.LINEAR, TextureWrap.REPEAT)));
    }

    @Test
    void companionPolicies() {
        // D-P13-42: full atlas derives minification from the accepted mip count
        TextureParameterSpec zero = ParameterPolicy.companionAtlasPolicy(0);
        assertEquals(TextureMinFilter.NEAREST, zero.minFilter());
        assertEquals(TextureMagFilter.NEAREST, zero.magFilter());
        assertEquals(TextureWrap.REPEAT, zero.wrap());
        TextureParameterSpec mipped = ParameterPolicy.companionAtlasPolicy(3);
        assertEquals(TextureMinFilter.NEAREST_MIPMAP_LINEAR, mipped.minFilter());
        assertEquals(TextureMagFilter.NEAREST, mipped.magFilter());
        assertEquals(TextureWrap.REPEAT, mipped.wrap());
        // standalone 1x1 level-zero kind default
        TextureParameterSpec standalone = ParameterPolicy.standaloneDefaultPolicy();
        assertEquals(TextureMinFilter.NEAREST, standalone.minFilter());
        assertEquals(TextureMagFilter.NEAREST, standalone.magFilter());
        assertEquals(TextureWrap.REPEAT, standalone.wrap());
        // missing-sprite fill inherits the atlas object's policy, never the standalone one
        assertEquals(ParameterPolicy.companionAtlasPolicy(4),
            ParameterPolicy.filledSpritePolicy(4));
        assertEquals(ParameterPolicy.companionAtlasPolicy(0),
            ParameterPolicy.filledSpritePolicy(0));
        assertThrows(IllegalArgumentException.class,
            () -> ParameterPolicy.companionAtlasPolicy(-1));
    }

    @Test
    void facadeMapsWrapPerTargetAxis() {
        TextureParameterSpec repeat = spec(TextureMinFilter.NEAREST, TextureMagFilter.NEAREST,
            TextureWrap.REPEAT);
        // 1D: only S samples the spec wrap
        TextureParameters oneD = ParameterPolicy.facadeParameters(
            TextureAllocationTarget.TEXTURE_1D, repeat, 1);
        assertEquals(TextureWrap.REPEAT, oneD.wrapS());
        assertEquals(TextureWrap.CLAMP_TO_EDGE, oneD.wrapT());
        assertEquals(TextureWrap.CLAMP_TO_EDGE, oneD.wrapR());
        // 2D and RECTANGLE: S and T sample the spec wrap, R stays clamped
        for (TextureAllocationTarget target : new TextureAllocationTarget[]{
            TextureAllocationTarget.TEXTURE_2D, TextureAllocationTarget.RECTANGLE}) {
            TextureParameters parameters = ParameterPolicy.facadeParameters(target, repeat, 1);
            assertEquals(TextureWrap.REPEAT, parameters.wrapS());
            assertEquals(TextureWrap.REPEAT, parameters.wrapT());
            assertEquals(TextureWrap.CLAMP_TO_EDGE, parameters.wrapR());
        }
        // 3D: all three axes
        TextureParameters threeD = ParameterPolicy.facadeParameters(
            TextureAllocationTarget.TEXTURE_3D, repeat, 1);
        assertEquals(TextureWrap.REPEAT, threeD.wrapS());
        assertEquals(TextureWrap.REPEAT, threeD.wrapT());
        assertEquals(TextureWrap.REPEAT, threeD.wrapR());
        // direct min/mag passthrough
        TextureParameterSpec linear = spec(TextureMinFilter.LINEAR_MIPMAP_LINEAR,
            TextureMagFilter.LINEAR, TextureWrap.CLAMP_TO_EDGE);
        TextureParameters mapped = ParameterPolicy.facadeParameters(
            TextureAllocationTarget.TEXTURE_2D, linear, 4);
        assertEquals(TextureMinFilter.LINEAR_MIPMAP_LINEAR, mapped.minFilter());
        assertEquals(TextureMagFilter.LINEAR, mapped.magFilter());
    }

    @Test
    void facadeNeutralFieldsAndMipBaseline() {
        TextureParameterSpec mipped = spec(TextureMinFilter.NEAREST_MIPMAP_LINEAR,
            TextureMagFilter.NEAREST, TextureWrap.REPEAT);
        TextureParameters mipped4 = ParameterPolicy.facadeParameters(
            TextureAllocationTarget.TEXTURE_2D, mipped, 4);
        assertEquals(3, mipped4.maxLevel()); // admitted contiguous last mip
        assertEquals(0, mipped4.baseLevel());

        TextureParameterSpec nonmipped = spec(TextureMinFilter.NEAREST,
            TextureMagFilter.NEAREST, TextureWrap.REPEAT);
        TextureParameters levelZero = ParameterPolicy.facadeParameters(
            TextureAllocationTarget.TEXTURE_2D, nonmipped, 1);
        assertEquals(0, levelZero.maxLevel());
        assertEquals(0, levelZero.baseLevel());

        for (TextureParameters parameters : new TextureParameters[]{mipped4, levelZero}) {
            assertEquals(TextureCompareMode.NONE, parameters.compareMode());
            assertEquals(TextureCompareFunction.LEQUAL, parameters.compareFunction());
            assertEquals(new TextureBorderColor(0f, 0f, 0f, 0f), parameters.borderColor());
            assertEquals(-1000f, parameters.minLod());
            assertEquals(1000f, parameters.maxLod());
            assertEquals(0f, parameters.lodBias());
            assertEquals(1f, parameters.maxAnisotropy());
            assertEquals(TextureSwizzle.IDENTITY, parameters.swizzle());
        }
        // mipLevels below one is not a legal facade input
        assertThrows(IllegalArgumentException.class,
            () -> ParameterPolicy.facadeParameters(TextureAllocationTarget.TEXTURE_2D,
                nonmipped, 0));
    }
}
