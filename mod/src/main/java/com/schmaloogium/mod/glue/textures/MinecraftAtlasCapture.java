// SPDX-License-Identifier: GPL-3.0-or-later
package com.schmaloogium.mod.glue.textures;

import com.schmaloogium.Reference;
import com.schmaloogium.engine.buffers.AtlasId;
import com.schmaloogium.engine.textures.AnimationFrameDescriptor;
import com.schmaloogium.engine.textures.AtlasAnimationSnapshot;
import com.schmaloogium.engine.textures.AtlasCatalog;
import com.schmaloogium.engine.textures.AtlasDescriptor;
import com.schmaloogium.engine.textures.SpriteAnimationMetadata;
import com.schmaloogium.engine.textures.SpriteAnimationState;
import com.schmaloogium.engine.textures.SpriteDescriptor;
import com.schmaloogium.engine.textures.TextureCaptureSink;
import com.schmaloogium.mod.glue.frame.ResourceReloadBoundary;
import com.schmaloogium.mod.mixin.textures.TextureMapAccessor;
import com.schmaloogium.mod.mixin.textures.TextureAtlasSpriteAccessor;
import com.schmaloogium.mod.mixin.textures.AbstractTextureAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.relauncher.Side;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;

/** Render-thread atlas evidence; no native names or game objects leave this adapter. */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Side.CLIENT)
public final class MinecraftAtlasCapture {
    private static TextureMap active;
    private static int depth;
    private static long epoch;
    private static boolean failed;
    private static int allocatedName;
    private static IResourceManager resources;
    private static TextureMap acceptedMap;
    private static int acceptedName;
    private static long acceptedEpoch = -1;
    private static long tickSequence;
    private static AtlasDescriptor accepted;
    private static TextureCaptureSink sink;
    private static final Map<String, ResourceLocation> companionLocations = new HashMap<>();

    private MinecraftAtlasCapture() {}

    public static AtlasCatalog catalog(long resourceEpoch) {
        return accepted != null && acceptedEpoch == resourceEpoch
            && resourceEpoch == ResourceReloadBoundary.epoch()
            && ((AbstractTextureAccessor) acceptedMap).schmaloogium$textureName() == acceptedName
            ? new AtlasCatalog(List.of(accepted)) : AtlasCatalog.EMPTY;
    }

    public static Optional<AtlasId> atlasFor(ITextureObject texture, long resourceEpoch) {
        return texture == acceptedMap && accepted != null && acceptedEpoch == resourceEpoch
            && resourceEpoch == ResourceReloadBoundary.epoch()
            && ((AbstractTextureAccessor) texture).schmaloogium$textureName() == acceptedName
            ? Optional.of(accepted.id()) : Optional.empty();
    }

    public static void attach(TextureCaptureSink target, long resourceEpoch) {
        sink = Objects.requireNonNull(target, "target");
        target.invalidateStitch(resourceEpoch);
        for (AtlasDescriptor atlas : catalog(resourceEpoch).atlases()) {
            target.onStitchAccepted(atlas, true);
        }
    }

    public static void detach(TextureCaptureSink target) {
        if (sink == target) sink = null;
    }

    public static void invalidate(long resourceEpoch) {
        accepted = null;
        acceptedMap = null;
        acceptedName = 0;
        acceptedEpoch = -1;
        resources = null;
        companionLocations.clear();
        if (active != null) failed = true;
        if (sink != null) {
            try {
                sink.invalidateStitch(resourceEpoch);
            } catch (RuntimeException ex) {
                sink = null;
            }
        }
    }

    public static void textureDeleted(int nativeName) {
        if (accepted != null && acceptedName == nativeName) invalidate(ResourceReloadBoundary.epoch());
        if (active != null && allocatedName == nativeName && nativeName > 0) failed = true;
    }

