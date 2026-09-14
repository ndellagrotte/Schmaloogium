// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.compat.vertex;

import com.schmaloogium.engine.vertex.Classic56Layout;
import com.schmaloogium.mod.glue.vertex.VertexBufferSidecar;
import com.schmaloogium.mod.glue.vertex.VertexUploads;

import net.minecraft.client.renderer.vertex.VertexBuffer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

/**
 * H10-VBO-UPLOAD (PHASE_10_DOC §4.5): after a successful upload of an extended product
 * the count is recomputed from the uploaded byte range and the actual 56-byte stride
 * (vanilla divides by its construction format's 28), and the sidecar is published;
 * a delete clears it.
 */
@Mixin(VertexBuffer.class)
public abstract class MixinVertexBuffer implements VertexBufferSidecar {

    @Shadow private int count;

    private boolean schmaloogium$extended;
    private long schmaloogium$serial;

    @Override
    public boolean schmaloogium$extended() {
        return schmaloogium$extended;
    }

    @Override
    public long schmaloogium$serial() {
        return schmaloogium$serial;
    }

    @Override
    public int schmaloogium$count() {
        return count;
    }

    @Override
    public void schmaloogium$setUpload(boolean extended, long serial, int vertices) {
        this.schmaloogium$extended = extended;
        this.schmaloogium$serial = serial;
        this.count = vertices;
    }

    @Override
    public void schmaloogium$clearUpload() {
        this.schmaloogium$extended = false;
        this.schmaloogium$serial = 0L;
    }

    @Inject(method = "bufferData(Ljava/nio/ByteBuffer;)V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$afterUpload(ByteBuffer data, CallbackInfo ci) {
        VertexUploads.Pending pending = VertexUploads.pending();
        if (pending != null && pending.extended()) {
            schmaloogium$setUpload(true, pending.serial(), data.limit() / Classic56Layout.STRIDE_BYTES);
        } else {
            schmaloogium$clearUpload();
        }
    }

    @Inject(method = "deleteGlBuffers()V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$afterDelete(CallbackInfo ci) {
        schmaloogium$clearUpload();
    }
}
