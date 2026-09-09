// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl.record;

import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.gl.AlphaBlendOverride;
import com.schmaloogium.engine.gl.AlphaTestState;
import com.schmaloogium.engine.gl.BlendState;
import com.schmaloogium.engine.gl.BlitSpec;
import com.schmaloogium.engine.gl.BorrowedDepthAttachmentHandle;
import com.schmaloogium.engine.gl.ClearTarget;
import com.schmaloogium.engine.gl.ColorClearValue;
import com.schmaloogium.engine.gl.ColorInternalFormat;
import com.schmaloogium.engine.gl.CompileResult;
import com.schmaloogium.engine.gl.DepthAttachmentFormat;
import com.schmaloogium.engine.gl.DrawService;
import com.schmaloogium.engine.gl.FogState;
import com.schmaloogium.engine.gl.FramebufferDrawSlot;
import com.schmaloogium.engine.gl.FramebufferHandle;
import com.schmaloogium.engine.gl.FramebufferService;
import com.schmaloogium.engine.gl.FramebufferStatus;
import com.schmaloogium.engine.gl.FramebufferTarget;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.gl.GLError;
import com.schmaloogium.engine.gl.GLErrorKind;
import com.schmaloogium.engine.gl.GLHandle;
import com.schmaloogium.engine.gl.LegacyGeometryInputPrimitive;
import com.schmaloogium.engine.gl.LegacyGeometryOutputPrimitive;
import com.schmaloogium.engine.gl.LinkResult;
import com.schmaloogium.engine.gl.LinkedGeometryInputPrimitive;
import com.schmaloogium.engine.gl.FullscreenPrimitive;
import com.schmaloogium.engine.gl.ProgramHandle;
import com.schmaloogium.engine.gl.SamplerInitializationResult;
import com.schmaloogium.engine.gl.SamplerUnitAssignment;
import com.schmaloogium.engine.gl.ShaderHandle;
import com.schmaloogium.engine.gl.ShaderService;
import com.schmaloogium.engine.gl.ShaderStage;
import com.schmaloogium.engine.gl.StateAspect;
import com.schmaloogium.engine.gl.StateService;
import com.schmaloogium.engine.gl.StateSnapshot;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.gl.TextureData;
import com.schmaloogium.engine.gl.TextureExtent;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.gl.TextureRegion;
import com.schmaloogium.engine.gl.TextureService;
import com.schmaloogium.engine.gl.TextureSpec;
import com.schmaloogium.engine.gl.UniformLocation;
import com.schmaloogium.engine.gl.UniformService;
import com.schmaloogium.engine.gl.ValidateResult;
import com.schmaloogium.engine.gl.VertexBindMode;
import com.schmaloogium.engine.gl.VertexBindRejection;
import com.schmaloogium.engine.gl.VertexBindResult;
import com.schmaloogium.engine.gl.VertexBinding;
import com.schmaloogium.engine.gl.VertexInputService;
import com.schmaloogium.engine.gl.VertexSource;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.vertex.AttributePointer;
import com.schmaloogium.engine.vertex.ConventionalInput;
import com.schmaloogium.engine.vertex.StorageType;
import com.schmaloogium.engine.vertex.VertexGeometryInput;
import com.schmaloogium.engine.vertex.VertexInputPlan;
import com.schmaloogium.engine.vertex.VertexLayout;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.zip.CRC32;

/**
 * The recording/replay device for headless tests (PHASE_1_DOC §4.7.5): implements
 * {@link GLDevice} and every service, appends one {@link GLCall} per mutating facade
 * call (plus the explicitly query-shaped events the doc names), answers query-shaped
 * calls from the profile or the supplied {@link ScriptedResponses}, and drains canned
 * and rule-generated errors per [D-P1-30]. Object-creation calls return synthetic
 * handles — monotonic sequence numbers wrapped in the appropriate handle type, never
 * reused including after a delete — which is what makes
 * {@link ReplayAssertions#noUseAfterDelete()} possible here. The rendered log form is
 * stable and deterministic: no timestamps, no identity hash codes, bulk data logged by
 * summary only.
 *
 * <p>Recorder parity with the native rules (§4.7.4a, §4.7.4b, §4.7.6): precondition
 * rejections append nothing; scripted driver errors retain the attempted event and land
 * in the next drain; the committed linked-input metadata comes only from the script or
 * an unoverridden native TRIANGLES default (never a guess); borrowed-depth and vertex
 * sources carry this recorder's private issuer token, and forged or wrong-device values
 * are rejected before any append.
 */
public final class RecordingGLDevice implements GLDevice {

    // ================================================================= identities

    private abstract static class RecObject implements GLCallLog.Named {
        final String name;

        RecObject(long seq, String kind) {
            this.name = kind + "#" + seq;
        }

        @Override
        public final String stableName() {
            return name;
        }

        @Override
        public final String toString() {
            return name;
        }
    }

    private static final class RecProgram extends RecObject implements ProgramHandle {
        RecProgram(long seq) {
            super(seq, "program");
        }
    }

    private static final class RecShader extends RecObject implements ShaderHandle {
        RecShader(long seq) {
            super(seq, "shader");
        }
    }

    private static final class RecTexture extends RecObject implements TextureHandle {
        RecTexture(long seq) {
            super(seq, "texture");
        }
    }

    private static final class RecFramebuffer extends RecObject implements FramebufferHandle {
        RecFramebuffer(long seq) {
            super(seq, "framebuffer");
        }
    }

    /** Borrowed depth, minted only by {@link #borrowDepthAttachment} with this device token. */
    private static final class RecBorrowedDepth extends RecObject
            implements BorrowedDepthAttachmentHandle {
        final RecordingGLDevice owner;
        final DepthAttachmentFormat format;

        RecBorrowedDepth(long seq, RecordingGLDevice owner, DepthAttachmentFormat format) {
            super(seq, "borrowedDepth");
            this.owner = owner;
            this.format = format;
        }
    }

    private static final class RecLocation implements UniformLocation, GLCallLog.Named {
        final RecProgram program;
        final String uniformName;
        final boolean present;

        RecLocation(RecProgram program, String uniformName, boolean present) {
            this.program = program;
            this.uniformName = uniformName;
            this.present = present;
        }

        @Override
        public boolean isAbsent() {
            return !present;
        }

        @Override
        public String stableName() {
            return program.stableName() + "." + uniformName;
        }

        @Override
        public String toString() {
            return stableName();
        }
    }

    private static final class RecSnapshot implements StateSnapshot {
        final Set<StateAspect> aspects;
        final Map<StateAspect, Object> values;

        RecSnapshot(Set<StateAspect> aspects, Map<StateAspect, Object> values) {
            this.aspects = EnumSet.copyOf(aspects);
            this.values = Map.copyOf(values);
        }
    }

    private static final class RecLease implements AlphaBlendOverride, GLCallLog.Named {
        boolean consumed;

        RecLease(long seq) {
            this.seqName = "lease#" + seq;
        }

        private final String seqName;

        @Override
        public String stableName() {
            return seqName;
        }

        @Override
        public String toString() {
            return seqName;
        }

        @Override
        public void close() {
            // idempotent per D-P1-58; the service records the closure event once
        }
    }

    private static final class RecBinding implements VertexBinding, GLCallLog.Named {
        private final String seqName;
        boolean consumed;

        RecBinding(long seq) {
            this.seqName = "binding#" + seq;
        }

        @Override
        public String stableName() {
            return seqName;
        }

        @Override
        public String toString() {
            return seqName;
        }
    }

    private static final class RecClientRange extends RecObject implements VertexSource.ClientRange {
        final int byteOffset;
        final int vertexCount;

        RecClientRange(long seq, int byteOffset, int vertexCount) {
            super(seq, "clientRange");
            this.byteOffset = byteOffset;
            this.vertexCount = vertexCount;
        }
    }

    private static final class RecBorrowedVbo extends RecObject implements VertexSource.BorrowedVbo {
        final long byteLength;

        RecBorrowedVbo(long seq, long byteLength) {
            super(seq, "borrowedVbo");
            this.byteLength = byteLength;
        }
    }

    private static final class RecDisplayList extends RecObject
            implements VertexSource.DisplayListReplay {
        final VertexLayout layout;
        final VertexInputPlan capturePlan;

        RecDisplayList(long seq, VertexLayout layout, VertexInputPlan capturePlan) {
            super(seq, "displayList");
            this.layout = layout;
            this.capturePlan = capturePlan;
        }
    }

    // ================================================================= object state

    private static final class ProgramState {
        String label;
        final Set<RecShader> attached = new LinkedHashSet<>();
        boolean geometryAttached;
        LegacyGeometryInputPrimitive legacyInput;
        LegacyGeometryOutputPrimitive legacyOutput;
        int legacyMaxVertices;
        boolean linkAttempted;
        boolean linked;
        boolean linkFailed;
        Optional<LinkedGeometryInputPrimitive> committedInput = Optional.empty();
        boolean deleted;
    }

