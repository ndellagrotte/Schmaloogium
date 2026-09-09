// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.glrec;

import com.schmaloogium.engine.gl.AlphaBlendOverride;
import com.schmaloogium.engine.gl.AlphaTestState;
import com.schmaloogium.engine.gl.BlendState;
import com.schmaloogium.engine.gl.BlitSpec;
import com.schmaloogium.engine.gl.BorrowedDepthAttachmentHandle;
import com.schmaloogium.engine.gl.ClearTarget;
import com.schmaloogium.engine.gl.ColorClearValue;
import com.schmaloogium.engine.gl.CompileResult;
import com.schmaloogium.engine.gl.DebugService;
import com.schmaloogium.engine.gl.DrawService;
import com.schmaloogium.engine.gl.FogState;
import com.schmaloogium.engine.gl.FramebufferDrawSlot;
import com.schmaloogium.engine.gl.FramebufferHandle;
import com.schmaloogium.engine.gl.FramebufferService;
import com.schmaloogium.engine.gl.FramebufferStatus;
import com.schmaloogium.engine.gl.FramebufferTarget;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.gl.GLHandle;
import com.schmaloogium.engine.gl.GLError;
import com.schmaloogium.engine.gl.GLErrorKind;
import com.schmaloogium.engine.gl.LegacyGeometryInputPrimitive;
import com.schmaloogium.engine.gl.LegacyGeometryOutputPrimitive;
import com.schmaloogium.engine.gl.LinkResult;
import com.schmaloogium.engine.gl.LinkedGeometryInputPrimitive;
import com.schmaloogium.engine.gl.ProgramHandle;
import com.schmaloogium.engine.gl.SamplerInitializationResult;
import com.schmaloogium.engine.gl.SamplerUnitAssignment;
import com.schmaloogium.engine.gl.ShaderHandle;
import com.schmaloogium.engine.gl.ShaderService;
import com.schmaloogium.engine.gl.ShaderStage;
import com.schmaloogium.engine.gl.StateAspect;
import com.schmaloogium.engine.gl.StateService;
import com.schmaloogium.engine.gl.StateSnapshot;
import com.schmaloogium.engine.gl.TextureData;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.gl.TextureParameters;
import com.schmaloogium.engine.gl.TextureRegion;
import com.schmaloogium.engine.gl.TextureService;
import com.schmaloogium.engine.gl.TextureSpec;
import com.schmaloogium.engine.gl.UniformLocation;
import com.schmaloogium.engine.gl.UniformService;
import com.schmaloogium.engine.gl.ValidateResult;
import com.schmaloogium.engine.gl.VertexBindMode;
import com.schmaloogium.engine.gl.VertexBindResult;
import com.schmaloogium.engine.gl.VertexBinding;
import com.schmaloogium.engine.vertex.VertexInputPlan;
import com.schmaloogium.engine.gl.VertexInputService;
import com.schmaloogium.engine.vertex.VertexLayout;
import com.schmaloogium.engine.gl.VertexSource;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;

/**
 * A scripted, recording {@link GLDevice}: the conformance harness's headless stand-in
 * for the Phase 1 recorder backend ({@code engine.gl.record} has not landed and stays
 * P1-owned; this class only implements P1's public facade and is the seam the real
 * recorder replaces). It serves two T0 obligations:
 *
 * <ul>
 *   <li><b>no GL errors</b> — scripted error arms let a test prove the pipeline both
 *       observes a drained {@link GLError} and fails T0 on it (never red→green);</li>
 *   <li><b>stable frame loop</b> — every facade call is recorded into a bounded,
 *       deterministic {@link ScriptedCallLog} that {@link FrameReplay} slices into
 *       per-frame windows and compares.</li>
 * </ul>
 *
 * <p>No window system, no LWJGL, no real GL: object lifecycle is bookkeeping and every
 * compile/link/validate reports success unless an error arm is pending. Drain semantics
 * follow [D-P1-30]: draining clears, and a drain returns everything armed-and-hit plus
 * anything attached since the previous drain.
 */
public final class ScriptedGLDevice implements GLDevice {

    private final GLCapabilityProfile profile;
    private final ScriptedCallLog log = new ScriptedCallLog();
    private final Queue<ArmedError> armed = new ArrayDeque<>();
    private final List<GLError> pending = new ArrayList<>();
    private final List<ScriptedBinding> liveBindings = new ArrayList<>();
    private long serial = 1;
    private boolean groupActive;

    private final Shaders shaders = new Shaders();
    private final Uniforms uniforms = new Uniforms();
    private final Textures textures = new Textures();
    private final Framebuffers framebuffers = new Framebuffers();
    private final State state = new State();
    private final Draw draw = new Draw();
    private final Debug debug = new Debug();
    private final VertexInputs vertexInputs = new VertexInputs();

