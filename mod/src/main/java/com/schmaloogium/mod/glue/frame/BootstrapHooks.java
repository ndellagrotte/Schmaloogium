// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.frame;

import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.mod.glue.CapabilityProbe;

/**
 * The bootstrap bridge (PHASE_7_DOC §4.10.2, H-BOOT-01/02/03): loader events land here,
 * and the H-BOOT-02 {@code OpenGlHelper.func_77474_a} RETURN hook captures the GL
 * capability profile exactly once — no GL work is attempted before it.
 */
public final class BootstrapHooks {

    private static volatile boolean glReady;
    private static volatile boolean clientLoadingComplete;
    private static volatile boolean earlyConfigurationLoaded;

    private BootstrapHooks() {
    }

    /** H-BOOT-01a: configuration + both prerequisites available (Forge preInit tail). */
    public static void onEarlyConfigurationLoaded() {
        earlyConfigurationLoaded = true;
    }

    /**
     * H-BOOT-02: RETURN of {@code OpenGlHelper.initializeTextures()} — the first point at
     * which a current GL context is guaranteed on the render thread. Runs the stage-2
     * capability probe once and publishes the profile to the compat context.
     */
    public static void onGlReady() {
        if (glReady) {
            return;
        }
        glReady = true;
        // Stage-2 probe placement (P1 §4.12): the first GL-context-guaranteed point.
        GLCapabilityProfile profile = CapabilityProbe.capture();
        CapabilityHolder.publish(profile);
        FrameRuntime.installRenderThreadPredicate(
                net.minecraft.client.Minecraft.getMinecraft()::isCallingFromMinecraftThread);
    }

    /** The captured profile, or empty before H-BOOT-02 has run. */
    public static java.util.Optional<GLCapabilityProfile> capturedProfile() {
        return CapabilityHolder.peek();
    }

    /** H-BOOT-03: first {@code GuiMainMenu.initGui()} RETURN; startup may stay deferred. */
    public static void onClientLoadingComplete() {
        clientLoadingComplete = true;
    }

    public static boolean isGlReady() {
        return glReady;
    }

    public static boolean isClientLoadingComplete() {
        return clientLoadingComplete;
    }

    public static boolean isEarlyConfigurationLoaded() {
        return earlyConfigurationLoaded;
    }
}
