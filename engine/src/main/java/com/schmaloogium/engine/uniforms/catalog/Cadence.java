// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.catalog;

/**
 * Acquisition cadence buckets (PHASE_6_DOC §4.3, D-P6-2): when a value is REPLACED.
 * Activation is a separate, always-on policy — every successful shader activation visits
 * every declared, enabled built-in regardless of this bucket; "refresh" never resamples a
 * provider or advances an EMA.
 */
public enum Cadence {
    /** Configuration/world-provider install: constants and inert legacy metrics. */
    ONCE,
    /** The logical tick differs from the last sampled tick. */
    PER_TICK,
    /** An accepted new frame ID. */
    PER_FRAME,
    /** A typed hook/event method on {@code UniformEventSink}. */
    SIGNAL
}
