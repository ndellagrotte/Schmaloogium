// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame.shadow;

import com.schmaloogium.mod.glue.shadow.ShadowTraversalGuard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The {@code shadowPlayer=false} content switch (PHASE_8_DOC §4.8.2): under the shadow
 * entity-call guard the view entity's draw is cancelled at the static entry every entity
 * traversal uses. Task F reuses this seam for the per-entity ID scope.
 */
@Mixin(RenderManager.class)
public abstract class MixinRenderManager {

    @Inject(method = "renderEntityStatic(Lnet/minecraft/entity/Entity;FZ)V", at = @At("HEAD"),
            cancellable = true, require = 0, expect = 1)
    private void schmaloogium$shadowPlayer(Entity entity, float partialTicks, boolean debug,
            CallbackInfo ci) {
        if (ShadowTraversalGuard.cancelPlayer() && entity == Minecraft.getMinecraft().getRenderViewEntity()) {
            ci.cancel();
        }
    }
}
