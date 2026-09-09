// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * The four reserved debug-flag NAMES (PHASE_1_DOC §4.9.3, [D-P1-21]): namespace
 * {@code -Dschmaloogium.debug.*}, all boolean, absent means off, never read before
 * {@code mod.core} has bootstrapped. Constants only — nothing here reads system
 * properties; each flag's behavior belongs to its owning phase.
 *
 * <p>Only {@link #RECORD_GL} and {@link #GL_LABELS} are the GL-facing flags: the facade's
 * per-call {@code glGetError} cadence keys on those two and nothing else ([D-P1-30]).
 */
public final class DebugFlags {

    /** Dump fully-processed shader sources to disk; the dump itself is Phase 3's. */
    public static final String SAVE_SOURCES = "schmaloogium.debug.saveSources";

    /** {@code CapabilityProbe} writes the live {@link GLCapabilityProfile} in the §4.7.2
     *  text form; the fixture-production loop. One-shot at init. */
    public static final String DUMP_CAPABILITIES = "schmaloogium.debug.dumpCapabilities";

    /** Wrap the live {@link GLDevice} in a recorder over a bounded {@code GLCallLog} ring
     *  (default 100 000 calls); opt-in, allocation-heavy, off by default. */
    public static final String RECORD_GL = "schmaloogium.debug.recordGL";

    /** Activate {@link DebugService} (KHR_debug labels/groups); implementation is v0.5 /
     *  Phase 14 (§4.7.8). Also puts the LWJGL backend on the per-call error cadence. */
    public static final String GL_LABELS = "schmaloogium.debug.glLabels";

    private DebugFlags() {
    }
}
