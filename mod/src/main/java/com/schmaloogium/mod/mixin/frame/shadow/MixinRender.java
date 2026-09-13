// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame.shadow;

import com.schmaloogium.mod.glue.shadow.ShadowTraversalGuard;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * H8-BLOB-01 (PHASE_8_DOC §4.4): while a composition with a ready shadow plan is installed,
 * vanilla's blob shadow is replaced by the shader shadow, so the blob draw inside
 * {@code doRenderShadowAndFire} becomes a no-op; fire rendering is untouched.
 */
@Mixin(Render.class)
public abstract class MixinRender<T extends Entity> {

    @Redirect(method = "doRenderShadowAndFire(Lnet/minecraft/entity/Entity;DDDFF)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/Render;renderShadow(Lnet/minecraft/entity/Entity;DDDFF)V"),
            require = 0, expect = 1)
    private void schmaloogium$blobShadow(Render<T> render, Entity entity, double x, double y, double z,
            float shadowAlpha, float partialTicks) {
        if (ShadowTraversalGuard.blobShadowsSuppressed()) {
            return;
        }
        schmaloogium$renderShadow(entity, x, y, z, shadowAlpha, partialTicks);
    }

    @org.spongepowered.asm.mixin.Shadow(prefix = "schmaloogium$")
    private void schmaloogium$renderShadow(Entity entity, double x, double y, double z,
            float shadowAlpha, float partialTicks) {
        throw new AbstractMethodError("shadow");
    }
}
