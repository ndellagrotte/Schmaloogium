// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.frame;

import com.schmaloogium.engine.buffers.Extent2i;
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
import com.schmaloogium.engine.gl.GLError;
import com.schmaloogium.engine.gl.StateAspect;
import com.schmaloogium.engine.registry.StageId;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * The {@link FrameRenderPort} over the LWJGL3 device (PHASE_7_DOC §2.2, §4.6): the single
 * vanilla-visible state boundary of the frame driver. Engine framebuffers bind through the
 * Phase 1 facade; the {@code Screen} target is Minecraft's own main framebuffer (never GL
 * name 0). A fullscreen draw sets identity projection/model-view (composite programs use
 * {@code ftransform()}), a no-depth/no-fog/no-lighting posture, unit 0 texturing for the
 * fixed-function {@code final} passthrough, the exact scaled viewport, and restores
 * everything it touched in {@code finally}. Blend/alpha go through the facade so a Phase 4
 * activation lock keeps its lease (the facade write is a no-op under the lock); they are
 * restored by the port only for the fixed-function case, where no lock is held.
 */
public final class DeviceRenderPort implements FrameRenderPort {

    private static final Logger LOG = LogManager.getLogger("schmaloogium.frame.port");

    private final GLDevice device;
    private final Supplier<Extent2i> targetExtent;
    private long screenDrawLoggedForInstall = -1;

