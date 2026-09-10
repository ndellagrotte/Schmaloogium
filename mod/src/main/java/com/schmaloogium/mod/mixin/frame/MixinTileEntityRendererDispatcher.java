// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame;

import com.schmaloogium.engine.frame.dispatch.RenderSection;
import com.schmaloogium.engine.frame.ScopeOpenResult;
import com.schmaloogium.mod.glue.frame.FrameHooks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * H-ENTITY-03 (PHASE_7_DOC §4.10.4, program routing at v0.1; P9 ID augmentation at v0.3):
 * the lower TileEntityRendererDispatcher entry used by normal, global, damaged, direct
 * and nested block-entity calls — one AROUND-shaped HEAD/RETURN pair plus the guaranteed
 * frame-finally drain. SRG method target per D-5.
 */
@Mixin(TileEntityRendererDispatcher.class)
public abstract class MixinTileEntityRendererDispatcher {

    @Inject(method = "func_192854_a(Lnet/minecraft/tileentity/TileEntity;DDDFIF)V",
            at = @At("HEAD"), require = 0, expect = 1)
    private void schmaloogium$blockEntityEnter(TileEntity tileEntity, double x, double y, double z,
            float partialTicks, int destroyStage, float partialTickOffset, CallbackInfo ci) {
        this.blockEntityScope = FrameHooks.enterSection(RenderSection.BLOCK_ENTITIES);
    }

    @Inject(method = "func_192854_a(Lnet/minecraft/tileentity/TileEntity;DDDFIF)V",
            at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$blockEntityExit(TileEntity tileEntity, double x, double y, double z,
            float partialTicks, int destroyStage, float partialTickOffset, CallbackInfo ci) {
        FrameHooks.exitSection(RenderSection.BLOCK_ENTITIES, this.blockEntityScope);
        this.blockEntityScope = null;
    }

    private ScopeOpenResult blockEntityScope;
}
