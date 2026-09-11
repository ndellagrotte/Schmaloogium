// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.uniforms;

import com.schmaloogium.engine.buffers.MainDepthSnapshot;
import com.schmaloogium.engine.gl.FramebufferDrawSlot;
import com.schmaloogium.engine.gl.FramebufferHandle;
import com.schmaloogium.engine.gl.FramebufferStatus;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.log.Logs;
import com.schmaloogium.engine.uniforms.spi.CenterDepthRequest;
import com.schmaloogium.engine.uniforms.spi.CenterDepthResult;
import com.schmaloogium.engine.uniforms.spi.CenterDepthSource;
import com.schmaloogium.mod.glue.frame.DepthTex0Bridge;

import java.util.List;
import java.util.Objects;

/**
 * The synchronous center-depth read (PHASE_6_DOC §4.2, D-P6-1) over the borrowed vanilla
 * main depth: one engine-owned, depth-only read framebuffer whose attachment follows the
 * depthtex0 bridge's version, read through {@code FramebufferService.readDepthPixel}. Any
 * failure answers the truthful {@link CenterDepthResult.Unavailable}; the runtime treats
 * that as no sample this frame, never as an error.
 */
public final class McCenterDepthSource implements CenterDepthSource {

    static final String UNAVAILABLE = "schmaloogium.uniforms.centerdepth.unavailable";

    private final GLDevice device;
    private FramebufferHandle framebuffer;
    private long attachedVersion = Long.MIN_VALUE;
    private boolean complete;

    public McCenterDepthSource(GLDevice device) {
        this.device = Objects.requireNonNull(device, "device");
    }

    @Override
    public CenterDepthResult readCenter(CenterDepthRequest request) {
        try {
            if (!(DepthTex0Bridge.get().current() instanceof MainDepthSnapshot.Available depth)) {
                return new CenterDepthResult.Unavailable(UNAVAILABLE);
            }
            if (framebuffer == null) {
                framebuffer = device.framebuffers().create("schmaloogium:centerdepth");
            }
            if (depth.version() != attachedVersion) {
                device.framebuffers().attachDepth(framebuffer, depth.texture());
                device.framebuffers().drawBuffers(framebuffer, List.of(new FramebufferDrawSlot.None()));
                FramebufferStatus status = device.framebuffers().check(framebuffer);
                complete = status == FramebufferStatus.COMPLETE;
                attachedVersion = depth.version();
                if (!complete) {
                    Logs.channel(LogChannels.UNIFORMS).warn(
                            "center-depth read framebuffer incomplete: {}", status);
                }
            }
            if (!complete) {
                return new CenterDepthResult.Unavailable(UNAVAILABLE);
            }
            float value = device.framebuffers().readDepthPixel(framebuffer, request.pixelX(),
                    request.pixelY());
            if (!Float.isFinite(value) || value < 0f || value > 1f) {
                return new CenterDepthResult.Unavailable(UNAVAILABLE);
            }
            return new CenterDepthResult.Sample(value);
        } catch (RuntimeException e) {
            return new CenterDepthResult.Unavailable(UNAVAILABLE);
        }
    }
}
