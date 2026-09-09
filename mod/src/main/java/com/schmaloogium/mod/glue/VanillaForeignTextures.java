// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.ForeignTextureProvider;
import com.schmaloogium.engine.gl.TextureHandle;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The vanilla foreign-texture provider (PHASE_1_DOC §4.7.3): maps the engine's opaque
 * packface keys - the two vanilla vocabularies - to platform texture identifiers. Vanilla's
 * texture manager is resolved LAZILY per call: the resolution is the backend's problem,
 * not the caller's, and the provider answers empty for every key the platform does not
 * (yet) have. Handles are minted by the currently installed LWJGL3 device so backend
 * classification (concrete class + owning-device identity) holds; before a device is
 * installed, every key answers empty - nothing could bind anyway.
 */
public final class VanillaForeignTextures implements ForeignTextureProvider {

    private static final AtomicReference<Lwjgl3GLDevice> DEVICE_REF = new AtomicReference<>();

    /** Called by the {@link Lwjgl3GLDevice} constructor; the provider outlives devices. */
    static void install(Lwjgl3GLDevice device) {
        DEVICE_REF.set(device);
    }

    public VanillaForeignTextures() {
    }

    @Override
    public Optional<TextureHandle> handleFor(String key) {
        if (key == null || key.isEmpty()) {
            return Optional.empty();
        }
        Lwjgl3GLDevice device = DEVICE_REF.get();
        if (device == null) {
            return Optional.empty();
        }
        ResourceLocation resource;
        try {
            resource = new ResourceLocation(key);
        } catch (RuntimeException e) {
            return Optional.empty(); // not a resolvable vanilla key in this scope
        }
        // Lazy per-call resolution: validity across a vanilla reload is our problem.
        return Optional.of(new Lwjgl3ForeignTexture(device, key, () -> {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc == null) {
                return -1;
            }
            net.minecraft.client.renderer.texture.ITextureObject texture =
                    mc.getTextureManager().getTexture(resource);
            return texture == null ? -1 : texture.getGlTextureId();
        }));
    }

    /** "minecraft:dynamic/lightmap_1" - the atlas-keyed face Phase 13 binds per pass. */
    public static final String LIGHTMAP_KEY = "minecraft:dynamic/lightmap_1";
}
