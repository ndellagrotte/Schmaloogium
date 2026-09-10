// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.frame;

import com.schmaloogium.engine.buffers.Extent2i;
import com.schmaloogium.engine.buffers.MainDepthPreparation;
import com.schmaloogium.engine.frame.AnaglyphEye;
import com.schmaloogium.engine.pack.DimensionKey;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * The narrow Minecraft/LWJGL seam behind {@link FrameHooks} (PHASE_7_DOC §4.10.2 "dumb
 * bridge call" column). Each method reads exactly the vanilla quantity the catalog names
 * — world epoch, tick clock, dimension identity, terrain token, window extents, anaglyph
 * mode — and nothing else. Contains no policy and no caching beyond the matrix scratch.
 */
public final class McFrameState {

    private static final FloatBuffer MATRIX_SCRATCH = ByteBuffer.allocateDirect(16 * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer();

    private McFrameState() {
    }

    /** Monotonic per-world epoch: changes on world load (H-WORLD-01 resets the driver). */
    public static long worldEpoch() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        net.minecraft.client.multiplayer.WorldClient world = mc.world;
        return world == null ? 0L : world.getWorldTime();
    }

    /** The vanilla world tick counter backing previous/current-tick uniform pairs. */
    public static long logicalTick() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        net.minecraft.client.multiplayer.WorldClient world = mc.world;
        return world == null ? 0L : world.getTotalWorldTime();
    }

    /** Interpolation weight between logical ticks, in [0,1]. */
    public static double smoothingTimeTicks() {
        return net.minecraft.client.Minecraft.getMinecraft().getRenderPartialTicks();
    }

    /** The client dimension identity (engine-owned key; legacy id only). */
    public static DimensionKey dimension() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        net.minecraft.client.multiplayer.WorldClient world = mc.world;
        return world == null ? new DimensionKey(java.util.OptionalInt.empty())
                : new DimensionKey(java.util.OptionalInt.of(world.provider.getDimensionType().getId()));
    }

    /** Current drawing-buffer extent. */
    public static Extent2i targetView() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        return new Extent2i(mc.displayWidth, mc.displayHeight);
    }

    /** Previously completed frame's extent (flip source). */
    public static Extent2i priorCompletedFramebuffer() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        return new Extent2i(mc.displayWidth, mc.displayHeight);
    }

    /** 1.12.2 ships no stereo rendering; anaglyph is always mono here. */
    public static AnaglyphEye anaglyphEye() {
        return AnaglyphEye.LEFT;
    }

    /** P5's depth-input preparation: the borrowed vanilla main depth (depthtex0 bridge). */
    public static MainDepthPreparation mainDepthPreparation() {
        return DepthTex0Bridge.get().prepare(targetView());
    }

    /** Latest observed display extent, updated by the H-RESIZE hooks. */
    private static volatile Extent2i lastObserved = new Extent2i(0, 0);

    /** H-RESIZE-01/02 landing: record the newest extent for the next frame begin. */
    public static void noteExtentChanged() {
        lastObserved = targetView();
    }

    public static FloatBuffer matrixScratch() {
        MATRIX_SCRATCH.clear();
        return MATRIX_SCRATCH;
    }

    public static void getFloat(int glEnum, FloatBuffer buffer) {
        GL11.glGetFloatv(glEnum, buffer);
    }
}
