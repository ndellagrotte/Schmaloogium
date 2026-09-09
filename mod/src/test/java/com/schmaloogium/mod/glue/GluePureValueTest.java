// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.BlitSpec;
import com.schmaloogium.engine.gl.ClearTarget;
import com.schmaloogium.engine.gl.FramebufferStatus;
import com.schmaloogium.engine.gl.FramebufferTarget;
import com.schmaloogium.engine.gl.PixelFormat;
import com.schmaloogium.engine.gl.PixelType;
import com.schmaloogium.engine.gl.ShaderStage;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pure-value glue behaviour, headless: the GL name tables round-trip their closed
 * engine vocabularies onto the REAL driver constants (pinned here as the GL-spec hex
 * values - the test source set has no LWJGL on its classpath), and the vanilla
 * foreign-texture provider answers empty before a device is installed.
 */
class GluePureValueTest {

    // Desktop GL constants, from the OpenGL specification (stable forever).
    private static final int GL_COLOR_BUFFER_BIT = 0x00004000;
    private static final int GL_DEPTH_BUFFER_BIT = 0x00000100;
    private static final int GL_STENCIL_BUFFER_BIT = 0x00000400;
    private static final int GL_VERTEX_SHADER = 0x8B31;
    private static final int GL_FRAGMENT_SHADER = 0x8B30;
    private static final int GL_READ_FRAMEBUFFER = 0x8CA8;
    private static final int GL_DRAW_FRAMEBUFFER = 0x8CA9;
    private static final int GL_FRAMEBUFFER_COMPLETE = 0x8CD5;
    private static final int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 0x8CD6;
    private static final int GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS = 0x8CD9;
    private static final int GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS = 0x8DA8;
    private static final int GL_FRAMEBUFFER_UNDEFINED = 0x8219;

    @Test
    void framebufferStatusMapsEveryRealGlValue() {
        assertEquals(FramebufferStatus.COMPLETE,
                GlNames.framebufferStatus(GL_FRAMEBUFFER_COMPLETE));
        assertEquals(FramebufferStatus.INCOMPLETE_ATTACHMENT,
                GlNames.framebufferStatus(GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT));
        assertEquals(FramebufferStatus.INCOMPLETE_DIMENSIONS,
                GlNames.framebufferStatus(GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS));
        assertEquals(FramebufferStatus.INCOMPLETE_LAYER_TARGETS,
                GlNames.framebufferStatus(GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS));
        assertEquals(FramebufferStatus.UNDEFINED,
                GlNames.framebufferStatus(GL_FRAMEBUFFER_UNDEFINED));
    }

    @Test
    void unknownStatusFallsIntoUnknownNotException() {
        assertEquals(FramebufferStatus.UNKNOWN, GlNames.framebufferStatus(0x1BADB002));
    }

    @Test
    void clearMaskComposesTheExactTargets() {
        int colorDepth = GlNames.glClearMask(EnumSet.of(ClearTarget.COLOR, ClearTarget.DEPTH));
        assertEquals(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, colorDepth);
        assertEquals(GL_STENCIL_BUFFER_BIT,
                GlNames.glClearMask(EnumSet.of(ClearTarget.STENCIL)));
    }

    @Test
    void bindingTargetsAreRealConstants() {
        assertEquals(GL_READ_FRAMEBUFFER,
                GlNames.glFramebufferBindingTarget(FramebufferTarget.READ));
        assertEquals(GL_DRAW_FRAMEBUFFER,
                GlNames.glFramebufferBindingTarget(FramebufferTarget.DRAW));
    }

    @Test
    void shaderStagesMapToCoreValues() {
        assertEquals(GL_VERTEX_SHADER, GlNames.glShaderStage(ShaderStage.VERTEX));
        assertEquals(GL_FRAGMENT_SHADER, GlNames.glShaderStage(ShaderStage.FRAGMENT));
    }

    @Test
    void pixelArithmeticMatchesTheFormatTable() {
        assertEquals(4, GlNames.componentCount(PixelFormat.RGBA));
        assertEquals(1, GlNames.componentCount(PixelFormat.RED));
        assertEquals(1, GlNames.byteWidth(PixelType.UNSIGNED_BYTE));
        assertEquals(4, GlNames.bytesPerPixel(PixelFormat.RGBA, PixelType.UNSIGNED_BYTE));
        assertTrue(GlNames.packedType(PixelType.UNSIGNED_INT_8_8_8_8_REV));
    }

    @Test
    void blitFilterVocabularyIsClosed() {
        assertEquals(2, BlitSpec.BlitFilter.values().length);
    }

    @Test
    void foreignTexturesAnswerEmptyBeforeDeviceInstall() {
        VanillaForeignTextures provider = new VanillaForeignTextures();

        assertEquals(Optional.empty(), provider.handleFor(null));
        assertEquals(Optional.empty(), provider.handleFor(""));
        assertEquals(Optional.empty(), provider.handleFor("not a resloc!"));
        // A well-formed key with no installed device cannot mint a handle either.
        assertEquals(Optional.empty(), provider.handleFor("minecraft:textures/atlas/blocks.png"));
        assertFalse(provider.handleFor(VanillaForeignTextures.LIGHTMAP_KEY).isPresent());
    }

    @Test
    void lightmapKeyIsTheVanillaAtlasKey() {
        assertEquals("minecraft:dynamic/lightmap_1", VanillaForeignTextures.LIGHTMAP_KEY);
    }
}
