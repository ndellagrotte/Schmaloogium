// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame.shadow;

import com.schmaloogium.mod.glue.shadow.ShadowTraversalGuard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.Set;

/**
 * H8-TRAVERSE-01 and H8-REBUILD-01-ENTRY (PHASE_8_DOC §4.7 step 3/4): inside the one
 * {@code setupTerrain} the shadow world port forces under {@link ShadowTraversalGuard}, the
 * three {@code RenderChunk.setFrameIndex} sites mark a pass-local identity set instead of
 * vanilla's frame index, the single {@code renderChunksMany} read answers false, the seed
 * query answers all six directions, compiled visibility is short-circuited, and neighbour
 * expansion is filtered by the prism's allowed set. Every redirect passes vanilla through
 * while the guard is inactive; the entry injection counts rebuild-branch entries.
 */
@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobalShadowTraversal {

    private static final String SETUP =
            "setupTerrain(Lnet/minecraft/entity/Entity;DLnet/minecraft/client/renderer/culling/ICamera;IZ)V";
    private static final String SET_FRAME_INDEX =
            "Lnet/minecraft/client/renderer/chunk/RenderChunk;setFrameIndex(I)Z";

    /** H8-TRAVERSE-01-VISIT-SEED: the camera-cell seed. */
    @Redirect(method = SETUP, at = @At(value = "INVOKE", target = SET_FRAME_INDEX, ordinal = 0),
            require = 0, expect = 1)
    private boolean schmaloogium$visitSeed(RenderChunk chunk, int frameIndex) {
        return ShadowTraversalGuard.visit(chunk, frameIndex);
    }

    /** H8-TRAVERSE-01-VISIT-FALLBACK-SEED: the out-of-world fallback seeds. */
    @Redirect(method = SETUP, at = @At(value = "INVOKE", target = SET_FRAME_INDEX, ordinal = 1),
            require = 0, expect = 1)
    private boolean schmaloogium$visitFallbackSeed(RenderChunk chunk, int frameIndex) {
        return ShadowTraversalGuard.visit(chunk, frameIndex);
    }

    /** H8-TRAVERSE-01-VISIT-NEIGHBOR: the breadth-first neighbour visit. */
    @Redirect(method = SETUP, at = @At(value = "INVOKE", target = SET_FRAME_INDEX, ordinal = 2),
            require = 0, expect = 1)
    private boolean schmaloogium$visitNeighbor(RenderChunk chunk, int frameIndex) {
        return ShadowTraversalGuard.visit(chunk, frameIndex);
    }

    /** H8-TRAVERSE-01-RENDER-CHUNKS-MANY: no path-direction pruning under shadow. */
    @Redirect(method = SETUP, at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/Minecraft;renderChunksMany:Z", opcode = Opcodes.GETFIELD),
            require = 0, expect = 1)
    private boolean schmaloogium$renderChunksMany(Minecraft mc) {
        return !ShadowTraversalGuard.active() && mc.renderChunksMany;
    }

    /** H8-TRAVERSE-01-SEED-DIRECTIONS: the seed query answers every direction under shadow. */
    @Redirect(method = SETUP, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderGlobal;getVisibleFacings(Lnet/minecraft/util/math/BlockPos;)Ljava/util/Set;"),
            require = 0, expect = 1)
    private Set<EnumFacing> schmaloogium$seedDirections(RenderGlobal renderGlobal, BlockPos pos) {
        if (ShadowTraversalGuard.active()) {
            return EnumSet.allOf(EnumFacing.class);
        }
        return schmaloogium$getVisibleFacings(pos);
    }

    /** H8-TRAVERSE-01-COMPILED-VISIBILITY: compiled occlusion never prunes shadow traversal. */
    @Redirect(method = SETUP, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/chunk/CompiledChunk;isVisible(Lnet/minecraft/util/EnumFacing;Lnet/minecraft/util/EnumFacing;)Z"),
            require = 0, expect = 1)
    private boolean schmaloogium$compiledVisibility(CompiledChunk compiled, EnumFacing from, EnumFacing to) {
        return ShadowTraversalGuard.active() || compiled.isVisible(from, to);
    }

    /** H8-TRAVERSE-01-NEIGHBOR: vanilla's neighbour, filtered by the prism's allowed set. */
    @Redirect(method = SETUP, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderGlobal;getRenderChunkOffset(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/chunk/RenderChunk;Lnet/minecraft/util/EnumFacing;)Lnet/minecraft/client/renderer/chunk/RenderChunk;"),
            require = 0, expect = 1)
    private RenderChunk schmaloogium$neighbor(RenderGlobal renderGlobal, BlockPos playerPos,
            RenderChunk base, EnumFacing facing) {
        return ShadowTraversalGuard.filterNeighbor(schmaloogium$getRenderChunkOffset(playerPos, base, facing));
    }

    /** H8-REBUILD-01-ENTRY: the rebuild branch replaces the list right after {@code dirty = false}. */
    @Inject(method = SETUP, at = @At(value = "FIELD",
            target = "Lnet/minecraft/client/renderer/RenderGlobal;renderInfos:Ljava/util/List;",
            opcode = Opcodes.PUTFIELD), require = 0, expect = 1)
    private void schmaloogium$rebuildEntry(Entity viewEntity, double partialTicks, ICamera camera,
            int frameCount, boolean playerSpectator, CallbackInfo ci) {
        ShadowTraversalGuard.noteRebuildEntry((RenderGlobal) (Object) this);
    }

    @org.spongepowered.asm.mixin.Shadow(prefix = "schmaloogium$")
    private Set<EnumFacing> schmaloogium$getVisibleFacings(BlockPos pos) {
        throw new AbstractMethodError("shadow");
    }

    @org.spongepowered.asm.mixin.Shadow(prefix = "schmaloogium$")
    private RenderChunk schmaloogium$getRenderChunkOffset(BlockPos playerPos, RenderChunk base,
            EnumFacing facing) {
        throw new AbstractMethodError("shadow");
    }
}
