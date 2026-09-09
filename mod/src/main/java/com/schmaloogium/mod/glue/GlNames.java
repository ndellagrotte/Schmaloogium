// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.AlphaTestState;
import com.schmaloogium.engine.gl.BlendState;
import com.schmaloogium.engine.gl.ClearTarget;
import com.schmaloogium.engine.gl.ColorInternalFormat;
import com.schmaloogium.engine.gl.DepthAttachmentFormat;
import com.schmaloogium.engine.gl.FramebufferStatus;
import com.schmaloogium.engine.gl.FramebufferTarget;
import com.schmaloogium.engine.gl.FogState;
import com.schmaloogium.engine.gl.GLErrorKind;
import com.schmaloogium.engine.gl.PixelFormat;
import com.schmaloogium.engine.gl.PixelType;
import com.schmaloogium.engine.gl.ShaderStage;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.gl.TextureCompareFunction;
import com.schmaloogium.engine.gl.TextureCompareMode;
import com.schmaloogium.engine.gl.TextureMagFilter;
import com.schmaloogium.engine.gl.TextureMinFilter;
import com.schmaloogium.engine.gl.TextureWrap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL31;

import java.util.EnumSet;

/**
 * The only place the facade's closed engine vocabularies meet native GL constants
 * (PHASE_1_DOC §4.7.4: "No GL constants appear in any signature … the LWJGL3 backend
 * maps them to {@code GL_*}"). Every mapping is total; an unmapped engine constant is a
 * compile-time-visible addition to {@code :engine}, not a silent default.
 */
final class GlNames {

    private GlNames() {
    }

    // --- shader stages -------------------------------------------------------

    static int glShaderStage(ShaderStage stage) {
        return switch (stage) {
            case VERTEX -> GL20.GL_VERTEX_SHADER;
            case FRAGMENT -> GL20.GL_FRAGMENT_SHADER;
            case GEOMETRY -> org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
        };
    }


    // --- alpha test ----------------------------------------------------------

    static int glAlphaFunc(AlphaTestState.AlphaTestFunction f) {
        return switch (f) {
            case NEVER -> GL11.GL_NEVER;
            case LESS -> GL11.GL_LESS;
            case EQUAL -> GL11.GL_EQUAL;
            case LEQUAL -> GL11.GL_LEQUAL;
            case GREATER -> GL11.GL_GREATER;
            case NOTEQUAL -> GL11.GL_NOTEQUAL;
            case GEQUAL -> GL11.GL_GEQUAL;
            case ALWAYS -> GL11.GL_ALWAYS;
        };
    }

    // --- blend factors -------------------------------------------------------

    static int glBlendFactor(BlendState.BlendFactor f) {
        return switch (f) {
            case ZERO -> GL11.GL_ZERO;
            case ONE -> GL11.GL_ONE;
            case SRC_COLOR -> GL11.GL_SRC_COLOR;
            case ONE_MINUS_SRC_COLOR -> GL11.GL_ONE_MINUS_SRC_COLOR;
            case DST_COLOR -> GL11.GL_DST_COLOR;
            case ONE_MINUS_DST_COLOR -> GL11.GL_ONE_MINUS_DST_COLOR;
            case SRC_ALPHA -> GL11.GL_SRC_ALPHA;
            case ONE_MINUS_SRC_ALPHA -> GL11.GL_ONE_MINUS_SRC_ALPHA;
            case DST_ALPHA -> GL11.GL_DST_ALPHA;
            case ONE_MINUS_DST_ALPHA -> GL11.GL_ONE_MINUS_DST_ALPHA;
            case CONSTANT_COLOR -> GL14.GL_CONSTANT_COLOR;
            case ONE_MINUS_CONSTANT_COLOR -> GL14.GL_ONE_MINUS_CONSTANT_COLOR;
            case CONSTANT_ALPHA -> GL14.GL_CONSTANT_ALPHA;
            case ONE_MINUS_CONSTANT_ALPHA -> GL14.GL_ONE_MINUS_CONSTANT_ALPHA;
            case SRC_ALPHA_SATURATE -> GL11.GL_SRC_ALPHA_SATURATE;
        };
    }

    // --- fog -----------------------------------------------------------------