    public ScriptedGLDevice(GLCapabilityProfile profile) {
        this.profile = Objects.requireNonNull(profile, "profile");
    }

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

    @Override
    public StateService state() {
        return state;
    }

    @Override
    public DrawService draw() {
        return draw;
    }

    @Override
    public DebugService debug() {
        return debug;
    }

    @Override
    public VertexInputService vertexInputs() {
        return vertexInputs;
    }

    @Override
    public List<GLError> drainErrors() {
        List<GLError> drained = List.copyOf(pending);
        pending.clear();
        return drained;
    }

    public ScriptedCallLog log() {
        return log;
    }

    /** Arms one error for the next call with the given op; the T0 pipeline must turn
     *  it into a manifest {@code gl_errors} row and a T0 failure. */
    public void armError(String op, String subject, GLErrorKind kind, String detail) {
        armed.add(new ArmedError(op, new GLError(op, subject, kind, detail)));
    }

    private void record(String op, String subject) {
        String detail = "";
        ArmedError hit = null;
        for (ArmedError candidate : armed) {
            if (candidate.op().equals(op)) {
                hit = candidate;
                break;
            }
        }
        if (hit != null) {
            armed.remove(hit);
            pending.add(hit.error());
            detail = "error=" + hit.error().kind();
        }
        log.record(op, subject, detail);
    }

    private record ArmedError(String op, GLError error) {
    }

    // ------------------------------------------------------------------
    // Handles
    // ------------------------------------------------------------------

    private abstract static class ScriptedHandle {
        final long serial;
        final String label;

        ScriptedHandle(long serial, String label) {
            this.serial = serial;
            this.label = label;
        }

        String name() {
            return label + "@" + serial;
        }
    }

    private static final class ShaderObj extends ScriptedHandle implements ShaderHandle {
        ShaderObj(long serial, String label) {
            super(serial, label);
        }
    }

    private static final class ProgramObj extends ScriptedHandle implements ProgramHandle {
        ProgramObj(long serial, String label) {
            super(serial, label);
        }
    }

    private static final class FramebufferObj extends ScriptedHandle implements FramebufferHandle {
        FramebufferObj(long serial, String label) {
            super(serial, label);
        }
    }

    private static final class TextureObj extends ScriptedHandle implements TextureHandle {
        TextureObj(long serial, String label) {
            super(serial, label);
        }
    }

    private record ScriptedUniform(String name) implements UniformLocation {
        @Override
        public boolean isAbsent() {
            return false;
        }
    }

    private final class ScriptedBinding implements VertexBinding {
        private final long id = serial++;

        String name() {
            return "binding@" + id;
        }
    }

    // ------------------------------------------------------------------
    // Services
    // ------------------------------------------------------------------

    private final class Shaders implements ShaderService {
        @Override
        public ShaderHandle createShader(ShaderStage stage, String source) {
            ShaderObj handle = new ShaderObj(serial++,
                stage.toString().toLowerCase(Locale.ROOT));
            record("shaders.createShader", handle.name());
            return handle;
        }

        @Override
        public CompileResult compile(ShaderHandle shader) {
            record("shaders.compile", nameOf(shader));
            return new CompileResult(true, "", null);
        }

        @Override
        public ProgramHandle createProgram() {
            ProgramObj handle = new ProgramObj(serial++, "program");
            record("shaders.createProgram", handle.name());
            return handle;
        }

        @Override
        public void attach(ProgramHandle p, ShaderHandle s) {
            record("shaders.attach", nameOf(p));
        }

        @Override
        public void bindAttributeLocation(ProgramHandle p, int location, String name) {
            record("shaders.bindAttributeLocation", name);
        }

        @Override
        public void configureLegacyGeometry(ProgramHandle p, LegacyGeometryInputPrimitive input,
                LegacyGeometryOutputPrimitive output, int maxVerticesOut) {
            record("shaders.configureLegacyGeometry", nameOf(p));
        }

        @Override
        public LinkResult link(ProgramHandle p) {
            record("shaders.link", nameOf(p));
            return new LinkResult(true, "", null);
        }

        @Override
        public Optional<LinkedGeometryInputPrimitive> linkedGeometryInput(ProgramHandle p) {
            record("shaders.linkedGeometryInput", nameOf(p));
            return Optional.empty();
        }

        @Override
        public SamplerInitializationResult initializeSamplerUnits(ProgramHandle p,
                List<SamplerUnitAssignment> assignments) {
            record("shaders.initializeSamplerUnits", nameOf(p));
            return new SamplerInitializationResult.Completed();
        }

        @Override
        public ValidateResult validate(ProgramHandle p) {
            record("shaders.validate", nameOf(p));
            return new ValidateResult(true, "", null);
        }

