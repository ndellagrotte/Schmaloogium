// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.gl.ProgramHandle;
import com.schmaloogium.engine.preprocess.MaterializationFingerprint;
import com.schmaloogium.engine.registry.ProgramSamplerLayout;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.ProgramStateBundle;
import com.schmaloogium.engine.registry.ProgramUniformLayout;

import java.util.List;

/**
 * Phase-4-private compiled binding (PHASE_4_DOC §2.2). A shader binding owns exactly one
 * immutable GL program handle; the fixed-function binding is the handle-free terminal. The
 * resolved backup chain points at the provider's single immutable binding — a child never
 * overlays any part of it (§4.6, D-P4-4).
 */
public sealed interface CompiledProgramBinding {

    /** The provider slot that owns this binding. */
    com.schmaloogium.engine.registry.ProgramSlotId provider();

    ProgramStateBundle state();

    /** One linked shader program with its complete immutable metadata. */
    record ShaderProgram(
            com.schmaloogium.engine.registry.ProgramSlotId provider,
            ProgramHandle handle,
            ProgramStateBundle state,
            ProgramUniformLayout uniformLayout,
            ProgramSamplerLayout samplerLayout,
            List<MaterializationFingerprint> sources) implements CompiledProgramBinding {

        public ShaderProgram {
            java.util.Objects.requireNonNull(provider, "provider");
            java.util.Objects.requireNonNull(handle, "handle");
            java.util.Objects.requireNonNull(state, "state");
            java.util.Objects.requireNonNull(uniformLayout, "uniformLayout");
            java.util.Objects.requireNonNull(samplerLayout, "samplerLayout");
            sources = List.copyOf(sources);
        }
    }

    /** The fixed-function terminal: no program object, default state, no locks. */
    record FixedFunction(
            com.schmaloogium.engine.registry.ProgramSlotId provider,
            ProgramStateBundle state) implements CompiledProgramBinding {

        public FixedFunction {
            java.util.Objects.requireNonNull(provider, "provider");
            java.util.Objects.requireNonNull(state, "state");
        }
    }
}
