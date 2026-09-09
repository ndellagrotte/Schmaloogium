// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.gl.CompileResult;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.LinkResult;
import com.schmaloogium.engine.gl.LinkedGeometryInputPrimitive;
import com.schmaloogium.engine.gl.LegacyGeometryInputPrimitive;
import com.schmaloogium.engine.gl.LegacyGeometryOutputPrimitive;
import com.schmaloogium.engine.gl.ProgramHandle;
import com.schmaloogium.engine.gl.SamplerInitializationResult;
import com.schmaloogium.engine.gl.SamplerUnitAssignment;
import com.schmaloogium.engine.gl.ShaderHandle;
import com.schmaloogium.engine.gl.ShaderService;
import com.schmaloogium.engine.gl.ShaderStage;
import com.schmaloogium.engine.gl.ValidateResult;

import org.lwjgl.opengl.ARBGeometryShader4;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.schmaloogium.mod.glue.GlDiagnostics.diagnostic;

/**
 * The LWJGL3 shader pipeline (PHASE_1_DOC §4.7.4). Program-selection has exactly two
 * explicit modes (D-P1-39): {@link #use(ProgramHandle)} selects a live linked program,
 * {@link #useFixedFunction()} selects program zero — zero never appears in the API.
 * [D-P1-59]'s sampler-initialization protocol runs with no installed sampler strategy at
 * v0.1: strategy-owned sampler bindings on units 0-15 are an empty set, so their clear is
 * a no-op, and the previous selection is always restored.
 */
final class Lwjgl3ShaderService implements ShaderService {

    private static final int MAX_SAMPLER_UNITS = 16;

    private final Lwjgl3GLDevice device;
    private final GLCapabilityProfile profile;
    private final AtomicInteger sequence = new AtomicInteger();

    private final Map<Lwjgl3ShaderHandle, ShaderRecord> shaderRecords = new HashMap<>();
    private final Map<Lwjgl3ProgramHandle, ProgramRecord> programRecords = new HashMap<>();

    Lwjgl3ShaderService(Lwjgl3GLDevice device) {
        this.device = device;
        this.profile = device.capabilities();
    }

    private static final class ShaderRecord {
        String source;
        boolean compiled;
        String compileLog;
    }

    private static final class ProgramRecord {
        final Set<ShaderStage> attachedStages = EnumSet.noneOf(ShaderStage.class);
        boolean geometryAttached;
        LegacyGeometryInputPrimitive configuredInput;
        LegacyGeometryOutputPrimitive configuredOutput;
        int configuredMaxVerticesOut;
        boolean configured;
        boolean linkAttempted;
        boolean linkSucceeded;
        LinkedGeometryInputPrimitive linkedInput;
        String linkLog;
    }

    private String label(String kind) {
        return kind + "-" + sequence.incrementAndGet();
    }

    // ------------------------------------------------------------- creation

    @Override
    public ShaderHandle createShader(ShaderStage stage, String source) {
        device.requireRenderThread("shaders.createShader");
        if (stage == null || source == null) {
            throw new IllegalArgumentException("shaders.createShader: stage and source must not be null");
        }
        int name = GL20.glCreateShader(GlNames.glShaderStage(stage));
        Lwjgl3ShaderHandle handle = new Lwjgl3ShaderHandle(device, stage, name, label("shader"));
        GL20.glShaderSource(name, source);
        device.noteMutation("shaders.createShader", handle.subjectLabel());
        shaderRecords.put(handle, new ShaderRecord());
        return handle;
    }

    @Override
    public CompileResult compile(ShaderHandle shader) {
        device.requireRenderThread("shaders.compile");
        Lwjgl3ShaderHandle h = device.shaderOf(shader, "shaders.compile");
        device.noteMutation("shaders.compile", h.subjectLabel());
        GL20.glCompileShader(h.glName());
        int[] status = new int[1];
        GL20.glGetShaderiv(h.glName(), GL20.GL_COMPILE_STATUS, status);
        String log = h.glName() != 0 ? GL20.glGetShaderInfoLog(h.glName()) : "";
        ShaderRecord rec = shaderRecords.get(h);
        boolean success = status[0] == GL11.GL_TRUE;
        if (rec != null) {
            rec.compiled = success;
            rec.compileLog = log;
        }
        if (success) {
            return new CompileResult(true, log, null);
        }
        return new CompileResult(false, log, diagnostic("gl.shader.compile",
                log.isEmpty() ? "shader compilation failed" : log));
    }

    @Override
    public ProgramHandle createProgram() {
        device.requireRenderThread("shaders.createProgram");
        int name = GL20.glCreateProgram();
        Lwjgl3ProgramHandle handle = new Lwjgl3ProgramHandle(device, name, label("program"));
        device.noteMutation("shaders.createProgram", handle.subjectLabel());
        programRecords.put(handle, new ProgramRecord());
        return handle;
    }

