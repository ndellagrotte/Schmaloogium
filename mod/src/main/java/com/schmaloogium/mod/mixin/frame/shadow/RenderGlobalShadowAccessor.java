// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.mixin.frame.shadow;

import com.schmaloogium.mod.glue.shadow.RenderGlobalShadowAccess;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Set;

/**
 * The H8-RESTORE-01 / H8-ENTITY-01 / H8-REBUILD-01-SETTER / H8-TERRAIN-01 / H8-CLOUD-01 /
 * H8-ENTITY-01-METHOD accessor and invoker rows (PHASE_8_DOC §4.13.1). Every accessor is
 * a generated method the hook-anchor audit counts in the transformed class; a failed
 * resolution fails the whole mixin and leaves its rows at zero (shadows disabled).
 */
@Mixin(RenderGlobal.class)
public interface RenderGlobalShadowAccessor extends RenderGlobalShadowAccess {

    @Accessor("renderInfos")
    @Override
    List<?> schmaloogium$renderInfos();

    @Accessor("renderInfos")
    @Override
    void schmaloogium$setRenderInfos(List<?> renderInfos);

    @Accessor("displayListEntitiesDirty")
    @Override
    boolean schmaloogium$displayListEntitiesDirty();

    @Accessor("displayListEntitiesDirty")
    @Override
    void schmaloogium$setDisplayListEntitiesDirty(boolean dirty);

    @Accessor("lastViewEntityX")
    @Override
    double schmaloogium$lastViewEntityX();

    @Accessor("lastViewEntityX")
    @Override
    void schmaloogium$setLastViewEntityX(double value);

    @Accessor("lastViewEntityY")
    @Override
    double schmaloogium$lastViewEntityY();

    @Accessor("lastViewEntityY")
    @Override
    void schmaloogium$setLastViewEntityY(double value);

    @Accessor("lastViewEntityZ")
    @Override
    double schmaloogium$lastViewEntityZ();

    @Accessor("lastViewEntityZ")
    @Override
    void schmaloogium$setLastViewEntityZ(double value);

    @Accessor("lastViewEntityPitch")
    @Override
    double schmaloogium$lastViewEntityPitch();

    @Accessor("lastViewEntityPitch")
    @Override
    void schmaloogium$setLastViewEntityPitch(double value);

    @Accessor("lastViewEntityYaw")
    @Override
    double schmaloogium$lastViewEntityYaw();

    @Accessor("lastViewEntityYaw")
    @Override
    void schmaloogium$setLastViewEntityYaw(double value);

    @Accessor("debugFixedClippingHelper")
    @Override
    ClippingHelper schmaloogium$debugFixedClippingHelper();

    @Accessor("debugFixedClippingHelper")
    @Override
    void schmaloogium$setDebugFixedClippingHelper(ClippingHelper helper);

    @Accessor("debugFixTerrainFrustum")
    @Override
    boolean schmaloogium$debugFixTerrainFrustum();

    @Accessor("debugFixTerrainFrustum")
    @Override
    void schmaloogium$setDebugFixTerrainFrustum(boolean value);

    @Accessor("viewFrustum")
    @Override
    ViewFrustum schmaloogium$viewFrustum();

    @Accessor("chunksToUpdate")
    @Override
    Set<RenderChunk> schmaloogium$chunksToUpdate();

    @Accessor("prevRenderSortX")
    @Override
    double schmaloogium$prevRenderSortX();

    @Accessor("prevRenderSortX")
    @Override
    void schmaloogium$setPrevRenderSortX(double value);

    @Accessor("prevRenderSortY")
    @Override
    double schmaloogium$prevRenderSortY();

    @Accessor("prevRenderSortY")
    @Override
    void schmaloogium$setPrevRenderSortY(double value);

    @Accessor("prevRenderSortZ")
    @Override
    double schmaloogium$prevRenderSortZ();

    @Accessor("prevRenderSortZ")
    @Override
    void schmaloogium$setPrevRenderSortZ(double value);

    @Accessor("renderEntitiesStartupCounter")
    @Override
    int schmaloogium$renderEntitiesStartupCounter();

    @Accessor("renderEntitiesStartupCounter")
    @Override
    void schmaloogium$setRenderEntitiesStartupCounter(int value);

    @Accessor("countEntitiesTotal")
    @Override
    int schmaloogium$countEntitiesTotal();

    @Accessor("countEntitiesTotal")
    @Override
    void schmaloogium$setCountEntitiesTotal(int value);

    @Accessor("countEntitiesRendered")
    @Override
    int schmaloogium$countEntitiesRendered();

    @Accessor("countEntitiesRendered")
    @Override
    void schmaloogium$setCountEntitiesRendered(int value);

    @Accessor("countEntitiesHidden")
    @Override
    int schmaloogium$countEntitiesHidden();

    @Accessor("countEntitiesHidden")
    @Override
    void schmaloogium$setCountEntitiesHidden(int value);

    @Invoker("setDisplayListEntitiesDirty")
    @Override
    void schmaloogium$invokeSetDisplayListEntitiesDirty();

    @Invoker("renderBlockLayer")
    @Override
    int schmaloogium$invokeRenderBlockLayer(BlockRenderLayer layer, double partialTicks, int pass,
            Entity entity);

    @Invoker("renderClouds")
    @Override
    void schmaloogium$invokeRenderClouds(float partialTicks, int pass, double x, double y, double z);

    @Invoker("renderEntities")
    @Override
    void schmaloogium$invokeRenderEntities(Entity viewEntity, ICamera camera, float partialTicks);
}
