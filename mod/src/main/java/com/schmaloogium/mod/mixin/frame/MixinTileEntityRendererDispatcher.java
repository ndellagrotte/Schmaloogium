// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame;

import com.schmaloogium.engine.frame.dispatch.RenderSection;
import com.schmaloogium.engine.frame.ScopeOpenResult;
import com.schmaloogium.mod.glue.frame.FrameHooks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * H-ENTITY-03 (PHASE_7_DOC §4.10.4, program routing at v0.1; P9 ID augmentation at v0.3):
 * the lower TileEntityRendererDispatcher entry used by normal, global, damaged, direct
 * and nested block-entity calls — one AROUND-shaped HEAD/RETURN pair plus the guaranteed
 * frame-finally drain. SRG method target per D-5.
 */
@Mixin(TileEntityRendererDispatcher.class)
public abstract class MixinTileEntityRendererDispatcher {

    @Inject(method = "render(Lnet/minecraft/tileentity/TileEntity;DDDFIF)V",
            at = @At("HEAD"), require = 0, expect = 1)
    private void schmaloogium$blockEntityEnter(TileEntity tileEntity, double x, double y, double z,
            float partialTicks, int destroyStage, float partialTickOffset, CallbackInfo ci) {
        this.blockEntityScope = FrameHooks.enterSection(RenderSection.BLOCK_ENTITIES);
    }

    @Inject(method = "render(Lnet/minecraft/tileentity/TileEntity;DDDFIF)V",
            at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$blockEntityExit(TileEntity tileEntity, double x, double y, double z,
            float partialTicks, int destroyStage, float partialTickOffset, CallbackInfo ci) {
        FrameHooks.exitSection(RenderSection.BLOCK_ENTITIES, this.blockEntityScope);
        this.blockEntityScope = null;
    }

    private ScopeOpenResult blockEntityScope;

    /**
     * H9-BLOCK-ENTITY-ID-01 (PHASE_9_DOC §4.12): the P9 id scope nests inside the P7 pair
     * with a real {@code finally}, around the one actual renderer call (slow path).
     */
    @Redirect(method = "render(Lnet/minecraft/tileentity/TileEntity;DDDFIF)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/tileentity/TileEntitySpecialRenderer;"
                            + "render(Lnet/minecraft/tileentity/TileEntity;DDDFIF)V"),
            require = 0, expect = 1)
    private void schmaloogium$blockEntityIdSlow(TileEntitySpecialRenderer<TileEntity> renderer,
            TileEntity tileEntity, double x, double y, double z, float partialTicks, int destroyStage,
            float alpha) {
        com.schmaloogium.mod.glue.id.IdHooks.enterBlockEntity(tileEntity);
        try {
            renderer.render(tileEntity, x, y, z, partialTicks, destroyStage, alpha);
        } finally {
            com.schmaloogium.mod.glue.id.IdHooks.leaveBlockEntity();
        }
    }

    /** The same scope around the Forge fast path (a Forge-added method: no remap). */
    @Redirect(method = "render(Lnet/minecraft/tileentity/TileEntity;DDDFIF)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/tileentity/TileEntitySpecialRenderer;"
                            + "renderTileEntityFast(Lnet/minecraft/tileentity/TileEntity;DDDFIF"
                            + "Lnet/minecraft/client/renderer/BufferBuilder;)V",
                    remap = false),
            require = 0, expect = 1)
    private void schmaloogium$blockEntityIdFast(TileEntitySpecialRenderer<TileEntity> renderer,
            TileEntity tileEntity, double x, double y, double z, float partialTicks, int destroyStage,
            float alpha, net.minecraft.client.renderer.BufferBuilder buffer) {
        com.schmaloogium.mod.glue.id.IdHooks.enterBlockEntity(tileEntity);
        try {
            renderer.renderTileEntityFast(tileEntity, x, y, z, partialTicks, destroyStage, alpha, buffer);
        } finally {
            com.schmaloogium.mod.glue.id.IdHooks.leaveBlockEntity();
        }
    }

    /** {@code shadowBlockEntities=false} (PHASE_8_DOC §4.8.2): no block entity under the shadow guard. */
    @Inject(method = "render(Lnet/minecraft/tileentity/TileEntity;DDDFIF)V",
            at = @At("HEAD"), cancellable = true, require = 0, expect = 1)
    private void schmaloogium$shadowBlockEntities(TileEntity tileEntity, double x, double y, double z,
            float partialTicks, int destroyStage, float partialTickOffset, CallbackInfo ci) {
        if (com.schmaloogium.mod.glue.shadow.ShadowTraversalGuard.cancelBlockEntity()) {
            ci.cancel();
        }
    }
}
