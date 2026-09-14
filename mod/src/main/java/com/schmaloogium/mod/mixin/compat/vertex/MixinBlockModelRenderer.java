// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.compat.vertex;

import com.schmaloogium.mod.glue.vertex.VertexIngress;

import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * H10-MODEL-ARRAY (PHASE_10_DOC §4.2 "typed bulk ingress"): the vanilla flat/smooth quad
 * paths append {@code BakedQuad} data in the 7-int BLOCK record regardless of the
 * builder's format, so these two call sites are the typed expanders. Forge's lighting
 * pipeline packs at the builder's own stride and needs no adapter.
 */
@Mixin(BlockModelRenderer.class)
public abstract class MixinBlockModelRenderer {

    @Redirect(method = "renderQuadsFlat", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/BufferBuilder;addVertexData([I)V"),
            require = 0, expect = 1)
    private void schmaloogium$addQuadsFlat(BufferBuilder buffer, int[] data) {
        VertexIngress.addBlockFormatQuads(buffer, data);
    }

    @Redirect(method = "renderQuadsSmooth", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/BufferBuilder;addVertexData([I)V"),
            require = 0, expect = 1)
    private void schmaloogium$addQuadsSmooth(BufferBuilder buffer, int[] data) {
        VertexIngress.addBlockFormatQuads(buffer, data);
    }
}
