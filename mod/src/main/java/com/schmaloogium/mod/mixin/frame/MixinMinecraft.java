// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame;

import com.schmaloogium.mod.glue.frame.FrameHooks;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The resize family (PHASE_7_DOC §4.10.7, H-RESIZE-01/02): window/display extent change
 * observation. Resize never mutates an open Phase 5 generation; the driver aborts via
 * the resize epoch at the next frame begin. SRG method targets per D-5.
 */
@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    /** H-RESIZE-01: actual framebuffer extent change (updateFramebufferSize). */
    @Inject(method = "func_147119_ah()V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$onUpdateFramebufferSize(CallbackInfo ci) {
        FrameHooks.onFramebufferExtentChanged();
    }

    /** H-RESIZE-02: window/display extent change (resize) — kept distinct from HiDPI. */
    @Inject(method = "func_71370_a(II)V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$onResize(int width, int height, CallbackInfo ci) {
        FrameHooks.onFramebufferExtentChanged();
    }
}
