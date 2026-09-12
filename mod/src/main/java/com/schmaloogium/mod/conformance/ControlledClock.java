// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.conformance;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The controlled capture clock (PHASE_2_DOC §5.1.1 "Controlled lifecycle"): once armed, every
 * rendered frame advances exactly {@code ticksPerFrame} client ticks with a fixed partial tick
 * and a fixed elapsed input, and — once gated — exactly {@code ticksPerFrame} integrated-server
 * ticks run before them, in lockstep. The server thread blocks at the head of its
 * {@code tick()} on a permit the client grants; the client blocks until those ticks complete.
 *
 * <p>Pure Java state (no Minecraft types) so it is unit-testable; the two mixins are thin
 * callers. {@link #release()} opens every gate unconditionally, so shutdown can never deadlock.
 */
public final class ControlledClock {

    private static volatile boolean armed;
    private static volatile boolean gated;
    private static volatile int ticksPerFrame = 1;
    private static volatile float partialTicks;
    private static volatile float frameTimeSeconds = 0.05f;
    private static volatile long hangCeilingMillis = 20_000L;
    private static final Semaphore SERVER_PERMITS = new Semaphore(0);
    private static final Semaphore SERVER_DONE = new Semaphore(0);
    private static final AtomicLong CLIENT_TICKS = new AtomicLong();
    private static final AtomicLong SERVER_TICKS = new AtomicLong();
    private static final AtomicLong RENDERED_FRAMES = new AtomicLong();
    private static volatile String failure;
    private static volatile boolean animationHeld;

    private ControlledClock() {
    }

    /** Holds the atlas animation tick (H-CLOCK-04) until the clock arms; cleared by release(). */
    public static void holdAnimation(boolean hold) {
        animationHeld = hold;
    }

    public static boolean isAnimationHeld() {
        return animationHeld;
    }

    /** Fixes the client tick/partial/elapsed inputs; server lockstep starts with {@link #gate()}. */
    public static synchronized void arm(int ticks, float partial, float seconds, long ceilingMillis) {
        if (ticks <= 0 || !(partial >= 0f && partial < 1f) || !(seconds > 0f)) {
            throw new IllegalArgumentException("invalid clock parameters");
        }
        ticksPerFrame = ticks;
        partialTicks = partial;
        frameTimeSeconds = seconds;
        hangCeilingMillis = ceilingMillis;
        CLIENT_TICKS.set(0);
        SERVER_TICKS.set(0);
        RENDERED_FRAMES.set(0);
        failure = null;
        SERVER_PERMITS.drainPermits();
        SERVER_DONE.drainPermits();
        animationHeld = false; // animation advances with the controlled ticks from here on
        armed = true;
    }

    /** Starts integrated-server lockstep: from now on the server ticks only on permits. */
    public static void gate() {
        gated = true;
    }

    /** Opens every gate and disarms; safe to call from any thread, any number of times. */
    public static void release() {
        gated = false;
        armed = false;
        animationHeld = false;
        SERVER_PERMITS.release(1 << 20);
    }

    public static boolean isArmed() {
        return armed;
    }

    public static boolean isGated() {
        return gated;
    }

    public static int ticksPerFrame() {
        return ticksPerFrame;
    }

    public static float partialTicks() {
        return partialTicks;
    }

    public static float frameTimeSeconds() {
        return frameTimeSeconds;
    }

    public static long clientTicks() {
        return CLIENT_TICKS.get();
    }

    public static long serverTicks() {
        return SERVER_TICKS.get();
    }

    public static long renderedFrames() {
        return RENDERED_FRAMES.get();
    }

    public static String failure() {
        return failure;
    }

    /**
     * Client thread, once per frame from the {@code Timer} mixin: grants the server its ticks,
     * waits for them (hang ceiling), counts the client ticks the frame will run. Returns false
     * when not armed (vanilla timing stays untouched).
     */
    public static boolean stepFrame() {
        if (!armed) {
            return false;
        }
        if (gated) {
            SERVER_DONE.drainPermits();
            SERVER_PERMITS.release(ticksPerFrame);
            try {
                if (!SERVER_DONE.tryAcquire(ticksPerFrame, hangCeilingMillis, TimeUnit.MILLISECONDS)) {
                    failure = "integrated server did not complete " + ticksPerFrame
                            + " lockstep tick(s) within " + hangCeilingMillis + " ms";
                    release();
                    return false;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                failure = "interrupted while waiting for the integrated server";
                release();
                return false;
            }
        }
        CLIENT_TICKS.addAndGet(ticksPerFrame);
        RENDERED_FRAMES.incrementAndGet();
        return true;
    }

    /** Server thread, at the head of {@code IntegratedServer.tick()}: blocks until permitted. */
    public static void awaitServerPermit() {
        if (!gated) {
            return;
        }
        try {
            SERVER_PERMITS.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Server thread, after the vanilla tick body ran: counts and signals completion. */
    public static void serverTickDone() {
        if (!gated) {
            return;
        }
        SERVER_TICKS.incrementAndGet();
        SERVER_DONE.release();
    }
}
