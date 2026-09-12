// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.ColorInternalFormat;
import com.schmaloogium.engine.gl.DepthAttachmentFormat;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.PixelLayout;
import com.schmaloogium.engine.gl.PixelType;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.gl.TextureData;
import com.schmaloogium.engine.gl.TextureExtent;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.gl.TextureParameters;
import com.schmaloogium.engine.gl.TextureService;
import com.schmaloogium.engine.gl.TextureSpec;
import com.schmaloogium.engine.gl.TextureSwizzle;

import net.minecraft.client.renderer.GlStateManager;

import org.lwjgl.opengl.GL11;
import com.schmaloogium.engine.gl.TextureCompareMode;
import com.schmaloogium.engine.gl.TextureMagFilter;
import com.schmaloogium.engine.gl.TextureMinFilter;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The LWJGL3 texture service (PHASE_1_DOC §4.7.7, §4.7.7a, [D-P1-71], [D-P1-29]).
 *
 * <p>Lifetime law: logical create yields no native texture; the first admitted
 * allocation materializes it, applying the retained debug label; deleting an
 * unmaterialized handle retires it without a native delete, and deleting a bound
 * materialized texture never resurrects its name.
 *
 * <p>Allocation/upload are zero-disturbance: active unit, the touched target binding,
 * unpack-PBO and every touched unpack setting are saved and restored in {@code finally}.
 * State issued through {@code GlStateManager} where the doc's table says so
 * ([D-P1-29]): {@code bindToUnit} goes through {@code setActiveTexture}/
 * {@code bindTexture}; the other targets' binds use raw {@code glBindTexture} because
 * the vanilla cache models only the 2D slot.
 */
final class Lwjgl3TextureService implements TextureService {

    private static final int FIXED_UNIT_COUNT = 16;

    private final Lwjgl3GLDevice device;
    private final GLCapabilityProfile profile;
    private final AtomicInteger sequence = new AtomicInteger();

    Lwjgl3TextureService(Lwjgl3GLDevice device) {
        this.device = device;
        this.profile = device.capabilities();
    }

    private String label(String debugLabel) {
        return debugLabel != null ? debugLabel : "texture-" + sequence.incrementAndGet();
    }

    // ------------------------------------------------------------- create/allocate

    @Override
    public TextureHandle create(String debugLabel) {
        // No render-thread requirement: logical creation touches no GL ([D-P1-71]).
        return new Lwjgl3OwnedTexture(device, label(debugLabel));
    }

