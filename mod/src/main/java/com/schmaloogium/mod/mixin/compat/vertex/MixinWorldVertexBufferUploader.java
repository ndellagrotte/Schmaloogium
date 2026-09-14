// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.compat.vertex;

import com.schmaloogium.mod.glue.vertex.Block56Format;
import com.schmaloogium.mod.glue.vertex.ChunkDrawBridge;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldVertexBufferUploader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * H10-CLIENT (PHASE_10_DOC §4.6 "non-VBO chunk display lists"): around the real draw of an
 * extended builder (display-list compile with VBOs off), the generic pointers are issued
 * through the P1 facade in LIST_CAPTURE mode so the list captures the attributes; every
 * other builder (GUI, particles, entities) takes the original call untouched.
 */
@Mixin(WorldVertexBufferUploader.class)
public abstract class MixinWorldVertexBufferUploader {

    @Redirect(method = "draw(Lnet/minecraft/client/renderer/BufferBuilder;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GlStateManager;glDrawArrays(III)V"),
            require = 0, expect = 1)
    private void schmaloogium$drawClient(int mode, int first, int count, BufferBuilder builder) {
        if (count > 0 && Block56Format.isExtended(builder.getVertexFormat())) {
            ChunkDrawBridge.drawClient(builder.getByteBuffer(), count,
                    () -> GlStateManager.glDrawArrays(mode, first, count));
        } else {
            GlStateManager.glDrawArrays(mode, first, count);
        }
    }
}
