// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame;

import com.schmaloogium.mod.conformance.ControlledClock;

import net.minecraft.client.renderer.texture.TextureManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * H-CLOCK-04 (Phase 2 §4.4 "animated textures" row; FEATURE class, fallback NONE): the atlas
 * animation counters advance once per client tick from atlas stitch, so the frame shown at a
 * capture ordinal would depend on how many uncontrolled ticks passed before the clock armed.
 * While the capture agent holds animation, {@code TextureManager.tick()} is skipped; from arming
 * on it runs once per controlled tick, so the animation tick at every ordinal is fixed by the
 * plan. Vanilla behaviour when no capture is armed.
 */
@Mixin(TextureManager.class)
public abstract class MixinTextureManager {

    @Inject(method = "tick()V", at = @At("HEAD"), cancellable = true, expect = 1)
    private void schmaloogium$holdAnimation(CallbackInfo ci) {
        if (ControlledClock.isAnimationHeld()) {
            ci.cancel();
        }
    }
}
