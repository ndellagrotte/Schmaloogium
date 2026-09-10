// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.schmaloogium.engine.buffers.AtlasId;
import com.schmaloogium.engine.gl.TextureRegion;
import com.schmaloogium.engine.textures.AtlasAnimationSupport.Verdict;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The §8.2 animation_postVanillaSnapshot check (PHASE_13_DOC §4.1.7, H13-ATLAS-04): a
 * reordered frame map with unequal durations stays lossless, consistent post-vanilla
 * snapshots apply, stale or inconsistent ones discard, weight corruption restores
 * frame 0, and mip frame regions halve with a one-texel floor.
 */
class AtlasAnimationSupportTest {

    private static final AtlasId ATLAS = new AtlasId("minecraft:textures/atlas/blocks.png");

    /** Reordered sequence (source frames 2,0,1) with unequal durations 3,1,2. */
    private static SpriteAnimationMetadata reorderedAnimation() {
        return new SpriteAnimationMetadata(List.of(
            new AnimationFrameDescriptor(2, 3),
            new AnimationFrameDescriptor(0, 1),
            new AnimationFrameDescriptor(1, 2)), true);
    }

    private static SpriteAnimationState state(String icon, int elapsed, int duration,
            double weight) {
        return new SpriteAnimationState(icon, 1, 2, 0, elapsed, duration, weight);
    }

    private static AtlasAnimationSnapshot snapshot(SpriteAnimationState... states) {
        return new AtlasAnimationSnapshot(ATLAS, 7, 42, List.of(states));
    }

    // ------------------------------------------------------------------ named check

    /** §8.2 animation_postVanillaSnapshot. */
    @Test
    void animation_postVanillaSnapshot() {
        // The reordered frame map with unequal durations is preserved in sequence order,
        // never sorted back into source-frame order.
        assertEquals(List.of(
                new AnimationFrameDescriptor(2, 3),
                new AnimationFrameDescriptor(0, 1),
                new AnimationFrameDescriptor(1, 2)),
            reorderedAnimation().frames());

        // A consistent post-vanilla snapshot applies.
        assertEquals(Verdict.APPLY, AtlasAnimationSupport.validate(
            snapshot(state("block/stone", 2, 3, 2.0 / 3.0)), 7, 41,
            Set.of("block/stone", "block/dirt")));
        // Interpolation off: the glue carries weight 0 and the snapshot still applies.
        assertEquals(Verdict.APPLY, AtlasAnimationSupport.validate(
            snapshot(state("block/stone", 2, 3, 0.0)), 7, 41, null));

        // A stale epoch is discarded with no upload.
        assertEquals(Verdict.DISCARD, AtlasAnimationSupport.validate(
            snapshot(state("block/stone", 2, 3, 2.0 / 3.0)), 8, 41, null));
        // A null snapshot is discarded.
        assertEquals(Verdict.DISCARD, AtlasAnimationSupport.validate(
            null, 7, 41, null));
        // Tick sequences must increase strictly.
        assertEquals(Verdict.DISCARD, AtlasAnimationSupport.validate(
            snapshot(state("block/stone", 2, 3, 2.0 / 3.0)), 7, 42, null));
        // Frame-map membership is enforced when the known-icon set is supplied.
        assertEquals(Verdict.DISCARD, AtlasAnimationSupport.validate(
            snapshot(state("block/gravel", 1, 2, 0.5)), 7, 41,
            Set.of("block/stone", "block/dirt")));

        assertEquals(Verdict.DISCARD, AtlasAnimationSupport.validate(
            snapshot(state("block/stone", -1, 3, 0.0)), 7, 41, null));
        // A nonpositive duration discards.
        assertEquals(Verdict.DISCARD, AtlasAnimationSupport.validate(
            snapshot(state("block/stone", 0, 0, 0.0)), 7, 41, null));

        // Weight corruption restores frame 0.
        assertEquals(Verdict.FALLBACK_FRAME0, AtlasAnimationSupport.validate(
            snapshot(state("block/stone", 2, 3, 0.9)), 7, 41, null));
        assertEquals(Verdict.FALLBACK_FRAME0, AtlasAnimationSupport.validate(
            snapshot(state("block/stone", 2, 3, Double.NaN)), 7, 41, null));
        assertEquals(Verdict.FALLBACK_FRAME0, AtlasAnimationSupport.validate(
            snapshot(state("block/stone", 2, 3, Double.POSITIVE_INFINITY)), 7, 41, null));
    }

    @Test
    void tickSequenceMustStrictlyIncrease() {
        AtlasAnimationSnapshot snapshot =
            snapshot(state("block/stone", 1, 2, 0.5));
        assertEquals(Verdict.APPLY, AtlasAnimationSupport.validate(snapshot, 7, 41, null));
        assertEquals(Verdict.DISCARD,
            AtlasAnimationSupport.validate(snapshot, 7, 42, null));
        assertEquals(Verdict.DISCARD,
            AtlasAnimationSupport.validate(snapshot, 7, 100, null));
    }

    @Test
    void expectedWeightInterpolatesExactlyAndZeroMeansNoInterpolation() {
        assertEquals(2.0 / 3.0, AtlasAnimationSupport.expectedWeight(
            state("block/stone", 2, 3, 0.0)));
        assertEquals(0.25, AtlasAnimationSupport.expectedWeight(
            state("block/stone", 2, 8, 0.0)));
        assertEquals(0.0, AtlasAnimationSupport.expectedWeight(
            state("block/stone", 0, 5, 0.0)));
        assertEquals(1.0, AtlasAnimationSupport.expectedWeight(
            state("block/stone", 5, 5, 0.0)));
    }

    @Test
    void frameRegionsCoverEveryMipLevelWithHalvedOriginsAndExtentFloor() {
        SpriteDescriptor sprite = new SpriteDescriptor("block/stone", 2, 3, 5, 3,
            1, false, SpriteAnimationMetadata.singleFrame(0));
        assertEquals(List.of(
                new TextureRegion(2, 3, 0, 5, 3, 1),
                new TextureRegion(1, 1, 0, 2, 1, 1),
                new TextureRegion(0, 0, 0, 1, 1, 1)),
            AtlasAnimationSupport.frameRegions(sprite, 2));
        assertEquals(List.of(new TextureRegion(2, 3, 0, 5, 3, 1)),
            AtlasAnimationSupport.frameRegions(sprite, 0));
    }

    @Test
    void snapshotCopiesAndRejectsNullRows() {
        List<SpriteAnimationState> rows =
            new ArrayList<>(List.of(state("block/stone", 1, 2, 0.5)));
        AtlasAnimationSnapshot snapshot = new AtlasAnimationSnapshot(ATLAS, 7, 42, rows);
        rows.clear();
        assertEquals(1, snapshot.sprites().size(), "the snapshot copies, never retains");
        assertThrows(NullPointerException.class,
            () -> new AtlasAnimationSnapshot(ATLAS, 7, 42, null));
        assertThrows(NullPointerException.class,
            () -> new AtlasAnimationSnapshot(ATLAS, 7, 42,
                new ArrayList<>(List.of((SpriteAnimationState) null))));
        assertThrows(NullPointerException.class,
            () -> new SpriteAnimationState(null, 0, 0, 1, 0, 1, 0.0));
    }
}
