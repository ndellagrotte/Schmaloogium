// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors
package com.schmaloogium.engine.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.schmaloogium.engine.pack.DimensionKey;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

/** ProgramStateEvaluator's sorted collections require ProgramKey natural order. */
class ProgramKeyOrderTest {

    private static ProgramKey key(Integer legacyId, String program) {
        return new ProgramKey(legacyId == null ? DimensionKey.BASE : DimensionKey.world(legacyId),
            program);
    }

    @Test
    void programKeysSortInSortedCollectionsWithoutClassCastException() {
        Set<ProgramKey> executable = new TreeSet<>();
        executable.add(key(null, "shadow"));
        executable.add(key(1, "composite"));
        executable.add(key(null, "composite"));
        executable.add(key(0, "begin"));
        // dimension-major order (BASE before world dims, ascending legacy id),
        // then unsigned-UTF-8 program name order inside each dimension
        assertEquals(List.of("composite", "shadow", "begin", "composite"),
            executable.stream().map(ProgramKey::programName).toList());

        Map<ProgramKey, String> states = new TreeMap<>();
        for (ProgramKey key : executable) {
            states.put(key, key.programName());
        }
        assertEquals(4, states.size());
        assertEquals("composite", states.get(key(null, "composite")));
    }
}
