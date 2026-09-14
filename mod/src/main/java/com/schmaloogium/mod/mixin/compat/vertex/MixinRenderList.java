// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.compat.vertex;

import com.schmaloogium.mod.glue.vertex.ChunkDrawBridge;

import net.minecraft.client.renderer.RenderList;
import net.minecraft.util.BlockRenderLayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * H10-LIST-REPLAY (PHASE_10_DOC §4.6 :860): a compiled display list replays its captured
 * generic attribute values as current values, so the layer end restores the documented
 * neutrals. There is no separate entry guard: at v0.3a a list of another epoch cannot be
 * reached, because every epoch change recreates every chunk product through
 * {@code loadRenderers()} before the next frame is admitted.
 */
@Mixin(RenderList.class)
public abstract class MixinRenderList {

    @Inject(method = "renderChunkLayer(Lnet/minecraft/util/BlockRenderLayer;)V", at = @At("RETURN"),
            require = 0, expect = 1)
    private void schmaloogium$replayExit(BlockRenderLayer layer, CallbackInfo ci) {
        ChunkDrawBridge.endListReplay();
    }
}