    public DeviceRenderPort(GLDevice device, Supplier<Extent2i> targetExtent) {
        this.device = Objects.requireNonNull(device, "device");
        this.targetExtent = Objects.requireNonNull(targetExtent, "targetExtent");
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
            Extent2i extent = targetExtent.get();
            switch (target) {
                case PassDrawTarget.EngineFramebuffer engine ->
                        device.framebuffers().bind(FramebufferTarget.DRAW, engine.framebuffer());
                case PassDrawTarget.Screen screen -> {
                    // 1.12.2 presents through Minecraft's own main framebuffer, not FBO 0
                    // (PHASE_5_DOC §1.3): the "screen" target is that framebuffer.
                    Minecraft mc = Minecraft.getMinecraft();
                    if (mc != null && mc.getFramebuffer() != null) {
                        mc.getFramebuffer().bindFramebuffer(false);
                    } else {
                        device.framebuffers().bindDefault(FramebufferTarget.DRAW);
                    }
                    applyAnaglyphMask(mc, eye);
                }
            }
            // The main estate is sized to the display at v0.1, so both targets share it.
            device.state().viewport(0, 0, Math.max(1, extent.width()), Math.max(1, extent.height()));
            return new PortResult.Completed();
        } catch (IllegalArgumentException e) {
            return new PortResult.Rejected(PortRejection.STALE_TARGET);
        } catch (RuntimeException e) {
            return new PortResult.Failed(new FailureId("schmaloogium.frame.port.bind"));
        }
    }

    /**
     * Vanilla's anaglyph colour masks per eye (EntityRenderer.renderWorld: pass 0 masks
     * red off, pass 1 masks green/blue off). Off at v0.1: mono passes carry LEFT and the
     * option is false, so this never writes.
     */
    private static void applyAnaglyphMask(Minecraft mc, AnaglyphEye eye) {
        if (mc == null || mc.gameSettings == null || !mc.gameSettings.anaglyph) {
            return;
        }
        if (eye == AnaglyphEye.LEFT) {
            GlStateManager.colorMask(false, true, true, false);
        } else {
            GlStateManager.colorMask(true, false, false, false);
        }
    }

    @Override
    public PortResult drawFullscreen(FullscreenDraw draw) {
        Objects.requireNonNull(draw, "draw");
        boolean fixedFunction = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM) == 0;
        // Facade snapshot: viewport/depth/fog always; blend/alpha only when no Phase 4
        // lock can be held (fixed function) - the restore path writes past the lock.
        EnumSet<StateAspect> aspects = EnumSet.of(StateAspect.VIEWPORT, StateAspect.DEPTH_TEST,
                StateAspect.DEPTH_MASK, StateAspect.FOG);
        if (fixedFunction) {
            aspects.add(StateAspect.BLEND);
            aspects.add(StateAspect.ALPHA_TEST);
        }
        com.schmaloogium.engine.gl.StateSnapshot facade;
        try {
            facade = device.state().snapshot(aspects);
        } catch (RuntimeException e) {
            return new PortResult.Failed(new FailureId("schmaloogium.frame.port.draw:snapshot"));
        }
        // Native reads for the enables the facade does not track (reads never stale the
        // GlStateManager cache; every write below goes through it).
        int activeUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        boolean lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
        boolean cull = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
        boolean texture0 = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
        boolean texture1 = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
        float[] color = new float[16];
        GL11.glGetFloatv(GL11.GL_CURRENT_COLOR, color);
        // Vanilla leaves unit 0 in whatever texture-environment mode its last draw used
        // (BLEND/COMBINE for lightmap and glint work would invert or darken the quad); the
        // passthrough and every composite draw take the texel as-is.
        int texEnvMode = GL11.glGetTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE);
        boolean matricesPushed = false;
        try {
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            matricesPushed = true;

            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.disableFog();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            device.state().blend(null);
            device.state().alphaTest(null);
            // Unit 1 (the lightmap) must not modulate the passthrough; unit 0 samples.
            GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
            GlStateManager.disableTexture2D();
            GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
            GlStateManager.enableTexture2D();
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_REPLACE);
            GlStateManager.color(1f, 1f, 1f, 1f);

            int[] viewport = FullscreenViewport.compute(targetExtent.get(), draw.viewport());
            device.state().viewport(viewport[0], viewport[1], viewport[2], viewport[3]);

            logDrawIntrospection(draw, fixedFunction);
            device.draw().fullscreenQuad();
            List<GLError> errors = device.drainErrors();
            if (!errors.isEmpty()) {
                return new PortResult.Failed(new FailureId(
                        "schmaloogium.frame.port.draw:" + errors.get(0).detail()));
            }
            logFirstScreenDraw(draw, fixedFunction, viewport);
            return new PortResult.Completed();
        } catch (IllegalArgumentException e) {
            return new PortResult.Rejected(PortRejection.UNSUPPORTED);
        } catch (RuntimeException e) {
            return new PortResult.Failed(new FailureId("schmaloogium.frame.port.draw"));
        } finally {
            if (matricesPushed) {
                GlStateManager.matrixMode(GL11.GL_PROJECTION);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                GlStateManager.popMatrix();
            }
            device.state().restore(facade);
            // Two-step pokes: the GlStateManager cache may hold the value we set above.
            GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
            pokeTexture2D(texture1);
            GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
            pokeTexture2D(texture0);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, texEnvMode);
            if (lighting) {
                GlStateManager.enableLighting();
            } else {
                GlStateManager.disableLighting();
            }
            if (cull) {
                GlStateManager.enableCull();
            } else {
                GlStateManager.disableCull();
            }
            GlStateManager.color(color[0], color[1], color[2], color[3]);
            GlStateManager.setActiveTexture(activeUnit);
        }
    }

    private static void pokeTexture2D(boolean enabled) {
        if (enabled) {
            GlStateManager.disableTexture2D();
            GlStateManager.enableTexture2D();
        } else {
            GlStateManager.enableTexture2D();
            GlStateManager.disableTexture2D();
        }
    }

    private final java.util.Set<String> introspectionLogged = new java.util.HashSet<>();

    /** H-PORT-DRAW evidence (one line per slot per install): the real GL objects at draw time. */
    private void logDrawIntrospection(FullscreenDraw draw, boolean fixedFunction) {
        String key = FrameHooks.installEpoch() + ":" + draw.pass().slot().packName();
        if (!introspectionLogged.add(key)) {
            return;
        }
        int unit0 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        int drawFbo = GL11.glGetInteger(org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        int attachment0 = drawFbo == 0 ? -1 : org.lwjgl.opengl.GL30.glGetFramebufferAttachmentParameteri(
                org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER, org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0,
                org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME);
        int program = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        LOG.info("H-PORT-DRAW {} (install #{}): unit0 texture {} drawFbo {} attachment0 {} program {} blend {} fixedFunction {}",
                draw.pass().slot().packName(), FrameHooks.installEpoch(), unit0, drawFbo, attachment0, program,
                GL11.glIsEnabled(GL11.GL_BLEND), fixedFunction);
    }

    /** H-FRAME-08 evidence: the first final draw into Minecraft's framebuffer per install. */
    private void logFirstScreenDraw(FullscreenDraw draw, boolean fixedFunction, int[] viewport) {
        if (draw.pass().step().stage() != StageId.FINAL) {
            return;
        }
        long install = FrameHooks.installEpoch();
        if (screenDrawLoggedForInstall == install) {
            return;
        }
        screenDrawLoggedForInstall = install;
        LOG.info("H-FRAME-08 final pass drawn to Minecraft framebuffer (install #{}): viewport {}x{} at {},{} program {}",
                install, viewport[2], viewport[3], viewport[0], viewport[1],
                fixedFunction ? "fixed-function passthrough" : "shader");
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