        @Override
        public void use(ProgramHandle p) {
            record("shaders.use", nameOf(p));
        }

        @Override
        public void useFixedFunction() {
            record("shaders.useFixedFunction", "");
        }

        @Override
        public void delete(ProgramHandle p) {
            record("shaders.delete", nameOf(p));
        }

        @Override
        public void delete(ShaderHandle s) {
            record("shaders.delete", nameOf(s));
        }
    }

    private final class Uniforms implements UniformService {
        @Override
        public UniformLocation locate(ProgramHandle p, String name) {
            record("uniforms.locate", name);
            return new ScriptedUniform(name);
        }

        @Override
        public void upload(UniformLocation loc, int v) {
            record("uniforms.upload", nameOf(loc));
        }

        @Override
        public void upload(UniformLocation loc, int x, int y) {
            record("uniforms.upload", nameOf(loc));
        }

        @Override
        public void upload(UniformLocation loc, int x, int y, int z, int w) {
            record("uniforms.upload", nameOf(loc));
        }

        @Override
        public void upload(UniformLocation loc, float v) {
            record("uniforms.upload", nameOf(loc));
        }

        @Override
        public void upload(UniformLocation loc, float x, float y) {
            record("uniforms.upload", nameOf(loc));
        }

        @Override
        public void upload(UniformLocation loc, float x, float y, float z) {
            record("uniforms.upload", nameOf(loc));
        }

        @Override
        public void upload(UniformLocation loc, float x, float y, float z, float w) {
            record("uniforms.upload", nameOf(loc));
        }

        @Override
        public void uploadMatrix4(UniformLocation loc, float[] m16, boolean transpose) {
            record("uniforms.uploadMatrix4", nameOf(loc));
        }

        private String nameOf(UniformLocation loc) {
            return loc instanceof ScriptedUniform named ? named.name() : "uniform";
        }
    }

    private final class Textures implements TextureService {
        @Override
        public TextureHandle create(String debugLabel) {
            TextureObj handle = new TextureObj(serial++, debugLabel);
            record("textures.create", handle.name());
            return handle;
        }

        @Override
        public void allocate(TextureHandle t, TextureSpec spec) {
            record("textures.allocate", nameOf(t));
        }

        @Override
        public void setParameters(TextureHandle t, TextureParameters p) {
            record("textures.setParameters", nameOf(t));
        }

        @Override
        public void upload(TextureHandle t, TextureData data) {
            record("textures.upload", nameOf(t));
        }

        @Override
        public void prepareUnitBindings(int occupiedUnitMask) {
            record("textures.prepareUnitBindings",
                "mask=0x" + Integer.toHexString(occupiedUnitMask));
        }

        @Override
        public void bindToUnit(int unit, TextureHandle t) {
            record("textures.bindToUnit", nameOf(t));
        }

        @Override
        public void generateMipmap(TextureHandle t) {
            record("textures.generateMipmap", nameOf(t));
        }

        @Override
        public void delete(TextureHandle t) {
            record("textures.delete", nameOf(t));
        }

        private String nameOf(TextureHandle t) {
            return t instanceof TextureObj named ? named.name() : "texture";
        }
    }

    private final class Framebuffers implements FramebufferService {
        @Override
        public FramebufferHandle create(String debugLabel) {
            FramebufferObj handle = new FramebufferObj(serial++, debugLabel);
            record("framebuffers.create", handle.name());
            return handle;
        }

        @Override
        public BorrowedDepthAttachmentHandle borrowDepthAttachment(TextureHandle platformTexture) {
            record("framebuffers.borrowDepthAttachment", "borrowed");
            return new BorrowedDepthAttachmentHandle() {
            };
        }

        @Override
        public void attachColor(FramebufferHandle f, int attachmentIndex, TextureHandle t) {
            record("framebuffers.attachColor", nameOf(f));
        }

        @Override
        public void attachDepth(FramebufferHandle f, TextureHandle t) {
            record("framebuffers.attachDepth", nameOf(f));
        }

        @Override
        public void attachDepthStencil(FramebufferHandle f, TextureHandle t) {
            record("framebuffers.attachDepthStencil", nameOf(f));
        }

        @Override
        public void drawBuffers(FramebufferHandle f, List<FramebufferDrawSlot> slots) {
            record("framebuffers.drawBuffers", nameOf(f));
        }

        @Override
        public FramebufferStatus check(FramebufferHandle f) {
            record("framebuffers.check", nameOf(f));
            return FramebufferStatus.COMPLETE;
        }

        @Override
        public void bind(FramebufferTarget target, FramebufferHandle f) {
            record("framebuffers.bind", nameOf(f));
        }

