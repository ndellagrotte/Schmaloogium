// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame;

import com.schmaloogium.engine.frame.DrawDisposition;
import com.schmaloogium.engine.frame.dispatch.RenderSection;
import com.schmaloogium.engine.frame.ScopeOpenResult;
import com.schmaloogium.mod.glue.frame.FrameHooks;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The RenderGlobal scope family (PHASE_7_DOC §4.10.3/§4.10.5, H-SKY-01/03,
 * H-TERRAIN-01/02, H-DAMAGE-01, H-ENTITY-01, H-CLOUD-01, H-BORDER-01).
 * Sun/moon draws use local finally-balanced child scopes. Method targets are MCP;
 * remapJar produces their SRG refmap.
 */
@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal {

    /** H-SKY-01: enter gbuffers_skybasic around the whole sky pass. */
    @Inject(method = "renderSky(FI)V", at = @At("HEAD"), require = 0, expect = 1)
    private void schmaloogium$skyBasicEnter(float partialTicks, int pass, CallbackInfo ci) {
        this.skyScope = FrameHooks.enterSection(RenderSection.SKY_BASIC);
    }

    @Inject(method = "renderSky(FI)V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$skyBasicExit(float partialTicks, int pass, CallbackInfo ci) {
        FrameHooks.exitSection(RenderSection.SKY_BASIC, this.skyScope);
        this.skyScope = null;
    }

    private ScopeOpenResult skyScope;

    /**
     * H-SKY-03: the only draw between the sun and moon texture field reads.
     * Cleanroom 0.6.10-alpha renderSky(FI)V has a preceding sunrise fan, which
     * this field-bounded slice deliberately excludes.
     */
    @Redirect(method = "renderSky(FI)V",
            slice = @Slice(
                    from = @At(value = "FIELD",
                            target = "Lnet/minecraft/client/renderer/RenderGlobal;SUN_TEXTURES:Lnet/minecraft/util/ResourceLocation;"),
                    to = @At(value = "FIELD",
                            target = "Lnet/minecraft/client/renderer/RenderGlobal;MOON_PHASES_TEXTURES:Lnet/minecraft/util/ResourceLocation;")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;draw()V"),
            require = 0, expect = 1, allow = 1)
    private void schmaloogium$sunDraw(Tessellator tessellator) {
        schmaloogium$skyTexturedDraw(tessellator, true);
    }

    /** H-SKY-03: stop before star brightness; stars and lower void remain sky-basic. */
    @Redirect(method = "renderSky(FI)V",
            slice = @Slice(
                    from = @At(value = "FIELD",
                            target = "Lnet/minecraft/client/renderer/RenderGlobal;MOON_PHASES_TEXTURES:Lnet/minecraft/util/ResourceLocation;"),
                    to = @At(value = "INVOKE",
                            target = "Lnet/minecraft/client/multiplayer/WorldClient;getStarBrightness(F)F")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;draw()V"),
            require = 0, expect = 1, allow = 1)
    private void schmaloogium$moonDraw(Tessellator tessellator) {
        schmaloogium$skyTexturedDraw(tessellator, false);
    }

    @Unique
    private static void schmaloogium$skyTexturedDraw(Tessellator tessellator, boolean sun) {
        ScopeOpenResult scope = FrameHooks.enterSection(RenderSection.SKY_TEXTURED);
        try {
            if (!FrameHooks.skyTextureAllowed(sun)
                    || scope instanceof ScopeOpenResult.Opened opened
                    && opened.draw() == DrawDisposition.OMIT_OPERATION) {
                // reset alone leaves isDrawing set and breaks the next vanilla begin.
                tessellator.getBuffer().finishDrawing();
                tessellator.getBuffer().reset();
            } else {
                tessellator.draw();
            }
        } finally {
            FrameHooks.exitSection(RenderSection.SKY_TEXTURED, scope);
        }
    }

    /**
     * H-TERRAIN-01: solid/cutout/cutout-mipped terrain scopes. H-TERRAIN-02: the
     * TRANSLUCENT layer at this same four-argument HEAD is the translucent trigger — the
     * driver closes the opaque scope, runs the deferred family and opens gbuffers_water
     * (PHASE_7_DOC §4.5); the mixin only names the section.
     */
    @Inject(method = "renderBlockLayer(Lnet/minecraft/util/BlockRenderLayer;DILnet/minecraft/entity/Entity;)I",
            at = @At("HEAD"), require = 0, expect = 1)
    private void schmaloogium$terrainEnter(BlockRenderLayer layer, double partialTicks, int pass,
            Entity viewEntity, CallbackInfoReturnable<Integer> cir) {
        this.terrainScope = FrameHooks.enterSection(sectionFor(layer));
    }

    @Inject(method = "renderBlockLayer(Lnet/minecraft/util/BlockRenderLayer;DILnet/minecraft/entity/Entity;)I",
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
            case TRANSLUCENT:
                return RenderSection.TERRAIN_TRANSLUCENT;
            default:
                return RenderSection.TERRAIN_CUTOUT;
        }
    }

    /** H-ENTITY-01: main entity pass scope. */
    @Inject(method = "renderEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;F)V",
            at = @At("HEAD"), require = 0, expect = 1)
    private void schmaloogium$entitiesEnter(Entity viewEntity, net.minecraft.client.renderer.culling.ICamera camera,
            float partialTicks, CallbackInfo ci) {
        this.entitiesScope = FrameHooks.enterSection(RenderSection.ENTITIES);
    }

    @Inject(method = "renderEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;F)V",
            at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$entitiesExit(Entity viewEntity, net.minecraft.client.renderer.culling.ICamera camera,
            float partialTicks, CallbackInfo ci) {
        FrameHooks.exitSection(RenderSection.ENTITIES, this.entitiesScope);
        this.entitiesScope = null;
    }

    private ScopeOpenResult entitiesScope;

    /**
     * H-SHADOW-OUTLINE-01 (PHASE_8_DOC §4.8.2): the outline predicate that gates the whole
     * glow subpass answers false only for the exact shadow entity call; the original
     * predicate and its retained state are otherwise untouched.
     */
    @Redirect(method = "renderEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;F)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderGlobal;isRenderEntityOutlines()Z"),
            require = 0, expect = 1)
    private boolean schmaloogium$shadowOutlinePredicate(RenderGlobal renderGlobal) {
        if (com.schmaloogium.mod.glue.shadow.ShadowTraversalGuard.entitiesActive()) {
            return false;
        }
        return schmaloogium$isRenderEntityOutlines();
    }

    @org.spongepowered.asm.mixin.Shadow(prefix = "schmaloogium$")
    protected abstract boolean schmaloogium$isRenderEntityOutlines();

    /** H-CLOUD-01: clouds scope. */
    @Inject(method = "renderClouds(FIDDD)V", at = @At("HEAD"), require = 0, expect = 1)
    private void schmaloogium$cloudsEnter(float partialTicks, int pass, double viewEntityX,
            double viewEntityY, double viewEntityZ, CallbackInfo ci) {
        this.cloudScope = FrameHooks.enterSection(RenderSection.CLOUDS);
    }

    @Inject(method = "renderClouds(FIDDD)V", at = @At("RETURN"), require = 0, expect = 1)
    private void schmaloogium$cloudsExit(float partialTicks, int pass, double viewEntityX,
            double viewEntityY, double viewEntityZ, CallbackInfo ci) {
        FrameHooks.exitSection(RenderSection.CLOUDS, this.cloudScope);
        this.cloudScope = null;
    }

    private ScopeOpenResult cloudScope;

    /** H-BORDER-01: world-border scope. */
    @Inject(method = "renderWorldBorder(Lnet/minecraft/entity/Entity;F)V", at = @At("HEAD"),
            require = 0, expect = 1)
    private void schmaloogium$borderEnter(Entity viewEntity, float partialTicks, CallbackInfo ci) {
        this.borderScope = FrameHooks.enterSection(RenderSection.WORLD_BORDER);
    }

    @Inject(method = "renderWorldBorder(Lnet/minecraft/entity/Entity;F)V", at = @At("RETURN"),
            require = 0, expect = 1)
    private void schmaloogium$borderExit(Entity viewEntity, float partialTicks, CallbackInfo ci) {
        FrameHooks.exitSection(RenderSection.WORLD_BORDER, this.borderScope);
        this.borderScope = null;
    }

    private ScopeOpenResult borderScope;

    /** H-DAMAGE-01: damaged-block overlay scope. */
    @Inject(method = "drawBlockDamageTexture(Lnet/minecraft/client/renderer/Tessellator;"
            + "Lnet/minecraft/client/renderer/BufferBuilder;Lnet/minecraft/entity/Entity;F)V",
            at = @At("HEAD"), require = 0, expect = 1)
    private void schmaloogium$damageEnter(net.minecraft.client.renderer.Tessellator tessellator,
            net.minecraft.client.renderer.BufferBuilder builder, Entity viewEntity,
            float partialTicks, CallbackInfo ci) {
        this.damageScope = FrameHooks.enterSection(RenderSection.DAMAGED_BLOCK);
    }

    @Inject(method = "drawBlockDamageTexture(Lnet/minecraft/client/renderer/Tessellator;"
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