    public static void begin(TextureMap map, IResourceManager manager) {
        if (depth++ != 0) {
            failed = true;
            TextureHooks.beginOuterMapLoad(TextureHooks.DESIGNATED_BLOCK_ITEM_ATLAS,
                ResourceReloadBoundary.epoch());
            return;
        }
        invalidate(ResourceReloadBoundary.epoch());
        active = map;
        resources = manager;
        epoch = ResourceReloadBoundary.epoch();
        com.schmaloogium.mod.glue.frame.BootstrapHooks.capturedProfile().ifPresent(
            profile -> TextureHooks.recordCapabilities(profile.maxTextureSize()));
        failed = map != Minecraft.getMinecraft().getTextureMapBlocks();
        allocatedName = 0;
        TextureHooks.beginOuterMapLoad(TextureHooks.DESIGNATED_BLOCK_ITEM_ATLAS, epoch);
    }

    public static void end(TextureMap map, boolean normal) {
        if (depth <= 0) return;
        failed |= !normal || map != active || epoch != ResourceReloadBoundary.epoch();
        TextureHooks.endOuterMapLoad(normal && !failed);
        if (--depth != 0) return;
        try {
            if (!failed) {
                Optional<AtlasDescriptor> result = TextureHooks.capture().lastAccepted();
                if (result.isPresent() && allocatedName > 0
                        && ((AbstractTextureAccessor) map).schmaloogium$textureName() == allocatedName) {
                    accepted = result.get();
                    acceptedMap = map;
                    acceptedName = allocatedName;
                    acceptedEpoch = epoch;
                    tickSequence = 0;
                    if (sink != null) sink.onStitchAccepted(accepted, true);
                    return;
                }
            }
            invalidate(ResourceReloadBoundary.epoch());
        } catch (RuntimeException ex) {
            invalidate(ResourceReloadBoundary.epoch());
        } finally {
            active = null;
        }
    }

