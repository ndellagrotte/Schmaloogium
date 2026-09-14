// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.shadow;

import com.schmaloogium.engine.shadow.ShadowCameraProjection;
import com.schmaloogium.engine.shadow.ShadowDrawResult;
import com.schmaloogium.engine.shadow.ShadowFrustum;
import com.schmaloogium.engine.shadow.ShadowPolicy;
import com.schmaloogium.engine.shadow.ShadowTraversalChunkCursor;
import com.schmaloogium.engine.shadow.ShadowTraversalPlan;
import com.schmaloogium.engine.shadow.ShadowWorldPort;
import com.schmaloogium.engine.shadow.ShadowWorldSample;
import com.schmaloogium.engine.uniforms.Float3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

/**
 * The Minecraft implementation of the engine world seam (PHASE_8_DOC D-P8-9, §4.4, §4.7,
 * §4.8). Every vanilla traversal/state detail — the forced second setup under the
 * H8-TRAVERSE-01 guard, main-list restoration, the Forge render-pass/counter interop of the
 * entity passes and the translucent sort-cache restoration — lives here; the engine slot sees
 * only typed results.
 */
public final class McShadowWorldPort implements ShadowWorldPort {

    private static final com.schmaloogium.engine.log.Log LOG =
            com.schmaloogium.engine.log.Logs.channel(com.schmaloogium.engine.log.LogChannels.SHADOW);

    private final ShadowPolicy.ShadowContent content;
    private McShadowInvocationState current;
    private ShadowWorldSample currentSample;
    private Object mainWorld;
    private Entity mainViewEntity;
    private int mainRenderDistance;
    private static final boolean PROBE = Boolean.getBoolean("schmaloogium.debug.probeBuffers");
    private static int solidDraws;
    private static volatile boolean setupLogged;
    private static volatile boolean entitiesLogged;

    public McShadowWorldPort(ShadowPolicy.ShadowContent content) {
        this.content = java.util.Objects.requireNonNull(content, "content");
    }

    @Override
    public ShadowWorldSample sample(com.schmaloogium.engine.frame.ShadowFrameView frame) {
        return McShadowWorldState.sample(frame);
    }

