// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame.ids;

import com.schmaloogium.mod.glue.id.IdHooks;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.FloatBuffer;

/**
 * H9-COLOR-01/02 (PHASE_9_DOC §4.13, P7-owned observer rows): the four floats vanilla
 * hands {@code GL_TEXTURE_ENV_COLOR} for the hurt/flash effect are copied into the
 * {@code entityColor} uniform, the original call runs unchanged; {@code unsetBrightness}
 * restores the neutral colour. Nothing is reconstructed from vanilla's formula.
 */
@Mixin(RenderLivingBase.class)
public abstract class MixinRenderLivingBase<T extends EntityLivingBase> {

    private static final int GL_TEXTURE_ENV_COLOR = 8705;

    @Redirect(method = "setBrightness(Lnet/minecraft/entity/EntityLivingBase;FZ)Z",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GlStateManager;glTexEnv(IILjava/nio/FloatBuffer;)V"),
            require = 0, expect = 1)
    private void schmaloogium$entityColor(int target, int parameterName, FloatBuffer parameters) {
        if (parameterName == GL_TEXTURE_ENV_COLOR && parameters != null
                && parameters.remaining() >= 4) {
            int p = parameters.position();
            IdHooks.noteEntityColor(parameters.get(p), parameters.get(p + 1),
                    parameters.get(p + 2), parameters.get(p + 3));
        }
        GlStateManager.glTexEnv(target, parameterName, parameters);
    }

    @Inject(method = "unsetBrightness()V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$entityColorUnset(CallbackInfo ci) {
        IdHooks.clearEntityColor();
    }
}
