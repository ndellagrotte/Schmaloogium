// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.registry.BarrierContext;
import com.schmaloogium.engine.registry.BarrierParticipantResult;
import com.schmaloogium.engine.registry.ProgramBindingSelection;
import com.schmaloogium.engine.registry.ProgramBindingParticipant;
import com.schmaloogium.engine.registry.BoundProgramUniformAccess;
import com.schmaloogium.engine.registry.ResolvedProgramDescriptor;

import java.util.Objects;

/**
 * One opaque barrier position (PHASE_4_DOC §4.10). The barrier invokes the private
 * participant; no component accessor exists on the public bundle.
 */
record ProgramBindingParticipantRef(ProgramBindingParticipant participant, String position) {

    ProgramBindingParticipantRef {
        Objects.requireNonNull(participant, "participant");
        Objects.requireNonNull(position, "position");
    }

    BarrierParticipantResult invoke(
            ResolvedProgramDescriptor binding,
            ProgramBindingSelection selection,
            BoundProgramUniformAccess uniforms) {
        return participant.afterBind(binding, selection.originatingContext(), uniforms);
    }
}
