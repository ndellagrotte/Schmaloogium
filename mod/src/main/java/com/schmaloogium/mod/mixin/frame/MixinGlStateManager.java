// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame;

import com.schmaloogium.mod.glue.AlphaBlendOverrideHooks;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The D-P7-43 CORE alpha/blend anchor family (PHASE_7_DOC §4.10.1/§4.10.6): three alpha
 * setters and six blend setters as method-entry/normal-exit anchors — nine HEAD sites and
 * six normal RETURN sites, one of each listed kind per descriptor. HEAD cancels before
 * vanilla cache/native mutation only when the P1-owned predicate holds; normal blend
 * RETURN publishes effective values. Missing, duplicate or mismatched anchors keep shader
 * admission closed and report SHADERS_OFF — never partial coverage. SRG method targets
 * per D-5.
 */
@Mixin(GlStateManager.class)
public abstract class MixinGlStateManager {

    // ---------------------------------------------------------- H-ALPHA (3 HEAD anchors)

    @Inject(method = "func_179141_d()V", at = @At("HEAD"), cancellable = true,
            require = 0, expect = 1)
    private void schmaloogium$suppressEnableAlpha(CallbackInfo ci) {
        if (AlphaBlendOverrideHooks.suppressAlphaMutation()) {
            ci.cancel();
        }
    }

    @Inject(method = "func_179118_c()V", at = @At("HEAD"), cancellable = true,
            require = 0, expect = 1)
    private void schmaloogium$suppressDisableAlpha(CallbackInfo ci) {
        if (AlphaBlendOverrideHooks.suppressAlphaMutation()) {
            ci.cancel();
        }
    }

    @Inject(method = "func_179092_a(IF)V", at = @At("HEAD"), cancellable = true,
            require = 0, expect = 1)
    private void schmaloogium$suppressAlphaFunc(int func, float ref, CallbackInfo ci) {
        if (AlphaBlendOverrideHooks.suppressAlphaMutation()) {
            ci.cancel();
        }
    }

    // ---------------------------------------------------------- H-BLEND (6 HEAD + 6 RETURN)

    @Inject(method = "func_179147_l()V", at = @At("HEAD"), cancellable = true,
            require = 0, expect = 1)
    private void schmaloogium$suppressEnableBlend(CallbackInfo ci) {
        if (AlphaBlendOverrideHooks.suppressBlendMutation()) {
            ci.cancel();
        }
    }

    @Inject(method = "func_179147_l()V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$publishEnableBlend(CallbackInfo ci) {
        AlphaBlendOverrideHooks.publishEffectiveBlend();
    }

    @Inject(method = "func_179084_k()V", at = @At("HEAD"), cancellable = true,
            require = 0, expect = 1)
    private void schmaloogium$suppressDisableBlend(CallbackInfo ci) {
        if (AlphaBlendOverrideHooks.suppressBlendMutation()) {
            ci.cancel();
        }
    }

    @Inject(method = "func_179084_k()V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$publishDisableBlend(CallbackInfo ci) {
        AlphaBlendOverrideHooks.publishEffectiveBlend();
    }

    @Inject(method = "func_187401_a(Lnet/minecraft/client/renderer/GlStateManager$SourceFactor;"
            + "Lnet/minecraft/client/renderer/GlStateManager$DestFactor;)V",
            at = @At("HEAD"), cancellable = true, require = 0, expect = 1)
    private void schmaloogium$suppressTryBlendFactorPair(
            GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor,
            CallbackInfo ci) {
        if (AlphaBlendOverrideHooks.suppressBlendMutation()) {
            ci.cancel();
        }
    }

    @Inject(method = "func_187401_a(Lnet/minecraft/client/renderer/GlStateManager$SourceFactor;"
            + "Lnet/minecraft/client/renderer/GlStateManager$DestFactor;)V",
            at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$publishTryBlendFactorPair(
            GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor,
            CallbackInfo ci) {
        AlphaBlendOverrideHooks.publishEffectiveBlend();
    }

    @Inject(method = "func_179112_b(II)V", at = @At("HEAD"), cancellable = true,
            require = 0, expect = 1)
    private void schmaloogium$suppressBlendFunc(int srcFactor, int dstFactor, CallbackInfo ci) {
        if (AlphaBlendOverrideHooks.suppressBlendMutation()) {
            ci.cancel();
        }
    }

    @Inject(method = "func_179112_b(II)V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$publishBlendFunc(int srcFactor, int dstFactor, CallbackInfo ci) {
        AlphaBlendOverrideHooks.publishEffectiveBlend();
    }

    @Inject(method = "func_187428_a(Lnet/minecraft/client/renderer/GlStateManager$SourceFactor;"
            + "Lnet/minecraft/client/renderer/GlStateManager$DestFactor;"
            + "Lnet/minecraft/client/renderer/GlStateManager$SourceFactor;"
            + "Lnet/minecraft/client/renderer/GlStateManager$DestFactor;)V",
            at = @At("HEAD"), cancellable = true, require = 0, expect = 1)
    private void schmaloogium$suppressTryBlendSeparate(
            GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor,
            GlStateManager.SourceFactor srcFactorAlpha, GlStateManager.DestFactor dstFactorAlpha,
            CallbackInfo ci) {
        if (AlphaBlendOverrideHooks.suppressBlendMutation()) {
            ci.cancel();
        }
    }

    @Inject(method = "func_187428_a(Lnet/minecraft/client/renderer/GlStateManager$SourceFactor;"
            + "Lnet/minecraft/client/renderer/GlStateManager$DestFactor;"
            + "Lnet/minecraft/client/renderer/GlStateManager$SourceFactor;"
            + "Lnet/minecraft/client/renderer/GlStateManager$DestFactor;)V",
            at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$publishTryBlendSeparate(
            GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor,
            GlStateManager.SourceFactor srcFactorAlpha, GlStateManager.DestFactor dstFactorAlpha,
            CallbackInfo ci) {
        AlphaBlendOverrideHooks.publishEffectiveBlend();
    }

    @Inject(method = "func_179120_a(IIII)V", at = @At("HEAD"), cancellable = true,
            require = 0, expect = 1)
    private void schmaloogium$suppressBlendFuncSeparate(int srcFactor, int dstFactor,
            int srcFactorAlpha, int dstFactorAlpha, CallbackInfo ci) {
        if (AlphaBlendOverrideHooks.suppressBlendMutation()) {
            ci.cancel();
        }
    }

    @Inject(method = "func_179120_a(IIII)V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$publishBlendFuncSeparate(int srcFactor, int dstFactor,
            int srcFactorAlpha, int dstFactorAlpha, CallbackInfo ci) {
        AlphaBlendOverrideHooks.publishEffectiveBlend();
    }
}