    static int glFogMode(FogState.FogMode mode) {
        return switch (mode) {
            case LINEAR -> GL11.GL_LINEAR;
            case EXP -> GL11.GL_EXP;
            case EXP2 -> GL11.GL_EXP2;
        };
    }

    // --- clears --------------------------------------------------------------

    static int glClearMask(EnumSet<ClearTarget> targets) {
        int mask = 0;
        for (ClearTarget t : targets) {
            mask |= switch (t) {
                case COLOR -> GL11.GL_COLOR_BUFFER_BIT;
                case DEPTH -> GL11.GL_DEPTH_BUFFER_BIT;
                case STENCIL -> GL11.GL_STENCIL_BUFFER_BIT;
            };
        }
        return mask;
    }

    // --- framebuffer targets/status -----------------------------------------

    static int glFramebufferBindingTarget(FramebufferTarget target) {
        return switch (target) {
            case READ -> GL30.GL_READ_FRAMEBUFFER;
            case DRAW -> GL30.GL_DRAW_FRAMEBUFFER;
            case READ_AND_DRAW -> GL30.GL_FRAMEBUFFER;
        };
    }

    static FramebufferStatus framebufferStatus(int glStatus) {
        return switch (glStatus) {
            case GL30.GL_FRAMEBUFFER_COMPLETE -> FramebufferStatus.COMPLETE;
            case GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT -> FramebufferStatus.INCOMPLETE_ATTACHMENT;
            case GL30.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT -> FramebufferStatus.INCOMPLETE_MISSING_ATTACHMENT;
            case EXT_DIMENSIONS -> FramebufferStatus.INCOMPLETE_DIMENSIONS;
            case EXT_FORMATS -> FramebufferStatus.INCOMPLETE_FORMATS;
            case GL30.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER -> FramebufferStatus.INCOMPLETE_DRAW_BUFFER;
            case GL30.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER -> FramebufferStatus.INCOMPLETE_READ_BUFFER;
            case GL30.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE -> FramebufferStatus.INCOMPLETE_MULTISAMPLE;
            case GL32.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS -> FramebufferStatus.INCOMPLETE_LAYER_TARGETS;
            case GL30.GL_FRAMEBUFFER_UNSUPPORTED -> FramebufferStatus.UNSUPPORTED;
            case GL_FRAMEBUFFER_UNDEFINED -> FramebufferStatus.UNDEFINED;
            default -> FramebufferStatus.UNKNOWN;
        };
    }

    private static final int GL_FRAMEBUFFER_UNDEFINED = GL30.GL_FRAMEBUFFER_UNDEFINED;
    // Pre-core ARB_framebuffer_object statuses (desktop numbers; LWJGL exposes the EXT spellings).
    private static final int EXT_DIMENSIONS = org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT;
    private static final int EXT_FORMATS = org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT;

    // --- texture parameters --------------------------------------------------

    static int glMinFilter(TextureMinFilter f) {
        return switch (f) {
            case NEAREST -> GL11.GL_NEAREST;
            case LINEAR -> GL11.GL_LINEAR;
            case NEAREST_MIPMAP_NEAREST -> GL11.GL_NEAREST_MIPMAP_NEAREST;
            case LINEAR_MIPMAP_NEAREST -> GL11.GL_LINEAR_MIPMAP_NEAREST;
            case NEAREST_MIPMAP_LINEAR -> GL11.GL_NEAREST_MIPMAP_LINEAR;
            case LINEAR_MIPMAP_LINEAR -> GL11.GL_LINEAR_MIPMAP_LINEAR;
        };
    }

    static int glMagFilter(TextureMagFilter f) {
        return switch (f) {
            case NEAREST -> GL11.GL_NEAREST;
            case LINEAR -> GL11.GL_LINEAR;
        };
    }

    static int glWrap(TextureWrap w) {
        return switch (w) {
            case REPEAT -> GL11.GL_REPEAT;
            case CLAMP_TO_EDGE -> GL12.GL_CLAMP_TO_EDGE;
        };
    }

    static int glCompareMode(TextureCompareMode m) {
        return switch (m) {
            case NONE -> GL14.GL_NONE;
            case REF_TO_TEXTURE -> GL14.GL_COMPARE_R_TO_TEXTURE;
        };
    }

