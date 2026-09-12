// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame;

import com.schmaloogium.mod.conformance.ControlledClock;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * H-CLOCK-03 (Phase 2 §4.4 determinism ledger; FEATURE class, fallback NONE): the integrated
 * server's periodic time-update packet rewrites the client's total world time, and under the
 * lockstep clock its arrival races the client's scheduled-task drain by one frame. While the
 * clock is gated the client's total world time advances only by its own controlled ticks;
 * the packet's day time (fixed by the scene) still applies. Vanilla behaviour otherwise.
 */
@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient {

    @Redirect(method = "handleTimeUpdate(Lnet/minecraft/network/play/server/SPacketTimeUpdate;)V",
            at = @At(value = "INVOKE",
                target = "Lnet/minecraft/client/multiplayer/WorldClient;setTotalWorldTime(J)V"),
            expect = 1)
    private void schmaloogium$controlledTotalTime(WorldClient world, long totalWorldTime) {
        if (!ControlledClock.isGated()) {
            world.setTotalWorldTime(totalWorldTime);
        }
    }
}
