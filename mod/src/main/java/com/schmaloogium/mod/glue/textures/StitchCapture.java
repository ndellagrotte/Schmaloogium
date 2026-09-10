// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.textures;

import com.schmaloogium.engine.buffers.AtlasId;
import com.schmaloogium.engine.textures.AtlasDescriptor;
import com.schmaloogium.engine.textures.SpriteDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The bounded D-P13-35/D-P13-40 stitch-capture state machine (§4.6): one outer map-load
 * scope, at most one live stitch token, at most one captured {@code (width, height,
 * mipmapLevels)}, the staged sprite list of the active attempt, and fixed scalar counters.
 * Minecraft-free: callers feed plain scalars; Post is the only acceptance moment and
 * consumes the token's acceptance exactly once. Every anomaly poisons the current attempt
 * fail-closed until outer cleanup — there is no last-observation win.
 */
public final class StitchCapture {

    private boolean outerActive;
    private String outerIdentity;
    private long outerEpoch;

    private boolean tokenLive;
    private String tokenIdentity;
    private long tokenEpoch;

    private boolean poisoned;

    private boolean captureRecorded;
    private int captureWidth;
    private int captureHeight;
    private int captureMipmapLevels;

    private final List<SpriteDescriptor> staged = new ArrayList<>();
    private AtlasDescriptor acceptedThisAttempt;

    private Optional<AtlasDescriptor> lastAccepted = Optional.empty();

    /** The resource epoch of {@link #lastAccepted}; drives reload-borne unscoped Pre. */
    private long lastAcceptedEpoch = Long.MIN_VALUE;

    private long poisonedAttempts;
    private long acceptedAttempts;

    /** H13-ATLAS-06 enter: reserves the scope and invalidates prior availability. */
    public void beginOuterMapLoad(String atlasIdentity, long resourceReloadEpoch) {
        if (outerActive) {
            // Reentry/nested conflicting map load: poisons the outer owner's attempt and
            // never replaces or releases its scope.
            poison();
            return;
        }
        outerActive = true;
        outerIdentity = atlasIdentity;
        outerEpoch = resourceReloadEpoch;
        resetAttempt();
    }

    /**
     * H13-ATLAS-06 exit (the wrapper's finally): clears the scope on every exit. Normal
     * completion after one accepted Post retains the accepted descriptor; unsuccessful,
     * poisoned or Post-less exits discard the whole attempt. Never throws.
     */
    public void endOuterMapLoad(boolean completedNormally) {
        if (!outerActive) {
            return;
        }
        if (completedNormally && !poisoned && acceptedThisAttempt != null) {
            lastAccepted = Optional.of(acceptedThisAttempt);
            lastAcceptedEpoch = outerEpoch;
        } else {
            // Unsuccessful, poisoned or Post-less exits discard the retained result.
            lastAccepted = Optional.empty();
            discardAttemptData();
        }
        outerActive = false;
        outerIdentity = null;
        resetAttempt();
    }

    /**
     * H13-ATLAS-01 Pre: invalidates previous accepted/pending data and mints the scope's
     * one stitch token. A reload between outer attempts makes the Pre itself open the new
     * attempt scope (invalidating prior accepted/pending data); a duplicate or mismatched
     * scoped Pre poisons.
     */
    public void onStitchBegin(String atlasIdentity, long resourceReloadEpoch) {
        if (!outerActive) {
            if (lastAccepted.isEmpty() || resourceReloadEpoch <= lastAcceptedEpoch) {
                // No retained availability, or no epoch advance: an unscoped Pre mints
                // no authority of its own.
                return;
            }
            // Reload between outer attempts: the Pre itself opens the new attempt
            // scope, invalidating prior accepted/pending data (H13-ATLAS-01).
            lastAccepted = Optional.empty();
            beginOuterMapLoad(atlasIdentity, resourceReloadEpoch);
            tokenLive = true;
            tokenIdentity = atlasIdentity;
            tokenEpoch = resourceReloadEpoch;
            return;
        }
        if (poisoned) {
            return;
        }
        if (!matches(atlasIdentity, resourceReloadEpoch) || tokenLive) {
            poison();
            return;
        }
        // H13-ATLAS-01: Pre invalidates previously accepted and pending data.
        lastAccepted = Optional.empty();
        discardAttemptData();
        tokenLive = true;
        tokenIdentity = atlasIdentity;
        tokenEpoch = resourceReloadEpoch;
    }