    @Override
    public void allocate(TextureHandle t, TextureSpec spec) {
        device.requireRenderThread("textures.allocate");
        Lwjgl3OwnedTexture tex = device.ownedTextureOf(t, "textures.allocate");
        if (spec == null) {
            throw new IllegalArgumentException("textures.allocate: spec must not be null");
        }
        // Reallocation within the same fixed target is legal; a target change is not.
        // The sealed interface carries no accessors; the records do (PHASE_1_DOC §4.7.7).
        final TextureAllocationTarget target;
        final TextureExtent extent;
        final int mipLevels;
        if (spec instanceof TextureSpec.ColorTextureSpec colorSpec) {
            target = colorSpec.target();
            extent = colorSpec.extent();
            mipLevels = colorSpec.mipLevels();
        } else if (spec instanceof TextureSpec.DepthTextureSpec depthSpec) {
            target = depthSpec.target();
            extent = depthSpec.extent();
            mipLevels = depthSpec.mipLevels();
        } else {
            throw new IllegalArgumentException("textures.allocate: unknown spec form");
        }
        if (tex.target != null && target != tex.target) {
            throw new IllegalStateException("textures.allocate: target already fixed to " + tex.target);
        }
        checkLimits(target, extent.width(), extent.height(), extent.depth());
        if (spec instanceof TextureSpec.DepthTextureSpec depthSpec
                && depthSpec.format() == DepthAttachmentFormat.DEPTH_COMPONENT
                && !profile.hasExtension("GL_ARB_depth_texture") && !profile.atLeast(1, 4)) {
            throw new UnsupportedOperationException(
                    "textures.allocate: depth textures need GL_ARB_depth_texture or GL 1.4+");
        }
        int name = materialize(tex);
        // Save everything the definition touches; restore in finally.
        int savedActiveUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        int savedBinding = targetBinding(target);
        boolean pboSupported = profile.atLeast(2, 1);
        int savedPbo = pboSupported ? GL21.glGetInteger(GL21.GL_PIXEL_UNPACK_BUFFER_BINDING) : 0;
        int[] unpack = saveUnpackSettings();
        try {
            if (pboSupported) {
                GL15Bind.pixelUnpack(0); // null means null, never offset 0 in a borrowed PBO
            }
            GlStateManager.setActiveTexture(savedActiveUnit);
            bindForWork(GlNames.glTextureTarget(target), name);
            defineStorage(tex, spec, target, extent, mipLevels);
            if (device.recordsLabels()) {
                Lwjgl3DebugService.applyLabelIfPossible(device, GL11.GL_TEXTURE, name, tex.subjectLabel());
            }
        } finally {
            restoreUnpackSettings(unpack);
            if (pboSupported) {
                GL15Bind.pixelUnpack(savedPbo);
            }
            GlStateManager.setActiveTexture(savedActiveUnit);
            if (target == TextureAllocationTarget.TEXTURE_2D) {
                GlStateManager.bindTexture(savedBinding);
            } else {
                GL11.glBindTexture(GlNames.glTextureTarget(target), savedBinding);
            }
        }
        if (tex.target == null) {
            tex.target = target;
            tex.allocatedWidth = extent.width();
            tex.allocatedHeight = extent.height();
            tex.allocatedDepth = extent.depth();
            tex.mipLevels = mipLevels;
            if (spec instanceof TextureSpec.ColorTextureSpec colorSpec) {
                tex.colorFormat = colorSpec.format();
            } else if (spec instanceof TextureSpec.DepthTextureSpec depthSpec) {
                tex.depthFormat = depthSpec.format();
                tex.levelZeroDepthStencil = depthSpec.format() == DepthAttachmentFormat.DEPTH24_STENCIL8;
            }
        }
        device.noteMutation("textures.allocate", tex.subjectLabel());
    }

    private int materialize(Lwjgl3OwnedTexture tex) {
        if (!tex.materialized()) {
            tex.materialize(GL11.glGenTextures());
        }
        return tex.glName();
    }

    private void checkLimits(TextureAllocationTarget target, int w, int h, int d) {
        int max2D = profile.maxTextureSize();
        switch (target) {
            case TEXTURE_1D -> rejectOver("1D width", w, max2D);
            case TEXTURE_2D -> {
                rejectOver("2D width", w, max2D);
                rejectOver("2D height", h, max2D);
            }
            case TEXTURE_3D -> {
                if (profile.max3DTextureSize() <= 0) {
                    throw new UnsupportedOperationException(
                            "textures.allocate: TEXTURE_3D unsupported (no 3D texture entry points)");
                }
                rejectOver("3D width", w, profile.max3DTextureSize());
                rejectOver("3D height", h, profile.max3DTextureSize());
                rejectOver("3D depth", d, profile.max3DTextureSize());
            }
            case RECTANGLE -> {
                int maxRect = profile.maxRectangleTextureSize();
                if (maxRect <= 0) {
                    throw new UnsupportedOperationException(
                            "textures.allocate: RECTANGLE unsupported on this profile");
                }
                rejectOver("rectangle width", w, maxRect);
                rejectOver("rectangle height", h, maxRect);
            }
        }
    }

    private void rejectOver(String axis, int value, int max) {
        if (value > max) {
            throw new IllegalArgumentException(
                    "textures.allocate: " + axis + " " + value + " exceeds limit " + max);
        }
    }

