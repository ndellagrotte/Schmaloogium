// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import com.schmaloogium.engine.buffers.BufferInventory;
import com.schmaloogium.engine.buffers.BufferResourceSnapshot;
import com.schmaloogium.engine.buffers.BufferSizing;
import com.schmaloogium.engine.buffers.DepthCopyPoint;
import com.schmaloogium.engine.buffers.LogicalBuffer;
import com.schmaloogium.engine.buffers.MainDepthSnapshot;
import com.schmaloogium.engine.buffers.MainDepthSource;
import com.schmaloogium.engine.buffers.PhysicalSide;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.gl.BorrowedDepthAttachmentHandle;
import com.schmaloogium.engine.gl.DepthAttachmentFormat;
import com.schmaloogium.engine.gl.FramebufferHandle;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.gl.TextureMinFilter;
import com.schmaloogium.engine.registry.RegistryFingerprint;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The mutable estate state created by the candidate builder and carried into the accepted
 * view (PHASE_5_DOC §4.3-§4.13). Public only because the flat Java package split puts the
 * operators in {@code ...buffers.internal}; never consumed outside the buffers
 * implementation. GL handles live exclusively here and in the per-pair/per-destination
 * records; evidence never carries them.
 */
public final class EstateCore {

    /** One physical color side pair with its flip metadata (§4.4.1). */
    public static final class ColorPair {
        public final LogicalBuffer logical;
        public final TextureHandle sideA;
        public final TextureHandle sideB;
        public PhysicalSide committedMain;
        public boolean flipped;
        public long writeRevision;
        public long generatedRevision;
        public boolean chainFresh;
        public final boolean mipmapCapable;
        public final int mipLevels;
        public final TextureMinFilter baseMinFilter;

        ColorPair(LogicalBuffer logical, TextureHandle sideA, TextureHandle sideB,
                PhysicalSide committedMain, boolean mipmapCapable, int mipLevels,
                TextureMinFilter baseMinFilter) {
            this.logical = logical;
            this.sideA = sideA;
            this.sideB = sideB;
            this.committedMain = committedMain;
            this.flipped = false;
            this.writeRevision = 0;
            this.generatedRevision = -1;
            this.chainFresh = false;
            this.mipmapCapable = mipmapCapable;
            this.mipLevels = mipLevels;
            this.baseMinFilter = baseMinFilter;
        }

        /** Logical read/main side for the current flip state (§4.4.1 table). */
        public TextureHandle readSide() {
            PhysicalSide main = flipped ? committedMain.opposite() : committedMain;
            return main == PhysicalSide.A ? sideA : sideB;
        }

        /** Logical write/alt side for the current flip state (§4.4.1 table). */
        public TextureHandle writeSide() {
            PhysicalSide alt = flipped ? committedMain : committedMain.opposite();
            return alt == PhysicalSide.A ? sideA : sideB;
        }

        public TextureHandle side(PhysicalSide side) {
            return side == PhysicalSide.A ? sideA : sideB;
        }
    }

    /** One owned depth-copy destination with its D-P5-33 state. Inner so the bound texture
     *  can fall back to the borrowed depthtex0 (§4.9 destination state machine). */
    public final class DepthDestination {
        public final LogicalBuffer logical;
        public final TextureHandle texture;
        public final FramebufferHandle destinationFbo;
        public boolean initialized;

        DepthDestination(LogicalBuffer logical, TextureHandle texture,
                FramebufferHandle destinationFbo) {
            this.logical = logical;
            this.texture = texture;
            this.destinationFbo = destinationFbo;
            this.initialized = false;
        }

        /** The texture the depthtex1/2 unit binds right now (§4.9 state machine). */
        public TextureHandle boundTexture() {
            return initialized ? texture : cachedDepth.texture();
        }
    }

    public final GLDevice device;
    public final DiagnosticReporter diagnostics;
    public final RegistryFingerprint registryFingerprint;
    public final PlanningArtifacts plan;
    public final BufferResourceSnapshot.Available realized;
    public final Thread renderThread;
    public final List<ColorPair> colorPairs;

    // ---------------------------------------------------------------- lifecycle

    public long generation = -1;
    public boolean stale;
    public long depthAttachmentEpoch = 0;
    public boolean fullClearRequired = true;

    // ---------------------------------------------------------------- main depth

    public final MainDepthSource mainDepthSource;
    public MainDepthSnapshot.Available cachedDepth;

    // ---------------------------------------------------------------- depth copies

    public final List<DepthDestination> copyDestinations = new ArrayList<>();
    /** Copy points consumed in this frame, in scheduled order (§4.9). */
    public final List<DepthCopyPoint> consumedPoints = new ArrayList<>();

    // ---------------------------------------------------------------- object caches