    /**
     * H13-ATLAS-05 allocation capture for the active token's map: positive dimensions,
     * nonnegative mip count, and both axes within the recorded 2D maximum. Unrelated
     * allocations (no live token) are ignored; duplicate, mismatched or invalid extents
     * poison the attempt.
     */
    public void onAllocation(String atlasIdentity, long resourceReloadEpoch, int width,
                             int height, int mipmapLevels, int maxTextureSize) {
        if (!outerActive || !tokenLive || poisoned) {
            return;
        }
        if (!matches(atlasIdentity, resourceReloadEpoch)) {
            poison();
            return;
        }
        if (width <= 0 || height <= 0 || mipmapLevels < 0
                || maxTextureSize <= 0
                || width > maxTextureSize || height > maxTextureSize) {
            poison();
            return;
        }
        if (captureRecorded) {
            poison();
            return;
        }
        captureRecorded = true;
        captureWidth = width;
        captureHeight = height;
        captureMipmapLevels = mipmapLevels;
    }

    /**
     * Stages one populator-copied sprite for the active attempt; ignored outside a live,
     * unpoisoned matching token. Sprite extent validation happens at Post.
     */
    public void stageSprite(String atlasIdentity, SpriteDescriptor sprite) {
        if (!outerActive || !tokenLive || poisoned) {
            return;
        }
        if (!atlasIdentity.equals(tokenIdentity) || sprite == null) {
            poison();
            return;
        }
        staged.add(sprite);
    }

    /**
     * H13-ATLAS-02 Post — the only acceptance moment: requires the current map/epoch/
     * token, exactly one valid capture, captured mips equal to the map's admitted mip
     * count, the staged sprite set equal to the map's uploaded sprite count, and every
     * staged sprite within the captured extent. Constructs the canonical-ordered
     * descriptor and consumes the token's acceptance exactly once. Poisoned or failed
     * attempts answer empty and publish nothing.
     */
    public Optional<AtlasDescriptor> onStitchPost(String atlasIdentity, long resourceReloadEpoch,
                                                  int mapUploadedSprites, int mapMipmapLevels) {
        if (!outerActive) {
            return Optional.empty();
        }
        if (poisoned) {
            return Optional.empty();
        }
        if (!tokenLive || !matches(atlasIdentity, resourceReloadEpoch)) {
            poison();
            return Optional.empty();
        }
        if (acceptedThisAttempt != null) {
            poison();
            return Optional.empty();
        }
        if (!captureRecorded) {
            poison();
            return Optional.empty();
        }
        if (captureMipmapLevels != mapMipmapLevels) {
            poison();
            return Optional.empty();
        }
        if (staged.size() != mapUploadedSprites) {
            poison();
            return Optional.empty();
        }
        for (SpriteDescriptor sprite : staged) {
            if (sprite.originX() + sprite.width() > captureWidth
                    || sprite.originY() + sprite.height() > captureHeight) {
                poison();
                return Optional.empty();
            }
        }
        List<SpriteDescriptor> ordered = new ArrayList<>(staged);
        ordered.sort(AtlasDescriptor.SPRITE_ORDER);
        AtlasDescriptor descriptor = new AtlasDescriptor(new AtlasId(atlasIdentity),
            captureWidth, captureHeight, captureMipmapLevels, ordered);
        acceptedThisAttempt = descriptor;
        acceptedAttempts++;
        return Optional.of(descriptor);
    }

    /**
     * The descriptor retained by the last outer attempt that ended normally after one
     * accepted Post; a later Pre or failed attempt invalidates prior data per §4.6.
     */
    public Optional<AtlasDescriptor> lastAccepted() {
        return lastAccepted;
    }

    /** Fixed scalar: attempts poisoned since creation. */
    public long poisonedAttempts() {
        return poisonedAttempts;
    }

    /** Fixed scalar: accepted stitches since creation. */
    public long acceptedAttempts() {
        return acceptedAttempts;
    }

    // ------------------------------------------------------- test observability

    boolean scopeActive() {
        return outerActive;
    }

    boolean attemptPoisoned() {
        return poisoned;
    }

    int stagedCount() {
        return staged.size();
    }

    // ------------------------------------------------------- internals

    private boolean matches(String atlasIdentity, long resourceReloadEpoch) {
        return outerIdentity != null && outerIdentity.equals(atlasIdentity)
            && outerEpoch == resourceReloadEpoch;
    }

    private void resetAttempt() {
        discardAttemptData();
        tokenLive = false;
        tokenIdentity = null;
        tokenEpoch = 0L;
        poisoned = false;
    }

    private void discardAttemptData() {
        captureRecorded = false;
        captureWidth = 0;
        captureHeight = 0;
        captureMipmapLevels = 0;
        staged.clear();
        acceptedThisAttempt = null;
    }

    private void poison() {
        if (!poisoned) {
            poisoned = true;
            poisonedAttempts++;
        }
    }
}
