// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.smooth;

import java.util.Optional;

/**
 * The tick-domain half-life EMA (PHASE_6_DOC §4.5, D-P6-3/D-P6-4). For a target x, prior
 * accumulator s, elapsed Δt ticks and half-life h:
 * {@code d = (h <= 0) ? 0 : 2^(-Δt/h)}, {@code s' = x + (s-x)d}. Evaluated in
 * {@code double}; the caller casts the final float. Edge rules: the first valid sample
 * initializes s=x with no fade; Δt=0 retains s; negative/regressing time is an invariant
 * failure that retains the previous value and reports once; h=0 snaps to the target;
 * non-finite inputs never enter the accumulator; a world-epoch change reinitializes on
 * the new world's first sample (the runtime resets this smoother).
 *
 * <p>Not thread-safe: confined to the render thread's accepted-frame advance.
 */
public final class TickEma {

    /** Closed advance outcome. */
    public enum Kind { INITIALIZED, ADVANCED, RETAINED_ZERO_DT, REJECTED_REGRESSING_TIME, INVALID_TARGET }

    /** One advance result: the outcome and the pack-facing value after the advance. */
    public record Advance(Kind kind, double value) {

        public boolean changedValue() {
            return kind == Kind.INITIALIZED || kind == Kind.ADVANCED;
        }
    }

    private double accumulator;
    private boolean valid;
    private boolean regressReported;

    /** Whether this smoother holds a value (false before the first valid sample or after
     *  a world-epoch reset). */
    public boolean isValid() {
        return valid;
    }

    /** The current accumulator, or empty when invalid. */
    public Optional<Double> value() {
        return valid ? Optional.of(accumulator) : Optional.empty();
    }

    /** The pack-facing float; callers must check {@link #isValid()} first. */
    public float floatValue() {
        return (float) accumulator;
    }

    /** The pack-facing int: truncation toward zero (D-P6-4). */
    public int intValue() {
        return (int) accumulator;
    }

    /** Clears the accumulator; the next valid sample initializes without fade. */
    public void invalidate() {
        valid = false;
        accumulator = 0d;
    }

    /**
     * Advances once toward {@code target} over {@code elapsedTicks} with half-life
     * {@code halflifeTicks}. Never advances twice for the same coordinate — the runtime
     * supplies non-negative elapsed ticks only.
     */
    public Advance advance(double target, long elapsedTicks, double halflifeTicks) {
        if (!Double.isFinite(target)) {
            return new Advance(Kind.INVALID_TARGET, accumulator);
        }
        if (elapsedTicks < 0) {
            // Invariant failure: retain the previous value, report once.
            regressReported = true;
            return new Advance(Kind.REJECTED_REGRESSING_TIME, accumulator);
        }
        if (!valid) {
            accumulator = target;
            valid = true;
            return new Advance(Kind.INITIALIZED, accumulator);
        }
        if (elapsedTicks == 0) {
            return new Advance(Kind.RETAINED_ZERO_DT, accumulator);
        }
        double d = halflifeTicks <= 0d
                ? 0d
                : Math.exp(-Math.log(2d) * elapsedTicks / halflifeTicks);
        accumulator = target + (accumulator - target) * d;
        return new Advance(Kind.ADVANCED, accumulator);
    }

    /** True when a regressing-time invariant failure has occurred and not yet been
     *  reported by the runtime's once-latch; reporting consumes the flag. */
    public boolean pollRegressReported() {
        boolean was = regressReported;
        regressReported = false;
        return was;
    }
}
