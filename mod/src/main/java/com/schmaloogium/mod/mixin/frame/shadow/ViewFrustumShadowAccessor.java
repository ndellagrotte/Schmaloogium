// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame.shadow;

import com.schmaloogium.mod.glue.shadow.McShadowWorldPort;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** PHASE_8_DOC §4.7: the prism's chunk coordinates map through the protected toroidal lookup. */
@Mixin(ViewFrustum.class)
public interface ViewFrustumShadowAccessor extends McShadowWorldPort.ViewFrustumAccess {

    @Invoker("getRenderChunk")
    @Override
    RenderChunk schmaloogium$renderChunk(BlockPos pos);
}
