// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.frame;

import com.schmaloogium.engine.buffers.PassDrawTarget;
import com.schmaloogium.engine.frame.AnaglyphEye;
import com.schmaloogium.engine.frame.FailureId;
import com.schmaloogium.engine.frame.spi.FrameRenderPort;
import com.schmaloogium.engine.frame.spi.FullscreenDraw;
import com.schmaloogium.engine.frame.spi.PortRejection;
import com.schmaloogium.engine.frame.spi.PortResult;
import com.schmaloogium.engine.frame.spi.StateSnapshot;
import com.schmaloogium.engine.gl.FramebufferTarget;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.gl.StateAspect;

import java.util.EnumSet;
import java.util.Objects;

/**
 * The v0.1 {@link FrameRenderPort}: every verb routes through the engine's own device
 * facade, so no vanilla state is touched outside the Phase 1 tracker. Task B replaces the
 * draw semantics (viewport scaling, gbuffer scope routing, final blit); the snapshot,
 * normalize, bind and restore verbs are complete as written.
 */
public final class DeviceRenderPort implements FrameRenderPort {

    private final GLDevice device;

    public DeviceRenderPort(GLDevice device) {
        this.device = Objects.requireNonNull(device, "device");
    }

    private record Snapshot(com.schmaloogium.engine.gl.StateSnapshot inner) implements StateSnapshot {
    }

    @Override
    public StateSnapshot snapshotState() {
        return new Snapshot(device.state().snapshot(EnumSet.allOf(StateAspect.class)));
    }

    @Override
    public PortResult normalizeForEngine() {
        try {
            device.state().depthTest(true);
            device.state().depthMask(true);
            device.state().blend(null);
            device.state().alphaTest(null);
            return new PortResult.Completed();
        } catch (RuntimeException e) {
            return new PortResult.Failed(new FailureId("schmaloogium.frame.port.normalize"));
        }
    }

    @Override
    public PortResult bind(PassDrawTarget target, AnaglyphEye eye) {
        try {
            switch (target) {
                case PassDrawTarget.EngineFramebuffer engine ->
                        device.framebuffers().bind(FramebufferTarget.DRAW, engine.framebuffer());
                case PassDrawTarget.Screen screen -> {
                    // 1.12.2 presents through Minecraft's own main framebuffer, not FBO 0:
                    // the "screen" target is that framebuffer (a mod-side vanilla operation).
                    net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
                    if (mc != null && mc.getFramebuffer() != null) {
                        mc.getFramebuffer().bindFramebuffer(false);
                    } else {
                        device.framebuffers().bindDefault(FramebufferTarget.DRAW);
                    }
                }
            }
            return new PortResult.Completed();
        } catch (IllegalArgumentException e) {
            return new PortResult.Rejected(PortRejection.STALE_TARGET);
        } catch (RuntimeException e) {
            return new PortResult.Failed(new FailureId("schmaloogium.frame.port.bind"));
        }
    }

    @Override
    public PortResult drawFullscreen(FullscreenDraw draw) {
        try {
            // v0.1: the driver issues instance 0 of 1 at full viewport; Task B adds the
            // per-program viewport scale over the bound target's extent.
            device.draw().fullscreenQuad();
            return new PortResult.Completed();
        } catch (RuntimeException e) {
            return new PortResult.Failed(new FailureId("schmaloogium.frame.port.draw"));
        }
    }

    @Override
    public PortResult restore(StateSnapshot snapshot) {
        if (!(snapshot instanceof Snapshot minted)) {
            return new PortResult.Rejected(PortRejection.UNSUPPORTED);
        }
        try {
            device.state().restore(minted.inner());
            return new PortResult.Completed();
        } catch (RuntimeException e) {
            return new PortResult.Failed(new FailureId("schmaloogium.frame.port.restore"));
        }
    }
}
