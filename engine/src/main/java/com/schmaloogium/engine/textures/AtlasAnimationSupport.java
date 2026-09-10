// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.gl.TextureRegion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Pure sprite-animation support (PHASE_13_DOC §4.1.7, H13-ATLAS-04): validation of the
 * post-vanilla snapshot, the exact next-frame weight law, and the per-mip sprite frame
 * regions companion uploads use. A weight mismatch is corruption — the affected sprite
 * restores frame 0 rather than freezing or uploading a stale frame.
 */
public final class AtlasAnimationSupport {

    private AtlasAnimationSupport() {
    }

    /** The upload verdict for one snapshot. */
    public enum Verdict {
        /** Consistent snapshot: drive the companion frames, weight and mip regions. */
        APPLY,
        /** Stale or structurally invalid: discard with no upload. */
        DISCARD,
        /** Weight corruption: restore frame 0 (no interpolated upload) for the atlas. */
        FALLBACK_FRAME0
    }

    /**
     * Validates a snapshot against the current reload epoch and the last accepted tick
     * sequence, without sprite-membership checking (the membership set is unknown to the
     * caller). See {@link #validate(AtlasAnimationSnapshot, long, long, Set)}.
     */
    public static Verdict validate(AtlasAnimationSnapshot snapshot,
            long currentResourceReloadEpoch, long lastAcceptedTickSequence) {
        return validate(snapshot, currentResourceReloadEpoch, lastAcceptedTickSequence, null);
    }

    /**
     * Validates a snapshot. Null snapshots, stale epochs, non-increasing tick sequences,
     * icons outside the atlas's known sprite set (when {@code knownIconNames} is
     * non-null), nonpositive durations and elapsed ticks outside
     * {@code [0, currentDuration]} discard the snapshot with no upload. A weight that is
     * not finite or that matches neither the interpolating expectation
     * ({@code elapsed/currentDuration}) nor the non-interpolating {@code 0.0} is
     * corruption and restores frame 0.
     */
    public static Verdict validate(AtlasAnimationSnapshot snapshot,
            long currentResourceReloadEpoch, long lastAcceptedTickSequence,
            Set<String> knownIconNames) {
        if (snapshot == null) {
            return Verdict.DISCARD;
        }
        if (snapshot.resourceReloadEpoch() != currentResourceReloadEpoch) {
            return Verdict.DISCARD;
        }
        if (snapshot.tickSequence() <= lastAcceptedTickSequence) {
            return Verdict.DISCARD;
        }
        for (SpriteAnimationState state : snapshot.sprites()) {
            if (knownIconNames != null && !knownIconNames.contains(state.iconName())) {
                return Verdict.DISCARD;
            }
            if (state.currentDuration() <= 0) {
                return Verdict.DISCARD;
            }
            if (state.elapsedTicks() < 0
                    || state.elapsedTicks() > state.currentDuration()) {
                return Verdict.DISCARD;
            }
            double weight = state.nextFrameWeight();
            if (!Double.isFinite(weight)
                    || (weight != 0.0 && weight != expectedWeight(state))) {
                return Verdict.FALLBACK_FRAME0;
            }
        }
        return Verdict.APPLY;
    }

    /**
     * The exact interpolating weight: {@code elapsedTicks/currentDuration}. A
     * non-interpolating sprite carries the weight {@code 0.0}; validation accepts exactly
     * the two well-formed values for a row — {@code 0.0} or the interpolating expectation.
     */
    public static double expectedWeight(SpriteAnimationState state) {
        Objects.requireNonNull(state, "state");
        return (double) state.elapsedTicks() / state.currentDuration();
    }

    /**
     * The sprite's frame region at every mip level, level 0 through {@code mipmapLevels}
     * (the highest index): origin halves with {@code >>l}, extent halves floored at one.
     */
    public static List<TextureRegion> frameRegions(SpriteDescriptor sprite, int mipmapLevels) {
        Objects.requireNonNull(sprite, "sprite");
        if (mipmapLevels < 0) {
            throw new IllegalArgumentException("mipmapLevels must be nonnegative: "
                + mipmapLevels);
        }
        List<TextureRegion> regions = new ArrayList<>(mipmapLevels + 1);
        for (int level = 0; level <= mipmapLevels; level++) {
            regions.add(new TextureRegion(
                sprite.originX() >> level, sprite.originY() >> level, 0,
                Math.max(1, sprite.width() >> level),
                Math.max(1, sprite.height() >> level), 1));
        }
        return List.copyOf(regions);
    }
}
