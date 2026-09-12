// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame;

import com.schmaloogium.mod.conformance.ControlledClock;

import net.minecraft.server.integrated.IntegratedServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * H-CLOCK-02 (Phase 2 §5.1.1 integrated-server lockstep; FEATURE class, fallback NONE): the
 * server thread's wall-clock loop keeps calling {@code tick()}; while the capture clock is
 * gated each call first waits for a permit the client grants per controlled step, and the
 * completed vanilla tick body is counted and signalled back. Ungated, both hooks are no-ops;
 * every gate opens on release/shutdown so the server can never be left blocked.
 */
@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer {

    @Inject(method = "tick()V", at = @At("HEAD"), expect = 1)
    private void schmaloogium$awaitPermit(CallbackInfo ci) {
        ControlledClock.awaitServerPermit();
    }

    @Inject(method = "tick()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tick()V", shift = At.Shift.AFTER),
            expect = 1)
    private void schmaloogium$tickDone(CallbackInfo ci) {
        ControlledClock.serverTickDone();
    }
}
