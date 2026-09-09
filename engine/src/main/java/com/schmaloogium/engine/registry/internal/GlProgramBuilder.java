// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.gl.CompileResult;
import com.schmaloogium.engine.gl.DebugService;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.gl.LinkResult;
import com.schmaloogium.engine.gl.LinkedGeometryInputPrimitive;
import com.schmaloogium.engine.gl.ProgramHandle;
import com.schmaloogium.engine.gl.SamplerInitializationResult;
import com.schmaloogium.engine.gl.ShaderHandle;
import com.schmaloogium.engine.gl.ShaderService;
import com.schmaloogium.engine.gl.ShaderStage;
import com.schmaloogium.engine.gl.ValidateResult;
import com.schmaloogium.engine.registry.ProgramBuildFailure;
import com.schmaloogium.engine.registry.ProgramBuildStage;
import com.schmaloogium.engine.registry.ProgramOwnBuildDisposition;
import com.schmaloogium.engine.registry.ProgramSamplerLayout;
import com.schmaloogium.engine.registry.ProgramSamplerLayoutFingerprint;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.preprocess.ShaderSourceStage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The render-thread GL transaction over planned slots (PHASE_4_DOC §4.7). Creates and
 * compiles shaders, configures native geometry exactly once before link, links, compares the
 * linked geometry input against the expected Phase-3 final form, initializes sampler units
 * pre-validation, validates, deletes shader handles in every path, and labels the program.
 * Unexpected backend exceptions poison nothing: cleanup runs through the delete-and-report
 * stages and the slot is diagnosed UNEXPECTED_BACKEND.
 */
final class GlProgramBuilder {

    private GlProgramBuilder() {
    }

    /** Executes the GL transaction for one planned slot; on success fills the binding. */
    static void build(
            Planner.PlannedSlot planned,
            GLDevice device,
            Map<ProgramSlotId, CompiledProgramBinding> bindingsOut,
            List<ProgramHandle> handlesOut) {
        ProgramSlotId slot = planned.descriptor.id();
        ShaderService shaders = device.shaders();
        List<ShaderHandle> stageShaders = new ArrayList<>();
        ProgramHandle program = null;
        try {
            program = shaders.createProgram();
            for (Planner.MaterializedStage stage : planned.materialized) {
                ShaderStage glStage = ShaderStage.valueOf(stage.stage().name());
                ShaderHandle handle = shaders.createShader(glStage, stage.source().transformedText());
                if (handle == null) {
                    fail(planned, ProgramBuildStage.COMPILE, Optional.of(stage.stage()),
                        "", "SHADER_CREATE_FAILED");
                    deleteAll(shaders, stageShaders);
                    if (program != null) {
                        shaders.delete(program);
                    }
                    return;
                }
                CompileResult compiled = shaders.compile(handle);
                if (!compiled.success()) {
                    fail(planned, ProgramBuildStage.COMPILE, Optional.of(stage.stage()),
                        sanitized(compiled.log()), "SHADER_COMPILE_FAILED");
                    deleteAll(shaders, stageShaders);
                    shaders.delete(handle);
                    shaders.delete(program);
                    return;
                }
                stageShaders.add(handle);
                shaders.attach(program, handle);
            }
            if (planned.nativeConfigure != null) {
                shaders.configureLegacyGeometry(
                    program,
                    planned.nativeConfigure.input(),
                    planned.nativeConfigure.output(),
                    planned.nativeConfigure.maxVerticesOut());
            }
            LinkResult linked = shaders.link(program);
            if (!linked.success()) {
                fail(planned, ProgramBuildStage.LINK, Optional.empty(),
                    sanitized(linked.log()), "LINK_FAILED");
                deleteAll(shaders, stageShaders);
                shaders.delete(program);
                return;
            }
            Optional<LinkedGeometryInputPrimitive> geometryInput =
                shaders.linkedGeometryInput(program);
            LinkedGeometryInputPrimitive expected =
                expectedOf(planned.expectedGeometryInput);
            if (geometryInput.isPresent() != (expected != null)
                || (expected != null && geometryInput.get() != expected)) {
                fail(planned, ProgramBuildStage.LINK, Optional.empty(), "",
                    "GEOMETRY_INPUT_MISMATCH");
                deleteAll(shaders, stageShaders);
                shaders.delete(program);
                return;
            }
            SamplerInitializationResult initialization =
                shaders.initializeSamplerUnits(program, planned.assignments);
            if (initialization instanceof SamplerInitializationResult.Failed failed) {
                boolean restored = failed.selectionRestored();
                fail(planned, ProgramBuildStage.SAMPLER_LAYOUT, Optional.empty(), "",
                    restored ? "SAMPLER_INITIALIZATION_FAILED"
                        : "SAMPLER_INITIALIZATION_UNRESTORED");
                deleteAll(shaders, stageShaders);
                shaders.delete(program);
                if (!restored) {
                    // Poisoned selection; the registry aborts as ShadersOff (§4.7).
                    planned.poisoned = true;
                }
                return;
            }
            ValidateResult validated = shaders.validate(program);
            if (!validated.success()) {
                fail(planned, ProgramBuildStage.VALIDATE, Optional.empty(),
                    sanitized(validated.log()), "PROGRAM_VALIDATE_FAILED");
                deleteAll(shaders, stageShaders);
                shaders.delete(program);
                return;
            }
            deleteAll(shaders, stageShaders);
            device.debug().label(program, "schmaloogium:" + slot.packName());
            planned.disposition = ProgramOwnBuildDisposition.SUCCEEDED;
            planned.samplerLayout = finalLayout(planned);
            handlesOut.add(program);
            bindingsOut.put(slot, new CompiledProgramBinding.ShaderProgram(
                slot,
                program,
                planned.stateBundle,
                planned.uniformLayout,
                planned.samplerLayout,
                planned.materialized.stream()
                    .map(stage -> stage.source().fingerprint())
                    .toList()));
        } catch (RuntimeException unexpected) {
            deleteAll(shaders, stageShaders);
            if (program != null) {
                try {
                    shaders.delete(program);
                } catch (RuntimeException ignored) {
                    // Contained: the diagnostic below still reports the slot failure.
                }
            }
            fail(planned, ProgramBuildStage.UNEXPECTED_BACKEND, Optional.empty(),
                String.valueOf(unexpected.getMessage()), "UNEXPECTED_BACKEND");
            planned.poisoned = unexpected instanceof IllegalStateException;
        }
    }

