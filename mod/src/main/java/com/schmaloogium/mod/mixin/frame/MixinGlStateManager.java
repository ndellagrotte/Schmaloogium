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

    @Inject(method = "enableAlpha()V", at = @At("HEAD"), cancellable = true,
            require = 0, expect = 1)
    private static void schmaloogium$suppressEnableAlpha(CallbackInfo ci) {
        if (AlphaBlendOverrideHooks.suppressAlphaMutation()) {
            ci.cancel();
        }
    }

    @Inject(method = "disableAlpha()V", at = @At("HEAD"), cancellable = true,
            require = 0, expect = 1)
    private static void schmaloogium$suppressDisableAlpha(CallbackInfo ci) {
        if (AlphaBlendOverrideHooks.suppressAlphaMutation()) {
            ci.cancel();
        }
    }

    @Inject(method = "alphaFunc(IF)V", at = @At("HEAD"), cancellable = true,
            require = 0, expect = 1)
    private static void schmaloogium$suppressAlphaFunc(int func, float ref, CallbackInfo ci) {
        if (AlphaBlendOverrideHooks.suppressAlphaMutation()) {
            ci.cancel();
        }
    }

    // ---------------------------------------------------------- H-BLEND (6 HEAD + 6 RETURN)

    @Inject(method = "enableBlend()V", at = @At("HEAD"), cancellable = true,
            require = 0, expect = 1)
    private static void schmaloogium$suppressEnableBlend(CallbackInfo ci) {
        if (AlphaBlendOverrideHooks.suppressBlendMutation()) {
            ci.cancel();
        }
    }

    @Inject(method = "enableBlend()V", at = @At("RETURN"), require = 0, expect = 1)
    private static void schmaloogium$publishEnableBlend(CallbackInfo ci) {
        AlphaBlendOverrideHooks.publishEffectiveBlend();
    }

    @Inject(method = "disableBlend()V", at = @At("HEAD"), cancellable = true,
            require = 0, expect = 1)
    private static void schmaloogium$suppressDisableBlend(CallbackInfo ci) {
        if (AlphaBlendOverrideHooks.suppressBlendMutation()) {
            ci.cancel();
        }
    }

    @Inject(method = "disableBlend()V", at = @At("RETURN"), require = 0, expect = 1)
    private static void schmaloogium$publishDisableBlend(CallbackInfo ci) {
        AlphaBlendOverrideHooks.publishEffectiveBlend();
    }

    @Inject(method = "blendFunc(Lnet/minecraft/client/renderer/GlStateManager$SourceFactor;"
            + "Lnet/minecraft/client/renderer/GlStateManager$DestFactor;)V",
            at = @At("HEAD"), cancellable = true, require = 0, expect = 1)
    private static void schmaloogium$suppressTryBlendFactorPair(
            GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor,
            CallbackInfo ci) {
        if (AlphaBlendOverrideHooks.suppressBlendMutation()) {
            ci.cancel();
        }
    }

    @Inject(method = "blendFunc(Lnet/minecraft/client/renderer/GlStateManager$SourceFactor;"
            + "Lnet/minecraft/client/renderer/GlStateManager$DestFactor;)V",
            at = @At("RETURN"), require = 0, expect = 1)
    private static void schmaloogium$publishTryBlendFactorPair(
            GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor,
            CallbackInfo ci) {
        AlphaBlendOverrideHooks.publishEffectiveBlend();
    }

    @Inject(method = "blendFunc(II)V", at = @At("HEAD"), cancellable = true,
            require = 0, expect = 1)
    private static void schmaloogium$suppressBlendFunc(int srcFactor, int dstFactor, CallbackInfo ci) {
        if (AlphaBlendOverrideHooks.suppressBlendMutation()) {
            ci.cancel();
        }
    }

    @Inject(method = "blendFunc(II)V", at = @At("RETURN"), require = 0, expect = 1)
    private static void schmaloogium$publishBlendFunc(int srcFactor, int dstFactor, CallbackInfo ci) {
        AlphaBlendOverrideHooks.publishEffectiveBlend();
    }

    @Inject(method = "tryBlendFuncSeparate(Lnet/minecraft/client/renderer/GlStateManager$SourceFactor;"
            + "Lnet/minecraft/client/renderer/GlStateManager$DestFactor;"
            + "Lnet/minecraft/client/renderer/GlStateManager$SourceFactor;"
            + "Lnet/minecraft/client/renderer/GlStateManager$DestFactor;)V",
            at = @At("HEAD"), cancellable = true, require = 0, expect = 1)
    private static void schmaloogium$suppressTryBlendSeparate(
            GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor,
            GlStateManager.SourceFactor srcFactorAlpha, GlStateManager.DestFactor dstFactorAlpha,
            CallbackInfo ci) {
        if (AlphaBlendOverrideHooks.suppressBlendMutation()) {
            ci.cancel();
        }
    }

    @Inject(method = "tryBlendFuncSeparate(Lnet/minecraft/client/renderer/GlStateManager$SourceFactor;"
            + "Lnet/minecraft/client/renderer/GlStateManager$DestFactor;"
            + "Lnet/minecraft/client/renderer/GlStateManager$SourceFactor;"
            + "Lnet/minecraft/client/renderer/GlStateManager$DestFactor;)V",
            at = @At("RETURN"), require = 0, expect = 1)
    private static void schmaloogium$publishTryBlendSeparate(
            GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor,
            GlStateManager.SourceFactor srcFactorAlpha, GlStateManager.DestFactor dstFactorAlpha,
            CallbackInfo ci) {
        AlphaBlendOverrideHooks.publishEffectiveBlend();
    }

    @Inject(method = "tryBlendFuncSeparate(IIII)V", at = @At("HEAD"), cancellable = true,
            require = 0, expect = 1)
    private static void schmaloogium$suppressBlendFuncSeparate(int srcFactor, int dstFactor,
            int srcFactorAlpha, int dstFactorAlpha, CallbackInfo ci) {
        if (AlphaBlendOverrideHooks.suppressBlendMutation()) {
            ci.cancel();
        }
    }

    @Inject(method = "tryBlendFuncSeparate(IIII)V", at = @At("RETURN"), require = 0, expect = 1)
    private static void schmaloogium$publishBlendFuncSeparate(int srcFactor, int dstFactor,
            int srcFactorAlpha, int dstFactorAlpha, CallbackInfo ci) {
        AlphaBlendOverrideHooks.publishEffectiveBlend();
    }
}
