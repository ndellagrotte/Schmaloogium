// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The participant outcome (PHASE_4_DOC §4.10): {@code Continue} proceeds; {@code Degraded}
 * disables only its named participant-owned uniform/expression scope, records the diagnostic
 * and processing continues.
 */
public sealed interface BarrierParticipantResult {

    record Continue() implements BarrierParticipantResult {
    }

    record Degraded(String diagnosticId, String disabledScope)
            implements BarrierParticipantResult {
    }
}