    private static ProgramSamplerLayout.Shader finalLayout(Planner.PlannedSlot planned) {
        ProgramSamplerLayout.Shader pending = (ProgramSamplerLayout.Shader) planned.samplerLayout;
        return new ProgramSamplerLayout.Shader(
            new ProgramSamplerLayoutFingerprint(
                UniformLayouts.shaderLayoutDigest(pending, planned.assignments)),
            pending.policyFingerprint(),
            pending.effectiveStage(),
            pending.validatedBands(),
            pending.declarations(),
            pending.validation());
    }

    private static LinkedGeometryInputPrimitive expectedOf(
            com.schmaloogium.engine.registry.GeometryInputRequirement requirement) {
        return switch (requirement) {
            case NONE -> null;
            case POINTS -> LinkedGeometryInputPrimitive.POINTS;
            case LINES -> LinkedGeometryInputPrimitive.LINES;
            case LINES_ADJACENCY -> LinkedGeometryInputPrimitive.LINES_ADJACENCY;
            case TRIANGLES -> LinkedGeometryInputPrimitive.TRIANGLES;
            case TRIANGLES_ADJACENCY -> LinkedGeometryInputPrimitive.TRIANGLES_ADJACENCY;
        };
    }

    private static void fail(
            Planner.PlannedSlot planned,
            ProgramBuildStage stage,
            Optional<ShaderSourceStage> shaderStage,
            String log,
            String diagnosticId) {
        planned.disposition = ProgramOwnBuildDisposition.FAILED;
        planned.failure = new ProgramBuildFailure(
            planned.descriptor.id(), Optional.empty(), stage, shaderStage,
            log, List.of(), List.of(), "FAILED", diagnosticId, Optional.empty());
    }

    private static String sanitized(String log) {
        return log == null ? "" : log;
    }

    private static void deleteAll(ShaderService shaders, List<ShaderHandle> shadersList) {
        for (ShaderHandle handle : shadersList) {
            shaders.delete(handle);
        }
    }
}