    /** Pass FBOs keyed by the full tagged positional route identity (§4.5). */
    public final Map<String, FramebufferHandle> passFbos = new LinkedHashMap<>();
    /**
     * The color texture currently attached at each pass FBO's physical attachment index
     * (§4.4.2 step 5: the attachment is derived from the frozen side, so a snapshot
     * re-attaches only when the frozen side differs from what the FBO holds). Seeded by
     * the candidate builder (side A) and maintained by {@link EstateViewImpl#snapshot}.
     */
    public final Map<FramebufferHandle, Map<Integer, TextureHandle>> attachedColor =
        new HashMap<>();
    /** Clear FBOs keyed by their ordered attachment identity. */
    public final Map<String, FramebufferHandle> clearFbos = new LinkedHashMap<>();
    public final List<FramebufferHandle> ownedFbos = new ArrayList<>();
    public final List<TextureHandle> ownedTextures = new ArrayList<>();

    // ---------------------------------------------------------------- frame protocol

    public long openFrameId = -1;
    public OpenPass openPass;
    public DrawBuffersNoneLeaseState openLease;
    public final List<com.schmaloogium.engine.registry.ProgramSlotId> consumedVirtuals =
        new ArrayList<>();
    // ------------------------------------------------------------------ shadow estate (§4.10)
    /** Shadow depth textures in row order (shadowtex0, shadowtex1); empty when the plan
     *  declares no shadow estate or the real estate failed at build. Populated by candidate
     *  build. */
    public final List<TextureHandle> shadowDepths = new ArrayList<>();
    /** Shadow color pairs in row order (shadowcolor0, shadowcolor1); generic pair/flip
     *  state, single-sided at v0.1 because shadowcomp execution is post-v0.5. */
    public final List<ShadowColorPair> shadowColorPairs = new ArrayList<>();
    /** The shadow FBO, allocated only when the real shadow estate is operable. */
    public FramebufferHandle shadowFbo;
    /** The shadow operation view, wired when the estate is operable (§4.10). */
    public com.schmaloogium.engine.buffers.ShadowEstateView shadowView;
    /** The bounded neutral cache, allocated before real sfb construction and retained
     *  through creation failure and runtime neutralization (§4.10). */
    public ShadowNeutralCache shadowNeutral;
    /** Set at runtime neutralization or at build-time real-estate failure; every later
     *  {@code shadow()} call then reports {@link com.schmaloogium.engine.buffers
     *  .ShadowEstateUnavailable} carrying {@link #shadowFailure} (§4.10). */
    public boolean shadowNeutralized;
    /** The first neutralizing/build failure, reported by later shadow() calls. */
    public com.schmaloogium.engine.buffers.BufferFailure shadowFailure;
    /** The stable diagnostic id of the first neutralization (repeated verbatim). */
    public String shadowNeutralDiagnostic;

    public TextureHandle shadowTexture(int index) {
        return index < shadowDepths.size() ? shadowDepths.get(index) : null;
    }

    public int shadowDepthCount() {
        return shadowDepths.size();
    }

    public ShadowColorPair shadowColorPair(int index) {
        return index < shadowColorPairs.size() ? shadowColorPairs.get(index) : null;
    }

    /** Planned (pack-facing) shadow depth texture count for this estate's plan. */
    public int shadowPlannedDepthCount() {
        return plan.plannedProjection().shadow().depthTextures();
    }

    /** Planned (pack-facing) shadow color texture count for this estate's plan. */
    public int shadowPlannedColorCount() {
        return plan.plannedProjection().shadow().colorTextures();
    }
    /** Shadow fixed-unit backing switches to the neutral cache once the estate degraded
     *  or the real estate failed at build (§4.10 neutral shadow bindings). */
    public boolean shadowNeutralBacked() {
        return shadowNeutralized || shadowFailure != null;
    }

    /** True when any color side holds flipped metadata (frame begin normalization check). */
    public boolean flippedAnywhere() {
        for (ColorPair pair : colorPairs) {
            if (pair.flipped) {
                return true;
            }
        }
        return false;
    }

    /** Failure helper for collaborators building closed BufferFailure values. */
    public com.schmaloogium.engine.buffers.BufferFailure failure(
            com.schmaloogium.engine.buffers.BufferFailureCode code, String messageKey) {
        return new com.schmaloogium.engine.buffers.BufferFailure(code, messageKey,
            messageKey, List.of(), java.util.Optional.empty(), java.util.Optional.empty());
    }

    /** Internal record of one open pass snapshot with its pending flip set (§4.4.2). */
    public static final class OpenPass {
        public final com.schmaloogium.engine.buffers.PassBufferSnapshot snapshot;
        public final List<LogicalBuffer> flipAfterPass;

        OpenPass(com.schmaloogium.engine.buffers.PassBufferSnapshot snapshot,
                List<LogicalBuffer> flipAfterPass) {
            this.snapshot = snapshot;
            this.flipAfterPass = List.copyOf(flipAfterPass);
        }
    }

    /** Internal draw-buffers-none lease state (§4.5.1). */
    public static final class DrawBuffersNoneLeaseState {
        public final long frameId;
        public boolean open = true;

        DrawBuffersNoneLeaseState(long frameId) {
            this.frameId = frameId;
        }
    }

    /** One shadow color side pair with its generic flip metadata (§4.10). Shadowcomp
     *  execution is post-v0.5, so the estate treats the pair single-sided (read side)
     *  until a shadow write schedule exists; completion still applies declared flips. */
    public static final class ShadowColorPair {
        public final LogicalBuffer logical;
        public final TextureHandle sideA;
        public final TextureHandle sideB;
        public PhysicalSide committedMain;
        public boolean flipped;
        public boolean chainFresh;

