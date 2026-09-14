// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.compat.vertex;

import com.schmaloogium.mod.glue.vertex.ChunkDrawBridge;
import com.schmaloogium.mod.glue.vertex.VertexBufferSidecar;

import net.minecraft.client.renderer.VboRenderList;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.BlockRenderLayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * H10-VBO-LAYER / H10-VBO-DRAW (PHASE_10_DOC §4.6): the per-VBO draw wrapper overrides
 * vanilla's fixed 28-byte pointer setup through the P1 facade immediately around the
 * actual draw (never a duplicate draw), and the layer end restores the predecessor once.
 */
@Mixin(VboRenderList.class)
public abstract class MixinVboRenderList {

    @Redirect(method = "renderChunkLayer(Lnet/minecraft/util/BlockRenderLayer;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/vertex/VertexBuffer;drawArrays(I)V"),
            require = 0, expect = 1)
    private void schmaloogium$drawVbo(VertexBuffer vbo, int mode, BlockRenderLayer layer) {
        ChunkDrawBridge.drawVbo((VertexBufferSidecar) vbo, layer.name(), () -> vbo.drawArrays(mode));
    }

    @Inject(method = "renderChunkLayer(Lnet/minecraft/util/BlockRenderLayer;)V", at = @At("RETURN"),
            require = 0, expect = 1)
    private void schmaloogium$layerEnd(BlockRenderLayer layer, CallbackInfo ci) {
        ChunkDrawBridge.endLayer();
    }
}
