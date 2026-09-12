// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.BlitSpec;
import com.schmaloogium.engine.gl.BorrowedDepthAttachmentHandle;
import com.schmaloogium.engine.gl.ColorClearValue;
import com.schmaloogium.engine.gl.DepthAttachmentFormat;
import com.schmaloogium.engine.gl.FramebufferDrawSlot;
import com.schmaloogium.engine.gl.FramebufferHandle;
import com.schmaloogium.engine.gl.FramebufferService;
import com.schmaloogium.engine.gl.FramebufferStatus;
import com.schmaloogium.engine.gl.FramebufferTarget;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.gl.TextureRegion;

import net.minecraft.client.renderer.GlStateManager;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The LWJGL3 framebuffer service (PHASE_1_DOC §4.7.5). Cross-object verbs wrap a
 * live same-device ordering defined by the receiving device; wrong-device, wrong-context,
 * forged, deleted and otherwise unknown values are rejected with
 * {@link IllegalArgumentException} before any binding mutation. Every verb that binds a
 * framebuffer restores the previous read/draw bindings in {@code finally}.
 */
final class Lwjgl3FramebufferService implements FramebufferService {

    private final Lwjgl3GLDevice device;
    private final GLCapabilityProfile profile;
    private final AtomicInteger sequence = new AtomicInteger();

    Lwjgl3FramebufferService(Lwjgl3GLDevice device) {
        this.device = device;
        this.profile = device.capabilities();
    }

    // ------------------------------------------------------------- creation/borrow

    @Override
    public FramebufferHandle create(String debugLabel) {
        device.requireRenderThread("framebuffers.create");
        int name = GL30.glGenFramebuffers();
        String label = debugLabel != null ? debugLabel : "framebuffer-" + sequence.incrementAndGet();
        device.noteMutation("framebuffers.create", label);
        return new Lwjgl3FramebufferHandle(device, name, label);
    }

    @Override
    public BorrowedDepthAttachmentHandle borrowDepthAttachment(TextureHandle platformTexture) {
        device.requireRenderThread("framebuffers.borrowDepthAttachment");
        if (!(platformTexture instanceof Lwjgl3ForeignTexture foreign)
                || foreign.owner() != device) {
            throw new IllegalArgumentException(
                    "framebuffers.borrowDepthAttachment: issuer must recognize a live ordinary foreign texture");
        }
        // The v0.1 issuer cannot prove the packed format; the main-depth bridge is Phase 5's.
        return new Lwjgl3BorrowedDepth(device, foreign, false);
    }

    // ------------------------------------------------------------- attachment

