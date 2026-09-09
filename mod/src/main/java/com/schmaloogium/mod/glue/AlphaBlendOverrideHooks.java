// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

/**
 * The concrete mod-only bridge the Phase 7 mutation hooks consult ([D-P1-57]): the
 * hooks in {@code mod.mixin.frame} are registered by P7 and call these static methods.
 * Everything answers {@code false} before a device is installed, while no lease is
 * held, and during the lease's own restoration - exactly the [D-P1-57] semantics, with
 * the device-identity lookup the hooks cannot do themselves.
 */
public final class AlphaBlendOverrideHooks {

    private static volatile Lwjgl3GLDevice device;

    private AlphaBlendOverrideHooks() {
    }

    /** Called by the {@link Lwjgl3GLDevice} constructor (mod.core stage 3). */
    public static void install(Lwjgl3GLDevice installed) {
        device = installed;
    }

    public static boolean suppressAlphaMutation() {
        Lwjgl3GLDevice d = device;
        return d != null && d.suppressesAlphaMutation();
    }

    public static boolean suppressBlendMutation() {
        Lwjgl3GLDevice d = device;
        return d != null && d.suppressesBlendMutation();
    }

    /**
     * P7's per-lock effective-blend event path does not exist at v0.1; the hook is a
     * no-op here and P7 publishes through its own channel.
     */
    public static void publishEffectiveBlend() {
        // v0.1: no event path (PHASE_1_DOC §4.7.4 [D-P1-57] note).
    }
}