    private record ShaderState(String label, ShaderStage stage) {
    }

    private static final class TextureState {
        String label;
        boolean deleted;
        TextureAllocationTarget target;
        boolean allocated;
        boolean depth;
        ColorInternalFormat colorFormat;
        DepthAttachmentFormat depthFormat;
        int width;
        int height;
    }

    private static final class FramebufferState {
        String label;
        boolean deleted;
        final Map<Integer, RecTexture> color = new HashMap<>();
        TextureHandle depth;
        boolean stencil;
        List<FramebufferDrawSlot> route = List.of();
    }

    private static final class VertexSourceState {
        final String kind;
        boolean retired;

        VertexSourceState(String kind) {
            this.kind = kind;
        }
    }

    // ================================================================= fields

    private final GLCapabilityProfile profile;
    private final ScriptedResponses responses;
    private final GLCallLog log;
    private final Thread renderThread;

    private long sequence;
    private final Map<RecProgram, ProgramState> programs = new HashMap<>();
    private final Map<RecShader, ShaderState> shaders = new HashMap<>();
    private final Map<RecTexture, TextureState> textures = new HashMap<>();
    private final Map<RecFramebuffer, FramebufferState> framebuffers = new HashMap<>();
    private final Map<VertexSource, VertexSourceState> vertexSources = new HashMap<>();

    private RecProgram activeProgram;
    private boolean fixedFunctionSelected;
    private boolean selectionUnknown;
    private boolean shaderAdmissionPoisoned;
    private boolean inputStackPoisoned;
    private RecLease openLease;
    private RecFramebuffer boundRead;
    private RecFramebuffer boundDraw;
    private final Map<Integer, RecTexture> unitBindings = new HashMap<>();
    private RecTexture lastBound;
    private long foreignAliases;
    private final Map<StateAspect, Object> stateValues = new EnumMap<>(StateAspect.class);
    private final Deque<RecBinding> bindingStack = new ArrayDeque<>();
    private final List<GLError> errorWindow = new ArrayList<>();
    private boolean mutatedSinceDrain;

    /** Records into a fresh {@link GLCallLog#unbounded()}. */
    public RecordingGLDevice(GLCapabilityProfile profile, ScriptedResponses responses) {
        this(profile, responses, GLCallLog.unbounded());
    }

    /** Records into a caller-supplied log (the live decorator's bounded ring, §4.9.3). */
    public RecordingGLDevice(GLCapabilityProfile profile, ScriptedResponses responses,
                             GLCallLog log) {
        this.profile = java.util.Objects.requireNonNull(profile, "profile");
        this.responses = java.util.Objects.requireNonNull(responses, "responses");
        this.log = java.util.Objects.requireNonNull(log, "log");
        this.renderThread = Thread.currentThread();
        stateValues.put(StateAspect.DEPTH_MASK, Boolean.TRUE);
        stateValues.put(StateAspect.DEPTH_TEST, Boolean.TRUE);
    }

    @Override
    public GLCapabilityProfile capabilities() {
        return profile;
    }

    public GLCallLog log() {
        return log;
    }

    @Override
    public ShaderService shaders() {
        return new ShaderSvc();
    }

    @Override
    public UniformService uniforms() {
        return new UniformSvc();
    }

    @Override
    public TextureService textures() {
        return new TextureSvc();
    }

    @Override
    public FramebufferService framebuffers() {
        return new FramebufferSvc();
    }

    @Override
    public StateService state() {
        return new StateSvc();
    }

    @Override
    public DrawService draw() {
        return new DrawSvc();
    }

    @Override
    public com.schmaloogium.engine.gl.DebugService debug() {
        return new DebugSvc();
    }

    @Override
    public VertexInputService vertexInputs() {
        return new VertexInputSvc();
    }

    @Override
    public List<GLError> drainErrors() {
        List<GLError> out = new ArrayList<>(errorWindow);
        responses.pollAllLeftoverGlErrors(out);
        if (!mutatedSinceDrain && out.isEmpty()) {
            return List.of();
        }
        mutatedSinceDrain = false;
        errorWindow.clear();
        return List.copyOf(out);
    }

    // ============================================================ fixture factories

    /**
     * Synthetic same-recorder client-range issuance (§4.7.6 recorder): mints only a
     * recorder identity; fixture bytes are borrowed, never logged.
     */
    public VertexSource.ClientRange clientVertexSource(
            String label, ByteBuffer bytes, int byteOffset, int vertexCount) {
        requireNonEmpty(label, "label");
        if (bytes == null) {
            throw new IllegalArgumentException("bytes must not be null");
        }
        if (byteOffset < 0 || vertexCount < 0) {
            throw new IllegalArgumentException(
                    "byteOffset and vertexCount must be nonnegative: " + byteOffset + ", " + vertexCount);
        }
        if ((long) byteOffset + vertexCount > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("range overflows");
        }
        RecClientRange source = new RecClientRange(nextSequence(), byteOffset, vertexCount);
        vertexSources.put(source, new VertexSourceState("ClientRange"));
        log.alias(source, label);
        return source;
    }

    /** Synthetic borrowed-VBO issuance; the whole supplied length is the source range. */
    public VertexSource.BorrowedVbo borrowedVertexVbo(String label, long byteLength) {
        requireNonEmpty(label, "label");
        if (byteLength <= 0) {
            throw new IllegalArgumentException("byteLength must be positive: " + byteLength);
        }
        RecBorrowedVbo source = new RecBorrowedVbo(nextSequence(), byteLength);
        vertexSources.put(source, new VertexSourceState("BorrowedVbo"));
        log.alias(source, label);
        return source;
    }

    /**
     * Synthetic replay-safe list issuance (D-P1-70): validates the capture plan against
     * the supplied layout and retains both defensively before returning the identity.
     */
    public VertexSource.DisplayListReplay borrowedVertexList(
            String label, VertexLayout layout, VertexInputPlan capturePlan) {
        requireNonEmpty(label, "label");
        if (layout == null) {
            throw new IllegalArgumentException("layout must not be null");
        }
        if (capturePlan == null) {
            throw new IllegalArgumentException("capturePlan must not be null");
        }
        if (!capturePlan.layoutFingerprint().equals(layout.fingerprint())) {
            throw new IllegalArgumentException("capture plan fingerprint does not match the layout: "
                    + capturePlan.layoutFingerprint() + " vs " + layout.fingerprint());
        }
        validatePlanAgainstLayout(capturePlan, layout);
        RecDisplayList source = new RecDisplayList(nextSequence(), layout, capturePlan);
        vertexSources.put(source, new VertexSourceState("DisplayListReplay"));
        log.alias(source, label);
        return source;
    }

    /** Retires a recorder-issued source; further binds reject with STALE_SOURCE. */
    public void retireVertexSource(VertexSource source) {
        if (source == null) {
            throw new IllegalArgumentException("source must not be null");
        }
        VertexSourceState state = vertexSources.get(source);
        if (state == null) {
            throw new IllegalArgumentException("source was not issued by this recorder");
        }
        state.retired = true;
    }

    // ================================================================= shared helpers

    private long nextSequence() {
        return ++sequence;
    }

