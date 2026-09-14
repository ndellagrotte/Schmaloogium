// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.compat.vertex;

import com.schmaloogium.mod.glue.vertex.VertexIngress;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * H10-BLOCK (PHASE_10_DOC §4.4): each per-block render call inside a rebuild pushes the
 * block's identity words (models and fluids alike) and pops on return.
 */
@Mixin(BlockRendererDispatcher.class)
public abstract class MixinBlockRendererDispatcher {

    @Inject(method = "renderBlock(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;"
            + "Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/BufferBuilder;)Z",
            at = @At("HEAD"), require = 0, expect = 1)
    private void schmaloogium$blockEnter(IBlockState state, BlockPos pos, IBlockAccess access,
            BufferBuilder buffer, CallbackInfoReturnable<Boolean> cir) {
        VertexIngress.blockEnter(state);
    }

    @Inject(method = "renderBlock(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;"
            + "Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/BufferBuilder;)Z",
            at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$blockExit(IBlockState state, BlockPos pos, IBlockAccess access,
            BufferBuilder buffer, CallbackInfoReturnable<Boolean> cir) {
        VertexIngress.blockExit();
    }
}
