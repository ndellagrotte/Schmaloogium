// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.frame;

import com.schmaloogium.engine.buffers.Extent2i;
import com.schmaloogium.engine.buffers.PassDrawTarget;
import com.schmaloogium.engine.frame.AnaglyphEye;
import com.schmaloogium.engine.frame.FailureId;
import com.schmaloogium.engine.frame.PipelineVersion;
import com.schmaloogium.engine.frame.spi.AtlasBindingEvidence;
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
import com.schmaloogium.mod.glue.textures.TextureBindingRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
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
    public AtlasBindingEvidence textureEvidence(PipelineVersion version, long epoch, boolean base) {
        return base ? TextureBindingRuntime.observeBase(version, epoch)
                : TextureBindingRuntime.observeNoBase(version, epoch);
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
                case PassDrawTarget.EngineFramebuffer engine -> {
                    device.framebuffers().bind(FramebufferTarget.DRAW, engine.framebuffer());
                    if (PROBE_BUFFERS) {
                        logBoundFramebuffer();
                    }
                }
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
                GlErrorLedger.record(errors, false);
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
        maybeLateProbe(draw);
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
        if (PROBE_BUFFERS) {
            LOG.info("H-PORT-PROBE {} (install #{}): {}", draw.pass().slot().packName(),
                    FrameHooks.installEpoch(), probeBoundUnits());
        }
    }

    /** Second probe shot per pass name, ~300 fullscreen draws later (a settled world). */
    private final java.util.Map<String, Integer> lateProbeCounts = new java.util.HashMap<>();

    private void maybeLateProbe(FullscreenDraw draw) {
        if (!PROBE_BUFFERS) {
            return;
        }
        String key = draw.pass().slot().packName();
        int n = lateProbeCounts.merge(key, 1, Integer::sum);
        if (n == 300) {
            LOG.info("H-PORT-PROBE-LATE {} (install #{}, draw {}): {}", key, FrameHooks.installEpoch(), n,
                    probeBoundUnits());
        }
    }

    private final java.util.Set<String> framebuffersLogged = new java.util.HashSet<>();

    /** H-PORT-BIND (probe only): the draw FBO's real colour/depth attachments and draw buffers. */
    private void logBoundFramebuffer() {
        int fbo = GL11.glGetInteger(org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        if (!framebuffersLogged.add(FrameHooks.installEpoch() + ":" + fbo)) {
            return;
        }
        StringBuilder colors = new StringBuilder();
        StringBuilder draws = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int type = org.lwjgl.opengl.GL30.glGetFramebufferAttachmentParameteri(
                    org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER, org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0 + i,
                    org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE);
            int name = type == GL11.GL_NONE ? 0 : org.lwjgl.opengl.GL30.glGetFramebufferAttachmentParameteri(
                    org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER, org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0 + i,
                    org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME);
            colors.append(i).append('=').append(name).append(' ');
            int drawBuffer = GL11.glGetInteger(org.lwjgl.opengl.GL20.GL_DRAW_BUFFER0 + i);
            draws.append(drawBuffer == GL11.GL_NONE ? "N" : String.valueOf(drawBuffer - org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0)).append(' ');
        }
        int depthType = org.lwjgl.opengl.GL30.glGetFramebufferAttachmentParameteri(
                org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER, org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT,
                org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE);
        int depthName = depthType == GL11.GL_NONE ? 0 : org.lwjgl.opengl.GL30.glGetFramebufferAttachmentParameteri(
                org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER, org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT,
                org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME);
        int status = org.lwjgl.opengl.GL30.glCheckFramebufferStatus(org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER);
        int[] vp = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, vp);
        LOG.info("H-PORT-BIND fbo {} (install #{}): colors[{}] depth(type {}) {} drawBuffers[{}] status {} viewport {}x{} depthTest {} depthMask {} colorMask {} cull {} alphaTest {} blend {} program {}",
                fbo, FrameHooks.installEpoch(), colors.toString().trim(), depthType, depthName, draws.toString().trim(),
                status, vp[2], vp[3], GL11.glIsEnabled(GL11.GL_DEPTH_TEST), GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK),
                GL11.glGetBoolean(GL11.GL_COLOR_WRITEMASK), GL11.glIsEnabled(GL11.GL_CULL_FACE),
                GL11.glIsEnabled(GL11.GL_ALPHA_TEST), GL11.glIsEnabled(GL11.GL_BLEND),
                GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM));
        while (GL11.glGetError() != GL11.GL_NO_ERROR) {
            // probe-only queries never charge the pass
        }
    }

    /** {@code -Dschmaloogium.debug.probeBuffers=true}: centre texel of every bound unit, one-shot. */
    private static final boolean PROBE_BUFFERS = Boolean.getBoolean("schmaloogium.debug.probeBuffers");

    /**
     * Debug evidence only: for each of the sixteen units, the bound 2D texture's name, size and
     * centre texel (RGBA, or depth when the texture is a depth format), read through a scratch
     * framebuffer. Raw GL with every touched binding restored; never on a hot path.
     */
    /** GlStateManager's cached view (active unit, per-unit 2D name) for divergence diagnosis. */
    private static String glStateManagerCache() {
        try {
            java.lang.reflect.Field active = net.minecraft.client.renderer.GlStateManager.class
                    .getDeclaredField("activeTextureUnit");
            active.setAccessible(true);
            java.lang.reflect.Field states = net.minecraft.client.renderer.GlStateManager.class
                    .getDeclaredField("textureState");
            states.setAccessible(true);
            Object[] units = (Object[]) states.get(null);
            StringBuilder sb = new StringBuilder("active=").append(active.getInt(null));
            for (int i = 0; i < units.length; i++) {
                java.lang.reflect.Field name = units[i].getClass().getDeclaredField("textureName");
                name.setAccessible(true);
                sb.append(' ').append(i).append('=').append(name.getInt(units[i]));
            }
            return sb.toString();
        } catch (ReflectiveOperationException | RuntimeException e) {
            return "unavailable(" + e.getClass().getSimpleName() + ")";
        }
    }

    /**
     * The storage facts a composite chain's auto-exposure depends on: the real internal
     * format (an unsized RGBA8 fallback where the pack asked for RGBA16 is the tell), the
     * highest level that actually has storage, and whether the min filter can resolve a
     * LOD at all. A {@code texture2DLod(..., 8)} against a LINEAR filter silently reads
     * level 0, which turns a scene-average exposure divisor into a per-pixel one.
     */
    private static String samplingDescription(int w, int h) {
        int internalFormat = org.lwjgl.opengl.GL11.glGetTexLevelParameteri(
                GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_INTERNAL_FORMAT);
        int maxLevel = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL);
        int minFilter = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER);
        int storedLevels = 0;
        for (int level = 0; level <= Math.min(maxLevel, 20); level++) {
            if (org.lwjgl.opengl.GL11.glGetTexLevelParameteri(
                    GL11.GL_TEXTURE_2D, level, GL11.GL_TEXTURE_WIDTH) <= 0) {
                break;
            }
            storedLevels++;
        }
        return String.format(java.util.Locale.ROOT,
                "fmt=0x%X maxLevel=%d storedLevels=%d minFilter=%s mipmapCapable=%b",
                internalFormat, maxLevel, storedLevels, minFilterName(minFilter),
                isMipmapFilter(minFilter));
    }

    private static boolean isMipmapFilter(int minFilter) {
        return minFilter == GL11.GL_NEAREST_MIPMAP_NEAREST
                || minFilter == GL11.GL_LINEAR_MIPMAP_NEAREST
                || minFilter == GL11.GL_NEAREST_MIPMAP_LINEAR
                || minFilter == GL11.GL_LINEAR_MIPMAP_LINEAR;
    }

    private static String minFilterName(int minFilter) {
        return switch (minFilter) {
            case GL11.GL_NEAREST -> "NEAREST";
            case GL11.GL_LINEAR -> "LINEAR";
            case GL11.GL_NEAREST_MIPMAP_NEAREST -> "NEAREST_MIPMAP_NEAREST";
            case GL11.GL_LINEAR_MIPMAP_NEAREST -> "LINEAR_MIPMAP_NEAREST";
            case GL11.GL_NEAREST_MIPMAP_LINEAR -> "NEAREST_MIPMAP_LINEAR";
            case GL11.GL_LINEAR_MIPMAP_LINEAR -> "LINEAR_MIPMAP_LINEAR";
            default -> "0x" + Integer.toHexString(minFilter);
        };
    }

    /**
     * The texel a classic auto-exposure fetch actually resolves. Packs compute
     * {@code avglod = log2(min(viewWidth, viewHeight))} and sample the 1x1 top of the
     * chain; reading that level directly says whether it holds a scene average or, when
     * no level was ever generated, whatever level 0 happens to contain.
     */
    private static String lodSample(int w, int h) {
        int avglod = (int) (Math.log(Math.max(1, Math.min(w, h))) / Math.log(2.0));
        int maxLevel = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL);
        int level = Math.min(avglod, Math.max(0, maxLevel));
        if (level == 0) {
            // The requested LOD collapsed to the base level: either no chain was ever
            // allocated or the filter cannot resolve one. The mean and centre already
            // describe level 0, so read nothing and say why the fetch lands there.
            return " lod" + avglod + "->0(no chain; reads base level)";
        }
        int lw = org.lwjgl.opengl.GL11.glGetTexLevelParameteri(
                GL11.GL_TEXTURE_2D, level, GL11.GL_TEXTURE_WIDTH);
        int lh = org.lwjgl.opengl.GL11.glGetTexLevelParameteri(
                GL11.GL_TEXTURE_2D, level, GL11.GL_TEXTURE_HEIGHT);
        if (lw <= 0 || lh <= 0) {
            return " lod" + level + "=(no storage)";
        }
        if (lw * lh > 4096) {
            return " lod" + level + "=(" + lw + "x" + lh + ", not read)";
        }
        java.nio.FloatBuffer lod = org.lwjgl.BufferUtils.createFloatBuffer(
                Math.max(4, lw * lh * 4));
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, level, GL11.GL_RGBA, GL11.GL_FLOAT, lod);
        int centre = ((lh / 2) * lw + lw / 2) * 4;
        return String.format(java.util.Locale.ROOT, " lod%d[%dx%d]=(%.6f,%.6f,%.6f,%.6f)",
                level, lw, lh, lod.get(centre), lod.get(centre + 1), lod.get(centre + 2),
                lod.get(centre + 3));
    }

    private static String probeBoundUnits() {
        int savedUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        int savedRead = GL11.glGetInteger(org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER_BINDING);
        int savedDraw = GL11.glGetInteger(org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        int scratch = org.lwjgl.opengl.GL30.glGenFramebuffers();
        StringBuilder out = new StringBuilder();
        out.append("glsm{").append(glStateManagerCache()).append("} ");
        try {
            org.lwjgl.opengl.GL30.glBindFramebuffer(org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER, scratch);
            for (int unit = 0; unit < 16; unit++) {
                GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
                int name = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
                if (name == 0) {
                    continue;
                }
                int w = org.lwjgl.opengl.GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
                int h = org.lwjgl.opengl.GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
                int depthBits = org.lwjgl.opengl.GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0,
                        org.lwjgl.opengl.GL30.GL_TEXTURE_DEPTH_SIZE);
                int texels = Math.max(1, w * h);
                java.nio.FloatBuffer px = org.lwjgl.BufferUtils.createFloatBuffer(texels * 4);
                if (depthBits > 0) {
                    org.lwjgl.opengl.GL30.glFramebufferTexture2D(org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER,
                            org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, name, 0);
                    GL11.glReadPixels(0, 0, w, h, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, px);
                    org.lwjgl.opengl.GL30.glFramebufferTexture2D(org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER,
                            org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, 0, 0);
                    float min = 1f;
                    int nearCount = 0;
                    for (int i = 0; i < texels; i++) {
                        float d = px.get(i);
                        if (d < min) {
                            min = d;
                        }
                        if (d < 0.9999f) {
                            nearCount++;
                        }
                    }
                    out.append(String.format(java.util.Locale.ROOT, " %d:tex%d %dx%d depth[min=%.4f centre=%.4f drawn=%.1f%%]",
                            unit, name, w, h, min, px.get((h / 2) * w + w / 2), 100.0 * nearCount / texels));
                } else {
                    org.lwjgl.opengl.GL30.glFramebufferTexture2D(org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER,
                            org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, name, 0);
                    GL11.glReadBuffer(org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0);
                    GL11.glReadPixels(0, 0, w, h, GL11.GL_RGBA, GL11.GL_FLOAT, px);
                    org.lwjgl.opengl.GL30.glFramebufferTexture2D(org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER,
                            org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, 0, 0);
                    double[] sum = new double[4];
                    float max = Float.NEGATIVE_INFINITY;
                    int nan = 0;
                    int inf = 0;
                    int distinct = 0;
                    float r0 = px.get(0);
                    float g0 = px.get(1);
                    float b0 = px.get(2);
                    for (int i = 0; i < texels; i++) {
                        for (int c = 0; c < 4; c++) {
                            float v = px.get(i * 4 + c);
                            sum[c] += v;
                            if (Float.isNaN(v)) {
                                nan++;
                            } else if (Float.isInfinite(v)) {
                                inf++;
                            } else if (v > max) {
                                max = v;
                            }
                        }
                        if (px.get(i * 4) != r0 || px.get(i * 4 + 1) != g0 || px.get(i * 4 + 2) != b0) {
                            distinct++;
                        }
                    }
                    int c = ((h / 2) * w + w / 2) * 4;
                    out.append(String.format(java.util.Locale.ROOT,
                            " %d:tex%d %dx%d %s mean=(%.4f,%.4f,%.4f,%.4f) centre=(%.4f,%.4f,%.4f,%.4f)"
                                    + " max=%.4f nan=%d inf=%d varied=%.1f%%%s",
                            unit, name, w, h, samplingDescription(w, h),
                            sum[0] / texels, sum[1] / texels, sum[2] / texels, sum[3] / texels,
                            px.get(c), px.get(c + 1), px.get(c + 2), px.get(c + 3),
                            max, nan, inf, 100.0 * distinct / texels, lodSample(w, h)));
                }
                // Drain whatever the probe raised so it is never attributed to the pass.
                while (GL11.glGetError() != GL11.GL_NO_ERROR) {
                    // discard
                }
            }
        } finally {
            org.lwjgl.opengl.GL30.glBindFramebuffer(org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER, savedRead);
            org.lwjgl.opengl.GL30.glBindFramebuffer(org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER, savedDraw);
            org.lwjgl.opengl.GL30.glDeleteFramebuffers(scratch);
            GL13.glActiveTexture(savedUnit);
        }
        return out.toString();
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
