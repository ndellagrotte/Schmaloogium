// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.vertex;

import com.schmaloogium.engine.registry.BarrierContext;
import com.schmaloogium.engine.registry.BarrierParticipantResult;
import com.schmaloogium.engine.registry.BoundProgramUniformAccess;
import com.schmaloogium.engine.registry.ExtendedAttribute;
import com.schmaloogium.engine.registry.GeometryInputRequirement;
import com.schmaloogium.engine.registry.ProgramBindingParticipant;
import com.schmaloogium.engine.registry.ResolvedProgramDescriptor;
import com.schmaloogium.engine.vertex.VertexGeometryInput;

import java.util.Set;

/**
 * The "shared declaration plan" input (PHASE_10_DOC §4.6): which classic attributes the
 * currently activated program declares. Every activation, main and shadow, passes through
 * the P4 barrier's participants, so decorating the P6 custom participant observes all of
 * them without a P4 change (Task F ruling 2). No {@code glGet*}, no source parse per
 * draw: the tracker is written at activation and cleared at scope exit / frame end.
 */
public final class VertexProgramInputTracker {

    /** The last activation's declarations and linked geometry input. */
    public record Declared(Set<ExtendedAttribute> attributes, VertexGeometryInput geometry,
                           String program) {
    }

    private static volatile Declared current;

    private VertexProgramInputTracker() {
    }

    /** Decorates a participant: forwards, then records the activated descriptor. */
    public static ProgramBindingParticipant participant(ProgramBindingParticipant inner) {
        return (binding, context, uniforms) -> {
            BarrierParticipantResult result = inner.afterBind(binding, context, uniforms);
            note(binding);
            return result;
        };
    }

    static void note(ResolvedProgramDescriptor binding) {
        current = new Declared(binding.state().attributes(),
                geometryOf(binding.state().geometryInput()), binding.effective().packName());
    }

    private static VertexGeometryInput geometryOf(GeometryInputRequirement requirement) {
        return switch (requirement) {
            case NONE -> VertexGeometryInput.NONE;
            case POINTS -> VertexGeometryInput.POINTS;
            case LINES -> VertexGeometryInput.LINES;
            case LINES_ADJACENCY -> VertexGeometryInput.LINES_ADJACENCY;
            case TRIANGLES -> VertexGeometryInput.TRIANGLES;
            case TRIANGLES_ADJACENCY -> VertexGeometryInput.TRIANGLES_ADJACENCY;
        };
    }

    /** The current activation's declarations, or null under fixed function / no scope. */
    public static Declared current() {
        return current;
    }

    public static void clear() {
        current = null;
    }
}
