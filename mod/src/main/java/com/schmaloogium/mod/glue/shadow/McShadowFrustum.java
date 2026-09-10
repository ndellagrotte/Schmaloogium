// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.shadow;

import com.schmaloogium.engine.shadow.ShadowAabb;
import com.schmaloogium.engine.shadow.ShadowFrustum;

import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * The vanilla {@link ICamera} adapter over a synthesized {@link ShadowFrustum}
 * (PHASE_8_DOC §4.7): the engine frustum already operates in camera-relative world
 * coordinates, so the vanilla camera position is subtracted from tested bounds. NaN
 * never means outside; a culling-disabled frustum accepts every box.
 */
public final class McShadowFrustum implements ICamera {

    private final ShadowFrustum frustum;
    private double originX;
    private double originY;
    private double originZ;

    public McShadowFrustum(ShadowFrustum frustum) {
        this.frustum = frustum;
    }

    @Override
    public boolean isBoundingBoxInFrustum(AxisAlignedBB box) {
        ShadowAabb relative = new ShadowAabb(
                box.minX - originX, box.minY - originY, box.minZ - originZ,
                box.maxX - originX, box.maxY - originY, box.maxZ - originZ);
        return frustum.intersects(relative);
    }

    @Override
    public void setPosition(double x, double y, double z) {
        this.originX = x;
        this.originY = y;
        this.originZ = z;
    }
}
