// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The barrier construction outcome (PHASE_4_DOC §4.10): a ready publication candidate, or an
 * opaque invalid diagnostic. Factory/backend exceptions are diagnosed as Invalid, never
 * thrown.
 */
public sealed interface BarrierConstructionResult {

    record Ready(BarrierPublicationCandidate candidate) implements BarrierConstructionResult {
    }

    record Invalid(String diagnosticId) implements BarrierConstructionResult {
    }
}
