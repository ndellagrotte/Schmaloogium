// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.DebugService;
import com.schmaloogium.engine.gl.DrawService;
import com.schmaloogium.engine.gl.GLError;
import com.schmaloogium.engine.gl.GLErrorKind;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.gl.FramebufferHandle;
import com.schmaloogium.engine.gl.FramebufferService;
import com.schmaloogium.engine.gl.ProgramHandle;
import com.schmaloogium.engine.gl.ShaderHandle;
import com.schmaloogium.engine.gl.ShaderService;
import com.schmaloogium.engine.gl.StateService;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.gl.TextureService;
import com.schmaloogium.engine.gl.UniformService;
import com.schmaloogium.engine.gl.VertexInputService;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * The single LWJGL3 implementation of the frozen {@code engine.gl} root
 * (PHASE_1_DOC §4.7.4, §8.2.1). Lives in {@code mod.glue} per the §2.1 package table;
 * construction is mod-side ({@code CapabilityProbe.capture()} + {@code mod.core} stage 3).
 *
 * <p>Two responsibilities sit here rather than in the services, because every service
 * verb shares them:
 * <ul>
 *   <li><b>Render-thread/context guards.</b> {@code Minecraft.getMinecraft() == null}
 *       or a null {@link GLContext#getCapabilities()} means a wrong context; running
 *       off the render thread is a wrong thread. Both raise {@link IllegalStateException}
 *       before any native call (the GL context is bound to the thread).</li>
 *   <li><b>Error attribution ([D-P1-30], [D-P1-31]).</b> Every mutating facade verb
 *       marks the drain; per-call cadence checks after each mutation; drains never query
 *       GL state on demand. A window holding several mutating calls attributes its
 *       errors as a batch: subjectLabel {@code "(batched, N calls)"}; a single-mutation
 *       window carries the exact verb and subject.</li>
 * </ul>
 */
public final class Lwjgl3GLDevice implements GLDevice {

    private final GLCapabilityProfile profile;

    /** [D-P1-21] flags, read once at construction ("never read before mod.core bootstraps"). */
    private final boolean debugFlagsRecordGl;
    private final boolean debugFlagsGlLabels;

    /** Per-call cadence iff either GL-facing flag is set (PHASE_1_DOC §0.34 narrowing). */
    private final boolean perCallCadence;

    // --- attribution state (D-P1-30) ---
    private boolean mutatedSinceDrain;
    private String singleOp;
    private String singleSubject;
    private int mutationCount;

    /** Errors of the current per-call window in call order. */
    private final List<GLError> pendingErrors = new ArrayList<>();

    // --- program-selection tracker (D-P1-39): true mirror; never queried per-draw ---
    private Lwjgl3ProgramHandle activeProgram;
    private boolean programSelectionUnknown;

    /** Vertex issuer authority ([D-P1-50]): private to the device. */
    private final Object vertexIssuerToken = new Object();

    private final ShaderService shaders;
    private final UniformService uniforms;
    private final TextureService textures;
    private final FramebufferService framebuffers;
    private final StateService state;
    private final DrawService draw;
    private final DebugService debug;
    private final VertexInputService vertexInput;

    public Lwjgl3GLDevice(GLCapabilityProfile profile) {
        this.profile = java.util.Objects.requireNonNull(profile, "profile");
        this.debugFlagsRecordGl = readFlag("schmaloogium.debug.recordGL");
        this.debugFlagsGlLabels = readFlag("schmaloogium.debug.glLabels");
        this.perCallCadence = debugFlagsRecordGl || debugFlagsGlLabels;
        this.shaders = new Lwjgl3ShaderService(this);
        this.uniforms = new Lwjgl3UniformService(this);
        this.textures = new Lwjgl3TextureService(this);
        this.framebuffers = new Lwjgl3FramebufferService(this);
        this.state = new Lwjgl3StateService(this);
        this.draw = new Lwjgl3DrawService(this);
        this.debug = new Lwjgl3DebugService(this);
        this.vertexInput = new Lwjgl3VertexInputService(this);
        // mod.core stage-3 side effects ([D-P1-57], §4.7.3): the provider resolves lazily
        // through this device; the P7 hooks consult this device's suppression gate.
        VanillaForeignTextures.install(this);
        AlphaBlendOverrideHooks.install(this);
    }

    private static boolean readFlag(String name) {
        return Boolean.getBoolean(name);
    }

    // ------------------------------------------------------------------ GLDevice

    @Override
    public GLCapabilityProfile capabilities() {
        return profile;
    }

    @Override
    public ShaderService shaders() {
        return shaders;
    }

    @Override
    public UniformService uniforms() {
        return uniforms;
    }

    @Override
    public TextureService textures() {
        return textures;
    }

    @Override
    public FramebufferService framebuffers() {
        return framebuffers;
    }

    // ------------------------------------------------------------------ handle auth

    Lwjgl3ProgramHandle programOf(ProgramHandle h, String verb) {
        return expectedHandle(h, Lwjgl3ProgramHandle.class, verb);
    }

    Lwjgl3ShaderHandle shaderOf(ShaderHandle s, String verb) {
        return expectedHandle(s, Lwjgl3ShaderHandle.class, verb);
    }

    Lwjgl3OwnedTexture ownedTextureOf(TextureHandle h, String verb) {
        return expectedHandle(h, Lwjgl3OwnedTexture.class, verb);
    }

    Lwjgl3FramebufferHandle framebufferOf(FramebufferHandle h, String verb) {
        return expectedHandle(h, Lwjgl3FramebufferHandle.class, verb);
    }

    private <H extends Lwjgl3Handle> H expectedHandle(Object handle, Class<H> expected, String verb) {
        if (!expected.isInstance(handle)) {
            throw new IllegalArgumentException(verb + ": foreign handle value (not issued by this backend)");
        }
        H h = expected.cast(handle);
        if (h.owner() != this || h.deleted()) {
            throw new IllegalArgumentException(verb + ": foreign, wrong-device or deleted handle");
        }
        return h;
    }
    @Override
    public StateService state() {
        return state;
    }

    @Override
    public DrawService draw() {
        return draw;
    }

    @Override
    public VertexInputService vertexInputs() {
        return vertexInput;
    }

    @Override
    public DebugService debug() {
        return debug;
    }


    // ------------------------------------------------------------------ guards

    /** Throws when off the render thread or without a current GL context. */
    void requireRenderThread(String verb) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null) {
            throw new IllegalStateException(verb + " requires the Minecraft client context");
        }
        if (!mc.isCallingFromMinecraftThread()) {
            throw new IllegalStateException(verb + " must run on the render thread");
        }
        if (org.lwjgl.opengl.GL.getCapabilities() == null) {
            throw new IllegalStateException(verb + " requires a current GL context");
        }
    }

    boolean onRenderThread() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc != null && mc.isCallingFromMinecraftThread()
                && org.lwjgl.opengl.GL.getCapabilities() != null;
    }

    // ------------------------------------------------------------------ attribution

    /** Marks one mutating facade verb. First-time windows record exact attribution. */
    void noteMutation(String op, String subjectLabel) {
        mutatedSinceDrain = true;
        mutationCount++;
        if (singleOp == null) {
            singleOp = op;
            singleSubject = subjectLabel;
        }
        if (perCallCadence) {
            collectErrors(op, subjectLabel);
        }
    }

    /** Queues a driver error reported outside any GL error flag (D-P1-39). */
    void queueError(String op, String subjectLabel, GLErrorKind kind, String detail) {
        pendingErrors.add(new GLError(op, subjectLabel, kind, detail));
    }

    /** True when the per-call debug cadence is active (recordGL or glLabels). */
    boolean perCallCadenceActive() {
        return perCallCadence;
    }

    /** Immediate glGetError loop under the per-call cadence, attributed to op/subject. */
    void checkErrorsNow(String op, String subjectLabel) {
        if (perCallCadence) {
            collectErrors(op, subjectLabel);
        }
    }

    /**
     * [D-P1-30]: per-call cadence → the errors of this window in call order; default
     * cadence → glGetError loop, batch-attributed. Never queries GL on demand otherwise;
     * elides entirely when no mutation occurred since the last drain.
     */
    @Override
    public List<GLError> drainErrors() {
        if (!mutatedSinceDrain && pendingErrors.isEmpty()) {
            return List.of();
        }
        mutatedSinceDrain = false;
        if (perCallCadence) {
            List<GLError> out = new ArrayList<>(pendingErrors);
            pendingErrors.clear();
            singleOp = null;
            singleSubject = null;
            mutationCount = 0;
            return out;
        }
        int n = mutationCount;
        String op = singleOp;
        String subject = singleSubject;
        singleOp = null;
        singleSubject = null;
        mutationCount = 0;
        List<GLError> out = new ArrayList<>();
        int err;
        while ((err = GL11.glGetError()) != GL11.GL_NO_ERROR) {
            if (n <= 1) {
                out.add(new GLError(op == null ? "(unknown)" : op,
                        subject == null ? "(unknown)" : subject,
                        GlNames.errorKind(err), null));
            } else {
                out.add(new GLError("(batched)", "(batched, " + n + " calls)",
                        GlNames.errorKind(err), null));
            }
        }
        return out;
    }

    // ------------------------------------------------------------------ alpha/blend lock

    private Lwjgl3AlphaBlendOverride alphaLease;
    private int bypassDepth;

    /** Registers the one active alpha/blend lease (D-P1-57). */
    void attachAlphaLease(Lwjgl3AlphaBlendOverride lease) {
        this.alphaLease = lease;
    }

    void detachAlphaLease(Lwjgl3AlphaBlendOverride lease) {
        if (this.alphaLease == lease) {
            this.alphaLease = null;
        }
    }

    boolean alphaLeaseHeld() {
        return alphaLease != null;
    }

    void enterBypass() {
        bypassDepth++;
    }

    void exitBypass() {
        bypassDepth--;
    }

    /** Suppression gate for the P7-registered mutation hooks ([D-P1-57]). */
    boolean suppressesAlphaMutation() {
        return alphaLease != null && alphaLease.holdsAlpha() && bypassDepth == 0;
    }

    boolean suppressesBlendMutation() {
        return alphaLease != null && alphaLease.holdsBlend() && bypassDepth == 0;
    }

    // ---------------------------------------------------------- lease plumbing

    /** Real-value capture for the alpha/blend lease (D-P1-57), via native reads. */
    com.schmaloogium.engine.gl.AlphaTestState captureAlphaForLock() {
        return ((Lwjgl3StateService) state).captureAlpha();
    }

    com.schmaloogium.engine.gl.BlendState captureBlendForLock() {
        return ((Lwjgl3StateService) state).captureBlend();
    }

    void stateIssueAlphaTest(com.schmaloogium.engine.gl.AlphaTestState value) {
        ((Lwjgl3StateService) state).issueAlphaTestForLock(value);
    }

    void stateIssueBlend(com.schmaloogium.engine.gl.BlendState value) {
        ((Lwjgl3StateService) state).issueBlendForLock(value);
    }

    /** Snapshot-restore write path, routed through the state service. */
    void stateWrite(com.schmaloogium.engine.gl.StateAspect aspect, Object value) {
        ((Lwjgl3StateService) state).writeAspect(aspect, value);
    }

    /**
     * The active program's linked geometry input requirement, NONE when no program is
     * selected or no geometry stage is linked (D-P1-48's fixed order).
     */
    com.schmaloogium.engine.vertex.VertexGeometryInput activeGeometryRequirement() {
        com.schmaloogium.engine.gl.ProgramHandle active = activeProgram();
        if (active == null) {
            return com.schmaloogium.engine.vertex.VertexGeometryInput.NONE;
        }
        return ((Lwjgl3ShaderService) shaders).linkedInputOf(active)
                .map(Lwjgl3GLDevice::toVertexGeometry)
                .orElse(com.schmaloogium.engine.vertex.VertexGeometryInput.NONE);
    }

    private static com.schmaloogium.engine.vertex.VertexGeometryInput toVertexGeometry(
            com.schmaloogium.engine.gl.LinkedGeometryInputPrimitive p) {
        return switch (p) {
            case POINTS -> com.schmaloogium.engine.vertex.VertexGeometryInput.POINTS;
            case LINES -> com.schmaloogium.engine.vertex.VertexGeometryInput.LINES;
            case LINES_ADJACENCY -> com.schmaloogium.engine.vertex.VertexGeometryInput.LINES_ADJACENCY;
            case TRIANGLES -> com.schmaloogium.engine.vertex.VertexGeometryInput.TRIANGLES;
            case TRIANGLES_ADJACENCY -> com.schmaloogium.engine.vertex.VertexGeometryInput.TRIANGLES_ADJACENCY;
        };
    }

    private void collectErrors(String op, String subjectLabel) {
        int err;
        while ((err = GL11.glGetError()) != GL11.GL_NO_ERROR) {
            pendingErrors.add(new GLError(op, subjectLabel, GlNames.errorKind(err), null));
        }
    }

    // ------------------------------------------------------------------ trackers

    Lwjgl3ProgramHandle activeProgram() {
        return activeProgram;
    }

    void setActiveProgram(Lwjgl3ProgramHandle program) {
        this.activeProgram = program;
        this.programSelectionUnknown = false;
    }

    void markProgramSelectionUnknown(String verb, String subject) {
        this.programSelectionUnknown = true;
        this.activeProgram = null;
        queueError(verb, subject, GLErrorKind.INVALID_OPERATION,
                "glUseProgram did not take effect (GL_CURRENT_PROGRAM mismatch)");
    }

    boolean programSelectionMirrors(Lwjgl3ProgramHandle program) {
        return !programSelectionUnknown && activeProgram == program;
    }

    Object vertexIssuerToken() {
        return vertexIssuerToken;
    }

    boolean recordsLabels() {
        return debugFlagsGlLabels;
    }

    boolean recordsGl() {
        return debugFlagsRecordGl;
    }
}