    @Override
    public void attach(ProgramHandle p, ShaderHandle s) {
        device.requireRenderThread("shaders.attach");
        Lwjgl3ProgramHandle program = device.programOf(p, "shaders.attach");
        if (!(s instanceof Lwjgl3ShaderHandle shader) || shader.owner() != device || shader.deleted()) {
            throw new IllegalArgumentException("shaders.attach: foreign, wrong-device or deleted shader handle");
        }
        ProgramRecord rec = programRecords.get(program);
        if (rec == null || rec.linkAttempted) {
            throw new IllegalStateException("shaders.attach: program already linked or unknown");
        }
        device.noteMutation("shaders.attach", program.subjectLabel());
        GL20.glAttachShader(program.glName(), shader.glName());
        rec.attachedStages.add(shader.stage());
        if (shader.stage() == ShaderStage.GEOMETRY) {
            rec.geometryAttached = true;
        }
    }

    @Override
    public void bindAttributeLocation(ProgramHandle p, int location, String name) {
        device.requireRenderThread("shaders.bindAttributeLocation");
        Lwjgl3ProgramHandle program = device.programOf(p, "shaders.bindAttributeLocation");
        ProgramRecord rec = programRecords.get(program);
        if (rec == null || rec.linkAttempted) {
            throw new IllegalStateException("shaders.bindAttributeLocation: program already linked or unknown");
        }
        if (name == null || location < 0) {
            throw new IllegalArgumentException("shaders.bindAttributeLocation: invalid attribute binding");
        }
        device.noteMutation("shaders.bindAttributeLocation", name);
        GL20.glBindAttribLocation(program.glName(), location, name);
    }

    @Override
    public void configureLegacyGeometry(ProgramHandle p,
                                        LegacyGeometryInputPrimitive input,
                                        LegacyGeometryOutputPrimitive output, int maxVerticesOut) {
        device.requireRenderThread("shaders.configureLegacyGeometry");
        Lwjgl3ProgramHandle program = device.programOf(p, "shaders.configureLegacyGeometry");
        ProgramRecord rec = programRecords.get(program);
        if (rec == null || rec.linkAttempted) {
            throw new IllegalStateException("shaders.configureLegacyGeometry: program already linked or unknown");
        }
        if (input == null || output == null || maxVerticesOut <= 0) {
            throw new IllegalArgumentException("shaders.configureLegacyGeometry: invalid geometry configuration");
        }
        if (!rec.geometryAttached) {
            throw new IllegalStateException(
                    "shaders.configureLegacyGeometry: no geometry shader attached to this program");
        }
        if (!profile.atLeast(3, 2) && !profile.hasExtension("GL_ARB_geometry_shader4")) {
            device.noteMutation("shaders.configureLegacyGeometry", program.subjectLabel());
            device.queueError("shaders.configureLegacyGeometry", program.subjectLabel(),
                    com.schmaloogium.engine.gl.GLErrorKind.INVALID_OPERATION,
                    "geometry shaders unsupported on this profile (no GL 3.2, no GL_ARB_geometry_shader4)");
            return;
        }
        device.noteMutation("shaders.configureLegacyGeometry", program.subjectLabel());
        int name = program.glName();
        ARBGeometryShader4.glProgramParameteriARB(name,
                ARBGeometryShader4.GL_GEOMETRY_INPUT_TYPE_ARB, GlNames.legacyGeometryInput(input));
        ARBGeometryShader4.glProgramParameteriARB(name,
                ARBGeometryShader4.GL_GEOMETRY_OUTPUT_TYPE_ARB, GlNames.legacyGeometryOutput(output));
        ARBGeometryShader4.glProgramParameteriARB(name,
                ARBGeometryShader4.GL_GEOMETRY_VERTICES_OUT_ARB, maxVerticesOut);
        rec.configured = true;
        rec.configuredInput = input;
        rec.configuredOutput = output;
        rec.configuredMaxVerticesOut = maxVerticesOut;
    }

    // ------------------------------------------------------------- linking

