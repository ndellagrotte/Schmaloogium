// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import java.util.Objects;

/** Outcome of planning (PHASE_5_DOC §2.2): a valid plan with resource evidence, or a failure. */
public sealed interface BufferPlanResult permits BufferPlanResult.Valid, BufferPlanResult.Invalid {

    /** Planning succeeded. */
    record Valid(BufferPlan plan, BufferResourceSnapshot.Available resources) implements BufferPlanResult {

        public Valid {
            Objects.requireNonNull(plan, "plan");
            Objects.requireNonNull(resources, "resources");
        }
    }

    /** Planning failed. */
    record Invalid(BufferFailure failure, BufferResourceSnapshot resources) implements BufferPlanResult {

        public Invalid {
            Objects.requireNonNull(failure, "failure");
            Objects.requireNonNull(resources, "resources");
        }
    }
}
