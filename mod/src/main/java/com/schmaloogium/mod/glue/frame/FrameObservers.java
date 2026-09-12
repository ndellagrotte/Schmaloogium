// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.frame;

import com.schmaloogium.engine.frame.spi.FrameCompletionObserver;

import java.util.Optional;

/**
 * The installed H-CAPTURE-01 observer slot the composition root reads when it builds a
 * {@code FrameCompositionRecord}: empty for normal play (the driver treats absence as a
 * no-op), set only by the capture agent while a plan is armed. Read on the render thread at
 * install time; the composition carries the value it saw.
 */
public final class FrameObservers {

    private static volatile FrameCompletionObserver observer;

    private FrameObservers() {
    }

    public static void install(FrameCompletionObserver o) {
        observer = o;
    }

    public static void clear() {
        observer = null;
    }

    public static Optional<FrameCompletionObserver> current() {
        return Optional.ofNullable(observer);
    }
}