    @Override
    public LinkResult link(ProgramHandle p) {
        device.requireRenderThread("shaders.link");
        Lwjgl3ProgramHandle program = device.programOf(p, "shaders.link");
        ProgramRecord rec = programRecords.get(program);
        if (rec == null) {
            throw new IllegalStateException("shaders.link: unknown program");
        }
        device.noteMutation("shaders.link", program.subjectLabel());
        int name = program.glName();
        GL20.glLinkProgram(name);
        rec.linkAttempted = true;
        int[] status = new int[1];
        GL20.glGetProgramiv(name, GL20.GL_LINK_STATUS, status);
        String log = GL20.glGetProgramInfoLog(name);
        rec.linkLog = log;
        rec.linkSucceeded = status[0] == GL11.GL_TRUE;
        if (rec.linkSucceeded) {
            rec.linkedInput = acquireLinkedGeometryInput(program, rec);
            if (rec.geometryAttached && rec.linkedInput == null) {
                // Metadata acquisition failure converts a linking success into an
                // unsuccessful link (PHASE_1_DOC §4.7.4): the executable requirement
                // cannot be proven on this profile.
                rec.linkSucceeded = false;
                String detail = "linked geometry input metadata unavailable on this profile";
                return new LinkResult(false, log, diagnostic("gl.shader.link.metadata", detail));
            }
            return new LinkResult(true, log, null);
        }
        return new LinkResult(false, log, diagnostic("gl.shader.link",
                log.isEmpty() ? "program linking failed" : log));
    }

    /**
     * Linked-geometry-input metadata acquisition (PHASE_1_DOC §4.7.4a): the core query on
     * GL 3.2+; the clean configured input below 3.2. Returns null when acquisition fails.
     */
    private LinkedGeometryInputPrimitive acquireLinkedGeometryInput(Lwjgl3ProgramHandle program,
                                                                    ProgramRecord rec) {
        if (!rec.geometryAttached) {
            return LinkedGeometryInputPrimitive.POINTS; // unreachable value; caller gates on geometryAttached
        }
        if (profile.atLeast(3, 2)) {
            int[] type = new int[1];
            GL20.glGetProgramiv(program.glName(), GL32.GL_GEOMETRY_INPUT_TYPE, type);
            return GlNames.linkedGeometryInput(type[0]); // null when strips/fans or unknown
        }
        if (rec.configured && rec.configuredInput == LegacyGeometryInputPrimitive.TRIANGLES) {
            return LinkedGeometryInputPrimitive.TRIANGLES;
        }
        return null;
    }

    @Override
    public Optional<LinkedGeometryInputPrimitive> linkedGeometryInput(ProgramHandle p) {
        device.requireRenderThread("shaders.linkedGeometryInput");
        Lwjgl3ProgramHandle program = device.programOf(p, "shaders.linkedGeometryInput");
        ProgramRecord rec = programRecords.get(program);
        if (rec == null || !rec.linkAttempted || !rec.linkSucceeded) {
            throw new IllegalStateException(
                    "shaders.linkedGeometryInput: program never linked or link failed");
        }
        return Optional.ofNullable(rec.linkedInput);
    }

    @Override
    public SamplerInitializationResult initializeSamplerUnits(
            ProgramHandle p, List<SamplerUnitAssignment> assignments) {
        device.requireRenderThread("shaders.initializeSamplerUnits");
        Lwjgl3ProgramHandle program = device.programOf(p, "shaders.initializeSamplerUnits");
        ProgramRecord rec = programRecords.get(program);
        if (rec == null || !rec.linkAttempted || !rec.linkSucceeded) {
            throw new IllegalStateException("shaders.initializeSamplerUnits: program not live (must be linked)");
        }
        if (assignments == null || assignments.isEmpty()) {
            throw new IllegalArgumentException("shaders.initializeSamplerUnits: assignments must be nonempty");
        }
        Set<String> seen = new HashSet<>();
        for (SamplerUnitAssignment a : assignments) {
            if (a == null || a.exactName() == null) {
                throw new IllegalArgumentException("shaders.initializeSamplerUnits: null assignment");
            }
            if (!seen.add(a.exactName())) {
                throw new IllegalArgumentException(
                        "shaders.initializeSamplerUnits: duplicate uniform name " + a.exactName());
            }
            if (a.unit() < 0 || a.unit() >= MAX_SAMPLER_UNITS) {
                throw new IllegalArgumentException(
                        "shaders.initializeSamplerUnits: unit " + a.unit() + " outside 0-15");
            }
        }
        // D-P1-59 step 1: retain the previous selection and drain any preceding errors.
        Lwjgl3ProgramHandle previous = device.activeProgram();
        int previousName = previous != null ? previous.glName() : 0;
        if (!device.drainErrors().isEmpty()) {
            return new SamplerInitializationResult.Failed("preceding errors left in the window", true);
        }
        // Step 2: privately select the candidate (tracker updated; no participant dispatch).
        GL20.glUseProgram(program.glName());
        device.setActiveProgram(program);
        // Step 3: locate + upload each sampler, draining each window, stopping at the
        // first error.
        for (SamplerUnitAssignment a : assignments) {
            int location = GL20.glGetUniformLocation(program.glName(), a.exactName());
            if (location < 0) {
                continue; // optimized out: no error, nothing to upload
            }
            GL20.glUniform1i(location, a.unit());
            if (device.perCallCadenceActive()) {
                device.checkErrorsNow("shaders.initializeSamplerUnits", a.exactName());
                if (!device.drainErrors().isEmpty()) {
                    restore(previous, previousName);
                    return new SamplerInitializationResult.Failed(
                            "driver error during sampler initialization of " + a.exactName(), true);
                }
            }
        }
        restore(previous, previousName);
        if (device.perCallCadenceActive()) {
            device.checkErrorsNow("shaders.initializeSamplerUnits", program.subjectLabel());
            if (!device.drainErrors().isEmpty()) {
                return new SamplerInitializationResult.Failed("driver error during restore", true);
            }
        }
        return new SamplerInitializationResult.Completed();
    }

