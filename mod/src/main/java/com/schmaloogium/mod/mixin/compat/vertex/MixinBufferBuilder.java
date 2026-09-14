// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.compat.vertex;

import com.schmaloogium.mod.glue.vertex.Block56Format;
import com.schmaloogium.mod.glue.vertex.BuilderSidecar;
import com.schmaloogium.mod.glue.vertex.VertexIngress;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.ByteBuffer;

/**
 * The builder rows (PHASE_10_DOC §4.2, §4.11): H10-BEGIN substitutes the 56-byte
 * projection for BLOCK inside an extended chunk task and initializes the sidecar;
 * H10-END / H10-ARRAY / H10-BULK finalize every completed quad; the H10-WRITER-* rows
 * select the semantic destination before each fluent store (the physical cursor would
 * otherwise walk into the normal slot after the lightmap); H10-RESET clears the sidecar.
 * An inactive builder pays one boolean per call.
 */
@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder implements BuilderSidecar {

    @Shadow private ByteBuffer byteBuffer;
    @Shadow private int vertexCount;
    @Shadow private VertexFormatElement vertexFormatElement;
    @Shadow private int vertexFormatIndex;
    @Shadow private int drawMode;
    @Shadow private VertexFormat vertexFormat;

    private boolean schmaloogium$extended;
    private long schmaloogium$serial;
    private int schmaloogium$finalizedThrough;

    // ----------------------------------------------------------------- sidecar

    @Override
    public boolean schmaloogium$extended() {
        return schmaloogium$extended;
    }

    @Override
    public void schmaloogium$setExtended(boolean extended, long serial) {
        this.schmaloogium$extended = extended;
        this.schmaloogium$serial = serial;
    }

    @Override
    public long schmaloogium$serial() {
        return schmaloogium$serial;
    }

    @Override
    public int schmaloogium$finalizedThrough() {
        return schmaloogium$finalizedThrough;
    }

    @Override
    public void schmaloogium$setFinalizedThrough(int vertices) {
        this.schmaloogium$finalizedThrough = vertices;
    }

    @Override
    public ByteBuffer schmaloogium$byteBuffer() {
        return byteBuffer;
    }

    @Override
    public int schmaloogium$vertexCount() {
        return vertexCount;
    }

    @Override
    public int schmaloogium$drawMode() {
        return drawMode;
    }

    @Override
    public void schmaloogium$selectElement(int elementIndex) {
        this.vertexFormatIndex = elementIndex;
        this.vertexFormatElement = vertexFormat.getElement(elementIndex);
    }

    // ----------------------------------------------------------------- begin / reset

    @ModifyVariable(method = "begin(ILnet/minecraft/client/renderer/vertex/VertexFormat;)V",
            at = @At("HEAD"), argsOnly = true, require = 0, expect = 1)
    private VertexFormat schmaloogium$beginFormat(VertexFormat requested) {
        return VertexIngress.beginFormat(requested);
    }

    @Inject(method = "begin(ILnet/minecraft/client/renderer/vertex/VertexFormat;)V",
            at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$beginInit(int glMode, VertexFormat format, CallbackInfo ci) {
        VertexIngress.beginInit(this, this.vertexFormat);
    }

    @Inject(method = "reset()V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$reset(CallbackInfo ci) {
        if (schmaloogium$extended) {
            VertexIngress.reset(this);
        }
    }

    // ----------------------------------------------------------------- completion

    @Inject(method = "endVertex()V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$endVertex(CallbackInfo ci) {
        if (schmaloogium$extended && (vertexCount & 3) == 0) {
            VertexIngress.finalizeCompleteQuads(this);
        }
    }

    @Inject(method = "addVertexData([I)V", at = @At("HEAD"), require = 0, expect = 1)
    private void schmaloogium$arrayEnter(int[] data, CallbackInfo ci) {
        if (schmaloogium$extended) {
            VertexIngress.notePartialQuadAtBoundary(this, "bulk entry");
        }
    }

    @Inject(method = "addVertexData([I)V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$arrayExit(int[] data, CallbackInfo ci) {
        if (schmaloogium$extended) {
            VertexIngress.finalizeCompleteQuads(this);
        }
    }

    /** Forge's bulk append (a Forge-added method): same protocol, data in the builder's format. */
    @Inject(method = "putBulkData(Ljava/nio/ByteBuffer;)V", at = @At("RETURN"), remap = false,
            require = 0, expect = 1)
    private void schmaloogium$bulkExit(ByteBuffer data, CallbackInfo ci) {
        if (schmaloogium$extended) {
            VertexIngress.finalizeCompleteQuads(this);
        }
    }

    // ----------------------------------------------------------------- semantic dispatch

    @Inject(method = "pos(DDD)Lnet/minecraft/client/renderer/BufferBuilder;", at = @At("HEAD"),
            require = 0, expect = 1)
    private void schmaloogium$writerPos(double x, double y, double z,
            CallbackInfoReturnable<BufferBuilder> cir) {
        if (schmaloogium$extended) {
            schmaloogium$selectElement(Block56Format.POSITION_INDEX);
        }
    }

    @Inject(method = "color(IIII)Lnet/minecraft/client/renderer/BufferBuilder;", at = @At("HEAD"),
            require = 0, expect = 1)
    private void schmaloogium$writerColor(int r, int g, int b, int a,
            CallbackInfoReturnable<BufferBuilder> cir) {
        if (schmaloogium$extended) {
            schmaloogium$selectElement(Block56Format.COLOR_INDEX);
        }
    }

    @Inject(method = "tex(DD)Lnet/minecraft/client/renderer/BufferBuilder;", at = @At("HEAD"),
            require = 0, expect = 1)
    private void schmaloogium$writerTex(double u, double v, CallbackInfoReturnable<BufferBuilder> cir) {
        if (schmaloogium$extended) {
            schmaloogium$selectElement(Block56Format.UV0_INDEX);
        }
    }

    @Inject(method = "lightmap(II)Lnet/minecraft/client/renderer/BufferBuilder;", at = @At("HEAD"),
            require = 0, expect = 1)
    private void schmaloogium$writerLightmap(int sky, int block, CallbackInfoReturnable<BufferBuilder> cir) {
        if (schmaloogium$extended) {
            schmaloogium$selectElement(Block56Format.UV1_INDEX);
        }
    }

    @Inject(method = "normal(FFF)Lnet/minecraft/client/renderer/BufferBuilder;", at = @At("HEAD"),
            require = 0, expect = 1)
    private void schmaloogium$writerNormal(float x, float y, float z,
            CallbackInfoReturnable<BufferBuilder> cir) {
        if (schmaloogium$extended) {
            schmaloogium$selectElement(Block56Format.NORMAL_INDEX);
        }
    }
}
