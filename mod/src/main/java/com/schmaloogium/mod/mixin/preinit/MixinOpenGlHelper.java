// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.preinit;

import com.schmaloogium.mod.glue.frame.BootstrapHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * H-BOOT-02 (PHASE_7_DOC §4.10.2): RETURN of {@code OpenGlHelper.initializeTextures} — the
 * first point at which a current GL context is guaranteed. Runs the stage-2 capability
 * probe exactly once; no GL work is attempted before it. SRG method target per D-5.
 */
@Mixin(net.minecraft.client.renderer.OpenGlHelper.class)
public abstract class MixinOpenGlHelper {

    @Inject(method = "initializeTextures()V", at = @At("RETURN"), require = 0, expect = 1)
    private static void schmaloogium$onGlReady(CallbackInfo ci) {
        BootstrapHooks.onGlReady();
    }
}
