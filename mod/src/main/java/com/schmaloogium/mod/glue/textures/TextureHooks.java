// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.textures;

import com.schmaloogium.engine.textures.AtlasAnimationSnapshot;
import com.schmaloogium.engine.textures.AtlasDescriptor;
import com.schmaloogium.engine.textures.TextureCaptureSink;

import java.util.Objects;

/**
 * The static stitch/animation bridge the (deferred) H13-ATLAS mixins and events call
 * (§4.6). Minecraft/LWJGL-free: every method receives plain scalars or engine types, so
 * the headless test classpath can load this class. At most one outer map-load scope is
 * owned at a time; nested conflicting work poisons the owner's attempt without replacing
 * or releasing its scope, and every hook failure stays contained (it never cancels or
 * replaces vanilla stitching).
 */
public final class TextureHooks {

    /**
     * The catalogued block/item atlas identity, exactly as vanilla 1.12.2 reports it to
     * {@code TextureStitchEvent} ({@code TextureMap.locationBlocksTexture}). Stated once,
     * here; every designation decision goes through {@link #isDesignatedBlockItemAtlas}.
     */
    public static final String DESIGNATED_BLOCK_ITEM_ATLAS = "minecraft:textures/atlas/blocks.png";

    private static final StitchCapture CAPTURE = new StitchCapture();

    private static TextureCaptureSink sink;
    private static boolean installed;

    /** The one owned outer scope plus its nesting depth. */
    private static boolean scopeOwned;
    private static int scopeDepth;
    private static String scopeIdentity;
    private static long scopeEpoch;

    /** The recorded 2D capability maximum for allocation-capture extent validation. */
    private static int recordedMaxTextureSize;

    private TextureHooks() {
    }

    /** Installs the capture acceptor. A second install is a hard wiring error. */
    public static void install(TextureCaptureSink sink) {
        if (installed) {
            throw new IllegalStateException("TextureHooks already installed");
        }
        installed = true;
        TextureHooks.sink = Objects.requireNonNull(sink, "sink");
    }

    /**
     * Records the captured capability profile's 2D maximum (bootstrap-owned, after the
     * native probe); unrecorded means allocation captures cannot validate extents and
     * fail closed at Post.
     */
    public static void recordCapabilities(int maxTextureSize) {
        recordedMaxTextureSize = maxTextureSize;
    }

    /** H13-ATLAS-06 enter: reserves the one outer scope before the original body. */
    public static void beginOuterMapLoad(String atlasIdentity, long epoch) {
        if (scopeOwned) {
            // Nested/conflicting load: forward so the outer attempt poisons fail-closed,
            // but never replace or release the outer owner's scope.
            scopeDepth++;
            CAPTURE.beginOuterMapLoad(atlasIdentity, epoch);
            return;
        }
        scopeOwned = true;
        scopeDepth = 1;
        scopeIdentity = atlasIdentity;
        scopeEpoch = epoch;
        CAPTURE.beginOuterMapLoad(atlasIdentity, epoch);
    }

    /**
     * H13-ATLAS-06 exit (the wrapper's finally): releases the scope only on the owner's
     * exit; nested exits leave the outer scope and its (poisoned) attempt in place.
     */
    public static void endOuterMapLoad(boolean completedNormally) {
        if (scopeDepth > 0) {
            scopeDepth--;
            if (scopeDepth == 0) {
                scopeOwned = false;
                scopeIdentity = null;
                CAPTURE.endOuterMapLoad(completedNormally);
            }
            return;
        }
        CAPTURE.endOuterMapLoad(completedNormally);
    }

    /** H13-ATLAS-01 Pre. Sink invalidation rides the outer-scope reservation semantics. */
    public static void onAtlasStitchBegin(String atlasIdentity, long epoch) {
        CAPTURE.onStitchBegin(atlasIdentity, epoch);
    }

    /**
     * H13-ATLAS-05 allocation capture for the active scoped token; ignored while no outer
     * scope is owned or no capability maximum has been recorded (fail closed).
     */
    public static void onAtlasStorageDefined(int width, int height, int mipmapLevels) {
        if (!scopeOwned || recordedMaxTextureSize <= 0) {
            return;
        }
        CAPTURE.onAllocation(scopeIdentity, scopeEpoch, width, height, mipmapLevels,
            recordedMaxTextureSize);
    }

    /**
     * H13-ATLAS-02 acceptance: runs the machine's only acceptance moment and pushes the
     * copied descriptor to the sink on success. Poisoned/failed attempts publish nothing.
     */
    public static void onAtlasStitchPost(String atlasIdentity, long epoch,
                                         int mapUploadedSprites, int mapMipmapLevels) {
        CAPTURE.onStitchPost(atlasIdentity, epoch, mapUploadedSprites, mapMipmapLevels)
            .ifPresent(TextureHooks::pushAccepted);
    }

    /** Pushes an already accepted descriptor to the sink with the designation verdict. */
    public static void onAtlasStitched(AtlasDescriptor descriptor) {
        if (!installed || sink == null) {
            return;
        }
        Objects.requireNonNull(descriptor, "descriptor");
        sink.onStitchAccepted(descriptor,
            isDesignatedBlockItemAtlas(descriptor.id().value()));
    }

    /** H13-ATLAS-04: hands the post-vanilla snapshot to the sink. */
    public static void onAtlasAnimationsUpdated(AtlasAnimationSnapshot snapshot) {
        if (!installed || sink == null) {
            return;
        }
        Objects.requireNonNull(snapshot, "snapshot");
        sink.applyAnimationSnapshot(snapshot);
    }

    /** The block/item atlas designation policy: exact identity match, one place. */
    public static boolean isDesignatedBlockItemAtlas(String atlasIdentity) {
        return DESIGNATED_BLOCK_ITEM_ATLAS.equals(atlasIdentity);
    }

    /** The shared machine, for the deferred adapter's read-only inspection needs. */
    static StitchCapture capture() {
        return CAPTURE;
    }

    private static void pushAccepted(AtlasDescriptor descriptor) {
        if (installed && sink != null) {
            sink.onStitchAccepted(descriptor,
                isDesignatedBlockItemAtlas(descriptor.id().value()));
        }
    }
}
