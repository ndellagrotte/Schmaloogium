// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.smooth;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pure math verification for the tick-domain EMA smoother used by wetness, eye
 * brightness and center depth: first valid sample initializes without fade, zero
 * elapsed ticks retain, closed-form per-tick advance matches the halflife algebra,
 * non-finite targets are rejected without corrupting the accumulator, regressing time
 * retains and flags once, and invalidation restarts the initialization path.
 */
class TickEmaTest {

    @Test
    void firstValidSampleInitializesWithoutFade() {
        TickEma ema = new TickEma();
        assertFalse(ema.isValid());
        assertEquals(TickEma.Kind.INITIALIZED,
                ema.advance(5.0d, 3L, 10.0d).kind());
        assertEquals(Optional.of(5.0d), ema.value());
    }

    @Test
    void zeroElapsedTicksRetain() {
        TickEma ema = new TickEma();
        ema.advance(5.0d, 1L, 10.0d);
        var outcome = ema.advance(9.0d, 0L, 10.0d);
        assertEquals(TickEma.Kind.RETAINED_ZERO_DT, outcome.kind());
        assertEquals(5.0d, outcome.value());
        assertEquals(Optional.of(5.0d), ema.value());
    }

    @Test
    void closedFormHalflifeAdvance() {
        TickEma ema = new TickEma();
        ema.advance(5.0d, 1L, 10.0d);
        var outcome = ema.advance(7.0d, 10L, 10.0d);
        assertEquals(TickEma.Kind.ADVANCED, outcome.kind());
        // d = 1 - 2^(-dt/h) = 0.5 for dt == h; 7 + (5-7)*0.5 = 6.
        assertEquals(6.0d, outcome.value(), 1e-12d);
    }

    @Test
    void zeroHalflifeSnapsToTarget() {
        TickEma ema = new TickEma();
        ema.advance(1.0d, 1L, 10.0d);
        var outcome = ema.advance(4.0d, 5L, 0.0d);
        assertEquals(TickEma.Kind.ADVANCED, outcome.kind());
        assertEquals(4.0d, outcome.value(), 0.0d);
    }

    @Test
    void nonFiniteTargetRejectedWithoutCorruption() {
        TickEma ema = new TickEma();
        ema.advance(5.0d, 1L, 10.0d);
        assertEquals(TickEma.Kind.INVALID_TARGET,
                ema.advance(Double.NaN, 3L, 10.0d).kind());
        assertEquals(TickEma.Kind.INVALID_TARGET,
                ema.advance(Double.POSITIVE_INFINITY, 3L, 10.0d).kind());
        assertEquals(Optional.of(5.0d), ema.value(),
                "invalid target leaves the accumulator untouched");
    }

    @Test
    void regressingTimeRetainsAndFlagsOnce() {
        TickEma ema = new TickEma();
        ema.advance(5.0d, 1L, 10.0d);
        var outcome = ema.advance(6.0d, -1L, 10.0d);
        assertEquals(TickEma.Kind.REJECTED_REGRESSING_TIME, outcome.kind());
        assertEquals(5.0d, outcome.value());
        assertTrue(ema.pollRegressReported(), "the first regress is reported");
        assertFalse(ema.pollRegressReported(), "reporting is consumed by one poll");
        // A later valid advance still works from the retained accumulator.
        assertEquals(TickEma.Kind.ADVANCED, ema.advance(6.0d, 10L, 10.0d).kind());
    }

    @Test
    void invalidationRestartsInitialization() {
        TickEma ema = new TickEma();
        ema.advance(5.0d, 1L, 10.0d);
        ema.invalidate();
        assertFalse(ema.isValid());
        var outcome = ema.advance(9.0d, 4L, 10.0d);
        assertEquals(TickEma.Kind.INITIALIZED, outcome.kind(),
                "after invalidation the next valid sample initializes without fade");
        assertEquals(9.0d, outcome.value());
    }

    @Test
    void packFacingIntTruncatesTowardZero() {
        TickEma ema = new TickEma();
        ema.advance(239.75d, 1L, 10.0d);
        assertEquals(239, ema.intValue());
        ema.invalidate();
        ema.advance(-239.75d, 1L, 10.0d);
        assertEquals(-239, ema.intValue(), "truncation toward zero, not floor");
    }
}
