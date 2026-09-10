// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.runtime;

import com.schmaloogium.engine.gl.UniformLocation;

import java.util.List;
import java.util.Objects;

/**
 * One immutable resolved sampler plan (PHASE_6_DOC §4.9): rows in ascending-unit then
 * fixed-name declaration order, each carrying the exact name, the cached location and
 * the fixed unit; or a terminal invalid state preserving the typed diagnostic. A cached
 * invalid plan remains a sampler-participant degradation — never an empty successful
 * plan. Absent locations were dropped at build time and are cached as absent in the
 * owning {@link ProgramUniformCacheKey} cache.
 */
public record SamplerPlan(List<Row> rows, String invalidDiagnostic) {

    /** One upload row: (name, location, fixedUnit). */
    public record Row(String name, UniformLocation location, int unit) {
        public Row {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(location, "location");
            if (!location.isAbsent() && (unit < 0 || unit > 15)) {
                throw new IllegalArgumentException("unit must be in 0..15: " + unit);
            }
        }
    }

    public SamplerPlan {
        rows = List.copyOf(rows);
        if (invalidDiagnostic != null && !rows.isEmpty()) {
            throw new IllegalArgumentException(
                    "an invalid plan carries a diagnostic and no rows");
        }
    }

    public static SamplerPlan valid(List<Row> rows) {
        return new SamplerPlan(rows, null);
    }

    public static SamplerPlan invalid(String diagnosticId) {
        return new SamplerPlan(List.of(), diagnosticId);
    }

    public boolean isValid() {
        return invalidDiagnostic == null;
    }
}