        @Override
        public void bindDefault(FramebufferTarget target) {
            record("framebuffers.bindDefault", "0");
        }

        @Override
        public void blit(FramebufferHandle src, FramebufferHandle dst, BlitSpec spec) {
            record("framebuffers.blit", nameOf(dst));
        }

        @Override
        public void initializeDepthTextureFromFramebuffer(FramebufferHandle src,
                TextureHandle dst, TextureRegion region) {
            record("framebuffers.initializeDepthTextureFromFramebuffer", ScriptedGLDevice.this.nameOf(dst));
        }

        @Override
        public void copyDepthToTexture(FramebufferHandle src, TextureHandle dst,
                TextureRegion region) {
            record("framebuffers.copyDepthToTexture", ScriptedGLDevice.this.nameOf(dst));
        }

        @Override
        public float readDepthPixel(FramebufferHandle f, int x, int y) {
            record("framebuffers.readDepthPixel", nameOf(f));
            return 0.5f;
        }

        @Override
        public void clearColorAttachment(FramebufferHandle f, int drawBufferIndex,
                ColorClearValue value) {
            record("framebuffers.clearColorAttachment", nameOf(f));
        }

        @Override
        public void delete(FramebufferHandle f) {
            record("framebuffers.delete", nameOf(f));
        }

        private String nameOf(FramebufferHandle f) {
            return f instanceof FramebufferObj named ? named.name() : "framebuffer";
        }
    }

    private final class State implements StateService {
        @Override
        public void viewport(int x, int y, int w, int h) {
            record("state.viewport", w + "x" + h);
        }

        @Override
        public void clearColor(float r, float g, float b, float a) {
            record("state.clearColor", "");
        }

        @Override
        public void clear(EnumSet<ClearTarget> targets) {
            record("state.clear", targets.toString());
        }

        @Override
        public void depthMask(boolean enabled) {
            record("state.depthMask", Boolean.toString(enabled));
        }

        @Override
        public void depthTest(boolean enabled) {
            record("state.depthTest", Boolean.toString(enabled));
        }

        @Override
        public void blend(BlendState blendState) {
            record("state.blend", blendState == null ? "disabled" : "enabled");
        }

        @Override
        public void alphaTest(AlphaTestState alphaTestState) {
            record("state.alphaTest", alphaTestState == null ? "disabled" : "enabled");
        }

        @Override
        public AlphaBlendOverride lockAlphaBlend(Optional<AlphaTestState> alpha,
                Optional<BlendState> blend) {
            record("state.lockAlphaBlend", "");
            return () -> record("state.lockAlphaBlend.close", "");
        }

        @Override
        public BlendState effectiveBlend() {
            record("state.effectiveBlend", "");
            return new BlendState(BlendState.BlendFactor.ZERO, BlendState.BlendFactor.ZERO,
                BlendState.BlendFactor.ZERO, BlendState.BlendFactor.ZERO);
        }

        @Override
        public void fog(FogState fogState) {
            record("state.fog", fogState == null ? "disabled" : "enabled");
        }

        @Override
        public StateSnapshot snapshot(EnumSet<StateAspect> aspects) {
            record("state.snapshot", aspects.size() + " aspects");
            return new StateSnapshot() {
            };
        }

        @Override
        public void restore(StateSnapshot snapshot) {
            record("state.restore", "");
        }
    }

    private final class Draw implements DrawService {
        @Override
        public void fullscreenQuad() {
            record("draw.fullscreenQuad", "");
        }
    }

    private final class Debug implements DebugService {
        @Override
        public void pushGroup(String label) {
            groupActive = true;
            record("debug.pushGroup", label);
        }

        @Override
        public void popGroup() {
            groupActive = false;
            record("debug.popGroup", "");
        }

        @Override
        public void label(GLHandle handle, String label) {
            record("debug.label", label);
        }

        @Override
        public boolean isActive() {
            return groupActive;
        }
    }

    private final class VertexInputs implements VertexInputService {
        @Override
        public VertexBindResult bind(VertexSource source, VertexLayout layout,
                VertexInputPlan plan, VertexBindMode mode) {
            ScriptedBinding binding = new ScriptedBinding();
            liveBindings.add(binding);
            record("vertexInputs.bind", binding.name());
            return new VertexBindResult.Bound(binding);
        }

        @Override
        public void restore(VertexBinding binding) {
            if (!(binding instanceof ScriptedBinding scripted)
                    || !liveBindings.remove(scripted)) {
                throw new IllegalArgumentException("restore of a foreign or closed binding");
            }
            record("vertexInputs.restore", scripted.name());
        }
    }

    private String nameOf(GLHandle handle) {
        if (handle instanceof ScriptedHandle scripted) {
            return scripted.name();
        }
        return "handle";
    }
}
