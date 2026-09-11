// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.uniforms;

import com.schmaloogium.engine.uniforms.Double3;
import com.schmaloogium.engine.uniforms.Float3;
import com.schmaloogium.engine.uniforms.FogSample;
import com.schmaloogium.engine.uniforms.Int2;
import com.schmaloogium.engine.uniforms.OptionalFloat;
import com.schmaloogium.engine.uniforms.OptionalValue;
import com.schmaloogium.engine.uniforms.spi.FrameSampleRequest;
import com.schmaloogium.engine.uniforms.spi.FrameUniformSample;
import com.schmaloogium.engine.uniforms.spi.OnceUniformSample;
import com.schmaloogium.engine.uniforms.spi.TickUniformSample;
import com.schmaloogium.engine.uniforms.spi.UniformPlatformProvider;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;

/**
 * The 1.12.2 {@link UniformPlatformProvider} (PHASE_6_DOC §4.2, implemented by mod.glue):
 * each sample reads exactly the vanilla quantities the built-in catalog names, once per
 * acquisition boundary, and never throws — a missing world or view entity yields the
 * neutral values every record accepts. Render thread only.
 */
public final class McUniformPlatformProvider implements UniformPlatformProvider {

    @Override
    public OnceUniformSample sampleOnce() {
        return new OnceUniformSample(0.05f);
    }

    @Override
    public TickUniformSample sampleTick(long worldEpoch, long logicalTick) {
        WorldClient world = Minecraft.getMinecraft().world;
        if (world == null) {
            return new TickUniformSample(worldEpoch, logicalTick, 0L, 0, 0f);
        }
        int moonPhase = world.provider.hasSkyLight() ? clampInt(world.getMoonPhase(), 0, 7) : 0;
        float rain = clamp01(world.getRainStrength(1f));
        return new TickUniformSample(worldEpoch, logicalTick, world.getWorldTime(), moonPhase, rain);
    }

    @Override
    public FrameUniformSample sampleFrame(FrameSampleRequest request) {
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = mc.world;
        Entity view = mc.getRenderViewEntity();
        float partial = mc.getRenderPartialTicks();
        if (!Float.isFinite(partial)) {
            partial = 0f;
        }
        Double3 camera = new Double3(0d, 0d, 0d);
        float eyeAltitude = 0f;
        Int2 eyeBrightness = Int2.zero();
        int eyeInWater = 0;
        float nightVision = 0f;
        float blindness = 0f;
        Float3 skyColor = Float3.zero();
        float sunAngle = 0f;
        if (world != null && view != null) {
            double x = finite(view.lastTickPosX + (view.posX - view.lastTickPosX) * partial);
            double y = finite(view.lastTickPosY + (view.posY - view.lastTickPosY) * partial
                    + view.getEyeHeight());
            double z = finite(view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * partial);
            camera = new Double3(x, y, z);
            eyeAltitude = (float) y;
            int packed = view.getBrightnessForRender();
            eyeBrightness = new Int2(clampInt(packed & 0xFFFF, 0, 240),
                    clampInt((packed >> 16) & 0xFFFF, 0, 240));
            eyeInWater = view.isInsideOfMaterial(Material.WATER) ? 1
                    : view.isInsideOfMaterial(Material.LAVA) ? 2 : 0;
            if (view instanceof EntityLivingBase living) {
                nightVision = nightVision(living, partial);
                blindness = living.getActivePotionEffect(MobEffects.BLINDNESS) != null ? 1f : 0f;
            }
            Vec3d sky = world.getSkyColor(view, partial);
            if (sky != null) {
                skyColor = new Float3(clamp01((float) sky.x), clamp01((float) sky.y),
                        clamp01((float) sky.z));
            }
            float celestial = world.getCelestialAngle(partial);
            sunAngle = Float.isFinite(celestial) ? celestial - (float) Math.floor(celestial) : 0f;
            if (sunAngle >= 1f || sunAngle < 0f) {
                sunAngle = 0f;
            }
        }
        float gamma = clamp01(mc.gameSettings.gammaSetting);
        float far = Math.max(0f, mc.gameSettings.renderDistanceChunks * 16f);
        return new FrameUniformSample(
                request.worldEpoch(),
                request.frameId(),
                camera,
                Float.isFinite(eyeAltitude) ? eyeAltitude : 0f,
                eyeBrightness,
                eyeInWater,
                nightVision,
                blindness,
                gamma,
                mc.gameSettings.hideGUI,
                far,
                sunAngle,
                new OptionalFloat.Absent(),
                skyColor,
                new OptionalValue.Absent<FogSample>());
    }

    /** Vanilla's night-vision fade shape: full above 200 ticks, then a 0.7±0.3 pulse. */
    private static float nightVision(EntityLivingBase living, float partial) {
        PotionEffect effect = living.getActivePotionEffect(MobEffects.NIGHT_VISION);
        if (effect == null) {
            return 0f;
        }
        int duration = effect.getDuration();
        if (duration > 200) {
            return 1f;
        }
        return clamp01(0.7f + (float) Math.sin((duration - partial) * Math.PI * 0.2d) * 0.3f);
    }

    private static float clamp01(float value) {
        if (!Float.isFinite(value)) {
            return 0f;
        }
        return value < 0f ? 0f : value > 1f ? 1f : value;
    }

    private static int clampInt(int value, int min, int max) {
        return value < min ? min : value > max ? max : value;
    }

    private static double finite(double value) {
        return Double.isFinite(value) ? value : 0d;
    }
}
