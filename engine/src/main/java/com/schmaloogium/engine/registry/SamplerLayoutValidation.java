// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import java.util.List;

/**
 * Closed sampler-layout validation evidence (PHASE_4_DOC §2.2). Empty conflict/unsupported
 * variants, unrelated witnesses/bands, malformed or duplicate payloads and nulls are invalid
 * callback output. Conflict plus unsupported evidence uses {@link ConflictingTypes} — never
 * a loss of either domain. Conflicts have units 0–15 ascending with witnesses in declaration
 * order.
 */
public sealed interface SamplerLayoutValidation {

    /** Every permitted band validated with no conflict or unsupported evidence. */
    record Valid() implements SamplerLayoutValidation {
    }

    /** Fixed-unit type conflicts, plus any coexisting unsupported-shape/name/domain issues. */
    record ConflictingTypes(List<FixedUnitSamplerConflict> conflicts,
            List<SamplerLayoutIssue> otherIssues) implements SamplerLayoutValidation {

        public ConflictingTypes {
            conflicts = List.copyOf(conflicts);
            if (conflicts.isEmpty()) {
                throw new IllegalArgumentException("ConflictingTypes requires conflicts");
            }
            otherIssues = List.copyOf(otherIssues);
        }
    }

    /** Unsupported names/domains/shapes without any fixed-unit conflict. */
    record Unsupported(List<SamplerLayoutIssue> issues) implements SamplerLayoutValidation {

        public Unsupported {
            issues = List.copyOf(issues);
            if (issues.isEmpty()) {
                throw new IllegalArgumentException("Unsupported requires issues");
            }
        }
    }
}