    @Override
    public void attachColor(FramebufferHandle f, int attachmentIndex, TextureHandle t) {
        device.requireRenderThread("framebuffers.attachColor");
        Lwjgl3FramebufferHandle fb = device.framebufferOf(f, "framebuffers.attachColor");
        Lwjgl3OwnedTexture tex = colorAttachable(t, "framebuffers.attachColor");
        int maxAttachments = profile.maxColorAttachments();
        if (attachmentIndex < 0 || attachmentIndex >= maxAttachments) {
            throw new IllegalArgumentException("framebuffers.attachColor: attachment index "
                    + attachmentIndex + " outside capability range 0-" + (maxAttachments - 1));
        }
        if (!tex.materialized() || tex.isDepth()) {
            throw new IllegalArgumentException(
                    "framebuffers.attachColor: texture must be an allocated color texture");
        }
        int savedRead = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        int savedDraw = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        try {
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fb.glName());
            GL30.glFramebufferTexture2D(GL30.GL_DRAW_FRAMEBUFFER,
                    GL30.GL_COLOR_ATTACHMENT0 + attachmentIndex,
                    GlNames.glTextureTarget(tex.target), tex.glName(), 0);
        } finally {
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, savedRead);
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, savedDraw);
        }
        while (fb.colorAttachments.size() <= attachmentIndex) {
            fb.colorAttachments.add(null);
        }
        fb.colorAttachments.set(attachmentIndex, tex);
        device.noteMutation("framebuffers.attachColor", fb.subjectLabel());
    }

    @Override
    public void attachDepth(FramebufferHandle f, TextureHandle t) {
        device.requireRenderThread("framebuffers.attachDepth");
        Lwjgl3FramebufferHandle fb = device.framebufferOf(f, "framebuffers.attachDepth");
        int glName = depthAttachableName(t, "framebuffers.attachDepth", false);
        int target = depthAttachableTarget(t);
        bindAndRestore(fb, () -> {
            GL30.glFramebufferTexture2D(GL30.GL_DRAW_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                    target, glName, 0);
            GL30.glFramebufferTexture2D(GL30.GL_DRAW_FRAMEBUFFER, GL30.GL_STENCIL_ATTACHMENT,
                    GL11.GL_TEXTURE_2D, 0, 0);
        });
        fb.depthAttachment = t instanceof Lwjgl3OwnedTexture owned ? owned : null;
        fb.stencilAttached = false;
        device.noteMutation("framebuffers.attachDepth", fb.subjectLabel());
    }

    @Override
    public void attachDepthStencil(FramebufferHandle f, TextureHandle t) {
        device.requireRenderThread("framebuffers.attachDepthStencil");
        Lwjgl3FramebufferHandle fb = device.framebufferOf(f, "framebuffers.attachDepthStencil");
        final boolean packed;
        final int glName;
        final int target;
        if (t instanceof Lwjgl3OwnedTexture owned && owned.owner() == device && !owned.deleted()) {
            if (owned.depthFormat != DepthAttachmentFormat.DEPTH24_STENCIL8) {
                throw new IllegalArgumentException(
                        "framebuffers.attachDepthStencil: owned texture is not DEPTH24_STENCIL8 storage");
            }
            packed = true;
            glName = owned.glName();
            target = GlNames.glTextureTarget(owned.target);
        } else if (t instanceof Lwjgl3BorrowedDepth borrowed && borrowed.owner() == device) {
            if (!borrowed.provesPackedDepthStencil()) {
                throw new IllegalArgumentException(
                        "framebuffers.attachDepthStencil: borrowed handle carries no authenticated "
                                + "DEPTH24_STENCIL8 metadata");
            }
            packed = true;
            glName = borrowed.resolve().orElseThrow(() -> new IllegalStateException(
                    "framebuffers.attachDepthStencil: borrowed depth no longer resolvable"));
            target = GL11.GL_TEXTURE_2D;
        } else {
            throw new IllegalArgumentException(
                    "framebuffers.attachDepthStencil: handle is neither an owned packed-depth texture "
                            + "nor an authenticated borrowed depth of this device");
        }
        boolean finalPacked = packed;
        bindAndRestore(fb, () -> {
            if (!finalPacked) {
                throw new IllegalStateException("unreachable");
            }
            GL30.glFramebufferTexture2D(GL30.GL_DRAW_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                    target, glName, 0);
            GL30.glFramebufferTexture2D(GL30.GL_DRAW_FRAMEBUFFER, GL30.GL_STENCIL_ATTACHMENT,
                    target, glName, 0);
        });
        fb.depthAttachment = t instanceof Lwjgl3OwnedTexture owned ? owned : null;
        fb.stencilAttached = true;
        device.noteMutation("framebuffers.attachDepthStencil", fb.subjectLabel());
    }

    /** Color attachments accept only owned, live, same-device textures (D-P1-41). */
    private Lwjgl3OwnedTexture colorAttachable(TextureHandle t, String verb) {
        if (!(t instanceof Lwjgl3OwnedTexture owned)) {
            throw new IllegalArgumentException(verb
                    + ": foreign and borrowed values are not legal here - only owned textures attach to colors");
        }
        return device.ownedTextureOf(owned, verb);
    }

    /** Depth attachments accept owned textures or this device's borrowed depth handles. */
    private int depthAttachableName(TextureHandle t, String verb, boolean requirePacked) {
        if (t instanceof Lwjgl3OwnedTexture owned) {
            device.ownedTextureOf(owned, verb);
            if (!owned.materialized() || !owned.isDepth()) {
                throw new IllegalArgumentException(verb + ": texture must be allocated depth storage");
            }
            if (requirePacked && owned.depthFormat != DepthAttachmentFormat.DEPTH24_STENCIL8) {
                throw new IllegalArgumentException(verb + ": texture is not DEPTH24_STENCIL8 storage");
            }
            return owned.glName();
        }
        if (t instanceof Lwjgl3BorrowedDepth borrowed && borrowed.owner() == device) {
            return borrowed.resolve().orElseThrow(() ->
                    new IllegalStateException(verb + ": borrowed depth no longer resolvable"));
        }
        throw new IllegalArgumentException(verb + ": handle is neither owned nor a borrowed depth of this device");
    }

    private int depthAttachableTarget(TextureHandle t) {
        if (t instanceof Lwjgl3OwnedTexture owned && owned.target != null) {
            return GlNames.glTextureTarget(owned.target);
        }
        return GL11.GL_TEXTURE_2D;
    }

    // ------------------------------------------------------------- routes

    @Override
    public void drawBuffers(FramebufferHandle f, List<FramebufferDrawSlot> slots) {
        device.requireRenderThread("framebuffers.drawBuffers");
        Lwjgl3FramebufferHandle fb = device.framebufferOf(f, "framebuffers.drawBuffers");
        if (slots == null) {
            throw new IllegalArgumentException("framebuffers.drawBuffers: slots must not be null");
        }
        if (slots.isEmpty()) {
            // A depth-only route (the v0.1 shadow estate with no shadowcolor textures): no colour
            // draw buffer at all. Same posture as an empty sampler assignment — a no-op route,
            // never a refusal (this refusal neutralized the shadow estate on every build).
            int savedDrawOnly = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
            try {
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fb.glName());
                GL11.glDrawBuffer(GL11.GL_NONE);
                GL11.glReadBuffer(GL11.GL_NONE);
            } finally {
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, savedDrawOnly);
            }
            fb.drawRoute = List.of();
            device.noteMutation("framebuffers.drawBuffers", fb.subjectLabel());
            return;
        }
        if (slots.size() > profile.maxDrawBuffers()) {
            throw new IllegalArgumentException("framebuffers.drawBuffers: " + slots.size()
                    + " slots exceed capability maximum " + profile.maxDrawBuffers());
        }
        Set<Integer> seen = new HashSet<>();
        int[] nativeSlots = new int[slots.size()];
        for (int i = 0; i < slots.size(); i++) {
            FramebufferDrawSlot slot = slots.get(i);
            if (slot == null) {
                throw new IllegalArgumentException("framebuffers.drawBuffers: null slot at " + i);
            }
            if (slot instanceof FramebufferDrawSlot.None) {
                nativeSlots[i] = GL11.GL_NONE;
                continue;
            }
            int index = ((FramebufferDrawSlot.Attachment) slot).index();
            if (index < 0 || index >= profile.maxDrawBuffers()) {
                throw new IllegalArgumentException("framebuffers.drawBuffers: attachment index "
                        + index + " out of capability range at position " + i);
            }
            if (!seen.add(index)) {
                throw new IllegalArgumentException(
                        "framebuffers.drawBuffers: duplicate attachment " + index);
            }
            nativeSlots[i] = GL30.GL_COLOR_ATTACHMENT0 + index;
        }
        int savedDraw = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        try {
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fb.glName());
            GL30.glDrawBuffers(nativeSlots);
        } finally {
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, savedDraw);
        }
        fb.drawRoute = List.copyOf(slots);
        device.noteMutation("framebuffers.drawBuffers", fb.subjectLabel());
    }

    // ------------------------------------------------------------- bind/check

    @Override
    public FramebufferStatus check(FramebufferHandle f) {
        device.requireRenderThread("framebuffers.check");
        Lwjgl3FramebufferHandle fb = device.framebufferOf(f, "framebuffers.check");
        int savedDraw = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        try {
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fb.glName());
            FramebufferStatus status = GlNames.framebufferStatus(
                    GL30.glCheckFramebufferStatus(GL30.GL_DRAW_FRAMEBUFFER));
            device.noteMutation("framebuffers.check", fb.subjectLabel());
            return status;
        } finally {
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, savedDraw);
        }
    }

    @Override
    public void bind(FramebufferTarget target, FramebufferHandle f) {
        device.requireRenderThread("framebuffers.bind");
        Lwjgl3FramebufferHandle fb = device.framebufferOf(f, "framebuffers.bind");
        GL30.glBindFramebuffer(GlNames.glFramebufferBindingTarget(target), fb.glName());
        device.noteMutation("framebuffers.bind", fb.subjectLabel());
    }

    @Override
    public void bindDefault(FramebufferTarget target) {
        device.requireRenderThread("framebuffers.bindDefault");
        GL30.glBindFramebuffer(GlNames.glFramebufferBindingTarget(target), 0);
        device.noteMutation("framebuffers.bindDefault", "(default)");
    }

    // ------------------------------------------------------------- blit

    @Override
    public void blit(FramebufferHandle src, FramebufferHandle dst, BlitSpec spec) {
        device.requireRenderThread("framebuffers.blit");
        Lwjgl3FramebufferHandle s = device.framebufferOf(src, "framebuffers.blit");
        Lwjgl3FramebufferHandle d = device.framebufferOf(dst, "framebuffers.blit");
        int mask = 0;
        for (BlitSpec.BlitMask m : spec.masks()) {
            mask |= m == BlitSpec.BlitMask.COLOR ? GL11.GL_COLOR_BUFFER_BIT : GL11.GL_DEPTH_BUFFER_BIT;
        }
        int filter = spec.filter() == BlitSpec.BlitFilter.LINEAR
                ? GL11.GL_LINEAR : GL11.GL_NEAREST;
        int savedRead = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        int savedDraw = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        try {
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, s.glName());
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, d.glName());
            TextureRegion srcR = spec.source();
            TextureRegion dstR = spec.destination();
            GL30.glBlitFramebuffer(srcR.x(), srcR.y(), srcR.x() + srcR.width(), srcR.y() + srcR.height(),
                    dstR.x(), dstR.y(), dstR.x() + dstR.width(), dstR.y() + dstR.height(),
                    mask, filter);
        } finally {
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, savedRead);
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, savedDraw);
        }
        device.noteMutation("framebuffers.blit", s.subjectLabel());
    }

    // ------------------------------------------------------------- depth copies

    @Override
    public void initializeDepthTextureFromFramebuffer(
            FramebufferHandle src, TextureHandle dst, TextureRegion region) {
        device.requireRenderThread("framebuffers.initializeDepthTextureFromFramebuffer");
        Lwjgl3FramebufferHandle from = device.framebufferOf(src, "framebuffers.initializeDepthTextureFromFramebuffer");
        Lwjgl3OwnedTexture to = device.ownedTextureOf(dst, "framebuffers.initializeDepthTextureFromFramebuffer");
        if (!to.materialized() || !to.isDepth() || to.target != com.schmaloogium.engine.gl.TextureAllocationTarget.TEXTURE_2D) {
            throw new IllegalArgumentException(
                    "framebuffers.initializeDepthTextureFromFramebuffer: dst must be an allocated 2D depth texture");
        }
        validateSourceRegion(from, region);
        int internal = GlNames.glDepthInternalFormat(to.depthFormat);
        int savedRead = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        int savedDraw = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        int savedUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        int savedTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        try {
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, from.glName());
            GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, to.glName());
            GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, internal,
                    region.x(), region.y(), region.width(), region.height(), 0);
        } finally {
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, savedRead);
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, savedDraw);
            GlStateManager.setActiveTexture(savedUnit);
            GlStateManager.bindTexture(savedTex);
        }
        to.allocatedWidth = region.width();
        to.allocatedHeight = region.height();
        to.allocatedDepth = 1;
        to.mipLevels = 1;
        to.levelZeroDepthDefined = true;
        device.noteMutation("framebuffers.initializeDepthTextureFromFramebuffer", from.subjectLabel());
    }

    @Override
    public void copyDepthToTexture(FramebufferHandle src, TextureHandle dst, TextureRegion region) {
        device.requireRenderThread("framebuffers.copyDepthToTexture");
        Lwjgl3FramebufferHandle from = device.framebufferOf(src, "framebuffers.copyDepthToTexture");
        Lwjgl3OwnedTexture to = device.ownedTextureOf(dst, "framebuffers.copyDepthToTexture");
        // STEADY COPY ONLY: dst level zero already defined by the initialization copy,
        // same depth/packed internal format and same extent.
        if (!to.levelZeroDepthDefined) {
            throw new IllegalStateException(
                    "framebuffers.copyDepthToTexture: dst level zero was never initialized by "
                            + "initializeDepthTextureFromFramebuffer");
        }
        if (region.width() != to.allocatedWidth || region.height() != to.allocatedHeight) {
            throw new IllegalArgumentException("framebuffers.copyDepthToTexture: region "
                    + region.width() + "x" + region.height() + " does not match the initialized extent "
                    + to.allocatedWidth + "x" + to.allocatedHeight);
        }
        validateSourceRegion(from, region);
        int savedRead = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        int savedUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        int savedTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        try {
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, from.glName());
            GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, to.glName());
            GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0,
                    region.x(), region.y(), region.width(), region.height());
        } finally {
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, savedRead);
            GlStateManager.setActiveTexture(savedUnit);
            GlStateManager.bindTexture(savedTex);
        }
        device.noteMutation("framebuffers.copyDepthToTexture", from.subjectLabel());
    }

    /** The source region must be positive and in-bounds against the depth attachment. */
    private void validateSourceRegion(Lwjgl3FramebufferHandle from, TextureRegion region) {
        Lwjgl3OwnedTexture depth = from.depthAttachment;
        if (depth == null) {
            throw new IllegalArgumentException("source framebuffer has no depth attachment");
        }
        if (region.x() + region.width() > depth.allocatedWidth
                || region.y() + region.height() > depth.allocatedHeight
                || region.depth() != 1) {
            throw new IllegalArgumentException(
                    "source region does not fit the depth attachment (" + depth.allocatedWidth
                            + "x" + depth.allocatedHeight + ")");
        }
    }

    // ------------------------------------------------------------- read/clear

    @Override
    public float readDepthPixel(FramebufferHandle f, int x, int y) {
        device.requireRenderThread("framebuffers.readDepthPixel");
        Lwjgl3FramebufferHandle fb = device.framebufferOf(f, "framebuffers.readDepthPixel");
        int savedRead = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        try {
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, fb.glName());
            GL11.glReadPixels(x, y, 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, depthBuffer);
            device.noteMutation("framebuffers.readDepthPixel", fb.subjectLabel());
            return depthBuffer[0];
        } finally {
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, savedRead);
        }
    }

    private final float[] depthBuffer = new float[1];

    @Override
    public void clearColorAttachment(FramebufferHandle f, int drawBufferIndex, ColorClearValue value) {
        device.requireRenderThread("framebuffers.clearColorAttachment");
        Lwjgl3FramebufferHandle fb = device.framebufferOf(f, "framebuffers.clearColorAttachment");
        Lwjgl3OwnedTexture tex = resolveClearTarget(fb, drawBufferIndex);
        boolean integerStorage = tex.colorFormat != null && GlNames.integerFormat(tex.colorFormat);
        if (!profile.atLeast(3, 0)
                && !(integerStorage && profile.hasExtension("GL_EXT_texture_integer"))) {
            throw new UnsupportedOperationException(
                    "framebuffers.clearColorAttachment: the typed GL2+EXT legacy tier is not supported "
                            + "at v0.1 (shipping contexts are GL 4.6 compatibility profiles)");
        }
        if (integerStorage
                && !profile.atLeast(3, 0)
                && !profile.hasExtension("GL_EXT_texture_integer")) {
            throw new UnsupportedOperationException(
                    "framebuffers.clearColorAttachment: integer clears need GL 3.0 or GL_EXT_texture_integer");
        }
        if (!integerStorage && !(value instanceof ColorClearValue.Floating)) {
            throw new IllegalArgumentException(
                    "framebuffers.clearColorAttachment: integer clear value against non-integer storage");
        }
        new TypedClear(fb, drawBufferIndex, value, integerStorage).run();
    }

    /** Positional route resolution and admission before any state is touched. */
    private Lwjgl3OwnedTexture resolveClearTarget(Lwjgl3FramebufferHandle fb, int drawBufferIndex) {
        List<FramebufferDrawSlot> route = fb.drawRoute;
        if (route == null) {
            throw new IllegalStateException(
                    "framebuffers.clearColorAttachment: framebuffer has no established draw route");
        }
        if (drawBufferIndex < 0 || drawBufferIndex >= route.size()) {
            throw new IllegalArgumentException("framebuffers.clearColorAttachment: draw buffer index "
                    + drawBufferIndex + " outside the established route of " + route.size());
        }
        FramebufferDrawSlot slot = route.get(drawBufferIndex);
        if (slot instanceof FramebufferDrawSlot.None) {
            throw new IllegalArgumentException(
                    "framebuffers.clearColorAttachment: route position " + drawBufferIndex + " is None");
        }
        int attachment = ((FramebufferDrawSlot.Attachment) slot).index();
        Lwjgl3OwnedTexture tex = attachment < fb.colorAttachments.size()
                ? fb.colorAttachments.get(attachment) : null;
        if (tex == null || tex.deleted() || !tex.materialized()) {
            throw new IllegalArgumentException(
                    "framebuffers.clearColorAttachment: attachment " + attachment + " has no defined storage");
        }
        return tex;
    }

    /**
     * The mandatory typed color clear (D-P1-67). Core path: snapshot the sanctioned
     * values, {@code glClearBuffer*} the resolved route position through the framebuffer,
     * restore in {@code finally}. The GL2+EXT legacy tier is rejected as unsupported at
     * v0.1 - shipping contexts are GL 4.6 compatibility profiles.
     */
    private final class TypedClear {
        private final Lwjgl3FramebufferHandle fb;
        private final int drawBufferIndex;
        private final ColorClearValue value;
        private final boolean integerStorage;

        private final int savedRead;
        private final int savedDraw;
        private final int[] savedViewport = new int[4];
        private final boolean savedScissor;
        private final int[] savedScissorBox = new int[4];
        private final boolean[] savedMask = new boolean[4];
        private final boolean savedDither;
        private final boolean savedDiscard;
        private final boolean savedSrgb;

        TypedClear(Lwjgl3FramebufferHandle fb, int drawBufferIndex, ColorClearValue value,
                   boolean integerStorage) {
            this.fb = fb;
            this.drawBufferIndex = drawBufferIndex;
            this.value = value;
            this.integerStorage = integerStorage;
            this.savedRead = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
            this.savedDraw = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
            GL11.glGetIntegerv(GL11.GL_VIEWPORT, savedViewport);
            this.savedScissor = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
            GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, savedScissorBox);
            getBooleanv(GL11.GL_COLOR_WRITEMASK, savedMask);
            this.savedDither = GL11.glIsEnabled(GL11.GL_DITHER);
            this.savedDiscard = GL11.glIsEnabled(GL30.GL_RASTERIZER_DISCARD);
            this.savedSrgb = profile.atLeast(3, 0)
                    && GL11.glIsEnabled(GL30.GL_FRAMEBUFFER_SRGB);
        }

        void run() {
            try {
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fb.glName());
                disable(GL11.GL_SCISSOR_TEST, savedScissor);
                disable(GL11.GL_DITHER, savedDither);
                disable(GL30.GL_RASTERIZER_DISCARD, savedDiscard);
                if (savedSrgb) {
                    GL11.glDisable(GL30.GL_FRAMEBUFFER_SRGB);
                }
                GlStateManager.colorMask(true, true, true, true);
                issueClear();
                device.noteMutation("framebuffers.clearColorAttachment", fb.subjectLabel());
            } finally {
                restore();
            }
        }

        private void issueClear() {
            if (value instanceof ColorClearValue.Floating f) {
                GL30.glClearBufferfv(GL11.GL_COLOR, drawBufferIndex,
                        new float[]{f.r(), f.g(), f.b(), f.a()});
            } else if (value instanceof ColorClearValue.Signed s) {
                GL30.glClearBufferiv(GL11.GL_COLOR, drawBufferIndex,
                        new int[]{s.r(), s.g(), s.b(), s.a()});
            } else if (value instanceof ColorClearValue.Unsigned u) {
                GL30.glClearBufferuiv(GL11.GL_COLOR, drawBufferIndex,
                        new int[]{(int) u.r(), (int) u.g(), (int) u.b(), (int) u.a()});
            } else {
                throw new IllegalArgumentException("unknown clear value form");
            }
        }

        private void restore() {
            try {
                GlStateManager.colorMask(savedMask[0], savedMask[1], savedMask[2], savedMask[3]);
                if (savedDither) {
                    GL11.glEnable(GL11.GL_DITHER);
                }
                if (savedDiscard) {
                    GL11.glEnable(GL30.GL_RASTERIZER_DISCARD);
                }
                if (savedSrgb) {
                    GL11.glEnable(GL30.GL_FRAMEBUFFER_SRGB);
                }
                if (savedScissor) {
                    GL11.glEnable(GL11.GL_SCISSOR_TEST);
                }
                GL11.glScissor(savedScissorBox[0], savedScissorBox[1],
                        savedScissorBox[2], savedScissorBox[3]);
            } finally {
                GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, savedRead);
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, savedDraw);
            }
        }

        private void disable(int capability, boolean currentlyEnabled) {
            if (currentlyEnabled) {
                GL11.glDisable(capability);
            }
        }
    }

    // GL_CLAMP_FRAGMENT_COLOR_ARB is a glClampColor mode, not an enable capability:
    // glIsEnabled/glEnable on it raise INVALID_ENUM, and glClearBuffer* is unaffected by
    // fragment clamping, so the typed clear neither saves nor restores it.

    private static void getBooleanv(int pname, boolean[] out) {
        try (var stack = org.lwjgl.system.MemoryStack.stackPush()) {
            var buf = stack.malloc(4);
            GL11.glGetBooleanv(pname, buf);
            for (int i = 0; i < out.length && i < 4; i++) {
                out[i] = buf.get(i) != 0;
            }
        }
    }

    // ------------------------------------------------------------- delete

    @Override
    public void delete(FramebufferHandle f) {
        device.requireRenderThread("framebuffers.delete");
        Lwjgl3FramebufferHandle fb = device.framebufferOf(f, "framebuffers.delete");
        GL30.glDeleteFramebuffers(fb.glName());
        fb.markDeleted();
        device.noteMutation("framebuffers.delete", fb.subjectLabel());
    }

    private interface GlBind {
        void bind();
    }

    private void bindAndRestore(Lwjgl3FramebufferHandle fb, GlBind action) {
        int savedRead = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        int savedDraw = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        try {
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fb.glName());
            action.bind();
        } finally {
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, savedRead);
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, savedDraw);
        }
    }
}