    private static final int GL_NONE = 0;

    static int glCompareFunction(TextureCompareFunction f) {
        return switch (f) {
            case NEVER -> GL11.GL_NEVER;
            case LESS -> GL11.GL_LESS;
            case EQUAL -> GL11.GL_EQUAL;
            case LEQUAL -> GL11.GL_LEQUAL;
            case GREATER -> GL11.GL_GREATER;
            case NOTEQUAL -> GL11.GL_NOTEQUAL;
            case GEQUAL -> GL11.GL_GEQUAL;
            case ALWAYS -> GL11.GL_ALWAYS;
        };
    }

    static int glTextureTarget(TextureAllocationTarget t) {
        return switch (t) {
            case TEXTURE_1D -> GL11.GL_TEXTURE_1D;
            case TEXTURE_2D -> GL11.GL_TEXTURE_2D;
            case TEXTURE_3D -> GL12.GL_TEXTURE_3D;
            case RECTANGLE -> GL31.GL_TEXTURE_RECTANGLE;
        };
    }

    // --- pixel transfer formats/types ---------------------------------------

    static int glPixelFormat(PixelFormat f) {
        return switch (f) {
            case RED -> GL11.GL_RED;
            case RG -> GL30.GL_RG;
            case RGB -> GL12.GL_RGB;
            case BGR -> GL12.GL_BGR;
            case RGBA -> GL11.GL_RGBA;
            case BGRA -> GL12.GL_BGRA;
            case RED_INTEGER -> GL30.GL_RED_INTEGER;
            case RG_INTEGER -> GL30.GL_RG_INTEGER;
            case RGB_INTEGER -> GL30.GL_RGB_INTEGER;
            case BGR_INTEGER -> GL30.GL_BGR_INTEGER;
            case RGBA_INTEGER -> GL30.GL_RGBA_INTEGER;
            case BGRA_INTEGER -> GL30.GL_BGRA_INTEGER;
        };
    }

    static int glPixelType(PixelType t) {
        return switch (t) {
            case BYTE -> GL11.GL_BYTE;
            case SHORT -> GL11.GL_SHORT;
            case INT -> GL11.GL_INT;
            case HALF_FLOAT -> GL30.GL_HALF_FLOAT;
            case FLOAT -> GL11.GL_FLOAT;
            case UNSIGNED_BYTE -> GL11.GL_UNSIGNED_BYTE;
            case UNSIGNED_BYTE_3_3_2 -> GL12.GL_UNSIGNED_BYTE_3_3_2;
            case UNSIGNED_BYTE_2_3_3_REV -> GL12.GL_UNSIGNED_BYTE_2_3_3_REV;
            case UNSIGNED_SHORT -> GL11.GL_UNSIGNED_SHORT;
            case UNSIGNED_SHORT_5_6_5 -> GL12.GL_UNSIGNED_SHORT_5_6_5;
            case UNSIGNED_SHORT_5_6_5_REV -> GL12.GL_UNSIGNED_SHORT_5_6_5_REV;
            case UNSIGNED_SHORT_4_4_4_4 -> GL12.GL_UNSIGNED_SHORT_4_4_4_4;
            case UNSIGNED_SHORT_4_4_4_4_REV -> GL12.GL_UNSIGNED_SHORT_4_4_4_4_REV;
            case UNSIGNED_SHORT_5_5_5_1 -> GL12.GL_UNSIGNED_SHORT_5_5_5_1;
            case UNSIGNED_SHORT_1_5_5_5_REV -> GL12.GL_UNSIGNED_SHORT_1_5_5_5_REV;
            case UNSIGNED_INT -> GL11.GL_UNSIGNED_INT;
            case UNSIGNED_INT_8_8_8_8 -> GL12.GL_UNSIGNED_INT_8_8_8_8;
            case UNSIGNED_INT_8_8_8_8_REV -> GL12.GL_UNSIGNED_INT_8_8_8_8_REV;
            case UNSIGNED_INT_10_10_10_2 -> GL12.GL_UNSIGNED_INT_10_10_10_2;
            case UNSIGNED_INT_2_10_10_10_REV -> GL12.GL_UNSIGNED_INT_2_10_10_10_REV;
        };
    }

