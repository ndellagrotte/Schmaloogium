// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.shadow;

import com.schmaloogium.engine.shadow.ShadowCameraProjection;
import com.schmaloogium.engine.shadow.ShadowDrawResult;
import com.schmaloogium.engine.shadow.ShadowTraversalChunkCursor;
import com.schmaloogium.engine.shadow.ShadowTraversalPlan;
import com.schmaloogium.engine.shadow.ShadowWorldPort;
import com.schmaloogium.engine.shadow.ShadowWorldSample;
import com.schmaloogium.engine.uniforms.Float3;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;

/**
 * The Minecraft implementation of the engine world seam (PHASE_8_DOC D-P8-9). Every
 * vanilla traversal/state detail — second setup, list restoration, entity/TE pass
 * interop, sort-cache restoration — lives here or in the shadow hooks; the engine side
 * sees only typed results. The §4.7 exact binding procedure (H8-TRAVERSE-01 redirects,
 * rebuild-entry assertion, pending-set union) activates with the shadow hook ledger;
 * until P7 wires the real slot this port reports typed rejections for setup, keeping
 * every consumer on the documented suppression paths.
 */
public final class McShadowWorldPort implements ShadowWorldPort {

    private static final String SETUP_NOT_WIRED =
            "schmaloogium.shadow.setup.not_wired";

    /** Monotonic per-shadow-setup frame count fed to vanilla's renderInfos cache. */
    private static long shadowFrameCount;

    @Override
    public ShadowWorldSample sample(com.schmaloogium.engine.frame.ShadowFrameView frame) {
        return McShadowWorldState.sample(frame);
    }

    @Override
    public ShadowStateResult openState(ShadowCameraProjection camera, ShadowWorldSample sample,
            com.schmaloogium.engine.buffers.Extent2i shadowExtent) {
        try {
            return new ShadowStateResult.Opened(
                    McShadowStateLease.open(shadowExtent.width(), shadowExtent.height()));
        } catch (RuntimeException e) {
            return new ShadowStateResult.Failed(Failure.STATE_CAPTURE,
                    "schmaloogium.shadow.fail.state_capture");
        }
    }

    @Override
    public ShadowDrawResult setupTerrain(ShadowTraversalView traversal) {
        Minecraft mc = Minecraft.getMinecraft();
        RenderGlobal renderGlobal = mc.renderGlobal;
        Entity viewEntity = mc.getRenderViewEntity();
        if (renderGlobal == null || viewEntity == null) {
            return new ShadowDrawResult.Rejected(Failure.TERRAIN_SETUP);
        }
        if (!(traversal.plan() instanceof ShadowTraversalPlan.SunAlignedPrism)
                && !(traversal.plan() instanceof ShadowTraversalPlan.FullLoadedView)) {
            return new ShadowDrawResult.Rejected(Failure.TERRAIN_SETUP);
        }
        // The full §4.7 binding (forced rebuild, H8-TRAVERSE-01 identity redirects,
        // rebuild-entry assertion and main-list restoration) requires the shadow hook
        // ledger on the live RenderGlobal path; until P7's slot wiring lands, report a
        // typed rejection so the transaction takes its documented suppression path.
        return new ShadowDrawResult.Failed(Failure.TERRAIN_SETUP, SETUP_NOT_WIRED);
    }

    @Override
    public ShadowDrawResult drawTerrain(ShadowTerrainBand band, ShadowTraversalView traversal) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity viewEntity = mc.getRenderViewEntity();
        if (viewEntity == null) {
            return new ShadowDrawResult.Rejected(Failure.DRAW);
        }
        BlockRenderLayer layer = layerOf(band);
        if (layer == null) {
            return new ShadowDrawResult.Rejected(Failure.DRAW);
        }
        try {
            mc.renderGlobal.renderBlockLayer(layer, mc.getRenderPartialTicks(), 0, viewEntity);
            return new ShadowDrawResult.Succeeded();
        } catch (RuntimeException e) {
            return new ShadowDrawResult.Failed(Failure.DRAW,
                    "schmaloogium.shadow.fail.draw_terrain");
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
            return new ShadowDrawResult.Failed(Failure.DRAW,
                    "schmaloogium.shadow.fail.draw_clouds");
        }
    }

    @Override
    public ShadowDrawResult drawEntities(ShadowEntityPass pass, ShadowTraversalView traversal) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity viewEntity = mc.getRenderViewEntity();
        if (viewEntity == null) {
            return new ShadowDrawResult.Rejected(Failure.DRAW);
        }
        try {
            mc.renderGlobal.renderEntities(viewEntity,
                    new McShadowFrustum(traversal.frustum()), mc.getRenderPartialTicks());
            return new ShadowDrawResult.Succeeded();
        } catch (RuntimeException e) {
            return new ShadowDrawResult.Failed(Failure.DRAW,
                    "schmaloogium.shadow.fail.draw_entities");
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

    /** Next shadow frame count for vanilla's cache keys; monotonic and shadow-local. */
    public static int nextShadowFrameCount() {
        return (int) (++shadowFrameCount & 0x7fffffffL);
    }

    /** Chunk cursor factory over the loaded section bounds for prism plans. */
    public static ShadowTraversalChunkCursor cursor(ShadowTraversalPlan.SunAlignedPrism plan,
            Float3 cameraPosition, ShadowWorldSample sample) {
        return ShadowTraversalChunkCursor.create(plan, cameraPosition,
                sample.worldMinSection(), sample.worldMaxSection());
    }
}
