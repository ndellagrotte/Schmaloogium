// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.frame;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The resource-reload boundary bridge (H-RESOURCE-01, §4.8.1): marks the pre-destructive
 * gate and post-listener completion. The epoch counter is the only carried state — the
 * quiescence/teardown transaction itself lives behind the composition root, and the
 * driver consumes epochs at frame begin (resource NONE quiescence at v0.1 keeps
 * configuration and refresh semantics; the full §5.3 transaction is the coordinator's).
 */
public final class ResourceReloadBoundary {

    private static final AtomicLong EPOCH = new AtomicLong();

    private ResourceReloadBoundary() {
    }

    /** HEAD of {@code func_110541_a}: before any destructive listener runs. */
    public static void begin() {
        EPOCH.incrementAndGet();
    }

    /** RETURN of {@code func_110541_a}: listeners completed. */
    public static void end() {
        // The completion observation shares the epoch: one monotonic boundary per reload.
    }

    /** The current reload epoch; changes across every reload boundary crossing. */
    public static long epoch() {
        return EPOCH.get();
    }
}
