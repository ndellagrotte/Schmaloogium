// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.shadow;

import com.schmaloogium.engine.buffers.Extent2i;
import com.schmaloogium.engine.config.CloudMode;
import com.schmaloogium.engine.frame.ShadowFrameView;
import com.schmaloogium.engine.shadow.ShadowWorldPort;
import com.schmaloogium.engine.shadow.ShadowWorldSample;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

/**
 * The Minecraft world reader behind §4.3 (PHASE_8_DOC D-P8-9): reads exactly the
 * quantities the seam names — camera presence, vanilla view distance, loaded section
 * bounds, cloud mode — and nothing else. No policy, no caching.
 */
public final class McShadowWorldState {

    private McShadowWorldState() {
    }

    /** Reads the current world snapshot for the given engine shadow frame. */
    public static ShadowWorldSample sample(ShadowFrameView frame) {
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = mc.world;
        boolean cameraPresent = world != null && mc.getRenderViewEntity() != null;
        int viewDistance = mc.gameSettings.renderDistanceChunks;
        int minSection = 0;
        int maxSection = world == null ? 0 : Math.min(15, (world.getHeight() - 1) >> 4);
        return new ShadowWorldSample(frame, cameraPresent, viewDistance,
                minSection, maxSection, cloudMode(mc));
    }

    private static CloudMode cloudMode(Minecraft mc) {
        // gameSettings.clouds: 0 = off, 1 = fast, 2 = fancy.
        switch (mc.gameSettings.clouds) {
            case 0:
                return CloudMode.OFF;
            case 1:
                return CloudMode.FAST;
            default:
                return CloudMode.FANCY;
        }
    }

    /** Current display extent, for viewport capture checks. */
    public static Extent2i displayExtent() {
        Minecraft mc = Minecraft.getMinecraft();
        return new Extent2i(mc.displayWidth, mc.displayHeight);
    }
}