    /** Component count of scalar formats; packed formats are one word per pixel. */
    static int componentCount(PixelFormat f) {
        return switch (f) {
            case RED, RED_INTEGER, BGR_INTEGER -> 1;
            case RG, RG_INTEGER -> 2;
            case RGB, RGB_INTEGER -> 3;
            case BGR -> 3;
            case RGBA, RGBA_INTEGER, BGRA, BGRA_INTEGER -> 4;
        };
    }

    /** Byte width of one scalar or one packed word (PHASE_1_DOC §4.7.7a). */
    static int byteWidth(PixelType t) {
        return switch (t) {
            case BYTE, UNSIGNED_BYTE, UNSIGNED_BYTE_3_3_2, UNSIGNED_BYTE_2_3_3_REV -> 1;
            case SHORT, HALF_FLOAT, UNSIGNED_SHORT, UNSIGNED_SHORT_5_6_5, UNSIGNED_SHORT_5_6_5_REV,
                    UNSIGNED_SHORT_4_4_4_4, UNSIGNED_SHORT_4_4_4_4_REV, UNSIGNED_SHORT_5_5_5_1,
                    UNSIGNED_SHORT_1_5_5_5_REV -> 2;
            case INT, FLOAT, UNSIGNED_INT, UNSIGNED_INT_8_8_8_8, UNSIGNED_INT_8_8_8_8_REV,
                    UNSIGNED_INT_10_10_10_2, UNSIGNED_INT_2_10_10_10_REV -> 4;
        };
    }

    static boolean packedType(PixelType t) {
        return switch (t) {
            case UNSIGNED_BYTE_3_3_2, UNSIGNED_BYTE_2_3_3_REV, UNSIGNED_SHORT_5_6_5,
                    UNSIGNED_SHORT_5_6_5_REV, UNSIGNED_SHORT_4_4_4_4, UNSIGNED_SHORT_4_4_4_4_REV,
                    UNSIGNED_SHORT_5_5_5_1, UNSIGNED_SHORT_1_5_5_5_REV, UNSIGNED_INT_8_8_8_8,
                    UNSIGNED_INT_8_8_8_8_REV, UNSIGNED_INT_10_10_10_2, UNSIGNED_INT_2_10_10_10_REV -> true;
            default -> false;
        };
    }

    static int bytesPerPixel(PixelFormat f, PixelType t) {
        return packedType(t) ? byteWidth(t) : componentCount(f) * byteWidth(t);
    }

    // --- internal formats ----------------------------------------------------

    static int glColorInternalFormat(ColorInternalFormat f) {
        return switch (f) {
            case R8 -> GL30.GL_R8;
            case RG8 -> GL30.GL_RG8;
            case RGB8 -> GL30.GL_RGB8;
            case RGBA8 -> GL30.GL_RGBA8;
            case R8_SNORM -> GL31.GL_R8_SNORM;
            case RG8_SNORM -> GL31.GL_RG8_SNORM;
            case RGB8_SNORM -> GL31.GL_RGB8_SNORM;
            case RGBA8_SNORM -> GL31.GL_RGBA8_SNORM;
            case R16 -> GL30.GL_R16;
            case RG16 -> GL30.GL_RG16;
            case RGB16 -> GL30.GL_RGB16;
            case RGBA16 -> GL30.GL_RGBA16;
            case R16_SNORM -> GL31.GL_R16_SNORM;
            case RG16_SNORM -> GL31.GL_RG16_SNORM;
            case RGB16_SNORM -> GL31.GL_RGB16_SNORM;
            case RGBA16_SNORM -> GL31.GL_RGBA16_SNORM;
            case R16F -> GL30.GL_R16F;
            case RG16F -> GL30.GL_RG16F;
            case RGB16F -> GL30.GL_RGB16F;
            case RGBA16F -> GL30.GL_RGBA16F;
            case R32F -> GL30.GL_R32F;
            case RG32F -> GL30.GL_RG32F;
            case RGB32F -> GL30.GL_RGB32F;
            case RGBA32F -> GL30.GL_RGBA32F;
            case R32I -> GL30.GL_R32I;
            case RG32I -> GL30.GL_RG32I;
            case RGB32I -> GL30.GL_RGB32I;
            case RGBA32I -> GL30.GL_RGBA32I;
            case R32UI -> GL30.GL_R32UI;
            case RG32UI -> GL30.GL_RG32UI;
            case RGB32UI -> GL30.GL_RGB32UI;
            case RGBA32UI -> GL30.GL_RGBA32UI;
            case R3_G3_B2 -> GL11.GL_R3_G3_B2;
            case RGB5_A1 -> GL11.GL_RGB5_A1;
            case RGB10_A2 -> GL11.GL_RGB10_A2;
            case R11F_G11F_B10F -> GL30.GL_R11F_G11F_B10F;
            case RGB9_E5 -> GL30.GL_RGB9_E5;
            // The private unsized-RGBA fallback representation; native GL_RGBA.
            case RGBA_COMPAT -> GL11.GL_RGBA;
        };
    }

