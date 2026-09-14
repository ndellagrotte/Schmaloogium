// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.compat.vertex;

import com.schmaloogium.mod.glue.vertex.ChunkTaskContext;

import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.ChunkRenderWorker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * H10-TASK (PHASE_10_DOC §4.4): the outermost task guard installs the task context (the
 * publication captured once for the whole task) and detaches it on return; a worker is
 * one thread, so the previous context rides on the worker instance.
 */
@Mixin(ChunkRenderWorker.class)
public abstract class MixinChunkRenderWorker {

    private ChunkTaskContext schmaloogium$previous;

    @Inject(method = "processTask(Lnet/minecraft/client/renderer/chunk/ChunkCompileTaskGenerator;)V",
            at = @At("HEAD"), require = 0, expect = 1)
    private void schmaloogium$taskEnter(ChunkCompileTaskGenerator generator, CallbackInfo ci) {
        this.schmaloogium$previous = ChunkTaskContext.install();
    }

    @Inject(method = "processTask(Lnet/minecraft/client/renderer/chunk/ChunkCompileTaskGenerator;)V",
            at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$taskExit(ChunkCompileTaskGenerator generator, CallbackInfo ci) {
        ChunkTaskContext.detach(this.schmaloogium$previous);
        this.schmaloogium$previous = null;
    }
}
