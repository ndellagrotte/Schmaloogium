// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame;

import com.schmaloogium.mod.glue.frame.DepthTex0Bridge;
import com.schmaloogium.mod.glue.frame.DepthTexture;

import net.minecraft.client.shader.Framebuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * H-FBO-01/02 (PHASE_7_DOC §4.10.7): observe the vanilla framebuffer's depth attachment for
 * the depthtex0 bridge, and H-FBO-03 (PHASE_5_DOC §4.8): replace the unsampleable depth
 * renderbuffer with a depth texture at creation. {@code createFramebuffer} runs with
 * {@code useDepth} suppressed so vanilla allocates no renderbuffer; at RETURN the flag is
 * restored and the glue-created texture is attached and recorded in {@code depthBuffer}.
 */
@Mixin(Framebuffer.class)
public abstract class MixinFramebuffer {

    @Shadow
    public int framebufferTextureWidth;

    @Shadow
    public int framebufferTextureHeight;

    @Shadow
    public int framebufferObject;

    @Shadow
    public int depthBuffer;

    @Shadow
    public boolean useDepth;

    /** True when {@code depthBuffer} names a texture this mixin created (not a renderbuffer). */
    @Unique
    private boolean schmaloogium$depthIsTexture;

    /** Remembered across one createFramebuffer call while useDepth is suppressed. */
    @Unique
    private boolean schmaloogium$wantedDepth;

    /** H-FBO-03 (1/2): suppress vanilla's depth-renderbuffer allocation. */
    @Inject(method = "createFramebuffer(II)V", at = @At("HEAD"), expect = 1)
    private void schmaloogium$suppressDepthRenderbuffer(int width, int height, CallbackInfo ci) {
        this.schmaloogium$wantedDepth = this.useDepth;
        this.useDepth = false;
    }

    /** H-FBO-03 (2/2): restore useDepth and attach the sampleable depth texture. */
    @Inject(method = "createFramebuffer(II)V", at = @At("RETURN"), expect = 1)
    private void schmaloogium$attachDepthTexture(int width, int height, CallbackInfo ci) {
        this.useDepth = this.schmaloogium$wantedDepth;
        if (!this.useDepth) {
            return;
        }
        Framebuffer self = (Framebuffer) (Object) this;
        this.depthBuffer = DepthTexture.createAndAttach(this.framebufferObject,
                this.framebufferTextureWidth, this.framebufferTextureHeight,
                self.isStencilEnabled());
        this.schmaloogium$depthIsTexture = true;
    }

    /** Delete our texture ourselves; vanilla's renderbuffer delete then sees -1 and skips. */
    @Inject(method = "deleteFramebuffer()V", at = @At("HEAD"), expect = 1)
    private void schmaloogium$deleteDepthTexture(CallbackInfo ci) {
        if (this.schmaloogium$depthIsTexture) {
            DepthTexture.delete(this.depthBuffer);
            this.depthBuffer = -1;
            this.schmaloogium$depthIsTexture = false;
        }
    }

    /** H-FBO-01: vanilla-FBO attachment epoch increment + safe-boundary rebuild offer. */
    @Inject(method = "createBindFramebuffer(II)V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$onCreateBindFramebuffer(int width, int height, CallbackInfo ci) {
        observeDepth();
    }

    /** H-FBO-02: final-target boundary observation. */
    @Inject(method = "bindFramebuffer(Z)V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$onBindFramebuffer(boolean viewPort, CallbackInfo ci) {
        observeDepth();
    }

    private void observeDepth() {
        Framebuffer self = (Framebuffer) (Object) this;
        // Only the main (screen-sized) framebuffer lends depthtex0, and only as a texture.
        if (this.schmaloogium$depthIsTexture
                && self.framebufferWidth == net.minecraft.client.Minecraft.getMinecraft().displayWidth
                && self.framebufferHeight
                        == net.minecraft.client.Minecraft.getMinecraft().displayHeight) {
            DepthTex0Bridge.observe(this.depthBuffer,
                    this.framebufferTextureWidth, this.framebufferTextureHeight);
        }
    }
}