    private void defineStorage(Lwjgl3OwnedTexture tex, TextureSpec spec,
                               TextureAllocationTarget target, TextureExtent extent, int mipLevels) {
        int glTarget = GlNames.glTextureTarget(target);
        if (spec instanceof TextureSpec.ColorTextureSpec colorSpec) {
            int internal = GlNames.glColorInternalFormat(colorSpec.format());
            PixelLayout.Color layout = colorSpec.allocationLayout();
            int format = GlNames.glPixelFormat(layout.format());
            int type = GlNames.glPixelType(layout.type());
            for (int level = 0; level < mipLevels; level++) {
                int w = Lwjgl3OwnedTexture.levelDimension(extent.width(), level);
                switch (target) {
                    case TEXTURE_1D -> GL11.glTexImage1D(glTarget, level, internal, w, 0, format, type, 0L);
                    case TEXTURE_2D, RECTANGLE -> GL11.glTexImage2D(glTarget, level, internal,
                            w, Lwjgl3OwnedTexture.levelDimension(extent.height(), level),
                            0, format, type, 0L);
                    case TEXTURE_3D -> GL12.glTexImage3D(glTarget, level, internal, w,
                            Lwjgl3OwnedTexture.levelDimension(extent.height(), level),
                            Lwjgl3OwnedTexture.levelDimension(extent.depth(), level),
                            0, format, type, 0L);
                }
            }
        } else if (spec instanceof TextureSpec.DepthTextureSpec depthSpec) {
            int internal = GlNames.glDepthInternalFormat(depthSpec.format());
            GL11.glTexImage2D(glTarget, 0, internal,
                    extent.width(), extent.height(), 0,
                    GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, 0L);
        }
    }
    // ------------------------------------------------------------- parameters

    /**
     * The working bind of an owned texture. 2D binds go through GlStateManager so its
     * per-unit cache tracks them: a raw bind followed by a cached "restore" left the cache
     * believing the saved name was bound while the working texture stayed bound, and the
     * next cached bind of that saved name was skipped (Task B fix-up 2026-09-11 - the
     * composite chain sampled the wrong ping-pong side).
     */
    private static void bindForWork(int glTarget, int name) {
        if (glTarget == GL11.GL_TEXTURE_2D) {
            GlStateManager.bindTexture(name);
        } else {
            GL11.glBindTexture(glTarget, name);
        }
    }

    @Override
    public void setParameters(TextureHandle t, TextureParameters p) {
        device.requireRenderThread("textures.setParameters");
        Lwjgl3OwnedTexture tex = device.ownedTextureOf(t, "textures.setParameters");
        if (p == null) {
            throw new IllegalArgumentException("textures.setParameters: parameters must not be null");
        }
        if (!tex.materialized()) {
            throw new IllegalStateException("textures.setParameters: texture has no storage yet");
        }
        validateParametersFor(tex, p);
        int savedActiveUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        int savedBinding = targetBinding(tex.target);
        try {
            GlStateManager.setActiveTexture(savedActiveUnit);
            int glTarget = GlNames.glTextureTarget(tex.target);
            bindForWork(glTarget, tex.glName());
            applyParameters(tex, glTarget, p);
        } finally {
            GlStateManager.setActiveTexture(savedActiveUnit);
            if (tex.target == TextureAllocationTarget.TEXTURE_2D) {
                GlStateManager.bindTexture(savedBinding);
            } else {
                GL11.glBindTexture(GlNames.glTextureTarget(tex.target), savedBinding);
            }
        }
        device.noteMutation("textures.setParameters", tex.subjectLabel());
    }

