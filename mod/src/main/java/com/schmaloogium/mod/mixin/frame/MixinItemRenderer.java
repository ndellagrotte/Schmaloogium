// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame;

import com.schmaloogium.mod.glue.frame.FrameHooks;
import net.minecraft.client.renderer.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * H-OVERLAY-01 (PHASE_7_DOC §4.10.5): first-person overlay lease around
 * {@code ItemRenderer.renderOverlays}. SRG method target per D-5.
 */
@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

    @Inject(method = "renderOverlays(F)V", at = @At("HEAD"), require = 0, expect = 1)
    private void schmaloogium$overlayEnter(float partialTicks, CallbackInfo ci) {
        this.overlayScope = FrameHooks.enterSection(
                com.schmaloogium.engine.frame.dispatch.RenderSection.FIRST_PERSON_OVERLAY);
    }

    @Inject(method = "renderOverlays(F)V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$overlayExit(float partialTicks, CallbackInfo ci) {
        FrameHooks.exitSection(
                com.schmaloogium.engine.frame.dispatch.RenderSection.FIRST_PERSON_OVERLAY,
                this.overlayScope);
        this.overlayScope = null;
    }

    private com.schmaloogium.engine.frame.ScopeOpenResult overlayScope;
}
