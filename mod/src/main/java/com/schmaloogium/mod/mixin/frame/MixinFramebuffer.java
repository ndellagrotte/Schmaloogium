// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame;

import com.schmaloogium.mod.glue.frame.DepthTex0Bridge;
import com.schmaloogium.mod.glue.frame.FrameHooks;
import net.minecraft.client.shader.Framebuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The framebuffer family (PHASE_7_DOC §4.10.7, H-FBO-01/02) plus the depthtex0 bridge
 * observation: the vanilla main framebuffer's depth attachment identity feeds
 * {@link DepthTex0Bridge} so the engine's Phase 5 can borrow it as {@code depthtex0}.
 * No vanilla handle is retained beyond its epoch — the bridge keeps only opaque
 * identity/version facts. SRG method targets per D-5.
 */
@Mixin(Framebuffer.class)
public abstract class MixinFramebuffer {

    @Shadow
    public int framebufferTextureWidth;

    @Shadow
    public int framebufferTextureHeight;

    @Shadow
    public int depthTexture;

    /** H-FBO-01: vanilla-FBO attachment epoch increment + safe-boundary rebuild offer. */
    @Inject(method = "func_147613_a(II)V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$onCreateBindFramebuffer(int width, int height, CallbackInfo ci) {
        observeDepth();
    }

    /** H-FBO-02: final-target boundary observation. */
    @Inject(method = "func_147610_a(Z)V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$onBindFramebuffer(boolean viewPort, CallbackInfo ci) {
        observeDepth();
    }

    private void observeDepth() {
        Framebuffer self = (Framebuffer) (Object) this;
        // Only the main (screen-sized) framebuffer lends depthtex0.
        if (self.framebufferWidth == net.minecraft.client.Minecraft.getMinecraft().displayWidth
                && self.framebufferHeight
                        == net.minecraft.client.Minecraft.getMinecraft().displayHeight) {
            DepthTex0Bridge.observe(this.depthTexture,
                    this.framebufferTextureWidth, this.framebufferTextureHeight);
        }
    }
}