    /** Capability/type matrix validated before any native call (D-P1-52). */
    private void validateParametersFor(Lwjgl3OwnedTexture tex, TextureParameters p) {
        if (tex.target == TextureAllocationTarget.RECTANGLE) {
            if (p.minFilter() != TextureMinFilter.NEAREST && p.minFilter() != TextureMinFilter.LINEAR) {
                throw new IllegalArgumentException(
                        "textures.setParameters: rectangle textures reject mipmap min filters");
            }
            if (p.baseLevel() != 0 || p.maxLevel() != 1000) {
                throw new IllegalArgumentException(
                        "textures.setParameters: rectangle textures reject non-default level bounds");
            }
        }
        boolean integer = tex.colorFormat != null && GlNames.integerFormat(tex.colorFormat);
        if (integer) {
            if (p.magFilter() != TextureMagFilter.NEAREST) {
                throw new IllegalArgumentException(
                        "textures.setParameters: integer textures require NEAREST magnification");
            }
            if (p.minFilter() != TextureMinFilter.NEAREST) {
                throw new IllegalArgumentException(
                        "textures.setParameters: integer textures require NEAREST minification");
            }
        }
        if (!tex.isDepth()) {
            if (p.compareMode() != TextureCompareMode.NONE) {
                throw new IllegalArgumentException(
                        "textures.setParameters: compare mode requires a depth or packed texture");
            }
            if (p.swizzle() == TextureSwizzle.LEGACY_DEPTH_LUMINANCE) {
                throw new IllegalArgumentException(
                        "textures.setParameters: LEGACY_DEPTH_LUMINANCE swizzle requires a depth texture");
            }
        }
    }

    private com.schmaloogium.engine.gl.TextureCompareMode TextureCompareModeNone() {
        return com.schmaloogium.engine.gl.TextureCompareMode.NONE;
    }

    private void applyParameters(Lwjgl3OwnedTexture tex, int glTarget, TextureParameters p) {
        GL11.glTexParameteri(glTarget, GL11.GL_TEXTURE_MIN_FILTER, GlNames.glMinFilter(p.minFilter()));
        GL11.glTexParameteri(glTarget, GL11.GL_TEXTURE_MAG_FILTER, GlNames.glMagFilter(p.magFilter()));
        GL11.glTexParameteri(glTarget, GL11.GL_TEXTURE_WRAP_S, GlNames.glWrap(p.wrapS()));
        GL11.glTexParameteri(glTarget, GL11.GL_TEXTURE_WRAP_T, GlNames.glWrap(p.wrapT()));
        if (tex.target == TextureAllocationTarget.TEXTURE_3D) {
            GL11.glTexParameteri(glTarget, GL12.GL_TEXTURE_WRAP_R, GlNames.glWrap(p.wrapR()));
        }
        if (tex.isDepth()) {
            GL11.glTexParameteri(glTarget, GL14.GL_TEXTURE_COMPARE_MODE, GlNames.glCompareMode(p.compareMode()));
            GL11.glTexParameteri(glTarget, GL14.GL_TEXTURE_COMPARE_FUNC, GlNames.glCompareFunction(p.compareFunction()));
        }
        // Border: exactly (0,0,0,0) so no unknown integer border state enters (D-P1-64).
        GL11.glTexParameterfv(glTarget, GL11.GL_TEXTURE_BORDER_COLOR, zeroBorder());
        GL11.glTexParameterf(glTarget, GL14.GL_TEXTURE_LOD_BIAS, p.lodBias());
        GL11.glTexParameteri(glTarget, GL30.GL_TEXTURE_BASE_LEVEL, p.baseLevel());
        GL11.glTexParameteri(glTarget, GL30.GL_TEXTURE_MAX_LEVEL, p.maxLevel());
        if (p.maxAnisotropy() == 1f && profile.hasExtension("GL_EXT_texture_filter_anisotropic")) {
            GL11.glTexParameterf(glTarget, EXT_ANISOTROPY, 1.0f);
        }
        if (p.swizzle() == TextureSwizzle.LEGACY_DEPTH_LUMINANCE) {
            if (profile.atLeast(3, 3) || profile.hasExtension("GL_ARB_texture_swizzle")
                    || profile.hasExtension("GL_EXT_texture_swizzle")) {
                GL33.glTexParameteriv(glTarget, GL33.GL_TEXTURE_SWIZZLE_RGBA, legacyLuminanceSwizzle());
            } else if (profile.hasExtension("GL_ARB_depth_texture") || profile.atLeast(1, 4)) {
                GL11.glTexParameteri(glTarget, GL14.GL_DEPTH_TEXTURE_MODE, GL11.GL_LUMINANCE);
            }
        }
    }