    private static void requireNonEmpty(String value, String name) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be null or empty");
        }
    }

    private void checkRenderThread() {
        if (Thread.currentThread() != renderThread) {
            throw new IllegalStateException(
                    "facade calls are render-thread/current-context only (PHASE_1_DOC §4.7.4)");
        }
    }


    private ProgramState ownedLiveProgram(ProgramHandle p) {
        if (!(p instanceof RecProgram program) || !programs.containsKey(program)) {
            throw new IllegalArgumentException(
                    "program handle was not created by this device: " + log.renderArg(p));
        }
        ProgramState state = programs.get(program);
        if (state.deleted) {
            throw new IllegalStateException("program is deleted: " + state.label);
        }
        return state;
    }

    private TextureState ownedLiveTexture(TextureHandle t) {
        if (!(t instanceof RecTexture texture) || !textures.containsKey(texture)) {
            throw new IllegalArgumentException(
                    "texture handle was not created by this device: " + log.renderArg(t));
        }
        TextureState state = textures.get(texture);
        if (state.deleted) {
            throw new IllegalStateException("texture is deleted: " + state.label);
        }
        return state;
    }

    private FramebufferState ownedLiveFramebuffer(FramebufferHandle f) {
        if (!(f instanceof RecFramebuffer framebuffer) || !framebuffers.containsKey(framebuffer)) {
            throw new IllegalArgumentException(
                    "framebuffer handle was not created by this device: " + log.renderArg(f));
        }
        FramebufferState state = framebuffers.get(framebuffer);
        if (state.deleted) {
            throw new IllegalStateException("framebuffer is deleted: " + state.label);
        }
        return state;
    }

    private void appendMutating(String op, Object... args) {
        mutatedSinceDrain = true;
        log.append(new GLCall(op, java.util.Arrays.asList(args)));
    }

    private void appendEvent(String op, Object... args) {
        log.append(new GLCall(op, java.util.Arrays.asList(args)));
    }

    /** Consumes a scripted error for this op into the current drain window, if any. */
    private void scriptErrorIntoWindow(String op) {
        GLError scripted = responses.pollGlError(op);
        if (scripted != null) {
            errorWindow.add(scripted);
        }
    }

    private EngineDiagnostic failureDiagnostic(String messageKey, String detail) {
        return new EngineDiagnostic(DiagnosticSeverity.ERROR, UserChannel.LOG_ONLY,
                messageKey, List.of(), detail, LogChannels.GL);
    }

    private static String crcHex(ByteBuffer texels) {
        CRC32 crc = new CRC32();
        ByteBuffer view = texels.duplicate();
        while (view.hasRemaining()) {
            byte[] chunk = new byte[Math.min(4096, view.remaining())];
            view.get(chunk);
            crc.update(chunk);
        }
        return String.format("%08x", crc.getValue());
    }

    private static String crcHex(float[] values) {
        CRC32 crc = new CRC32();
        for (float v : values) {
            crc.update(Float.floatToRawIntBits(v));
            crc.update(Float.floatToRawIntBits(v) >> 8);
            crc.update(Float.floatToRawIntBits(v) >> 16);
            crc.update(Float.floatToRawIntBits(v) >> 24);
        }
        return String.format("%08x", crc.getValue());
    }

    private static String crcHex(String source) {
        CRC32 crc = new CRC32();
        crc.update(source.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return String.format("%08x", crc.getValue());
    }

    private static boolean isSignedIntegerFormat(ColorInternalFormat format) {
        return switch (format) {
            case R32I, RG32I, RGB32I, RGBA32I -> true;
            default -> false;
        };
    }

    private static boolean isUnsignedIntegerFormat(ColorInternalFormat format) {
        return switch (format) {
            case R32UI, RG32UI, RGB32UI, RGBA32UI -> true;
            default -> false;
        };
    }

    // ================================================================= shader service

    private final class ShaderSvc implements ShaderService {

        @Override
        public ShaderHandle createShader(ShaderStage stage, String source) {
            checkRenderThread();
            if (stage == null) {
                throw new IllegalArgumentException("stage must not be null");
            }
            if (source == null) {
                throw new IllegalArgumentException("source must not be null");
            }
            RecShader shader = new RecShader(nextSequence());
            shaders.put(shader, new ShaderState(shader.stableName(), stage));
            appendMutating("shaders.createShader", shader, stage,
                    "source(" + source.length() + " chars, crc32=0x" + crcHex(source) + ")");
            return shader;
        }

        @Override
        public CompileResult compile(ShaderHandle shader) {
            checkRenderThread();
            if (!(shader instanceof RecShader rec) || !shaders.containsKey(rec)) {
                throw new IllegalArgumentException(
                        "shader handle was not created by this device: " + log.renderArg(shader));
            }
            if (shaders.get(rec) == null) {
                throw new IllegalStateException("shader is deleted");
            }
            String label = shaders.get(rec).label();
            appendMutating("shaders.compile", rec);
            String driverLog = responses.compileFailureFor(label);
            if (driverLog != null) {
                return new CompileResult(false, driverLog,
                        failureDiagnostic("schmaloogium.error.program.compile", driverLog));
            }
            return new CompileResult(true, "", null);
        }

        @Override
        public ProgramHandle createProgram() {
            checkRenderThread();
            RecProgram program = new RecProgram(nextSequence());
            ProgramState state = new ProgramState();
            state.label = program.stableName();
            programs.put(program, state);
            appendMutating("shaders.createProgram", program);
            return program;
        }

        @Override
        public void attach(ProgramHandle p, ShaderHandle s) {
            checkRenderThread();
            ProgramState state = ownedLiveProgram(p);
            if (!(s instanceof RecShader shader) || !shaders.containsKey(shader)
                    || shaders.get(shader) == null) {
                throw new IllegalArgumentException(
                        "shader handle was not created by this device: " + log.renderArg(s));
            }
            state.attached.add(shader);
            if (shaders.get(shader).stage() == ShaderStage.GEOMETRY) {
                state.geometryAttached = true;
            }
            appendMutating("shaders.attach", p, s);
        }

        @Override
        public void bindAttributeLocation(ProgramHandle p, int location, String name) {
            checkRenderThread();
            ProgramState state = ownedLiveProgram(p);
            if (state.linkAttempted) {
                throw new IllegalStateException("bindAttributeLocation is pre-link only");
            }
            if (location < 0 || name == null) {
                throw new IllegalArgumentException(
                        "location must be nonnegative and name nonnull: " + location + ", " + name);
            }
            appendMutating("shaders.bindAttributeLocation", p, location, name);
        }

        @Override
        public void configureLegacyGeometry(ProgramHandle p,
                                            LegacyGeometryInputPrimitive input,
                                            LegacyGeometryOutputPrimitive output,
                                            int maxVerticesOut) {
            checkRenderThread();
            if (p == null || input == null || output == null) {
                throw new IllegalArgumentException(
                        "null program or geometry arguments are rejected before driver calls");
            }
            if (maxVerticesOut <= 0) {
                throw new IllegalArgumentException(
                        "maxVerticesOut must be positive: " + maxVerticesOut);
            }
            ProgramState state = ownedLiveProgram(p);
            if (state.linkAttempted) {
                throw new IllegalStateException(
                        "configureLegacyGeometry is pre-link only; a link was already attempted");
            }
            if (!state.geometryAttached) {
                throw new IllegalStateException(
                        "the program has no attached geometry stage: " + state.label);
            }
            if (!profile.hasExtension("GL_ARB_geometry_shader4")) {
                errorWindow.add(new GLError("shaders.configureLegacyGeometry", state.label,
                        GLErrorKind.INVALID_OPERATION,
                        "default success requires GL_ARB_geometry_shader4 in the recorded profile"));
                return;
            }
            state.legacyInput = input;
            state.legacyOutput = output;
            state.legacyMaxVertices = maxVerticesOut;
            appendMutating("shaders.configureLegacyGeometry", p, input, output, maxVerticesOut);
            scriptErrorIntoWindow("shaders.configureLegacyGeometry");
        }

        @Override
        public LinkResult link(ProgramHandle p) {
            checkRenderThread();
            ProgramState state = ownedLiveProgram(p);
            appendMutating("shaders.link", p);
            state.linkAttempted = true;
            String driverLog = responses.linkFailureFor(state.label);
            if (driverLog != null) {
                state.linkFailed = true;
                return new LinkResult(false, driverLog,
                        failureDiagnostic("schmaloogium.error.program.link", driverLog));
            }
            if (state.geometryAttached) {
                LinkedGeometryInputPrimitive scripted = responses.linkedGeometryFor(state.label);
                LinkedGeometryInputPrimitive effective;
                if (scripted != null) {
                    effective = scripted;
                } else if (profile.hasExtension("GL_ARB_geometry_shader4")) {
                    effective = LinkedGeometryInputPrimitive.TRIANGLES;
                } else if (profile.atLeast(3, 2)) {
                    state.linkFailed = true;
                    String detail = "core geometry without a scripted effective input: "
                            + "linked-input metadata acquisition failed (§4.7.4a)";
                    return new LinkResult(false, detail, failureDiagnostic(
                            "schmaloogium.error.program.link", detail));
                } else {
                    state.linkFailed = true;
                    String detail = "geometry program without GL_ARB_geometry_shader4 and "
                            + "without a scripted effective input";
                    return new LinkResult(false, detail, failureDiagnostic(
                            "schmaloogium.error.program.link", detail));
                }
                state.linked = true;
                state.committedInput = Optional.of(effective);
                return new LinkResult(true, "", null);
            }
            state.linked = true;
            state.committedInput = Optional.empty();
            return new LinkResult(true, "", null);
        }

        @Override
        public Optional<LinkedGeometryInputPrimitive> linkedGeometryInput(ProgramHandle p) {
            checkRenderThread();
            ProgramState state = ownedLiveProgram(p);
            if (!state.linkAttempted || state.linkFailed || !state.linked) {
                throw new IllegalStateException(
                        "linkedGeometryInput requires a successful link: " + state.label);
            }
            Optional<LinkedGeometryInputPrimitive> result = state.committedInput;
            appendEvent("shaders.linkedGeometryInput", p, result);
            return result;
        }

        @Override
        public SamplerInitializationResult initializeSamplerUnits(
                ProgramHandle p, List<SamplerUnitAssignment> assignments) {
            checkRenderThread();
            ProgramState state = ownedLiveProgram(p);
            if (!state.linked) {
                throw new IllegalStateException(
                        "initializeSamplerUnits requires a linked program: " + state.label);
            }
            if (shaderAdmissionPoisoned) {
                throw new IllegalStateException(
                        "shader admission is poisoned by an earlier unprovable restoration");
            }
            if (assignments == null) {
                throw new IllegalArgumentException("assignments must not be null");
            }
            for (SamplerUnitAssignment assignment : assignments) {
                if (assignment == null || assignment.exactName() == null
                        || assignment.unit() < 0 || assignment.unit() > 15) {
                    throw new IllegalArgumentException(
                            "assignments must carry nonnull names and units 0..15: " + assignment);
                }
            }
            appendMutating("shaders.initializeSamplerUnits", p, List.copyOf(assignments));
            SamplerInitializationResult.Failed scripted =
                    responses.samplerInitFailureFor(state.label);
            if (scripted != null) {
                if (!scripted.selectionRestored()) {
                    shaderAdmissionPoisoned = true;
                }
                return scripted;
            }
            return new SamplerInitializationResult.Completed();
        }

        @Override
        public ValidateResult validate(ProgramHandle p) {
            checkRenderThread();
            ProgramState state = ownedLiveProgram(p);
            appendMutating("shaders.validate", p);
            String driverLog = responses.validateFailureFor(state.label);
            if (driverLog != null) {
                return new ValidateResult(false, driverLog,
                        failureDiagnostic("schmaloogium.error.program.validate", driverLog));
            }
            return new ValidateResult(true, "", null);
        }

        @Override
        public void use(ProgramHandle p) {
            checkRenderThread();
            ProgramState state = ownedLiveProgram(p);
            if (!state.linked || state.linkFailed) {
                throw new IllegalStateException(
                        "use requires a live linked program: " + state.label);
            }
            if (shaderAdmissionPoisoned) {
                throw new IllegalStateException("shader admission is poisoned");
            }
            GLError scriptedUse = responses.pollGlError("shaders.use");
            appendMutating("shaders.use", p);
            if (scriptedUse != null) {
                errorWindow.add(scriptedUse);
                selectionUnknown = true;
                activeProgram = null;
                return;
            }
            activeProgram = (RecProgram) p;
            fixedFunctionSelected = false;
            selectionUnknown = false;
        }

        @Override
        public void useFixedFunction() {
            GLError scriptedFF = responses.pollGlError("shaders.useFixedFunction");
            appendMutating("shaders.useFixedFunction");
            if (scriptedFF != null) {
                errorWindow.add(scriptedFF);
                selectionUnknown = true;
                return;
            }
            activeProgram = null;
            fixedFunctionSelected = true;
            selectionUnknown = false;
        }

        @Override
        public void delete(ProgramHandle p) {
            checkRenderThread();
            ProgramState state = ownedLiveProgram(p);
            state.deleted = true;
            if (activeProgram == p) {
                activeProgram = null;
                selectionUnknown = true;
            }
            appendMutating("shaders.delete", p);
        }

        @Override
        public void delete(ShaderHandle s) {
            checkRenderThread();
            if (!(s instanceof RecShader shader) || !shaders.containsKey(shader)) {
                throw new IllegalArgumentException(
                        "shader handle was not created by this device: " + log.renderArg(s));
            }
            if (shaders.remove(shader) == null) {
                throw new IllegalStateException("shader is deleted");
            }
            for (ProgramState state : programs.values()) {
                state.attached.remove(shader);
            }
            appendMutating("shaders.delete", s);
        }
    }

    // ================================================================ uniform service

    private final class UniformSvc implements UniformService {

        @Override
        public UniformLocation locate(ProgramHandle p, String name) {
            checkRenderThread();
            ProgramState state = ownedLiveProgram(p);
            if (name == null) {
                throw new IllegalArgumentException("name must not be null");
            }
            if (!state.linked) {
                throw new IllegalStateException("locate requires a linked program: " + state.label);
            }
            return new RecLocation((RecProgram) p, name, !responses.isUniformAbsent(name));
        }

        private RecLocation authentic(UniformLocation loc) {
            if (!(loc instanceof RecLocation location)) {
                throw new IllegalArgumentException(
                        "uniform location was not issued by this device: " + log.renderArg(loc));
            }
            return location;
        }

        @Override
        public void upload(UniformLocation loc, int v) {
            uploadInts(loc, v);
        }

        @Override
        public void upload(UniformLocation loc, int x, int y) {
            uploadInts(loc, x, y);
        }

        @Override
        public void upload(UniformLocation loc, int x, int y, int z, int w) {
            uploadInts(loc, x, y, z, w);
        }

        @Override
        public void upload(UniformLocation loc, float v) {
            uploadFloats(loc, v);
        }

        @Override
        public void upload(UniformLocation loc, float x, float y) {
            uploadFloats(loc, x, y);
        }

        @Override
        public void upload(UniformLocation loc, float x, float y, float z) {
            uploadFloats(loc, x, y, z);
        }

        @Override
        public void upload(UniformLocation loc, float x, float y, float z, float w) {
            uploadFloats(loc, x, y, z, w);
        }

        private void uploadInts(UniformLocation loc, int... values) {
            checkRenderThread();
            RecLocation location = authentic(loc);
            if (location.isAbsent()) {
                return;
            }
            if (programs.get(location.program) == null
                    || programs.get(location.program).deleted) {
                throw new IllegalStateException("the located program is deleted");
            }
            Object[] args = new Object[values.length + 1];
            args[0] = loc;
            for (int i = 0; i < values.length; i++) {
                args[i + 1] = values[i];
            }
            appendMutating("uniforms.upload", args);
        }

        private void uploadFloats(UniformLocation loc, float... values) {
            checkRenderThread();
            RecLocation location = authentic(loc);
            if (location.isAbsent()) {
                return;
            }
            if (programs.get(location.program) == null
                    || programs.get(location.program).deleted) {
                throw new IllegalStateException("the located program is deleted");
            }
            Object[] args = new Object[values.length + 1];
            args[0] = loc;
            for (int i = 0; i < values.length; i++) {
                args[i + 1] = values[i];
            }
            appendMutating("uniforms.upload", args);
        }

        @Override
        public void uploadMatrix4(UniformLocation loc, float[] m16, boolean transpose) {
            checkRenderThread();
            RecLocation location = authentic(loc);
            if (m16 == null || m16.length != 16) {
                throw new IllegalArgumentException("m16 must carry exactly 16 floats");
            }
            if (location.isAbsent()) {
                return;
            }
            appendMutating("uniforms.uploadMatrix4", loc, transpose,
                    "mat4(crc32=0x" + crcHex(m16) + ")");
        }
    }

    // ================================================================ texture service

    private final class TextureSvc implements TextureService {

        @Override
        public TextureHandle create(String debugLabel) {
            checkRenderThread();
            requireNonEmpty(debugLabel, "debugLabel");
            RecTexture texture = new RecTexture(nextSequence());
            TextureState state = new TextureState();
            state.label = debugLabel;
            textures.put(texture, state);
            appendMutating("textures.create", texture);
            return texture;
        }

        @Override
        public void allocate(TextureHandle t, TextureSpec spec) {
            checkRenderThread();
            TextureState state = ownedLiveTexture(t);
            if (spec == null) {
                throw new IllegalArgumentException("spec must not be null");
            }
            checkAllocationAdmission(state, spec);
            appendMutating("textures.allocate", t, spec);
        }

        private void checkAllocationAdmission(TextureState state, TextureSpec spec) {
            TextureAllocationTarget target = targetOf(spec);
            TextureExtent extent = extentOf(spec);
            switch (target) {
                case TEXTURE_1D -> {
                    if (extent.width() > profile.maxTextureSize()) {
                        throw new IllegalArgumentException("1D width exceeds max.textureSize: "
                                + extent.width() + " > " + profile.maxTextureSize());
                    }
                }
                case TEXTURE_2D -> {
                    if (extent.width() > profile.maxTextureSize()
                            || extent.height() > profile.maxTextureSize()) {
                        throw new IllegalArgumentException("2D extent exceeds max.textureSize: "
                                + extent.width() + "x" + extent.height()
                                + " > " + profile.maxTextureSize());
                    }
                }
                case TEXTURE_3D -> {
                    if (profile.max3DTextureSize() <= 0) {
                        throw new IllegalArgumentException(
                                "this profile supports no 3D textures (max.3DTextureSize = 0)");
                    }
                    if (extent.width() > profile.max3DTextureSize()
                            || extent.height() > profile.max3DTextureSize()
                            || extent.depth() > profile.max3DTextureSize()) {
                        throw new IllegalArgumentException("3D extent exceeds max.3DTextureSize: "
                                + extent.width() + "x" + extent.height() + "x" + extent.depth());
                    }
                }
                case RECTANGLE -> {
                    if (profile.maxRectangleTextureSize() <= 0) {
                        throw new IllegalArgumentException("this profile supports no rectangle "
                                + "textures (max.rectangleTextureSize = 0)");
                    }
                    if (extent.width() > profile.maxRectangleTextureSize()
                            || extent.height() > profile.maxRectangleTextureSize()) {
                        throw new IllegalArgumentException("rectangle extent exceeds "
                                + "max.rectangleTextureSize: " + extent.width() + "x" + extent.height());
                    }
                }
            }
            state.target = target;
            state.allocated = true;
            state.width = extent.width();
            state.height = extent.height();
            if (spec instanceof TextureSpec.DepthTextureSpec depthSpec) {
                state.depth = true;
                state.depthFormat = depthSpec.format();
                state.colorFormat = null;
            } else if (spec instanceof TextureSpec.ColorTextureSpec colorSpec) {
                state.depth = false;
                state.colorFormat = colorSpec.format();
                state.depthFormat = null;
            }
        }

        private TextureAllocationTarget targetOf(TextureSpec spec) {
            if (spec instanceof TextureSpec.ColorTextureSpec color) {
                return color.target();
            }
            return ((TextureSpec.DepthTextureSpec) spec).target();
        }

        private TextureExtent extentOf(TextureSpec spec) {
            if (spec instanceof TextureSpec.ColorTextureSpec color) {
                return color.extent();
            }
            return ((TextureSpec.DepthTextureSpec) spec).extent();
        }

        @Override
        public void setParameters(TextureHandle t, com.schmaloogium.engine.gl.TextureParameters p) {
            checkRenderThread();
            TextureState state = ownedLiveTexture(t);
            if (p == null) {
                throw new IllegalArgumentException("parameters must not be null");
            }
            appendMutating("textures.setParameters", t, p);
        }

        @Override
        public void upload(TextureHandle t, TextureData data) {
            checkRenderThread();
            TextureState state = ownedLiveTexture(t);
            if (data == null) {
                throw new IllegalArgumentException("data must not be null");
            }
            if (!state.allocated) {
                throw new IllegalStateException("upload requires an allocated texture: "
                        + state.label);
            }
            TextureRegion region = data.region();
            if (region.x() + (long) region.width() > state.width
                    || region.y() + (long) region.height() > state.height) {
                throw new IllegalArgumentException("upload region does not fit the allocated "
                        + "level: " + region + " vs " + state.width + "x" + state.height);
            }
            long texelCount = data.texels().remaining();
            appendMutating("textures.upload", t, region, data.mipLevel(), data.layout(),
                    texelCount, "crc32=0x" + crcHex(data.texels()));
        }

        @Override
        public void prepareUnitBindings(int occupiedUnitMask) {
            checkRenderThread();
            if ((occupiedUnitMask & ~0xFFFF) != 0) {
                throw new IllegalArgumentException(
                        "occupied-unit mask must be a 16-bit mask: " + occupiedUnitMask);
            }
            appendMutating("textures.prepareUnitBindings", occupiedUnitMask);
        }

        @Override
        public void bindToUnit(int unit, TextureHandle t) {
            checkRenderThread();
            if (unit < 0 || unit > 15) {
                throw new IllegalArgumentException("unit must be 0..15: " + unit);
            }
            classifyBindable(t, "bindToUnit");
            appendMutating("textures.bindToUnit", unit, t);
            if (t instanceof RecTexture texture) {
                unitBindings.put(unit, texture);
                lastBound = texture;
            }
        }

        @Override
        public void generateMipmap(TextureHandle t) {
            checkRenderThread();
            ownedLiveTexture(t);
            appendMutating("textures.generateMipmap", t);
        }

        @Override
        public void delete(TextureHandle t) {
            checkRenderThread();
            if (t instanceof RecBorrowedDepth) {
                throw new IllegalArgumentException(
                        "an authenticated borrowed-depth handle is never deletable (§4.7.5)");
            }
            TextureState state = ownedLiveTexture(t);
            state.deleted = true;
            unitBindings.values().removeIf(texture -> texture == t);
            if (lastBound == t) {
                lastBound = null;
            }
            appendMutating("textures.delete", t);
        }
    }

    /**
     * Classification before any call ([D-P1-40]): owned texture, authenticated
     * borrowed-depth of this device, or ordinary foreign value; forged borrowed markers
     * are rejected before any append.
     */
    private void classifyBindable(TextureHandle t, String verb) {
        if (t == null) {
            throw new IllegalArgumentException(verb + ": texture must not be null");
        }
        if (t instanceof RecBorrowedDepth borrowed) {
            if (borrowed.owner != RecordingGLDevice.this) {
                throw new IllegalArgumentException(
                        verb + ": borrowed-depth handle was minted by another device");
            }
            return;
        }
        if (t instanceof BorrowedDepthAttachmentHandle) {
            throw new IllegalArgumentException(
                    verb + ": forged borrowed-depth marker rejected before any call");
        }
        if (t instanceof RecTexture) {
            ownedLiveTexture(t);
            return;
        }
        log.alias(t, "foreignTexture#" + (++foreignAliases));
    }

    // ============================================================ framebuffer service

    private final class FramebufferSvc implements FramebufferService {

        @Override
        public FramebufferHandle create(String debugLabel) {
            checkRenderThread();
            requireNonEmpty(debugLabel, "debugLabel");
            RecFramebuffer framebuffer = new RecFramebuffer(nextSequence());
            FramebufferState state = new FramebufferState();
            state.label = debugLabel;
            framebuffers.put(framebuffer, state);
            appendMutating("framebuffers.create", framebuffer);
            return framebuffer;
        }

        @Override
        public BorrowedDepthAttachmentHandle borrowDepthAttachment(TextureHandle platformTexture) {
            checkRenderThread();
            if (platformTexture == null) {
                throw new IllegalArgumentException("platformTexture must not be null");
            }
            if (platformTexture instanceof RecTexture) {
                throw new IllegalArgumentException(
                        "borrowDepthAttachment wraps an ordinary foreign platform texture, "
                                + "not an engine-owned texture");
            }
            if (platformTexture instanceof BorrowedDepthAttachmentHandle) {
                throw new IllegalArgumentException(
                        "forged or previously borrowed marker rejected before any call");
            }
            RecBorrowedDepth borrowed = new RecBorrowedDepth(
                    nextSequence(), RecordingGLDevice.this, DepthAttachmentFormat.DEPTH_COMPONENT);
            log.alias(platformTexture, "platformTexture#" + borrowed.stableName());
            appendMutating("framebuffers.borrowDepthAttachment", borrowed, platformTexture);
            return borrowed;
        }

        /** Owned live texture, or authenticated borrowed-depth of this device (§4.7.4). */
        private TextureState classifyDepthCandidate(TextureHandle t, String verb) {
            if (t == null) {
                throw new IllegalArgumentException(verb + ": texture must not be null");
            }
            if (t instanceof RecBorrowedDepth borrowed) {
                if (borrowed.owner != RecordingGLDevice.this) {
                    throw new IllegalArgumentException(
                            verb + ": borrowed-depth handle was minted by another device");
                }
                return null;
            }
            if (t instanceof BorrowedDepthAttachmentHandle) {
                throw new IllegalArgumentException(
                        verb + ": forged borrowed-depth marker rejected before any call");
            }
            return ownedLiveTexture(t);
        }

        @Override
        public void attachColor(FramebufferHandle f, int attachmentIndex, TextureHandle t) {
            checkRenderThread();
            FramebufferState state = ownedLiveFramebuffer(f);
            if (attachmentIndex < 0 || attachmentIndex >= profile.maxColorAttachments()) {
                throw new IllegalArgumentException("attachment index outside this profile's "
                        + "max.colorAttachments: " + attachmentIndex);
            }
            ownedLiveTexture(t);
            state.color.put(attachmentIndex, (RecTexture) t);
            appendMutating("framebuffers.attachColor", f, attachmentIndex, t);
        }

        @Override
        public void attachDepth(FramebufferHandle f, TextureHandle t) {
            checkRenderThread();
            FramebufferState state = ownedLiveFramebuffer(f);
            classifyDepthCandidate(t, "attachDepth");
            state.depth = t;
            state.stencil = false;
            appendMutating("framebuffers.attachDepth", f, t, boundRead, boundDraw);
            scriptErrorIntoWindow("framebuffers.attachDepth");
        }

        @Override
        public void attachDepthStencil(FramebufferHandle f, TextureHandle t) {
            checkRenderThread();
            FramebufferState state = ownedLiveFramebuffer(f);
            TextureState owned = classifyDepthCandidate(t, "attachDepthStencil");
            if (owned != null) {
                if (!owned.depth || owned.depthFormat != DepthAttachmentFormat.DEPTH24_STENCIL8) {
                    throw new IllegalArgumentException("attachDepthStencil requires the exact "
                            + "packed DEPTH24_STENCIL8 metadata, got " + owned.depthFormat);
                }
            } else if (((RecBorrowedDepth) t).format != DepthAttachmentFormat.DEPTH24_STENCIL8) {
                throw new IllegalArgumentException("borrowed-depth metadata does not prove the "
                        + "combined DEPTH24_STENCIL8 format");
            }
            state.depth = t;
            state.stencil = true;
            appendMutating("framebuffers.attachDepthStencil", f, t, boundRead, boundDraw);
            scriptErrorIntoWindow("framebuffers.attachDepthStencil");
        }

        @Override
        public void drawBuffers(FramebufferHandle f, List<FramebufferDrawSlot> slots) {
            checkRenderThread();
            FramebufferState state = ownedLiveFramebuffer(f);
            if (slots == null) {
                throw new IllegalArgumentException("slots must not be null");
            }
            if (slots.size() > profile.maxDrawBuffers()) {
                throw new IllegalArgumentException("route length exceeds max.drawBuffers: "
                        + slots.size() + " > " + profile.maxDrawBuffers());
            }
            Set<Integer> seen = new HashSet<>();
            for (FramebufferDrawSlot slot : slots) {
                if (slot instanceof FramebufferDrawSlot.Attachment attachment
                        && !seen.add(attachment.index())) {
                    throw new IllegalArgumentException("duplicate attachment in route: "
                            + attachment.index());
                }
            }
            state.route = List.copyOf(slots);
            appendMutating("framebuffers.drawBuffers", f, state.route);
        }

        @Override
        public FramebufferStatus check(FramebufferHandle f) {
            checkRenderThread();
            FramebufferState state = ownedLiveFramebuffer(f);
            FramebufferStatus scripted = responses.statusFor(state.label);
            return scripted == null ? FramebufferStatus.COMPLETE : scripted;
        }

        @Override
        public void bind(FramebufferTarget target, FramebufferHandle f) {
            checkRenderThread();
            if (target == null) {
                throw new IllegalArgumentException("target must not be null");
            }
            FramebufferState state = ownedLiveFramebuffer(f);
            if (target == FramebufferTarget.READ || target == FramebufferTarget.READ_AND_DRAW) {
                boundRead = (RecFramebuffer) f;
            }
            if (target == FramebufferTarget.DRAW || target == FramebufferTarget.READ_AND_DRAW) {
                boundDraw = (RecFramebuffer) f;
            }
            appendMutating("framebuffers.bind", target, f);
        }

        @Override
        public void bindDefault(FramebufferTarget target) {
            checkRenderThread();
            if (target == null) {
                throw new IllegalArgumentException("target must not be null");
            }
            if (target == FramebufferTarget.READ || target == FramebufferTarget.READ_AND_DRAW) {
                boundRead = null;
            }
            if (target == FramebufferTarget.DRAW || target == FramebufferTarget.READ_AND_DRAW) {
                boundDraw = null;
            }
            appendMutating("framebuffers.bindDefault", target);
        }

        @Override
        public void blit(FramebufferHandle src, FramebufferHandle dst, BlitSpec spec) {
            checkRenderThread();
            FramebufferState srcState = ownedLiveFramebuffer(src);
            FramebufferState dstState = ownedLiveFramebuffer(dst);
            if (spec == null) {
                throw new IllegalArgumentException("spec must not be null");
            }
            appendMutating("framebuffers.blit", src, dst, spec, boundRead, boundDraw);
        }

        @Override
        public void initializeDepthTextureFromFramebuffer(
                FramebufferHandle src, TextureHandle dst, TextureRegion region) {
            checkRenderThread();
            FramebufferState srcState = ownedLiveFramebuffer(src);
            TextureState dstState = ownedLiveTexture(dst);
            if (region.z() != 0 || region.depth() != 1) {
                throw new IllegalArgumentException("the depth verbs take a 2D region "
                        + "(srcX, srcY, 0, width, height, 1): " + region);
            }
            if (srcState.depth == null) {
                throw new IllegalArgumentException(
                        "the source framebuffer has no depth attachment to initialize from");
            }
            TextureState depthInfo = srcState.depth instanceof RecTexture ownedDepth
                    ? textures.get(ownedDepth) : null;
            dstState.allocated = true;
            dstState.depth = true;
            dstState.depthFormat = depthInfo != null && depthInfo.depthFormat != null
                    ? depthInfo.depthFormat : DepthAttachmentFormat.DEPTH_COMPONENT;
            dstState.colorFormat = null;
            dstState.target = TextureAllocationTarget.TEXTURE_2D;
            dstState.width = region.width();
            dstState.height = region.height();
            appendMutating("framebuffers.initializeDepthTextureFromFramebuffer",
                    src, dst, region, boundRead, boundDraw, lastBoundTexture());
            scriptErrorIntoWindow("framebuffers.initializeDepthTextureFromFramebuffer");
        }

        @Override
        public void copyDepthToTexture(FramebufferHandle src, TextureHandle dst,
                                       TextureRegion region) {
            checkRenderThread();
            FramebufferState srcState = ownedLiveFramebuffer(src);
            TextureState dstState = ownedLiveTexture(dst);
            if (region.z() != 0 || region.depth() != 1) {
                throw new IllegalArgumentException("the depth verbs take a 2D region "
                        + "(srcX, srcY, 0, width, height, 1): " + region);
            }
            if (!dstState.allocated || !dstState.depth) {
                throw new IllegalArgumentException("steady copy requires an owned destination "
                        + "with defined depth storage; initialization defines it");
            }
            if (region.width() != dstState.width || region.height() != dstState.height) {
                throw new IllegalArgumentException("steady copy never redefines storage: region "
                        + region.width() + "x" + region.height() + " vs level zero "
                        + dstState.width + "x" + dstState.height);
            }
            if (srcState.depth == null) {
                throw new IllegalArgumentException(
                        "the source framebuffer has no depth attachment to copy");
            }
            TextureState depthInfo = srcState.depth instanceof RecTexture ownedDepth
                    ? textures.get(ownedDepth) : null;
            DepthAttachmentFormat srcFormat = depthInfo != null && depthInfo.depthFormat != null
                    ? depthInfo.depthFormat : DepthAttachmentFormat.DEPTH_COMPONENT;
            if (srcFormat != dstState.depthFormat) {
                throw new IllegalArgumentException("steady copy rejects mismatched storage: "
                        + srcFormat + " vs " + dstState.depthFormat);
            }
            appendMutating("framebuffers.copyDepthToTexture",
                    src, dst, region, boundRead, boundDraw, lastBoundTexture());
            scriptErrorIntoWindow("framebuffers.copyDepthToTexture");
        }

        private RecTexture lastBoundTexture() {
            return lastBound;
        }

        @Override
        public float readDepthPixel(FramebufferHandle f, int x, int y) {
            checkRenderThread();
            FramebufferState state = ownedLiveFramebuffer(f);
            if (x < 0 || y < 0) {
                throw new IllegalArgumentException("coordinates must be nonnegative: " + x + ", " + y);
            }
            Float scripted = responses.depthPixelFor(state.label, x, y);
            float answer = scripted == null ? 0.0f : scripted;
            appendEvent("framebuffers.readDepthPixel", f, x, y, answer);
            return answer;
        }

        @Override
        public void clearColorAttachment(FramebufferHandle f, int drawBufferIndex,
                                         ColorClearValue value) {
            checkRenderThread();
            FramebufferState state = ownedLiveFramebuffer(f);
            if (value == null) {
                throw new IllegalArgumentException("value must not be null");
            }
            if (drawBufferIndex < 0 || drawBufferIndex >= profile.maxDrawBuffers()) {
                throw new IllegalArgumentException("draw-buffer index outside this profile's "
                        + "max.drawBuffers: " + drawBufferIndex);
            }
            if (drawBufferIndex >= state.route.size()) {
                throw new IllegalArgumentException("draw-buffer index outside the established "
                        + "route of length " + state.route.size() + ": " + drawBufferIndex);
            }
            FramebufferDrawSlot slot = state.route.get(drawBufferIndex);
            if (!(slot instanceof FramebufferDrawSlot.Attachment attachment)) {
                throw new IllegalArgumentException("a None route slot clears nothing and is a "
                        + "hole, never a successful no-op: index " + drawBufferIndex);
            }
            RecTexture target = state.color.get(attachment.index());
            if (target == null) {
                throw new IllegalArgumentException("route slot " + drawBufferIndex + " names "
                        + "attachment " + attachment.index() + " with no attached color texture");
            }
            TextureState targetState = ownedLiveTexture(target);
            if (targetState.depth || !targetState.allocated) {
                throw new IllegalArgumentException("clearColorAttachment clears an attached "
                        + "defined color attachment");
            }
            boolean signed = isSignedIntegerFormat(targetState.colorFormat);
            boolean unsigned = isUnsignedIntegerFormat(targetState.colorFormat);
            if (value instanceof ColorClearValue.Floating && (signed || unsigned)) {
                throw new IllegalArgumentException("floating clear on an integer attachment");
            }
            if (value instanceof ColorClearValue.Signed && !signed) {
                throw new IllegalArgumentException("signed clear on a non-signed-integer attachment");
            }
            if (value instanceof ColorClearValue.Unsigned && !unsigned) {
                throw new IllegalArgumentException(
                        "unsigned clear on a non-unsigned-integer attachment");
            }
            if ((signed || unsigned)
                    && !(profile.atLeast(3, 0) || profile.hasExtension("GL_EXT_texture_integer"))) {
                throw new UnsupportedOperationException("integer clears require GL3 or "
                        + "GL_EXT_texture_integer (D-P1-67)");
            }
            if (value instanceof ColorClearValue.Floating && !profile.atLeast(3, 0)
                    && !profile.hasExtension("GL_ARB_texture_float")
                    && !profile.hasExtension("GL_EXT_color_buffer_float")) {
                throw new UnsupportedOperationException("floating clears require a float "
                        + "renderable capability (D-P1-67)");
            }
            appendMutating("framebuffers.clearColorAttachment", f, drawBufferIndex, value);
            scriptErrorIntoWindow("framebuffers.clearColorAttachment");
            GLError scriptedRestore = responses.pollGlError(
                    "framebuffers.clearColorAttachment.restore");
            boolean restored = scriptedRestore == null;
            if (scriptedRestore != null) {
                errorWindow.add(scriptedRestore);
                shaderAdmissionPoisoned = true;
            }
            appendMutating("framebuffers.clearColorAttachment.restore", f, restored);
        }
        @Override
        public void delete(FramebufferHandle f) {
            checkRenderThread();
            FramebufferState state = ownedLiveFramebuffer(f);
            state.deleted = true;
            if (boundRead == f) {
                boundRead = null;
            }
            if (boundDraw == f) {
                boundDraw = null;
            }
            appendMutating("framebuffers.delete", f);
        }
    }

    // ================================================================= state service

    private final class StateSvc implements StateService {

        @Override
        public void viewport(int x, int y, int w, int h) {
            checkRenderThread();
            if (w < 0 || h < 0) {
                throw new IllegalArgumentException("viewport extent must be nonnegative");
            }
            stateValues.put(StateAspect.VIEWPORT, List.of(x, y, w, h));
            appendMutating("state.viewport", x, y, w, h);
        }

        @Override
        public void clearColor(float r, float g, float b, float a) {
            checkRenderThread();
            stateValues.put(StateAspect.CLEAR_COLOR, List.of(r, g, b, a));
            appendMutating("state.clearColor", r, g, b, a);
        }

        @Override
        public void clear(EnumSet<ClearTarget> targets) {
            checkRenderThread();
            if (targets == null || targets.isEmpty()) {
                throw new IllegalArgumentException("clear targets must be a nonempty set");
            }
            if (targets.contains(ClearTarget.COLOR) && boundDraw != null) {
                FramebufferState drawState = framebuffers.get(boundDraw);
                for (RecTexture attached : drawState.color.values()) {
                    TextureState texState = textures.get(attached);
                    if (texState != null && !texState.depth && texState.allocated
                            && (isSignedIntegerFormat(texState.colorFormat)
                            || isUnsignedIntegerFormat(texState.colorFormat))) {
                        throw new IllegalArgumentException(
                                "state.clear(COLOR) must not target an integer destination (D-P1-67)");
                    }
                }
            }
            stateValues.put(StateAspect.CLEAR, EnumSet.copyOf(targets));
            appendMutating("state.clear", EnumSet.copyOf(targets));
        }

        @Override
        public void depthMask(boolean enabled) {
            checkRenderThread();
            stateValues.put(StateAspect.DEPTH_MASK, enabled);
            appendMutating("state.depthMask", enabled);
        }

        @Override
        public void depthTest(boolean enabled) {
            checkRenderThread();
            stateValues.put(StateAspect.DEPTH_TEST, enabled);
            appendMutating("state.depthTest", enabled);
        }

        @Override
        public void blend(BlendState state) {
            checkRenderThread();
            stateValues.put(StateAspect.BLEND, state);
            appendMutating("state.blend", state);
        }

        @Override
        public void alphaTest(AlphaTestState state) {
            checkRenderThread();
            stateValues.put(StateAspect.ALPHA_TEST, state);
            appendMutating("state.alphaTest", state);
        }

        @Override
        public AlphaBlendOverride lockAlphaBlend(Optional<AlphaTestState> alpha,
                                                 Optional<BlendState> blend) {
            checkRenderThread();
            if (alpha == null || blend == null) {
                throw new IllegalArgumentException(
                        "lockAlphaBlend requires nonnull optionals; empty means ordinary");
            }
            if (openLease != null) {
                throw new IllegalStateException(
                        "one lease per device; nested acquisition rejects before mutation");
            }
            RecLease lease = new RecLease(nextSequence());
            openLease = lease;
            appendMutating("state.lockAlphaBlend", alpha.orElse(null), blend.orElse(null));
            return new AlphaBlendOverride() {
                @Override
                public void close() {
                    if (lease.consumed || openLease != lease) {
                        return;
                    }
                    lease.consumed = true;
                    openLease = null;
                    appendMutating("state.lockAlphaBlend.close", lease);
                }
            };
        }

        @Override
        public BlendState effectiveBlend() {
            Object value = stateValues.get(StateAspect.BLEND);
            return value instanceof BlendState blend ? blend
                    : new BlendState(BlendState.BlendFactor.ZERO, BlendState.BlendFactor.ZERO,
                    BlendState.BlendFactor.ZERO, BlendState.BlendFactor.ZERO);
        }

        @Override
        public void fog(FogState state) {
            checkRenderThread();
            stateValues.put(StateAspect.FOG, state);
            appendMutating("state.fog", state);
        }

        @Override
        public StateSnapshot snapshot(EnumSet<StateAspect> aspects) {
            checkRenderThread();
            if (aspects == null || aspects.isEmpty()) {
                throw new IllegalArgumentException("snapshot aspects must be a nonempty set");
            }
            Map<StateAspect, Object> captured = new EnumMap<>(StateAspect.class);
            for (StateAspect aspect : aspects) {
                captured.put(aspect, stateValues.get(aspect));
            }
            return new RecSnapshot(aspects, captured);
        }

        @Override
        public void restore(StateSnapshot snapshot) {
            checkRenderThread();
            if (!(snapshot instanceof RecSnapshot rec)) {
                throw new IllegalArgumentException(
                        "snapshot was not taken by this device: " + snapshot);
            }
            stateValues.putAll(rec.values);
            appendMutating("state.restore", rec.aspects);
        }
    }

    // ================================================================== draw service

    private final class DrawSvc implements DrawService {

        @Override
        public void fullscreenQuad() {
            checkRenderThread();
            LinkedGeometryInputPrimitive input;
            if (selectionUnknown || (activeProgram == null && !fixedFunctionSelected)) {
                appendEvent("draw.fullscreenQuadRejected", "UNKNOWN_PROGRAM");
                return;
            }
            if (fixedFunctionSelected) {
                input = null;
            } else {
                ProgramState state = programs.get(activeProgram);
                input = state.committedInput.orElse(null);
            }
            FullscreenPrimitive primitive;
            if (input == null) {
                primitive = FullscreenPrimitive.QUADS;
            } else if (input == LinkedGeometryInputPrimitive.TRIANGLES) {
                primitive = FullscreenPrimitive.TRIANGLE_STRIP;
            } else {
                String label = activeProgram == null ? "fixed-function"
                        : programs.get(activeProgram).label;
                errorWindow.add(new GLError("draw.fullscreenQuad", label,
                        GLErrorKind.INVALID_OPERATION,
                        "input " + input + " cannot be served by the fullscreen primitive"));
                appendEvent("draw.fullscreenQuadRejected", "INCOMPATIBLE_INPUT");
                return;
            }
            appendMutating("draw.fullscreenQuad", primitive);
            scriptErrorIntoWindow("draw.fullscreenQuad");
        }
    }

    // ================================================================= debug service


    private final class DebugSvc implements com.schmaloogium.engine.gl.DebugService {

        @Override
        public void pushGroup(String label) {
            checkRenderThread();
            requireNonEmpty(label, "label");
            appendMutating("debug.pushGroup", label);
        }

        @Override
        public void popGroup() {
            checkRenderThread();
            appendMutating("debug.popGroup");
        }

        @Override
        public void label(GLHandle handle, String label) {
            checkRenderThread();
            if (label == null) {
                throw new IllegalArgumentException("label must not be null");
            }
            if (handle == null) {
                throw new IllegalArgumentException("handle must not be null");
            }
            if (handle instanceof RecBorrowedDepth borrowed) {
                if (borrowed.owner != RecordingGLDevice.this) {
                    throw new IllegalArgumentException(
                            "borrowed-depth handle was minted by another device");
                }
            } else if (handle instanceof BorrowedDepthAttachmentHandle) {
                throw new IllegalArgumentException("forged borrowed-depth marker rejected");
            } else if (handle instanceof RecProgram program) {
                ownedLiveProgram(program).label = label;
            } else if (handle instanceof RecTexture texture) {
                textures.get(texture).label = label;
            } else if (handle instanceof RecFramebuffer framebuffer) {
                framebuffers.get(framebuffer).label = label;
            } else if (handle instanceof RecShader shader) {
                ShaderState existing = shaders.get(shader);
                if (existing == null) {
                    throw new IllegalStateException("shader is deleted");
                }
                appendMutating("debug.label", handle, label);
                return;
            } else {
                log.alias(handle, label);
            }
            appendMutating("debug.label", handle, label);
        }

        @Override
        public boolean isActive() {
            return false;
        }
    }

    // =========================================================== vertex input service

        private VertexBindRejection validatePlan(VertexInputPlan plan, VertexLayout layout) {
            Set<Integer> locations = new HashSet<>();
            for (AttributePointer pointer : plan.pointers()) {
                if (pointer == null || pointer.name() == null) {
                    return VertexBindRejection.INVALID_PLAN;
                }
                if (pointer.location() < 0 || pointer.location() > 15) {
                    return VertexBindRejection.OUT_OF_RANGE;
                }
                if (!locations.add(pointer.location())) {
                    return VertexBindRejection.INVALID_PLAN;
                }
                if (pointer.byteOffset() < 0 || pointer.components() < 1
                        || pointer.components() > 4
                        || pointer.byteOffset() + (long) storageSize(pointer.storage())
                        > layout.strideBytes()) {
                    return VertexBindRejection.OUT_OF_RANGE;
                }
                if (plan.conventionalInputs().contains(ConventionalInput.POSITION)
                        && pointer.location() == 0) {
                    return VertexBindRejection.INVALID_PLAN;
                }
            }
            return null;
        }

        private void validatePlanAgainstLayout(VertexInputPlan plan, VertexLayout layout) {
            VertexBindRejection rejection = validatePlan(plan, layout);
            if (rejection != null) {
                throw new IllegalArgumentException(
                        "capture plan fails admission under the layout: " + rejection);
            }
        }
    private final class VertexInputSvc implements VertexInputService {

        @Override
        public VertexBindResult bind(VertexSource source, VertexLayout layout,
                                     VertexInputPlan plan, VertexBindMode mode) {
            checkRenderThread();
            if (source == null) {
                return new VertexBindResult.Rejected(VertexBindRejection.INVALID_SOURCE);
            }
            VertexSourceState sourceState = vertexSources.get(source);
            if (sourceState == null) {
                return new VertexBindResult.Rejected(VertexBindRejection.INVALID_SOURCE);
            }
            if (sourceState.retired) {
                return new VertexBindResult.Rejected(VertexBindRejection.STALE_SOURCE);
            }
            if (layout == null) {
                return new VertexBindResult.Rejected(VertexBindRejection.INVALID_LAYOUT);
            }
            if (layout.strideBytes() <= 0) {
                return new VertexBindResult.Rejected(VertexBindRejection.INVALID_LAYOUT);
            }
            for (com.schmaloogium.engine.vertex.VertexField field : layout.fields()) {
                if (field.byteOffset() < 0 || field.components() < 1 || field.components() > 4
                        || field.byteOffset() + (long) storageSize(field.storage())
                        > layout.strideBytes()) {
                    return field.byteOffset() < 0
                            ? new VertexBindResult.Rejected(VertexBindRejection.INVALID_LAYOUT)
                            : new VertexBindResult.Rejected(VertexBindRejection.OUT_OF_RANGE);
                }
            }
            if (plan == null) {
                return new VertexBindResult.Rejected(VertexBindRejection.INVALID_PLAN);
            }
            if (!plan.layoutFingerprint().equals(layout.fingerprint())) {
                return new VertexBindResult.Rejected(VertexBindRejection.INVALID_PLAN);
            }
            VertexBindRejection planRejection = validatePlan(plan, layout);
            if (planRejection != null) {
                return new VertexBindResult.Rejected(planRejection);
            }
            if (mode == null) {
                return new VertexBindResult.Rejected(VertexBindRejection.UNSUPPORTED_INPUT);
            }
            boolean modeCompatible = switch (mode) {
                case LIVE_DRAW -> source instanceof VertexSource.ClientRange
                        || source instanceof VertexSource.BorrowedVbo;
                case LIST_CAPTURE -> source instanceof VertexSource.ClientRange;
                case LIST_REPLAY_GUARD -> source instanceof VertexSource.DisplayListReplay;
            };
            if (!modeCompatible) {
                return new VertexBindResult.Rejected(VertexBindRejection.UNSUPPORTED_INPUT);
            }
            if (mode != VertexBindMode.LIST_CAPTURE) {
                VertexGeometryInput expected = plan.expectedGeometryInput();
                if (!expectedMatchesActual(expected, currentEffectiveInput())) {
                    return new VertexBindResult.Rejected(VertexBindRejection.UNSUPPORTED_INPUT);
                }
            }
            if (inputStackPoisoned) {
                return new VertexBindResult.Rejected(VertexBindRejection.UNSUPPORTED_INPUT);
            }
            RecBinding binding = new RecBinding(nextSequence());
            appendMutating("vertexInputs.bind", source, sourceState.kind, layout, plan, mode,
                    binding);
            GLError scriptedBind = responses.pollGlError("vertexInputs.bind");
            if (scriptedBind != null) {
                errorWindow.add(scriptedBind);
                appendMutating("vertexInputs.rollback", binding, true);
                return new VertexBindResult.Failed(
                        "vertexInputs.bind:" + binding.stableName());
            }
            bindingStack.push(binding);
            return new VertexBindResult.Bound(binding);
        }



        /** Null means unknown/unavailable; the Optional carries the committed requirement. */
        private Optional<LinkedGeometryInputPrimitive> currentEffectiveInput() {
            if (selectionUnknown || activeProgram == null) {
                return null;
            }
            ProgramState state = programs.get(activeProgram);
            return state == null ? null : state.committedInput;
        }

        private boolean expectedMatchesActual(VertexGeometryInput expected,
                                              Optional<LinkedGeometryInputPrimitive> actual) {
            if (actual == null) {
                return false;
            }
            if (expected == VertexGeometryInput.NONE) {
                return actual.isEmpty();
            }
            return actual.isPresent() && actual.get().name().equals(expected.name());
        }

        @Override
        public void restore(VertexBinding binding) {
            checkRenderThread();
            if (binding == null) {
                throw new IllegalArgumentException("binding must not be null");
            }
            if (!(binding instanceof RecBinding rec) || !bindingStack.contains(rec)) {
                throw new IllegalArgumentException(
                        "binding was not issued by this device: " + log.renderArg(binding));
            }
            if (rec.consumed) {
                throw new IllegalStateException("a consumed binding is never retried");
            }
            if (bindingStack.peek() != rec) {
                throw new IllegalStateException(
                        "restore accepts only the same device's live top binding (LIFO)");
            }
            bindingStack.pop();
            rec.consumed = true;
            appendMutating("vertexInputs.restore", rec, true);
            GLError scriptedRestore = responses.pollGlError("vertexInputs.restore");
            if (scriptedRestore != null) {
                errorWindow.add(scriptedRestore);
                appendMutating("vertexInputs.restore", rec, false);
                inputStackPoisoned = true;
                bindingStack.clear();
            }
        }
    }

    private static int storageSize(StorageType storage) {
        return switch (storage) {
            case FLOAT32 -> 4;
            case UINT8, INT8 -> 1;
            case INT16 -> 2;
        };
    }
}
