// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.shadow;

import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;

import java.util.List;
import java.util.Set;

/**
 * The game-side face of the H8-RESTORE-01 / H8-ENTITY-01 / H8-REBUILD-01 / H8-TERRAIN-01 /
 * H8-CLOUD-01 accessor rows (PHASE_8_DOC §4.13.1). The mixin
 * {@code mixin.frame.shadow.RenderGlobalShadowAccessor} extends this interface and carries
 * the {@code @Accessor}/{@code @Invoker} annotations; game code casts {@code RenderGlobal}
 * to this type (the mixin package is classloader-excluded, so it cannot be named here).
 */
public interface RenderGlobalShadowAccess {

    List<?> schmaloogium$renderInfos();

    void schmaloogium$setRenderInfos(List<?> renderInfos);

    boolean schmaloogium$displayListEntitiesDirty();

    void schmaloogium$setDisplayListEntitiesDirty(boolean dirty);

    double schmaloogium$lastViewEntityX();

    void schmaloogium$setLastViewEntityX(double value);

    double schmaloogium$lastViewEntityY();

    void schmaloogium$setLastViewEntityY(double value);

    double schmaloogium$lastViewEntityZ();

    void schmaloogium$setLastViewEntityZ(double value);

    double schmaloogium$lastViewEntityPitch();

    void schmaloogium$setLastViewEntityPitch(double value);

    double schmaloogium$lastViewEntityYaw();

    void schmaloogium$setLastViewEntityYaw(double value);

    ClippingHelper schmaloogium$debugFixedClippingHelper();

    void schmaloogium$setDebugFixedClippingHelper(ClippingHelper helper);

    boolean schmaloogium$debugFixTerrainFrustum();

    void schmaloogium$setDebugFixTerrainFrustum(boolean value);

    ViewFrustum schmaloogium$viewFrustum();

    Set<RenderChunk> schmaloogium$chunksToUpdate();

    double schmaloogium$prevRenderSortX();

    void schmaloogium$setPrevRenderSortX(double value);

    double schmaloogium$prevRenderSortY();

    void schmaloogium$setPrevRenderSortY(double value);

    double schmaloogium$prevRenderSortZ();

    void schmaloogium$setPrevRenderSortZ(double value);

    int schmaloogium$renderEntitiesStartupCounter();

    void schmaloogium$setRenderEntitiesStartupCounter(int value);

    int schmaloogium$countEntitiesTotal();

    void schmaloogium$setCountEntitiesTotal(int value);

    int schmaloogium$countEntitiesRendered();

    void schmaloogium$setCountEntitiesRendered(int value);

    int schmaloogium$countEntitiesHidden();

    void schmaloogium$setCountEntitiesHidden(int value);

    void schmaloogium$invokeSetDisplayListEntitiesDirty();

    int schmaloogium$invokeRenderBlockLayer(BlockRenderLayer layer, double partialTicks, int pass,
            Entity entity);

    void schmaloogium$invokeRenderClouds(float partialTicks, int pass, double x, double y, double z);

    void schmaloogium$invokeRenderEntities(Entity viewEntity,
            net.minecraft.client.renderer.culling.ICamera camera, float partialTicks);
}
