// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame;

import com.schmaloogium.engine.frame.dispatch.RenderSection;
import com.schmaloogium.engine.frame.ScopeOpenResult;
import com.schmaloogium.mod.glue.frame.FrameHooks;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

/**
 * The RenderGlobal scope family (PHASE_7_DOC §4.10.3/§4.10.5, H-SKY-01, H-TERRAIN-01/02,
 * H-DAMAGE-01, H-ENTITY-01, H-CLOUD-01, H-BORDER-01): balanced HEAD/RETURN scope pairs
 * whose open/close decisions are entirely the engine driver's. All method targets are
 * SRG per D-5.
 */
@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal {

    /** H-SKY-01: enter gbuffers_skybasic around the whole sky pass. */
    @Inject(method = "func_174976_a(FI)V", at = @At("HEAD"), require = 0, expect = 1)
    private void schmaloogium$skyBasicEnter(float partialTicks, int pass, CallbackInfo ci) {
        this.skyScope = FrameHooks.enterSection(RenderSection.SKY_BASIC);
    }

    @Inject(method = "func_174976_a(FI)V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$skyBasicExit(float partialTicks, int pass, CallbackInfo ci) {
        FrameHooks.exitSection(RenderSection.SKY_BASIC, this.skyScope);
        this.skyScope = null;
    }

    private ScopeOpenResult skyScope;

    /** H-TERRAIN-01: solid/cutout/cutout-mipped terrain scopes. */
    @Inject(method = "func_174977_a(Lnet/minecraft/util/BlockRenderLayer;DILnet/minecraft/entity/Entity;)I",
            at = @At("HEAD"), require = 0, expect = 1)
    private void schmaloogium$terrainEnter(BlockRenderLayer layer, double partialTicks, int pass,
            Entity viewEntity, CallbackInfoReturnable<Integer> cir) {
        if (layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.CUTOUT
                || layer == BlockRenderLayer.CUTOUT_MIPPED) {
            this.terrainScope = FrameHooks.enterSection(sectionFor(layer));
        }
    }

    @Inject(method = "func_174977_a(Lnet/minecraft/util/BlockRenderLayer;DILnet/minecraft/entity/Entity;)I",
            at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$terrainExit(BlockRenderLayer layer, double partialTicks, int pass,
            Entity viewEntity, CallbackInfoReturnable<Integer> cir) {
        if (this.terrainScope != null) {
            FrameHooks.exitSection(sectionFor(layer), this.terrainScope);
            this.terrainScope = null;
        }
    }

    private ScopeOpenResult terrainScope;

    private static RenderSection sectionFor(BlockRenderLayer layer) {
        switch (layer) {
            case SOLID:
                return RenderSection.TERRAIN_SOLID;
            case CUTOUT_MIPPED:
                return RenderSection.TERRAIN_CUTOUT_MIPPED;
            default:
                return RenderSection.TERRAIN_CUTOUT;
        }
    }

    /** H-ENTITY-01: main entity pass scope. */
    @Inject(method = "func_180446_a(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;F)V",
            at = @At("HEAD"), require = 0, expect = 1)
    private void schmaloogium$entitiesEnter(Entity viewEntity, net.minecraft.client.renderer.culling.ICamera camera,
            float partialTicks, CallbackInfo ci) {
        this.entitiesScope = FrameHooks.enterSection(RenderSection.ENTITIES);
    }

    @Inject(method = "func_180446_a(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;F)V",
            at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$entitiesExit(Entity viewEntity, net.minecraft.client.renderer.culling.ICamera camera,
            float partialTicks, CallbackInfo ci) {
        FrameHooks.exitSection(RenderSection.ENTITIES, this.entitiesScope);
        this.entitiesScope = null;
    }

    private ScopeOpenResult entitiesScope;

    /** H-CLOUD-01: clouds scope. */
    @Inject(method = "func_180447_b(FIDDD)V", at = @At("HEAD"), require = 0, expect = 1)
    private void schmaloogium$cloudsEnter(float partialTicks, int pass, double viewEntityX,
            double viewEntityY, double viewEntityZ, CallbackInfo ci) {
        this.cloudScope = FrameHooks.enterSection(RenderSection.CLOUDS);
    }

    @Inject(method = "func_180447_b(FIDDD)V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$cloudsExit(float partialTicks, int pass, double viewEntityX,
            double viewEntityY, double viewEntityZ, CallbackInfo ci) {
        FrameHooks.exitSection(RenderSection.CLOUDS, this.cloudScope);
        this.cloudScope = null;
    }

    private ScopeOpenResult cloudScope;

    /** H-BORDER-01: world-border scope. */
    @Inject(method = "func_180449_a(Lnet/minecraft/entity/Entity;F)V", at = @At("HEAD"),
            require = 0, expect = 1)
    private void schmaloogium$borderEnter(Entity viewEntity, float partialTicks, CallbackInfo ci) {
        this.borderScope = FrameHooks.enterSection(RenderSection.WORLD_BORDER);
    }

    @Inject(method = "func_180449_a(Lnet/minecraft/entity/Entity;F)V", at = @At("RETURN"),
            require = 0, expect = 1)
    private void schmaloogium$borderExit(Entity viewEntity, float partialTicks, CallbackInfo ci) {
        FrameHooks.exitSection(RenderSection.WORLD_BORDER, this.borderScope);
        this.borderScope = null;
    }

    private ScopeOpenResult borderScope;

    /** H-DAMAGE-01: damaged-block overlay scope. */
    @Inject(method = "func_174981_a(Lnet/minecraft/client/renderer/Tessellator;"
            + "Lnet/minecraft/client/renderer/BufferBuilder;Lnet/minecraft/entity/Entity;F)V",
            at = @At("HEAD"), require = 0, expect = 1)
    private void schmaloogium$damageEnter(net.minecraft.client.renderer.Tessellator tessellator,
            net.minecraft.client.renderer.BufferBuilder builder, Entity viewEntity,
            float partialTicks, CallbackInfo ci) {
        this.damageScope = FrameHooks.enterSection(RenderSection.DAMAGED_BLOCK);
    }

    @Inject(method = "func_174981_a(Lnet/minecraft/client/renderer/Tessellator;"
            + "Lnet/minecraft/client/renderer/BufferBuilder;Lnet/minecraft/entity/Entity;F)V",
            at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$damageExit(net.minecraft.client.renderer.Tessellator tessellator,
            net.minecraft.client.renderer.BufferBuilder builder, Entity viewEntity,
            float partialTicks, CallbackInfo ci) {
        FrameHooks.exitSection(RenderSection.DAMAGED_BLOCK, this.damageScope);
        this.damageScope = null;
    }

    private ScopeOpenResult damageScope;
}
