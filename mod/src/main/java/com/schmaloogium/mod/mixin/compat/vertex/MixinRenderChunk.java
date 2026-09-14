// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.compat.vertex;

import com.schmaloogium.mod.glue.vertex.ChunkTaskContext;

import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.RenderChunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** H10-BUILD: the rebuild guard resets the block stack around one chunk rebuild. */
@Mixin(RenderChunk.class)
public abstract class MixinRenderChunk {

    @Inject(method = "rebuildChunk(FFFLnet/minecraft/client/renderer/chunk/ChunkCompileTaskGenerator;)V",
            at = @At("HEAD"), require = 0, expect = 1)
    private void schmaloogium$buildEnter(float x, float y, float z, ChunkCompileTaskGenerator generator,
            CallbackInfo ci) {
        ChunkTaskContext context = ChunkTaskContext.current();
        if (context != null) {
            context.resetStamps();
        }
    }

    @Inject(method = "rebuildChunk(FFFLnet/minecraft/client/renderer/chunk/ChunkCompileTaskGenerator;)V",
            at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$buildExit(float x, float y, float z, ChunkCompileTaskGenerator generator,
            CallbackInfo ci) {
        ChunkTaskContext context = ChunkTaskContext.current();
        if (context != null) {
            context.resetStamps();
        }
    }
}