    private static final int EXT_ANISOTROPY = 0x84FE; // GL_TEXTURE_MAX_ANISOTROPY_EXT

    private static float[] zeroBorder() {
        return new float[]{0f, 0f, 0f, 0f};
    }

    /** Luminance-replicating swizzle: RGBA swizzles of (RED, RED, RED, ONE). */
    private static int[] legacyLuminanceSwizzle() {
        return new int[]{GL11.GL_RED, GL11.GL_RED, GL11.GL_RED, GL11.GL_ONE};
    }

    // ------------------------------------------------------------- upload

    @Override
    public void upload(TextureHandle t, TextureData data) {
        device.requireRenderThread("textures.upload");
        Lwjgl3OwnedTexture tex = device.ownedTextureOf(t, "textures.upload");
        if (!tex.materialized()) {
            throw new IllegalStateException("textures.upload: texture has no storage yet");
        }
        if (data.target() != tex.target) {
            throw new IllegalArgumentException(
                    "textures.upload: data target " + data.target() + " != allocation target " + tex.target);
        }
        if (data.mipLevel() < 0 || data.mipLevel() >= tex.mipLevels) {
            throw new IllegalArgumentException("textures.upload: mip level " + data.mipLevel() + " unallocated");
        }
        int levelW = Lwjgl3OwnedTexture.levelDimension(tex.allocatedWidth, data.mipLevel());
        int levelH = Lwjgl3OwnedTexture.levelDimension(tex.allocatedHeight, data.mipLevel());
        int levelD = Lwjgl3OwnedTexture.levelDimension(tex.allocatedDepth, data.mipLevel());
        com.schmaloogium.engine.gl.TextureRegion r = data.region();
        if (r.x() + r.width() > levelW || r.y() + r.height() > levelH || r.z() + r.depth() > levelD) {
            throw new IllegalArgumentException("textures.upload: region does not fit the allocated level");
        }
        if (data.layout() instanceof PixelLayout.Depth && tex.target != TextureAllocationTarget.TEXTURE_2D) {
            throw new IllegalArgumentException("textures.upload: depth layout admits only TEXTURE_2D");
        }
        long volume = (long) r.width() * r.height() * r.depth();
        int bpp = bytesPerPixel(data.layout());
        long expected = volume * bpp;
        ByteBuffer src = data.texels();
        if (src.remaining() != expected) {
            throw new IllegalArgumentException("textures.upload: payload is " + src.remaining()
                    + " bytes, expected " + expected);
        }
        int savedActiveUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        int savedBinding = targetBinding(tex.target);
        int[] unpack = saveUnpackSettings();
        boolean pboSupported = profile.atLeast(2, 1);
        int savedPbo = pboSupported ? GL21.glGetInteger(GL21.GL_PIXEL_UNPACK_BUFFER_BINDING) : 0;
        try {
            // Engine producers hand over any ByteBuffer (heap, wrapped, big-endian); LWJGL
            // needs a direct address, and GL decodes multi-byte texels in native order, so
            // a foreign byte order is declared through UNPACK_SWAP_BYTES rather than copied.
            GL11.glPixelStorei(GL11.GL_UNPACK_SWAP_BYTES,
                    src.order() == java.nio.ByteOrder.nativeOrder() ? 0 : 1);
            GL11.glPixelStorei(GL11.GL_UNPACK_LSB_FIRST, 0);
            GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, r.width());
            GL11.glPixelStorei(GL12.GL_UNPACK_IMAGE_HEIGHT, r.height());
            GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, r.y());
            GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, r.x());
            GL11.glPixelStorei(GL12.GL_UNPACK_SKIP_IMAGES, r.z());
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            if (pboSupported) {
                GL15Bind.pixelUnpack(0);
            }
            GlStateManager.setActiveTexture(savedActiveUnit);
            bindForWork(GlNames.glTextureTarget(tex.target), tex.glName());
            issueSubImage(tex, data, directTexels(src));
        } finally {
            restoreUnpackSettings(unpack);
            if (pboSupported) {
                GL15Bind.pixelUnpack(savedPbo);
            }
            GlStateManager.setActiveTexture(savedActiveUnit);
            if (tex.target == TextureAllocationTarget.TEXTURE_2D) {
                GlStateManager.bindTexture(savedBinding);
            } else {
                GL11.glBindTexture(GlNames.glTextureTarget(tex.target), savedBinding);
            }
        }
        device.noteMutation("textures.upload", tex.subjectLabel());
    }

    private static int bytesPerPixel(PixelLayout layout) {
        if (layout instanceof PixelLayout.Color c) {
            return GlNames.bytesPerPixel(c.format(), c.type());
        }
        return 4; // both depth transfer layouts are 4 bytes per texel
    }

    /** The remaining bytes as a direct buffer (a slice when already direct, else a copy). */
    private static ByteBuffer directTexels(ByteBuffer src) {
        ByteBuffer remaining = src.slice();
        if (remaining.isDirect()) {
            return remaining;
        }
        ByteBuffer direct = org.lwjgl.BufferUtils.createByteBuffer(remaining.remaining());
        direct.put(remaining.duplicate());
        direct.flip();
        return direct;
    }

    private void issueSubImage(Lwjgl3OwnedTexture tex, TextureData data, ByteBuffer texels) {
        var r = data.region();
        int glTarget = GlNames.glTextureTarget(tex.target);
        int level = data.mipLevel();
        if (data.layout() instanceof PixelLayout.Color c) {
            int format = GlNames.glPixelFormat(c.format());
            int type = GlNames.glPixelType(c.type());
            switch (tex.target) {
                case TEXTURE_1D -> GL11.glTexSubImage1D(glTarget, level, r.x(), r.width(), format, type, texels);
                case TEXTURE_2D, RECTANGLE -> GL11.glTexSubImage2D(glTarget, level, r.x(), r.y(),
                        r.width(), r.height(), format, type, texels);
                case TEXTURE_3D -> GL12.glTexSubImage3D(glTarget, level, r.x(), r.y(), r.z(),
                        r.width(), r.height(), r.depth(), format, type, texels);
            }
        } else if (data.layout() instanceof PixelLayout.Depth d) {
            int format = d.value() == com.schmaloogium.engine.gl.DepthTransferLayout.DEPTH_STENCIL_UNSIGNED_INT_24_8
                    ? GL30.GL_DEPTH_STENCIL
                    : GL11.GL_DEPTH_COMPONENT;
            int type = d.value() == com.schmaloogium.engine.gl.DepthTransferLayout.DEPTH_STENCIL_UNSIGNED_INT_24_8
                    ? GL30.GL_UNSIGNED_INT_24_8
                    : GL11.GL_FLOAT;
            GL11.glTexSubImage2D(glTarget, level, r.x(), r.y(), r.width(), r.height(), format, type, texels);
        }
    }

    // ------------------------------------------------------------- unit bindings

    @Override
    public void prepareUnitBindings(int occupiedUnitMask) {
        device.requireRenderThread("textures.prepareUnitBindings");
        if ((occupiedUnitMask & ~0xFFFF) != 0) {
            throw new IllegalArgumentException(
                    "textures.prepareUnitBindings: mask " + Integer.toHexString(occupiedUnitMask)
                            + " exceeds the 16 fixed units");
        }
        // Strategy NONE owns no native sampler objects; the clear is a no-op.
        device.noteMutation("textures.prepareUnitBindings", "(units)");
    }

    @Override
    public void bindToUnit(int unit, TextureHandle t) {
        device.requireRenderThread("textures.bindToUnit");
        if (unit < 0 || unit >= FIXED_UNIT_COUNT) {
            throw new IllegalArgumentException(
                    "textures.bindToUnit: unit " + unit + " outside the fixed 16-unit map");
        }
        int glName = resolveForBind(t, "textures.bindToUnit");
        int glTarget = bindTargetOf(t);
        // [D-P1-29]: active-unit selection and 2D binds through GlStateManager's cached
        // path; other targets bind raw because the vanilla cache models only the 2D slot.
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0 + unit);
        if (glTarget == GL11.GL_TEXTURE_2D) {
            GlStateManager.bindTexture(glName);
        } else {
            GL11.glBindTexture(glTarget, glName);
        }
        device.noteMutation("textures.bindToUnit", ((Lwjgl3Handle) t).subjectLabel());
    }

    private int resolveForBind(TextureHandle t, String verb) {
        if (t instanceof Lwjgl3OwnedTexture owned) {
            if (owned.owner() != device || owned.deleted() || !owned.materialized()) {
                throw new IllegalStateException(verb + ": unmaterialized, foreign or deleted owned texture");
            }
            return owned.glName();
        }
        if (t instanceof Lwjgl3ForeignTexture foreign) {
            if (foreign.owner() != device) {
                throw new IllegalArgumentException(verb + ": wrong-device foreign texture");
            }
            Integer name = foreign.resolve().orElse(null);
            if (name == null) {
                device.noteMutation(verb, foreign.subjectLabel());
                device.queueError(verb, foreign.subjectLabel(),
                        com.schmaloogium.engine.gl.GLErrorKind.INVALID_OPERATION,
                        "platform no longer holds a texture under key " + foreign.key());
                return 0;
            }
            return name;
        }
        if (t instanceof Lwjgl3BorrowedDepth borrowed) {
            Integer name = borrowed.resolve().orElse(null);
            if (name == null) {
                throw new IllegalStateException(verb + ": borrowed depth no longer resolvable");
            }
            return name;
        }
        throw new IllegalArgumentException(verb + ": foreign handle value (not issued by this backend)");
    }

    private int bindTargetOf(TextureHandle t) {
        if (t instanceof Lwjgl3OwnedTexture owned) {
            return GlNames.glTextureTarget(owned.target);
        }
        return GL11.GL_TEXTURE_2D; // vanilla platform textures are 2D
    }

    // ------------------------------------------------------------- mipmaps/delete

    @Override
    public void generateMipmap(TextureHandle t) {
        device.requireRenderThread("textures.generateMipmap");
        Lwjgl3OwnedTexture tex = device.ownedTextureOf(t, "textures.generateMipmap");
        if (!tex.materialized()) {
            throw new IllegalStateException("textures.generateMipmap: texture has no storage yet");
        }
        if (tex.target == TextureAllocationTarget.RECTANGLE) {
            throw new UnsupportedOperationException("textures.generateMipmap: rectangle textures reject generation");
        }
        if (!profile.atLeast(3, 0) && !profile.hasExtension("GL_EXT_framebuffer_object")
                && !profile.hasExtension("GL_ARB_framebuffer_object")) {
            throw new UnsupportedOperationException("textures.generateMipmap: no GL 3.0/FBO generation entry point");
        }
        int savedActiveUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        int savedBinding = targetBinding(tex.target);
        try {
            GlStateManager.setActiveTexture(savedActiveUnit);
            int glTarget = GlNames.glTextureTarget(tex.target);
            bindForWork(glTarget, tex.glName());
            GL30.glGenerateMipmap(glTarget);
            tex.mipLevels = fullChainLevels(tex.allocatedWidth, tex.allocatedHeight, tex.target, tex.allocatedDepth);
        } finally {
            GlStateManager.setActiveTexture(savedActiveUnit);
            if (tex.target == TextureAllocationTarget.TEXTURE_2D) {
                GlStateManager.bindTexture(savedBinding);
            } else {
                GL11.glBindTexture(GlNames.glTextureTarget(tex.target), savedBinding);
            }
        }
        device.noteMutation("textures.generateMipmap", tex.subjectLabel());
    }

    private static int fullChainLevels(int w, int h, TextureAllocationTarget target, int depth) {
        int max = switch (target) {
            case TEXTURE_1D -> w;
            case TEXTURE_3D -> Math.max(w, Math.max(h, Math.max(1, depth)));
            default -> Math.max(w, h);
        };
        return 32 - Integer.numberOfLeadingZeros(Math.max(1, max));
    }

    @Override
    public void delete(TextureHandle t) {
        device.requireRenderThread("textures.delete");
        if (!(t instanceof Lwjgl3OwnedTexture owned) || owned.owner() != device) {
            throw new IllegalArgumentException(
                    "textures.delete: foreign textures are never lifetime-managed by this backend");
        }
        if (owned.deleted()) {
            return;
        }
        if (owned.materialized()) {
            sweepBindingsAndDelete(owned);
        }
        owned.markDeleted();
    }

    /**
     * Lifetime-ending native deletion (PHASE_1_DOC §4.7.7): affected bindings and cache
     * entries become zero, unrelated bindings remain, the active unit is preserved, and
     * no deleted name is rebound.
     */
    private void sweepBindingsAndDelete(Lwjgl3OwnedTexture tex) {
        int savedActiveUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        int glTarget = GlNames.glTextureTarget(tex.target);
        try {
            for (int unit = 0; unit < FIXED_UNIT_COUNT; unit++) {
                int unitEnum = GL13.GL_TEXTURE0 + unit;
                GlStateManager.setActiveTexture(unitEnum);
                if (glTarget == GL11.GL_TEXTURE_2D) {
                    if (GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D) == tex.glName()) {
                        GlStateManager.bindTexture(0);
                    }
                } else if (GL11.glGetInteger(bindingEnumFor(glTarget)) == tex.glName()) {
                    GL11.glBindTexture(glTarget, 0);
                }
            }
        } finally {
            GlStateManager.setActiveTexture(savedActiveUnit);
        }
        GL11.glDeleteTextures(tex.glName());
        device.noteMutation("textures.delete", tex.subjectLabel());
    }

    private static int bindingEnumFor(int glTarget) {
        return switch (glTarget) {
            case GL11.GL_TEXTURE_1D -> GL11.GL_TEXTURE_BINDING_1D;
            case GL11.GL_TEXTURE_2D -> GL11.GL_TEXTURE_BINDING_2D;
            case GL12.GL_TEXTURE_3D -> GL12.GL_TEXTURE_BINDING_3D;
            case GL31.GL_TEXTURE_RECTANGLE -> GL31.GL_TEXTURE_BINDING_RECTANGLE;
            default -> GL11.GL_TEXTURE_BINDING_2D;
        };
    }

    private int targetBinding(TextureAllocationTarget target) {
        return GL11.glGetInteger(bindingEnumFor(GlNames.glTextureTarget(target)));
    }

    private static int[] saveUnpackSettings() {
        return new int[]{
                GL11.glGetInteger(GL11.GL_UNPACK_SWAP_BYTES),
                GL11.glGetInteger(GL11.GL_UNPACK_LSB_FIRST),
                GL11.glGetInteger(GL11.GL_UNPACK_ROW_LENGTH),
                GL11.glGetInteger(GL12.GL_UNPACK_IMAGE_HEIGHT),
                GL11.glGetInteger(GL11.GL_UNPACK_SKIP_ROWS),
                GL11.glGetInteger(GL11.GL_UNPACK_SKIP_PIXELS),
                GL11.glGetInteger(GL12.GL_UNPACK_SKIP_IMAGES),
                GL11.glGetInteger(GL11.GL_UNPACK_ALIGNMENT),
        };
    }

    private static void restoreUnpackSettings(int[] s) {
        GL11.glPixelStorei(GL11.GL_UNPACK_SWAP_BYTES, s[0]);
        GL11.glPixelStorei(GL11.GL_UNPACK_LSB_FIRST, s[1]);
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, s[2]);
        GL11.glPixelStorei(GL12.GL_UNPACK_IMAGE_HEIGHT, s[3]);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, s[4]);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, s[5]);
        GL11.glPixelStorei(GL12.GL_UNPACK_SKIP_IMAGES, s[6]);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, s[7]);
    }

    /** Indirection so the PBO constant resolves only on GL 2.1+ class loading paths. */
    private static final class GL15Bind {
        static void pixelUnpack(int buffer) {
            org.lwjgl.opengl.GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, buffer);
        }
    }
}
