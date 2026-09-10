// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame;

import com.schmaloogium.mod.glue.frame.ResourceReloadBoundary;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * H-RESOURCE-01 (PHASE_7_DOC §4.10.7/§4.8.1): synchronous pre-destructive quiescence
 * before the reload listeners run and post-listener completion at return. Resource
 * reloads execute only on safe boundaries (never mid-frame), so the HEAD/RETURN pair is
 * the whole-body AROUND guarantee; the OQ-4 ledger records the weaving proof leg. SRG
 * method target per D-5.
 */
@Mixin(SimpleReloadableResourceManager.class)
public abstract class MixinSimpleReloadableResourceManager {

    @Inject(method = "reloadResources(Ljava/util/List;)V", at = @At("HEAD"),
            require = 0, expect = 1)
    private void schmaloogium$reloadBegin(java.util.List listeners, CallbackInfo ci) {
        ResourceReloadBoundary.begin();
    }

    @Inject(method = "reloadResources(Ljava/util/List;)V", at = @At("RETURN"),
            require = 0, expect = 1)
    private void schmaloogium$reloadEnd(java.util.List listeners, CallbackInfo ci) {
        ResourceReloadBoundary.end();
    }
}