    @Override
    public ShadowStateResult openState(ShadowCameraProjection camera, ShadowWorldSample sample,
            com.schmaloogium.engine.buffers.Extent2i shadowExtent) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.getRenderViewEntity() == null || mc.renderGlobal == null) {
            return new ShadowStateResult.Rejected(Failure.STATE_CAPTURE);
        }
        try {
            current = new McShadowInvocationState();
            currentSample = sample;
            mainWorld = mc.world;
            mainViewEntity = mc.getRenderViewEntity();
            mainRenderDistance = mc.gameSettings.renderDistanceChunks;
            return new ShadowStateResult.Opened(McShadowStateLease.open(camera,
                    shadowExtent.width(), shadowExtent.height(), current));
        } catch (RuntimeException e) {
            current = null;
            LOG.warn("shadow state capture threw {}", String.valueOf(e));
            return new ShadowStateResult.Failed(Failure.STATE_CAPTURE,
                    "schmaloogium.shadow.fail.state_capture");
        }
    }

    // ------------------------------------------------------------------ §4.7 forced setup

    @Override
    public ShadowDrawResult setupTerrain(ShadowTraversalView traversal) {
        Minecraft mc = Minecraft.getMinecraft();
        RenderGlobal renderGlobal = mc.renderGlobal;
        Entity viewEntity = mc.getRenderViewEntity();
        McShadowInvocationState state = current;
        if (renderGlobal == null || viewEntity == null || state == null) {
            return new ShadowDrawResult.Rejected(Failure.TERRAIN_SETUP);
        }
        // Step 1: the world, view entity and render distance are the main setup's.
        if (mc.world != mainWorld || viewEntity != mainViewEntity
                || mc.gameSettings.renderDistanceChunks != mainRenderDistance) {
            return new ShadowDrawResult.Rejected(Failure.TERRAIN_SETUP);
        }
        if (ShadowTraversalGuard.active()) {
            return new ShadowDrawResult.Rejected(Failure.TERRAIN_SETUP);
        }
        RenderGlobalShadowAccess access = (RenderGlobalShadowAccess) renderGlobal;
        Set<RenderChunk> allowed = allowedSet(traversal, access.schmaloogium$viewFrustum(),
                viewEntity, mc.getRenderPartialTicks(), currentSample);
        // Step 2: capture before any mutation.
        List<?> mainList = access.schmaloogium$renderInfos();
        boolean savedDirty = access.schmaloogium$displayListEntitiesDirty();
        double lastX = access.schmaloogium$lastViewEntityX();
        double lastY = access.schmaloogium$lastViewEntityY();
        double lastZ = access.schmaloogium$lastViewEntityZ();
        double lastPitch = access.schmaloogium$lastViewEntityPitch();
        double lastYaw = access.schmaloogium$lastViewEntityYaw();
        ClippingHelper savedHelper = access.schmaloogium$debugFixedClippingHelper();
        boolean savedFixFrustum = access.schmaloogium$debugFixTerrainFrustum();
        List<RenderChunk> pending = new ArrayList<>(access.schmaloogium$chunksToUpdate());
        state.captureMainList(renderGlobal, mainList);
        boolean threw = false;
        int visited = -1;
        try {
            // Step 3: the setup-only guard, the debug fields neutralized, the public forcing
            // setter, exactly one setupTerrain with the shadow frustum and the main token.
            access.schmaloogium$setDebugFixedClippingHelper(null);
            access.schmaloogium$setDebugFixTerrainFrustum(false);
            ShadowTraversalGuard.enter(renderGlobal, allowed);
            access.schmaloogium$invokeSetDisplayListEntitiesDirty();
            McShadowFrustum frustum = new McShadowFrustum(traversal.frustum());
            double partial = mc.getRenderPartialTicks();
            frustum.setPosition(
                    viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partial,
                    viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partial,
                    viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partial);
            renderGlobal.setupTerrain(viewEntity, partial, frustum,
                    com.schmaloogium.mod.glue.frame.FrameHooks.reservedTerrainToken(),
                    mc.player != null && mc.player.isSpectator());
            visited = access.schmaloogium$renderInfos().size();
        } catch (RuntimeException e) {
            threw = true;
            LOG.warn("shadow setupTerrain threw {}", String.valueOf(e));
        } finally {
            // Step 5: disarm, restore the view cache and debug fields, union the pending set,
            // recompute dirty conservatively.
            int entries = ShadowTraversalGuard.rebuildEntries();
            ShadowTraversalGuard.exit();
            access.schmaloogium$setLastViewEntityX(lastX);
            access.schmaloogium$setLastViewEntityY(lastY);
            access.schmaloogium$setLastViewEntityZ(lastZ);
            access.schmaloogium$setLastViewEntityPitch(lastPitch);
            access.schmaloogium$setLastViewEntityYaw(lastYaw);
            access.schmaloogium$setDebugFixedClippingHelper(savedHelper);
            access.schmaloogium$setDebugFixTerrainFrustum(savedFixFrustum);
            Set<RenderChunk> currentPending = access.schmaloogium$chunksToUpdate();
            currentPending.addAll(pending);
            boolean postSetupDirty = access.schmaloogium$displayListEntitiesDirty();
            access.schmaloogium$setDisplayListEntitiesDirty(
                    savedDirty || postSetupDirty || !currentPending.isEmpty() || threw);
            if (threw || entries != 1) {
                // Step 6: a failed setup restores the main list immediately.
                state.restoreMainList();
                if (!threw) {
                    LOG.warn("shadow setupTerrain: rebuild entries {} != 1", entries);
                }
            } else if (!setupLogged) {
                setupLogged = true;
                LOG.info("H8-TERRAIN-01 first shadow setup: plan={} visited={} rebuildEntries={} allowed={}",
                        traversal.plan().getClass().getSimpleName(), visited, entries,
                        allowed == null ? "all" : String.valueOf(allowed.size()));
            }
            if (!threw && entries != 1) {
                threw = false;
            }
        }
        if (threw) {
            return new ShadowDrawResult.Failed(Failure.TERRAIN_SETUP,
                    "schmaloogium.shadow.fail.terrain_setup.threw");
        }
        if (!state.hasMainList()) {
            return new ShadowDrawResult.Failed(Failure.REBUILD_ASSERTION,
                    "schmaloogium.shadow.fail.terrain_setup.rebuild_entry");
        }
        return new ShadowDrawResult.Succeeded();
    }

    /** §4.7: the prism's chunks mapped through the view frustum, position-verified, deduplicated. */
    private static Set<RenderChunk> allowedSet(ShadowTraversalView traversal, ViewFrustum viewFrustum,
            Entity viewEntity, float partialTicks, ShadowWorldSample sample) {
        if (!(traversal.plan() instanceof ShadowTraversalPlan.SunAlignedPrism prism)) {
            return null;
        }
        Set<RenderChunk> allowed = Collections.newSetFromMap(new IdentityHashMap<>());
        double camX = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
        double camY = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
        double camZ = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;
        ShadowTraversalChunkCursor cursor = ShadowTraversalChunkCursor.create(prism,
                new Float3((float) camX, (float) camY, (float) camZ),
                sample.worldMinSection(), sample.worldMaxSection());
        McShadowFrustum frustum = new McShadowFrustum(traversal.frustum());
        frustum.setPosition(camX, camY, camZ);
        int[] coord = new int[3];
        while (cursor.advance(coord)) {
            BlockPos origin = new BlockPos(coord[0] << 4, coord[1] << 4, coord[2] << 4);
            RenderChunk chunk = ((ViewFrustumAccess) viewFrustum).schmaloogium$renderChunk(origin);
            if (chunk == null || !chunk.getPosition().equals(origin)) {
                continue;
            }
            if (frustum.isBoundingBoxInFrustum(chunk.boundingBox)) {
                allowed.add(chunk);
            }
        }
        return allowed;
    }

    /** The game-side face of the view-frustum accessor (mixin.frame.shadow). */
    public interface ViewFrustumAccess {
        RenderChunk schmaloogium$renderChunk(BlockPos pos);
    }

    // ------------------------------------------------------------------ §4.8 draws

    @Override
    public ShadowDrawResult drawTerrain(ShadowTerrainBand band, ShadowTraversalView traversal) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity viewEntity = mc.getRenderViewEntity();
        if (viewEntity == null || current == null || !current.hasMainList()) {
            return new ShadowDrawResult.Rejected(Failure.DRAW);
        }
        BlockRenderLayer layer = layerOf(band);
        if (layer == null) {
            return new ShadowDrawResult.Rejected(Failure.DRAW);
        }
        RenderGlobalShadowAccess access = (RenderGlobalShadowAccess) mc.renderGlobal;
        boolean translucent = layer == BlockRenderLayer.TRANSLUCENT;
        double sortX = 0, sortY = 0, sortZ = 0;
        if (translucent) {
            // §4.8.4: the three sort-cache doubles are rolled back bit-exactly.
            sortX = access.schmaloogium$prevRenderSortX();
            sortY = access.schmaloogium$prevRenderSortY();
            sortZ = access.schmaloogium$prevRenderSortZ();
        }
        try {
            boolean shot = PROBE && band == ShadowTerrainBand.SOLID && (++solidDraws == 1 || solidDraws == 300);
            if (shot) {
                LOG.info("H8-PROBE draw #{} {}: {} chunks in list, {} {}", solidDraws, band,
                        access.schmaloogium$renderInfos().size(), ShadowGlProbe.state(), ShadowGlProbe.attachments());
            }
            int drawn = mc.renderGlobal.renderBlockLayer(layer, mc.getRenderPartialTicks(), 0, viewEntity);
            if (shot) {
                LOG.info("H8-PROBE draw #{} {} returned {} (vanilla's rendered-chunk count); after: {}",
                        solidDraws, band, drawn, ShadowGlProbe.depthStats());
            }
            return new ShadowDrawResult.Succeeded();
        } catch (RuntimeException e) {
            LOG.warn("shadow terrain {} threw {}", band, String.valueOf(e));
            return new ShadowDrawResult.Failed(Failure.DRAW, "schmaloogium.shadow.fail.draw_terrain");
        } finally {
            if (translucent) {
                access.schmaloogium$setPrevRenderSortX(sortX);
                access.schmaloogium$setPrevRenderSortY(sortY);
                access.schmaloogium$setPrevRenderSortZ(sortZ);
            }
        }
    }

    @Override
    public ShadowDrawResult drawClouds(ShadowTraversalView traversal) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity viewEntity = mc.getRenderViewEntity();
        if (viewEntity == null) {
            return new ShadowDrawResult.Rejected(Failure.DRAW);
        }
        try {
            mc.renderGlobal.renderClouds(mc.getRenderPartialTicks(), 0,
                    viewEntity.posX, viewEntity.posY, viewEntity.posZ);
            return new ShadowDrawResult.Succeeded();
        } catch (RuntimeException e) {
            return new ShadowDrawResult.Failed(Failure.DRAW, "schmaloogium.shadow.fail.draw_clouds");
        }
    }

    @Override
    public ShadowDrawResult drawEntities(ShadowEntityPass pass, ShadowTraversalView traversal) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity viewEntity = mc.getRenderViewEntity();
        if (viewEntity == null || current == null || !current.hasMainList()) {
            return new ShadowDrawResult.Rejected(Failure.DRAW);
        }
        RenderGlobalShadowAccess access = (RenderGlobalShadowAccess) mc.renderGlobal;
        // §4.8.2 step 1/2: the startup counter belongs to the main traversal.
        int startup = access.schmaloogium$renderEntitiesStartupCounter();
        if (startup > 0) {
            return new ShadowDrawResult.Succeeded();
        }
        int savedPass = MinecraftForgeClient.getRenderPass();
        int total = access.schmaloogium$countEntitiesTotal();
        int rendered = access.schmaloogium$countEntitiesRendered();
        int hidden = access.schmaloogium$countEntitiesHidden();
        try {
            ForgeHooksClient.setRenderPass(pass == ShadowEntityPass.OPAQUE_ZERO ? 0 : 1);
            McShadowFrustum frustum = new McShadowFrustum(traversal.frustum());
            double partial = mc.getRenderPartialTicks();
            frustum.setPosition(
                    viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partial,
                    viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partial,
                    viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partial);
            ShadowTraversalGuard.enterEntities(content.blockEntities(), content.player());
            // H9 shadow admission (PHASE_9_DOC §4.12): entity/TE ids under the authenticated
            // shadow execution; nested exits restore before the admission releases.
            var admission = com.schmaloogium.mod.glue.id.IdHooks.openShadowAdmission(
                    com.schmaloogium.mod.glue.frame.FrameHooks.reservedTerrainToken());
            try {
                mc.renderGlobal.renderEntities(viewEntity, frustum, mc.getRenderPartialTicks());
            } finally {
                com.schmaloogium.mod.glue.id.IdHooks.closeAdmission(admission);
                ShadowTraversalGuard.exitEntities();
            }
            if (!entitiesLogged) {
                entitiesLogged = true;
                LOG.info("H8-ENTITY-01 first shadow entity pass {}: rendered {} of {}", pass,
                        access.schmaloogium$countEntitiesRendered(), access.schmaloogium$countEntitiesTotal());
            }
            return new ShadowDrawResult.Succeeded();
        } catch (RuntimeException e) {
            LOG.warn("shadow entities {} threw {}", pass, String.valueOf(e));
            return new ShadowDrawResult.Failed(Failure.DRAW, "schmaloogium.shadow.fail.draw_entities");
        } finally {
            ForgeHooksClient.setRenderPass(savedPass);
            access.schmaloogium$setRenderEntitiesStartupCounter(startup);
            access.schmaloogium$setCountEntitiesTotal(total);
            access.schmaloogium$setCountEntitiesRendered(rendered);
            access.schmaloogium$setCountEntitiesHidden(hidden);
        }
    }

    private static BlockRenderLayer layerOf(ShadowTerrainBand band) {
        switch (band) {
            case SOLID:
                return BlockRenderLayer.SOLID;
            case CUTOUT_MIPPED:
                return BlockRenderLayer.CUTOUT_MIPPED;
            case CUTOUT:
                return BlockRenderLayer.CUTOUT;
            case TRANSLUCENT:
                return BlockRenderLayer.TRANSLUCENT;
            default:
                return null;
        }
    }

    /** Chunk cursor factory over the loaded section bounds for prism plans. */
    public static ShadowTraversalChunkCursor cursor(ShadowTraversalPlan.SunAlignedPrism plan,
            Float3 cameraPosition, ShadowWorldSample sample) {
        return ShadowTraversalChunkCursor.create(plan, cameraPosition,
                sample.worldMinSection(), sample.worldMaxSection());
    }

    /** The frustum adapter for tests and callers that position it themselves. */
    static McShadowFrustum frustumAt(ShadowFrustum frustum, double x, double y, double z) {
        McShadowFrustum adapter = new McShadowFrustum(frustum);
        adapter.setPosition(x, y, z);
        return adapter;
    }
}
