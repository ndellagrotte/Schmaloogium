// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Per-row user policy over the five modernization rows (R-P14→P1-2, PHASE_1_DOC §4.7.8;
 * PHASE_14_DOC §2.1): sampler, DSA, debug, compile, readback. Sourced from the mod
 * config owned by {@code mod.core}; every row defaults to {@link Row#AUTO}, and AUTO
 * resolves to the reference-faithful path until that row's ledger entry closes
 * (D-P14-17). Policy may not overrule capability — a FORCE_ON on an incapable profile
 * still yields the fallback tier.
 */
public record GlModernizationPolicy(Row sampler, Row dsa, Row debug, Row compile, Row readback) {

    public GlModernizationPolicy {
        sampler = orAuto(sampler);
        dsa = orAuto(dsa);
        debug = orAuto(debug);
        compile = orAuto(compile);
        readback = orAuto(readback);
    }

    /** The all-AUTO policy — the shipped default. */
    public static GlModernizationPolicy allAuto() {
        return new GlModernizationPolicy(null, null, null, null, null);
    }

    private static Row orAuto(Row row) {
        return (row == null) ? Row.AUTO : row;
    }

    /** Per-row policy value: AUTO | FORCE_ON | FORCE_OFF. */
    public enum Row {
        AUTO,
        FORCE_ON,
        FORCE_OFF
    }
}
