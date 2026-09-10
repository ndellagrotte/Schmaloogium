// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.lifecycle;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The atomically replaced current-publication view (PHASE_7_DOC §4.1 step 9). The
 * composition root installs a complete tuple in one step; the driver reads it once per
 * frame and authenticates its tokens against the exact instance it opened under, so a
 * replacement publication makes outstanding tokens stale.
 */
public final class FrameCompositionSource {

    private final AtomicReference<Optional<FrameComposition>> current =
            new AtomicReference<>(Optional.empty());

    /** Installs the given composition atomically; empty installs shaders-off. */
    public void install(Optional<FrameComposition> composition) {
        current.set(composition == null ? Optional.empty() : composition);
    }

    /** The current composition, empty when shaders are off or nothing was published. */
    public Optional<FrameComposition> current() {
        return current.get();
    }
}