        ShadowColorPair(LogicalBuffer logical, TextureHandle sideA, TextureHandle sideB) {
            this.logical = logical;
            this.sideA = sideA;
            this.sideB = sideB;
            this.committedMain = PhysicalSide.A;
            this.flipped = false;
            this.chainFresh = false;
        }

        /** Logical read/main side for the current flip state. */
        public TextureHandle readSide() {
            PhysicalSide main = flipped ? committedMain.opposite() : committedMain;
            return main == PhysicalSide.A ? sideA : sideB;
        }

        /** Logical write/alt side for the current flip state. */
        public TextureHandle writeSide() {
            PhysicalSide alt = flipped ? committedMain : committedMain.opposite();
            return alt == PhysicalSide.A ? sideA : sideB;
        }
    }

    public EstateCore(GLDevice device, DiagnosticReporter diagnostics,
            RegistryFingerprint registryFingerprint, PlanningArtifacts plan,
            BufferResourceSnapshot.Available realized, MainDepthSource mainDepthSource,
            MainDepthSnapshot.Available initialDepth) {
        this.device = device;
        this.diagnostics = diagnostics;
        this.registryFingerprint = registryFingerprint;
        this.plan = plan;
        this.realized = realized;
        this.renderThread = Thread.currentThread();
        this.mainDepthSource = mainDepthSource;
        this.cachedDepth = initialDepth;
        this.colorPairs = new ArrayList<>();
    }

    // ---------------------------------------------------------------- helpers

    public void checkRenderThread() {
        if (Thread.currentThread() != renderThread) {
            throw new IllegalStateException(
                "estate operations are render-thread/current-context only (PHASE_5_DOC §2.2)");
        }
    }

    public boolean usable() {
        return !stale;
    }

    public ColorPair pair(LogicalBuffer logical) {
        for (ColorPair pair : colorPairs) {
            if (pair.logical.equals(logical)) {
                return pair;
            }
        }
        return null;
    }

    /** Reverse creation order teardown: clear FBOs, pass FBOs, copy destinations, colors,
     *  then the shadow estate (real handles and neutral objects alike, §4.10/§4.13). */
    public List<Runnable> teardownSteps() {
        List<Runnable> steps = new ArrayList<>();
        List<FramebufferHandle> clearFboOrder = new ArrayList<>(clearFbos.values());
        for (int index = clearFboOrder.size() - 1; index >= 0; index--) {
            FramebufferHandle fbo = clearFboOrder.get(index);
            steps.add(() -> device.framebuffers().delete(fbo));
        }
        List<FramebufferHandle> passFboOrder = new ArrayList<>(passFbos.values());
        for (int index = passFboOrder.size() - 1; index >= 0; index--) {
            FramebufferHandle fbo = passFboOrder.get(index);
            steps.add(() -> device.framebuffers().delete(fbo));
        }
        for (int index = copyDestinations.size() - 1; index >= 0; index--) {
            TextureHandle texture = copyDestinations.get(index).texture;
            steps.add(() -> device.textures().delete(texture));
        }
        for (int index = colorPairs.size() - 1; index >= 0; index--) {
            ColorPair pair = colorPairs.get(index);
            TextureHandle sideB = pair.sideB;
            TextureHandle sideA = pair.sideA;
            steps.add(() -> device.textures().delete(sideB));
            steps.add(() -> device.textures().delete(sideA));
        }
        if (shadowFbo != null) {
            steps.add(() -> device.framebuffers().delete(shadowFbo));
        }
        for (int index = shadowColorPairs.size() - 1; index >= 0; index--) {
            ShadowColorPair pair = shadowColorPairs.get(index);
            TextureHandle sideB = pair.sideB;
            TextureHandle sideA = pair.sideA;
            steps.add(() -> device.textures().delete(sideB));
            steps.add(() -> device.textures().delete(sideA));
        }
        for (int index = shadowDepths.size() - 1; index >= 0; index--) {
            TextureHandle texture = shadowDepths.get(index);
            steps.add(() -> device.textures().delete(texture));
        }
        if (shadowNeutral != null) {
            List<TextureHandle> neutrals = shadowNeutral.ownedObjects();
            for (int index = neutrals.size() - 1; index >= 0; index--) {
                TextureHandle texture = neutrals.get(index);
                steps.add(() -> device.textures().delete(texture));
            }
        }
        return steps;
    }

    /** Dense ascending tag of one positional route for FBO identity (§4.5, the 0N2 form). */
    public static String routeTag(List<com.schmaloogium.engine.registry.DrawRoutingSlot> slots) {
        StringBuilder tag = new StringBuilder();
        for (com.schmaloogium.engine.registry.DrawRoutingSlot slot : slots) {
            if (slot instanceof com.schmaloogium.engine.registry.DrawRoutingSlot.Attachment attachment) {
                tag.append('A').append(attachment.buffer().index());
            } else {
                tag.append('N');
            }
        }
        return tag.toString();
    }
}
