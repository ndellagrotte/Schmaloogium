// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.frame;

import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.log.Logs;
import com.schmaloogium.mod.glue.Lwjgl3GLDevice;

import java.util.Optional;

/**
 * The one real {@link Lwjgl3GLDevice} of the session, built on the render thread from the
 * H-BOOT-02 capability profile the first time the composition root asks for it. The device
 * constructor performs the stage-3 side effects (foreign textures, alpha/blend hooks) that
 * must happen exactly once, which is why this holder is the sole constructor call site.
 */
public final class DeviceHolder {

    private static volatile Lwjgl3GLDevice device;

    private DeviceHolder() {
    }

    /** The device, or empty before the GL profile exists. Render thread. */
    public static Optional<Lwjgl3GLDevice> acquire() {
        Lwjgl3GLDevice current = device;
        if (current != null) {
            return Optional.of(current);
        }
        Optional<GLCapabilityProfile> profile = BootstrapHooks.capturedProfile();
        if (profile.isEmpty()) {
            return Optional.empty();
        }
        synchronized (DeviceHolder.class) {
            if (device == null) {
                device = new Lwjgl3GLDevice(profile.get());
                Logs.channel(LogChannels.GL).info(
                        "Lwjgl3GLDevice constructed for the composition root (GL {}.{})",
                        profile.get().glVersionMajor(), profile.get().glVersionMinor());
            }
            return Optional.of(device);
        }
    }

    public static Optional<Lwjgl3GLDevice> current() {
        return Optional.ofNullable(device);
    }
}
