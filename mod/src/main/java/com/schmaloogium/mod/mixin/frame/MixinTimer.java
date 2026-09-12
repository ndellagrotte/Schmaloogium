// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame;

import com.schmaloogium.mod.conformance.ControlledClock;

import net.minecraft.util.Timer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * H-CLOCK-01 (Phase 2 §5.1.1 controlled clock; FEATURE class, fallback NONE):
 * {@code Minecraft.runGameLoop} calls {@code updateTimer()} once per frame, runs
 * {@code elapsedTicks} client ticks, then renders with {@code renderPartialTicks}. When the
 * capture clock is armed the wall-clock result is replaced by the plan's fixed
 * {@code ticksPerFrame}/{@code partialTicks}, after the integrated server ran its lockstep
 * ticks. Vanilla timing is untouched when the clock is not armed.
 */
@Mixin(Timer.class)
public abstract class MixinTimer {

    @Shadow public int elapsedTicks;
    @Shadow public float renderPartialTicks;
    @Shadow public float elapsedPartialTicks;

    @Inject(method = "updateTimer()V", at = @At("RETURN"), expect = 1)
    private void schmaloogium$controlledStep(CallbackInfo ci) {
        if (!ControlledClock.isArmed()) {
            return;
        }
        if (ControlledClock.stepFrame()) {
            this.elapsedTicks = ControlledClock.ticksPerFrame();
            this.renderPartialTicks = ControlledClock.partialTicks();
            this.elapsedPartialTicks = ControlledClock.partialTicks();
        }
    }
}