    private void restore(Lwjgl3ProgramHandle previous, int previousName) {
        GL20.glUseProgram(previousName);
        device.setActiveProgram(previous); // null encodes fixed function
    }

    // ------------------------------------------------------------- validation/use

    @Override
    public ValidateResult validate(ProgramHandle p) {
        device.requireRenderThread("shaders.validate");
        Lwjgl3ProgramHandle program = device.programOf(p, "shaders.validate");
        device.noteMutation("shaders.validate", program.subjectLabel());
        int name = program.glName();
        GL20.glValidateProgram(name);
        int[] status = new int[1];
        GL20.glGetProgramiv(name, GL20.GL_VALIDATE_STATUS, status);
        String log = GL20.glGetProgramInfoLog(name);
        boolean success = status[0] == GL11.GL_TRUE;
        if (success) {
            return new ValidateResult(true, log, null);
        }
        return new ValidateResult(false, log, diagnostic("gl.shader.validate",
                log.isEmpty() ? "program validation failed" : log));
    }

    @Override
    public void use(ProgramHandle p) {
        device.requireRenderThread("shaders.use");
        if (p == null) {
            throw new IllegalArgumentException(
                    "shaders.use: null is not a program handle - use shaders.useFixedFunction() (D-P1-39)");
        }
        Lwjgl3ProgramHandle program = device.programOf(p, "shaders.use");
        ProgramRecord rec = programRecords.get(program);
        if (rec == null || !rec.linkAttempted || !rec.linkSucceeded) {
            throw new IllegalStateException("shaders.use: program not live (must be linked)");
        }
        GL20.glUseProgram(program.glName());
        if (GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM) != program.glName()) {
            device.markProgramSelectionUnknown("shaders.use", program.subjectLabel());
            return;
        }
        device.setActiveProgram(program);
    }

    @Override
    public void useFixedFunction() {
        device.requireRenderThread("shaders.useFixedFunction");
        GL20.glUseProgram(0);
        if (GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM) != 0) {
            device.markProgramSelectionUnknown("shaders.useFixedFunction", "(fixed function)");
            return;
        }
        device.setActiveProgram(null);
        // Strategy-owned sampler bindings on units 0-15 (D-P1-60): none exist at v0.1.
    }

    // ------------------------------------------------------------- deletion

    @Override
    public void delete(ProgramHandle p) {
        device.requireRenderThread("shaders.delete");
        Lwjgl3ProgramHandle program = device.programOf(p, "shaders.delete");
        if (device.activeProgram() == program) {
            GL20.glUseProgram(0);
            device.setActiveProgram(null);
        }
        device.noteMutation("shaders.delete", program.subjectLabel());
        GL20.glDeleteProgram(program.glName());
        program.markDeleted();
        programRecords.remove(program);
    }

    @Override
    public void delete(ShaderHandle s) {
        device.requireRenderThread("shaders.delete");
        if (!(s instanceof Lwjgl3ShaderHandle shader) || shader.owner() != device || shader.deleted()) {
            throw new IllegalArgumentException("shaders.delete: foreign, wrong-device or deleted shader handle");
        }
        device.noteMutation("shaders.delete", shader.subjectLabel());
        GL20.glDeleteShader(shader.glName());
        shader.markDeleted();
        shaderRecords.remove(shader);
    }

    /** True when the given program is this backend's live linked program. */
    boolean isLiveLinked(ProgramHandle p) {
        if (!(p instanceof Lwjgl3ProgramHandle program) || program.owner() != device || program.deleted()) {
            return false;
        }
        ProgramRecord rec = programRecords.get(program);
        return rec != null && rec.linkAttempted && rec.linkSucceeded;
    }

    /** The live program's geometry input requirement, empty when no geometry stage. */
    Optional<LinkedGeometryInputPrimitive> linkedInputOf(ProgramHandle p) {
        if (!isLiveLinked(p)) {
            return Optional.empty();
        }
        return linkedGeometryInput(p);
    }
}
