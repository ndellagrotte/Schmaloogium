// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame;

import com.schmaloogium.mod.glue.frame.FrameHooks;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.culling.ICamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The core frame transaction hooks (PHASE_7_DOC §4.10.2, H-FRAME-00…07): the exact
 * frame-begin ordering D-P7-76, the once-per-frame matrix capture, the terrain-token
 * verified shadow slot boundary, idempotent normal finish and the outer-finally
 * guaranteed finish. No policy here — every decision is the engine driver's; all method
 * targets are MCP-named; remapJar generates the SRG refmap (D-5).
 */
@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

    @Shadow
    protected abstract void renderWorld(float partialTicks, long finishTimeNano);

    /** H-FRAME-01: frame counter read at HEAD, before the incrementing argument. */
    @Shadow
    private int frameCount;

    @Inject(method = "renderWorldPass(IFJ)V", at = @At("HEAD"), require = 0, expect = 1)
    private void schmaloogium$frameBegin(int pass, float partialTicks, long finishTimeNano,
            CallbackInfo ci) {
        FrameHooks.open(pass, partialTicks, finishTimeNano, this.frameCount);
    }

    /** H-FRAME-02: state normalization before vanilla's ordinal-0 depth clear. */
    @Inject(method = "renderWorldPass(IFJ)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GlStateManager;clear(I)V",
            ordinal = 0), require = 0, expect = 1)
    private void schmaloogium$beforeFirstClear(CallbackInfo ci) {
        FrameHooks.normalizeVanillaState();
    }

    /** H-FRAME-03: after the same clear — Phase 5 frame resources without matrix claim. */
    @Inject(method = "renderWorldPass(IFJ)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GlStateManager;clear(I)V",
            ordinal = 0, shift = At.Shift.AFTER), require = 0, expect = 1)
    private void schmaloogium$afterFirstClear(CallbackInfo ci) {
        FrameHooks.afterFirstClear();
    }

    /** H-FRAME-04: exact post-camera point, once per frame (D-P6-19). */
    @Inject(method = "renderWorldPass(IFJ)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/EntityRenderer;setupCameraTransform(FI)V",
            shift = At.Shift.AFTER), require = 0, expect = 1)
    private void schmaloogium$captureMainCamera(CallbackInfo ci) {
        FrameHooks.captureMainCamera();
    }

    /** H-FRAME-05: terrain-token verified shadow slot boundary, close in finally. */
    @Redirect(method = "renderWorldPass(IFJ)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderGlobal;setupTerrain("
                    + "Lnet/minecraft/entity/Entity;DLnet/minecraft/client/renderer/culling/ICamera;"
                    + "IZ)V"), require = 0, expect = 1)
    private void schmaloogium$aroundSetupTerrain(RenderGlobal renderGlobal, Entity viewEntity,
            double partialTicks, ICamera camera, int frameCount, boolean playerSpectator) {
        FrameHooks.invokeShadowSlotThenRestoreMain(frameCount, () ->
                renderGlobal.setupTerrain(viewEntity, partialTicks, camera, frameCount,
                        playerSpectator));
    }

    /** H-FRAME-06: idempotent normal-return finish. */
    @Inject(method = "renderWorldPass(IFJ)V", at = @At("TAIL"), require = 0, expect = 1)
    private void schmaloogium$finishNormal(CallbackInfo ci) {
        FrameHooks.finishNormal();
    }

    /**
     * H-FRAME-00 + H-FRAME-07: the outer boundary around the renderWorld invocation,
     * whose finally is the early-exit guarantee — strictly stronger than a TAIL-only
     * close. The bridge's finish is idempotent, so NORMAL then EARLY_RETURN no-ops.
     */
    @Redirect(method = "updateCameraAndRender(FJ)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/EntityRenderer;renderWorld(FJ)V"),
            require = 0, expect = 1)
    private void schmaloogium$aroundRenderWorld(EntityRenderer renderer, float partialTicks,
            long finishTimeNano) {
        Throwable thrown = null;
        try {
            this.renderWorld(partialTicks, finishTimeNano);
        } catch (Throwable t) {
            thrown = t;
            throw t;
        } finally {
            FrameHooks.finishGuaranteed(thrown);
        }
    }
}
