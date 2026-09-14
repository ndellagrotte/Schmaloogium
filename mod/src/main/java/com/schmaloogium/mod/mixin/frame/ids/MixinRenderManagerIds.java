// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame.ids;

import com.schmaloogium.mod.glue.id.IdHooks;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * H9-ENTITY-ID-01 (PHASE_9_DOC §4.12): the per-entity id scope around the lower entity
 * dispatch every path uses ({@code renderEntityStatic} forwards here, so one balanced
 * HEAD/RETURN pair covers both entry points; nested scopes stay LIFO). A throw skips the
 * RETURN; the frame's outer finally drains both stacks (H-FRAME-07 → {@code resetFrame}).
 */
@Mixin(RenderManager.class)
public abstract class MixinRenderManagerIds {

    @Inject(method = "renderEntity(Lnet/minecraft/entity/Entity;DDDFFZ)V", at = @At("HEAD"),
            require = 0, expect = 1)
    private void schmaloogium$entityIdEnter(Entity entity, double x, double y, double z, float yaw,
            float partialTicks, boolean debug, CallbackInfo ci) {
        IdHooks.enterEntity(entity);
    }

    @Inject(method = "renderEntity(Lnet/minecraft/entity/Entity;DDDFFZ)V", at = @At("RETURN"),
            require = 0, expect = 1)
    private void schmaloogium$entityIdExit(Entity entity, double x, double y, double z, float yaw,
            float partialTicks, boolean debug, CallbackInfo ci) {
        IdHooks.leaveEntity();
    }
}