    /** Unscoped direct inner loads invalidate, but cannot create stitch authority. */
    public static void innerLoad(TextureMap map) {
        if (active != map || depth != 1) invalidate(ResourceReloadBoundary.epoch());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void pre(TextureStitchEvent.Pre event) {
        if (active != event.getMap() || depth != 1 || failed) {
            invalidate(ResourceReloadBoundary.epoch());
            return;
        }
        TextureHooks.onAtlasStitchBegin(TextureHooks.DESIGNATED_BLOCK_ITEM_ATLAS, epoch);
    }

    public static void storage(int nativeName, int mips, int width, int height) {
        if (active == null || failed || depth != 1) return;
        if (!(active instanceof AbstractTextureAccessor access)) {
            failed = true;
            return;
        }
        if (access.schmaloogium$textureName() != nativeName) return;
        if (allocatedName != 0 || epoch != ResourceReloadBoundary.epoch()) {
            failed = true;
            return;
        }
        allocatedName = nativeName;
        TextureHooks.onAtlasStorageDefined(width, height, mips);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void post(TextureStitchEvent.Post event) {
        if (active != event.getMap() || depth != 1 || failed || allocatedName <= 0
                || ((AbstractTextureAccessor) active).schmaloogium$textureName() != allocatedName
                || epoch != ResourceReloadBoundary.epoch()) {
            invalidate(ResourceReloadBoundary.epoch());
            return;
        }
        try {
            TextureMapAccessor access = (TextureMapAccessor) active;
            for (TextureAtlasSprite sprite : access.schmaloogium$uploadedSprites().values()) {
                AnimationMetadataSection metadata = ((TextureAtlasSpriteAccessor) sprite).schmaloogium$animationMetadata();
                List<AnimationFrameDescriptor> frames = new ArrayList<>();
                if (metadata == null) {
                    frames.add(new AnimationFrameDescriptor(0, 1));
                } else {
                    for (int i = 0; i < metadata.getFrameCount(); i++) {
                        frames.add(new AnimationFrameDescriptor(metadata.getFrameIndex(i), metadata.getFrameTimeSingle(i)));
                    }
                }
                SpriteDescriptor copy = new SpriteDescriptor(sprite.getIconName(), sprite.getOriginX(),
                    sprite.getOriginY(), sprite.getIconWidth(), sprite.getIconHeight(), sprite.getFrameCount(),
                    metadata != null, new SpriteAnimationMetadata(frames, metadata != null && metadata.isInterpolate()));
                TextureHooks.capture().stageSprite(TextureHooks.DESIGNATED_BLOCK_ITEM_ATLAS, copy);
                ResourceLocation icon = new ResourceLocation(sprite.getIconName());
                for (String suffix : List.of("_n", "_s")) {
                    String key = TextureSourcePreparer.companionResourceIdentity(sprite.getIconName(), suffix);
                    ResourceLocation location = new ResourceLocation(icon.getNamespace(), active.getBasePath() + "/"
                        + icon.getPath() + suffix + ".png");
                    ResourceLocation prior = companionLocations.putIfAbsent(key, location);
                    if (prior != null && !prior.equals(location)) failed = true;
                }
            }
            if (!failed) TextureHooks.onAtlasStitchPost(TextureHooks.DESIGNATED_BLOCK_ITEM_ATLAS,
                epoch, access.schmaloogium$uploadedSprites().size(), access.schmaloogium$mipmapLevels());
        } catch (RuntimeException ex) {
            failed = true;
        }
    }

    public static void animations(TextureMap map) {
        if (sink == null || atlasFor(map, ResourceReloadBoundary.epoch()).isEmpty()) return;
        try {
            List<SpriteAnimationState> states = new ArrayList<>();
            for (TextureAtlasSprite sprite : ((TextureMapAccessor) map).schmaloogium$animatedSprites()) {
                TextureAtlasSpriteAccessor access = (TextureAtlasSpriteAccessor) sprite;
                AnimationMetadataSection metadata = access.schmaloogium$animationMetadata();
                int position = access.schmaloogium$frameCounter();
                int duration = metadata.getFrameTimeSingle(position);
                int elapsed = access.schmaloogium$tickCounter();
                states.add(new SpriteAnimationState(sprite.getIconName(), position, metadata.getFrameIndex(position),
                    metadata.getFrameIndex((position + 1) % metadata.getFrameCount()), elapsed, duration,
                    metadata.isInterpolate() ? (double) elapsed / duration : 0));
            }
            sink.applyAnimationSnapshot(new AtlasAnimationSnapshot(accepted.id(), acceptedEpoch, ++tickSequence, states));
        } catch (RuntimeException ex) {
            invalidate(ResourceReloadBoundary.epoch());
        }
    }

    public static TextureSourcePreparer.CompanionDiscovery companionDiscovery() {
        return MinecraftAtlasCapture::discover;
    }

    private static Optional<TextureSourcePreparer.CompanionDiscovery.DiscoveredCompanion> discover(String identity, long resourceEpoch) {
        if (catalog(resourceEpoch).atlases().isEmpty() || resources == null) return Optional.empty();
        ResourceLocation location = companionLocations.get(identity);
        if (location == null) return Optional.empty();
        try (IResource resource = resources.getResource(location)) {
            BufferedImage image = ImageIO.read(resource.getInputStream());
            if (image == null) return Optional.empty();
            AnimationMetadataSection metadata = resource.getMetadata("animation");
            int width = image.getWidth();
            int height = metadata == null ? image.getHeight() : width;
            if (image.getHeight() % height != 0) return Optional.empty();
            List<byte[]> frames = new ArrayList<>();
            for (int frame = 0; frame < image.getHeight() / height; frame++) {
                byte[] rgba = new byte[Math.multiplyExact(Math.multiplyExact(width, height), 4)];
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pixel = image.getRGB(x, frame * height + y);
                        int offset = (y * width + x) * 4;
                        rgba[offset] = (byte) (pixel >>> 16);
                        rgba[offset + 1] = (byte) (pixel >>> 8);
                        rgba[offset + 2] = (byte) pixel;
                        rgba[offset + 3] = (byte) (pixel >>> 24);
                    }
                }
                frames.add(rgba);
            }
            return Optional.of(new TextureSourcePreparer.CompanionDiscovery.DiscoveredCompanion(width, height, frames, resourceEpoch));
        } catch (IOException | RuntimeException ex) {
            return Optional.empty();
        }
    }
}
