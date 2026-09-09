// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.config.ColorAttachmentKey;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.config.EvaluatedProgramStates;
import com.schmaloogium.engine.config.FlipBufferKey;
import com.schmaloogium.engine.config.FlipOverride;
import com.schmaloogium.engine.config.ProgramKey;
import com.schmaloogium.engine.registry.BufferRef;
import com.schmaloogium.engine.registry.DrawRouting;
import com.schmaloogium.engine.registry.ProgramStateBundle;
import java.util.List;
import java.util.OptionalInt;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;

/**
 * The default program state path (PHASE_4_DOC §4.7): most programs declare no flip
 * blocks, so absence from the evaluated explicit-flip map is the common case and must
 * adapt to the default bundle without throwing.
 */
class PlannerAdaptStateTest {

    private static final ProgramKey KEY =
        new ProgramKey(new DimensionKey(OptionalInt.empty()), "gbuffers_textured");

    @Test
    void programWithoutExplicitFlipsAdaptsToDefaultBundle() {
        EvaluatedProgramStates states =
            new EvaluatedProgramStates(List.of(), new TreeMap<>());
        ProgramStateBundle bundle = Planner.adaptState(KEY, states, Optional.empty());
        assertInstanceOf(DrawRouting.AllUsedBuffers.class, bundle.drawRouting());
        assertTrue(bundle.explicitFlips().isEmpty());
        assertTrue(bundle.compositeMipmaps().isEmpty());
        assertEquals(1, bundle.instanceCount());
        assertTrue(bundle.legacyGeometry().isEmpty());
    }

    @Test
    void explicitFlipsProjectToColortexRefsWithTrueFalse() {
        TreeMap<ProgramKey, Map<FlipBufferKey, FlipOverride>> flips = new TreeMap<>();
        flips.put(KEY, Map.of(
            new FlipBufferKey(new ColorAttachmentKey(3)), FlipOverride.TRUE,
            new FlipBufferKey(new ColorAttachmentKey(7)), FlipOverride.FALSE));
        EvaluatedProgramStates states = new EvaluatedProgramStates(List.of(), flips);
        ProgramStateBundle bundle = Planner.adaptState(KEY, states, Optional.empty());
        assertEquals(2, bundle.explicitFlips().size());
        assertEquals(Boolean.TRUE,
            bundle.explicitFlips().get(new BufferRef(BufferDomain.COLORTEX, 3)));
        assertEquals(Boolean.FALSE,
            bundle.explicitFlips().get(new BufferRef(BufferDomain.COLORTEX, 7)));
        assertFalse(bundle.explicitFlips()
            .containsKey(new BufferRef(BufferDomain.COLORTEX, 0)));
    }

    @Test
    void flipsOfOtherProgramsDoNotLeakIntoTheBundle() {
        ProgramKey other =
            new ProgramKey(new DimensionKey(OptionalInt.empty()), "composite0");
        TreeMap<ProgramKey, Map<FlipBufferKey, FlipOverride>> flips = new TreeMap<>();
        flips.put(other, Map.of(
            new FlipBufferKey(new ColorAttachmentKey(1)), FlipOverride.TRUE));
        EvaluatedProgramStates states = new EvaluatedProgramStates(List.of(), flips);
        ProgramStateBundle bundle = Planner.adaptState(KEY, states, Optional.empty());
        assertTrue(bundle.explicitFlips().isEmpty());
    }
}
