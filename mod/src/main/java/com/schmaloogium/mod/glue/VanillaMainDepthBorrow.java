// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.BorrowedDepthAttachmentHandle;
import com.schmaloogium.mod.glue.frame.DepthTex0Bridge;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Mints the device-authentic borrowed handle for the vanilla main depth texture the
 * {@code Framebuffer} mixin observed (PHASE_5_DOC §4.8 piece 2): a foreign texture that
 * resolves to the bridge's current identity, lent through the device's own framebuffer
 * service so the estate can attach it as {@code depthtex0}.
 */
public final class VanillaMainDepthBorrow {

    public static final String KEY = "minecraft:framebuffer/main-depth";

    private VanillaMainDepthBorrow() {
    }

    public static Supplier<Optional<BorrowedDepthAttachmentHandle>> source(Lwjgl3GLDevice device) {
        Lwjgl3ForeignTexture platform = new Lwjgl3ForeignTexture(device, KEY,
                () -> (int) DepthTex0Bridge.observedIdentity());
        return () -> {
            try {
                if (DepthTex0Bridge.observedIdentity() <= 0L) {
                    return Optional.empty();
                }
                return Optional.of(device.framebuffers().borrowDepthAttachment(platform));
            } catch (RuntimeException e) {
                return Optional.empty();
            }
        };
    }
}
