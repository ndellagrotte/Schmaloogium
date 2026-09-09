// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.ProgramKey;

import com.schmaloogium.engine.config.EngineOptionData;

import java.util.Map;

/** Immutable raw program-state map ordered by dimension then program name. */
public record ProgramStateModel(Map<ProgramKey, ProgramState> programs) {

    public ProgramStateModel {
        java.util.SortedMap<ProgramKey, ProgramState> ordered =
            new java.util.TreeMap<>(ProgramKeyOrder.INSTANCE);
        ordered.putAll(java.util.Objects.requireNonNull(programs, "programs"));
        programs = java.util.Collections.unmodifiableSortedMap(ordered);
    }

    /** Shared canonical program-key order: dimension then unsigned-UTF-8 program name. */
    public static final class ProgramKeyOrder implements java.io.Serializable,
            java.util.Comparator<ProgramKey> {
        /** Singleton. */
        public static final ProgramKeyOrder INSTANCE = new ProgramKeyOrder();
        private static final long serialVersionUID = 1L;

        private ProgramKeyOrder() {
        }

        @Override
        public int compare(ProgramKey a, ProgramKey b) {
            int d = a.dimension().compareTo(b.dimension());
            if (d != 0) {
                return d;
            }
            return EngineOptionData.compareUnsignedUtf8(a.programName(), b.programName());
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }
}
