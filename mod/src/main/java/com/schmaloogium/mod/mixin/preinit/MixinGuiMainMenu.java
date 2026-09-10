// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.preinit;

import com.schmaloogium.mod.glue.frame.BootstrapHooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * H-BOOT-03 (PHASE_7_DOC §4.10.2 catalog row): RETURN of {@code GuiMainMenu.initGui()},
 * once — the recommended client-loading-complete moment. Shader startup may stay
 * deferred past it; the bridge only latches the flag. MCP-named target; the remapJar
 * refmap carries SRG for production.
 */
@Mixin(net.minecraft.client.gui.GuiMainMenu.class)
public abstract class MixinGuiMainMenu {

    @Inject(method = "initGui()V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$onClientLoadingComplete(CallbackInfo ci) {
        BootstrapHooks.onClientLoadingComplete();
    }
}
