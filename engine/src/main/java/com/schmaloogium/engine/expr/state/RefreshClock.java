// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.state;

/** Finite binary64 controller clock (§4.7): initially zero; the first refresh of an
 * activation establishes the frame counter without adding time; each later different
 * counter adds its finite non-negative frameTime exactly once; same-counter switches
 * leave the clock unchanged; inequality handles counter wrap. Advance commits before
 * context sampling and is never undone by later failures. */
public final class RefreshClock {

    private double seconds;
    private long lastCounter;
    private boolean established;

    public double controllerSeconds() {
        return seconds;
    }

    public ClockAdvance advance(long frameCounter, float frameTimeSeconds) {
        if (!Float.isFinite(frameTimeSeconds) || frameTimeSeconds < 0.0f) {
            return new ClockAdvance.Rejected("frameTime must be finite and non-negative");
        }
        if (!established) {
            established = true;
            lastCounter = frameCounter;
            return new ClockAdvance.Advanced(seconds);
        }
        if (frameCounter == lastCounter) {
            return new ClockAdvance.Advanced(seconds);
        }
        double updated = seconds + (double) frameTimeSeconds;
        if (!Double.isFinite(updated) || updated < 0.0d) {
            return new ClockAdvance.Rejected("controller clock overflow");
        }
        seconds = updated;
        lastCounter = frameCounter;
        return new ClockAdvance.Advanced(seconds);
    }

    /** Lifecycle reset: the next activation starts its clock at zero. */
    public void reset() {
        seconds = 0.0d;
        lastCounter = 0L;
        established = false;
    }
}
