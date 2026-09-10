// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.state;

import java.util.Arrays;

/** Plan-wide smooth cells (§4.7): committed value/timestamp pairs plus a per-refresh
 * transactional overlay. Each reached cell computes its own elapsed time from its own
 * last committed evaluation; a staged cell commits only with its owning definition.
 * State survives every program switch and resets only on lifecycle reset. */
public final class SmoothState {

    private final int cellCount;
    private final boolean[] initialized;
    private final float[] value;
    private final double[] lastSeconds;

    private final float[] overlayValue;
    private final double[] overlaySeconds;
    private final boolean[] overlayPresent;

    public SmoothState(int cellCount) {
        if (cellCount < 0) {
            throw new IllegalArgumentException("cellCount must be non-negative");
        }
        this.cellCount = cellCount;
        this.initialized = new boolean[cellCount];
        this.value = new float[cellCount];
        this.lastSeconds = new double[cellCount];
        this.overlayValue = new float[cellCount];
        this.overlaySeconds = new double[cellCount];
        this.overlayPresent = new boolean[cellCount];
    }

    public int cellCount() {
        return cellCount;
    }

    /** Opens one refresh overlay; earlier staged-but-uncommitted cells are discarded. */
    public void beginRefresh() {
        Arrays.fill(overlayPresent, false);
    }

    /** Commits every staged cell of the owning definition (transactional success). */
    public void commitCells(int[] cellIndices) {
        for (int cell : cellIndices) {
            if (overlayPresent[cell]) {
                initialized[cell] = true;
                value[cell] = overlayValue[cell];
                lastSeconds[cell] = overlaySeconds[cell];
                overlayPresent[cell] = false;
            }
        }
    }

    /** Discards every staged cell of the owning definition (transactional failure). */
    public void discardCells(int[] cellIndices) {
        for (int cell : cellIndices) {
            overlayPresent[cell] = false;
        }
    }

    /** Returns a committed cell snapshot for tests and diagnostics. */
    public CommittedCell cell(int index) {
        return new CommittedCell(initialized[index], value[index], lastSeconds[index]);
    }

    public record CommittedCell(boolean initialized, float value, double lastCommittedEvaluationSeconds) {
    }

    /** The exact §4.7 transition for target {@code t}, both fades, and controller time. */
    public SmoothAdvance advance(int cell, float target, float fadeIn, float fadeOut, double nowSeconds) {
        if (!Float.isFinite(target) || !Float.isFinite(fadeIn) || !Float.isFinite(fadeOut)) {
            return SmoothAdvance.rejected("DOMAIN", "smooth target and fades must be finite");
        }
        if (fadeIn < 0.0f || fadeOut < 0.0f) {
            return SmoothAdvance.rejected("DOMAIN", "smooth fade times must be non-negative");
        }
        float result;
        if (!initialized[cell]) {
            result = target;
        } else {
            float current = value[cell];
            double dt = nowSeconds - lastSeconds[cell];
            float fade = target > current ? fadeIn : fadeOut;
            if (dt <= 0.0d) {
                result = current;
            } else if (fade == 0.0f || dt >= (double) fade) {
                result = target;
            } else {
                float dtf = (float) dt;
                if (dtf <= 0.0f) {
                    result = current;
                } else {
                    float updates = fade / dtf;
                    float correction = 4.61f - 1.0f / (0.13f + updates / 10.0f);
                    float k = dtf / fade * correction;
                    k = Math.max(0.0f, Math.min(1.0f, k));
                    result = current + (target - current) * k;
                }
            }
        }
        if (!Float.isFinite(result)) {
            return SmoothAdvance.rejected("NON_FINITE", "smooth transition produced a non-finite value");
        }
        overlayValue[cell] = result;
        overlaySeconds[cell] = nowSeconds;
        overlayPresent[cell] = true;
        return SmoothAdvance.of(result);
    }

    /** Lifecycle reset clears every cell and timestamp. */
    public void reset() {
        Arrays.fill(initialized, false);
        Arrays.fill(value, 0.0f);
        Arrays.fill(lastSeconds, 0.0d);
        Arrays.fill(overlayPresent, false);
    }
}
