// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import java.util.List;
import java.util.Optional;

/**
 * Shader objects, programs, and the two program-selection modes (PHASE_1_DOC §4.7.4).
 * {@code compile}/{@code link}/{@code validate} return results and NEVER throw; §G2.4
 * rung 3's delete-and-report stays a data problem.
 *
 * <p>Program selection has two explicit modes, and neither mode is the whole state
 * barrier: {@link #use} requires a live linked program; {@link #useFixedFunction}
 * selects program zero — the backend owns that integer mapping, zero never appears in an
 * {@code :engine} signature, and neither null nor a synthetic sentinel handle is valid
 * ([D-P1-39]). Phase 4 still owns the universal program-state barrier policy.
 */
public interface ShaderService {

    ShaderHandle createShader(ShaderStage stage, String source);

    CompileResult compile(ShaderHandle shader);          // never throws

    ProgramHandle createProgram();

    void attach(ProgramHandle p, ShaderHandle s);

    /** Pre-link only. */
    void bindAttributeLocation(ProgramHandle p, int location, String name);

    /**
     * The one narrow legacy-geometry pre-link operation (§0.25, D-P1-44): replaces the
     * complete parameter triple before the program's first link attempt. Render
     * thread/current context only; the target must be the live, unpublished program of
     * this device with its geometry shader already attached. Null/foreign/forged handles,
     * null enum arguments or nonpositive counts reject with {@code IllegalArgumentException};
     * deleted programs, no attached geometry stage, wrong thread/context or a prior link
     * attempt reject with {@code IllegalStateException} — all before driver calls or
     * recorder mutation. Repeating it before link replaces the whole triple; there is no
     * accumulated delta, reset, post-link reconfiguration or ownership transfer. Phase 4
     * must first require {@code capabilities().hasExtension("GL_ARB_geometry_shader4")}.
     */
    void configureLegacyGeometry(ProgramHandle p,
                                 LegacyGeometryInputPrimitive input,
                                 LegacyGeometryOutputPrimitive output, int maxVerticesOut);

    LinkResult link(ProgramHandle p);                    // never throws

    /**
     * The immutable cached successful-link input requirement (§4.7.4a, D-P1-48):
     * {@code Optional.empty()} means no geometry stage, never unknown. Performs no GL
     * call, source scan, selection or allocation. Null/forged/wrong-device handle gives
     * {@code IllegalArgumentException}; wrong thread/context, deleted, never-linked or
     * failed-link handle gives {@code IllegalStateException} — all before GL.
     */
    Optional<LinkedGeometryInputPrimitive> linkedGeometryInput(ProgramHandle p);

    /**
     * Candidate sampler initialization (D-P1-59): Phase 4 alone calls it after
     * successful link/linked-input checks and before validation, with Phase 5's immutable
     * complete fixed-unit assignments. An empty assignment list completes without GL.
     * Never throws — failures come back as {@link SamplerInitializationResult.Failed},
     * and a failed or unprovable restoration poisons shader admission until safe backend
     * recovery.
     */
    SamplerInitializationResult initializeSamplerUnits(
            ProgramHandle p, List<SamplerUnitAssignment> assignments);

    ValidateResult validate(ProgramHandle p);            // never throws

    /** Select a live linked shader program. */
    void use(ProgramHandle p);

    /** Select program zero; no null/magic handle ([D-P1-39]). Also clears the strategy's
     *  sampler bindings on units 0-15 before returning (D-P1-60). */
    void useFixedFunction();

    void delete(ProgramHandle p);

    void delete(ShaderHandle s);
}
