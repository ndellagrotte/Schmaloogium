// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.frame;

import com.schmaloogium.engine.frame.lifecycle.FrameDriver;
import com.schmaloogium.engine.frame.lifecycle.FrameComposition;
import com.schmaloogium.engine.frame.lifecycle.FrameCompositionSource;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

/**
 * The mod-side frame runtime holder: owns the single {@link FrameDriver}, its composition
 * source and the render-thread predicate. The mixin catalog calls {@link FrameHooks}, the
 * future pipeline coordinator (or the mod's internal-pack bring-up) calls
 * {@link #installComposition(Optional)}; nothing else in the mod touches the driver.
 *
 * <p>Render-thread detection is a settable predicate so headless tests drive the pure
 * engine through this same seam; the live mod installs an OpenGL-thread check at
 * {@link BootstrapHooks#onGlReady()} time.
 */
public final class FrameRuntime {

    private static final AtomicReference<FrameDriver> DRIVER = new AtomicReference<>();
    private static final FrameCompositionSource COMPOSITIONS = new FrameCompositionSource();
    private static volatile BooleanSupplier renderThread = () -> true;

    private FrameRuntime() {
    }

    /** Installs the render-thread predicate; called once from the GL-ready bootstrap. */
    public static void installRenderThreadPredicate(BooleanSupplier predicate) {
        renderThread = predicate == null ? () -> true : predicate;
    }

    /** The driver, created lazily on first access (safe on any thread; contents confined). */
    public static FrameDriver driver() {
        FrameDriver driver = DRIVER.get();
        if (driver == null) {
            driver = new FrameDriver(renderThread, COMPOSITIONS);
            DRIVER.compareAndSet(null, driver);
        }
        return driver;
    }

    public static FrameCompositionSource compositions() {
        return COMPOSITIONS;
    }

    /**
     * Composition-root entry: atomically installs or clears the active publication. A
     * present publication re-opens admission (the driver's shaders-off latch belongs to the
     * publication that failed, never to its accepted replacement).
     */
    public static void installComposition(Optional<FrameComposition> composition) {
        COMPOSITIONS.install(composition);
        if (composition != null && composition.isPresent()) {
            driver().resetShadersOffLatch();
        }
    }
}
