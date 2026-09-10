// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The equality-only pipeline invalidation signal (PHASE_7_DOC §5.1). The value increments
 * exactly once after each fully accepted ready publication, accepted off publication, or
 * forced recovered-off outcome. Consumers compare for equality only — never a clock;
 * ordering and subtraction are forbidden, and no greater-than comparison survives wrap.
 * Render-thread-confined like every publication step.
 */
public record PipelineVersion(long value) {

    public PipelineVersion {
        if (value < 0) {
            throw new IllegalArgumentException("pipeline version must be non-negative: " + value);
        }
    }

    /** The initial version handed to the first publication attempt. */
    public static final PipelineVersion INITIAL = new PipelineVersion(0);

    /**
     * The monotonically increasing issuance counter behind {@link #next()}; coordinator
     * infrastructure, not a consumer-facing clock.
     */
    public static final class Counter {

        private final AtomicLong current = new AtomicLong();

        /** The current version without advancing it. */
        public PipelineVersion current() {
            return new PipelineVersion(current.get());
        }

        /** Advances exactly once and returns the new version. */
        public PipelineVersion next() {
            return new PipelineVersion(current.incrementAndGet());
        }
    }

    /** The successor version; coordinator-side issuance only. */
    public PipelineVersion next() {
        return new PipelineVersion(value + 1);
    }
}
