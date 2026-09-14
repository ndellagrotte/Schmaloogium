// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.compat.vertex;

import com.schmaloogium.mod.glue.vertex.VertexUploads;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.VertexBufferUploader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * H10-UPLOAD-DESCRIPTOR: the uploader resets the builder before {@code bufferData}, so the
 * product's descriptor (format, epoch serial) is captured here for H10-VBO-UPLOAD.
 */
@Mixin(VertexBufferUploader.class)
public abstract class MixinVertexBufferUploader {

    @Inject(method = "draw(Lnet/minecraft/client/renderer/BufferBuilder;)V", at = @At("HEAD"),
            require = 0, expect = 1)
    private void schmaloogium$uploadEnter(BufferBuilder builder, CallbackInfo ci) {
        VertexUploads.begin(builder);
    }

    @Inject(method = "draw(Lnet/minecraft/client/renderer/BufferBuilder;)V", at = @At("RETURN"),
            require = 0, expect = 1)
    private void schmaloogium$uploadExit(BufferBuilder builder, CallbackInfo ci) {
        VertexUploads.end();
    }
}