    /** Signed-integer (32I) storage class. */
    static boolean signedIntegerFormat(ColorInternalFormat f) {
        return switch (f) {
            case R32I, RG32I, RGB32I, RGBA32I -> true;
            default -> false;
        };
    }

    /** Unsigned-integer (32UI) storage class. */
    static boolean unsignedIntegerFormat(ColorInternalFormat f) {
        return switch (f) {
            case R32UI, RG32UI, RGB32UI, RGBA32UI -> true;
            default -> false;
        };
    }

    static boolean integerFormat(ColorInternalFormat f) {
        return signedIntegerFormat(f) || unsignedIntegerFormat(f);
    }

    static int glDepthInternalFormat(DepthAttachmentFormat f) {
        return switch (f) {
            case DEPTH_COMPONENT -> GL11.GL_DEPTH_COMPONENT;
            case DEPTH24_STENCIL8 -> GL30.GL_DEPTH24_STENCIL8;
        };
    }

    // --- legacy/linked geometry primitives (D-P1-44, §4.7.4a) ------------------

    static int legacyGeometryInput(com.schmaloogium.engine.gl.LegacyGeometryInputPrimitive input) {
        return switch (input) {
            case TRIANGLES -> GL11.GL_TRIANGLES;
        };
    }

    static int legacyGeometryOutput(com.schmaloogium.engine.gl.LegacyGeometryOutputPrimitive output) {
        return switch (output) {
            case TRIANGLE_STRIP -> GL11.GL_TRIANGLE_STRIP;
        };
    }

    /** Null when the native type is a strip/fan or unknown - metadata acquisition fails. */
    static com.schmaloogium.engine.gl.LinkedGeometryInputPrimitive linkedGeometryInput(int nativeType) {
        return switch (nativeType) {
            case GL11.GL_POINTS -> com.schmaloogium.engine.gl.LinkedGeometryInputPrimitive.POINTS;
            case GL11.GL_LINES -> com.schmaloogium.engine.gl.LinkedGeometryInputPrimitive.LINES;
            case GL_LINES_ADJACENCY -> com.schmaloogium.engine.gl.LinkedGeometryInputPrimitive.LINES_ADJACENCY;
            case GL11.GL_TRIANGLES -> com.schmaloogium.engine.gl.LinkedGeometryInputPrimitive.TRIANGLES;
            case GL_TRIANGLES_ADJACENCY -> com.schmaloogium.engine.gl.LinkedGeometryInputPrimitive.TRIANGLES_ADJACENCY;
            default -> null;
        };
    }

    private static final int GL_LINES_ADJACENCY = 10;
    private static final int GL_TRIANGLES_ADJACENCY = 12;

    // --- GL error kinds ------------------------------------------------------

    static GLErrorKind errorKind(int glError) {
        return switch (glError) {
            case GL11.GL_INVALID_ENUM -> GLErrorKind.INVALID_ENUM;
            case GL11.GL_INVALID_VALUE -> GLErrorKind.INVALID_VALUE;
            case GL11.GL_INVALID_OPERATION -> GLErrorKind.INVALID_OPERATION;
            case GL11.GL_OUT_OF_MEMORY -> GLErrorKind.OUT_OF_MEMORY;
            case GL30.GL_INVALID_FRAMEBUFFER_OPERATION -> GLErrorKind.INVALID_FRAMEBUFFER_OPERATION;
            default -> GLErrorKind.UNKNOWN;
        };
    }

}
