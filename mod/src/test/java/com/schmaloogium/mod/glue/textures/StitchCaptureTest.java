// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.textures;

import com.schmaloogium.engine.textures.AtlasAnimationSnapshot;
import com.schmaloogium.engine.textures.AtlasDescriptor;
import com.schmaloogium.engine.textures.SpriteDescriptor;
import com.schmaloogium.engine.textures.TextureCaptureSink;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The D-P13-35/40 stitch-capture machine (§8.2 atlas_actualExtentEpochAcceptance,
 * atlas_scopeExceptionalExit, atlas_normalOuterAndUnscopedInner): Post-only acceptance
 * with the actual captured extent, strict epoch/map/token matching, fail-closed poison
 * until outer cleanup, and no retained scope after any exit.
 */
class StitchCaptureTest {

    private static final String BLOCKS = "minecraft:textures/atlas/blocks.png";
    private static final String OTHER = "minecraft:textures/atlas/particles.png";

    private static SpriteDescriptor sprite(String name, int x, int y) {
        return SpriteDescriptor.staticSprite(name, x, y, 16, 16);
    }

    private static void completeAttempt(StitchCapture capture, String atlas, long epoch,
                                        int width, int height, int mips) {
        capture.beginOuterMapLoad(atlas, epoch);
        capture.onStitchBegin(atlas, epoch);
        capture.onAllocation(atlas, epoch, width, height, mips, 16384);
    }

    // ------------------------------------------------------------ atlas_normalOuterAndUnscopedInner

    @Test
    void normalOuterAttemptAcceptsActualExtentAndReleasesScope() {
        StitchCapture capture = new StitchCapture();
        completeAttempt(capture, BLOCKS, 5, 64, 32, 3);
        capture.stageSprite(BLOCKS, sprite("a", 0, 0));
        capture.stageSprite(BLOCKS, sprite("b", 16, 0));
        Optional<AtlasDescriptor> accepted = capture.onStitchPost(BLOCKS, 5, 2, 3);
        assertTrue(accepted.isPresent());
        AtlasDescriptor descriptor = accepted.get();
        assertEquals(64, descriptor.width());
        assertEquals(32, descriptor.height());
        assertEquals(3, descriptor.mipmapLevels());
        assertEquals(BLOCKS, descriptor.id().value());
        assertEquals(List.of("a", "b"),
            descriptor.sprites().stream().map(SpriteDescriptor::iconName).toList());
        capture.endOuterMapLoad(true);
        assertTrue(capture.lastAccepted().isPresent());
        assertSame(accepted.get(), capture.lastAccepted().get());
        assertFalse(capture.scopeActive());
        assertEquals(1, capture.acceptedAttempts());
        // The one scope is released: nothing is live for a stray follow-up Post.
        assertTrue(capture.onStitchPost(BLOCKS, 5, 2, 3).isEmpty());
    }

    @Test
    void equalSpriteSetsInDifferentlyPaddedAtlasesReportActualExtents() {
        StitchCapture capture = new StitchCapture();
        capture.beginOuterMapLoad(BLOCKS, 1);
        capture.onStitchBegin(BLOCKS, 1);
        capture.stageSprite(BLOCKS, sprite("a", 0, 0));
        capture.onAllocation(BLOCKS, 1, 32, 32, 2, 16384);
        Optional<AtlasDescriptor> tight = capture.onStitchPost(BLOCKS, 1, 1, 2);
        assertTrue(tight.isPresent());
        assertEquals(32, tight.get().width());
        assertEquals(32, tight.get().height());
        capture.endOuterMapLoad(true);
        // Same single sprite, different padded allocation → different actual extent.
        capture.beginOuterMapLoad(BLOCKS, 1);
        capture.onStitchBegin(BLOCKS, 1);
        capture.stageSprite(BLOCKS, sprite("a", 0, 0));
        capture.onAllocation(BLOCKS, 1, 128, 256, 2, 16384);
        Optional<AtlasDescriptor> padded = capture.onStitchPost(BLOCKS, 1, 1, 2);
        assertTrue(padded.isPresent());
        assertEquals(128, padded.get().width());
        assertEquals(256, padded.get().height());
        assertEquals(32, tight.get().width());
        capture.endOuterMapLoad(true);
    }

    @Test
    void directInnerLoadWithoutOuterScopeNeverMintsAuthority() {
        StitchCapture capture = new StitchCapture();
        capture.onStitchBegin(BLOCKS, 1);
        capture.onAllocation(BLOCKS, 1, 64, 32, 3, 16384);
        capture.stageSprite(BLOCKS, sprite("a", 0, 0));
        assertTrue(capture.onStitchPost(BLOCKS, 1, 1, 3).isEmpty());
        assertFalse(capture.scopeActive());
        assertEquals(0, capture.stagedCount());
        assertTrue(capture.lastAccepted().isEmpty());
        // Unscoped Post on a virgin machine publishes nothing either.
        assertTrue(capture.onStitchPost(OTHER, 1, 0, 0).isEmpty());
    }

    // ------------------------------------------------------------ poison semantics

    @Test
    void duplicatePrePoisonsUntilOuterCleanup() {
        StitchCapture capture = new StitchCapture();
        capture.beginOuterMapLoad(BLOCKS, 3);
        capture.onStitchBegin(BLOCKS, 3);
        capture.onStitchBegin(BLOCKS, 3); // duplicate Pre poisons the attempt
        assertTrue(capture.attemptPoisoned());
        assertTrue(capture.onStitchPost(BLOCKS, 3, 0, 0).isEmpty());
        assertEquals(0, capture.stagedCount()); // poisoned attempts stage nothing
        capture.endOuterMapLoad(true);
        assertTrue(capture.lastAccepted().isEmpty());
        // Poison does not survive cleanup: a fresh attempt works.
        completeAttempt(capture, BLOCKS, 3, 16, 16, 1);
        assertTrue(capture.onStitchPost(BLOCKS, 3, 0, 1).isPresent());
        capture.endOuterMapLoad(true);
    }

    @Test
    void postWithoutPrePoisonsTheAttempt() {
        StitchCapture capture = new StitchCapture();
        capture.beginOuterMapLoad(BLOCKS, 3);
        assertTrue(capture.onStitchPost(BLOCKS, 3, 0, 0).isEmpty());
        assertTrue(capture.attemptPoisoned());
        // Even a now-valid Pre cannot revive the poisoned attempt.
        capture.onStitchBegin(BLOCKS, 3);
        capture.onAllocation(BLOCKS, 3, 16, 16, 1, 16384);
        assertTrue(capture.onStitchPost(BLOCKS, 3, 0, 1).isEmpty());
        capture.endOuterMapLoad(false);
        assertFalse(capture.scopeActive());
    }

    @Test
    void missingAllocationPoisonsAtPost() {
        StitchCapture capture = new StitchCapture();
        completeAttempt(capture, BLOCKS, 3, -1, 16, 0); // invalid extent → poisoned now
        assertTrue(capture.attemptPoisoned());
        assertTrue(capture.onStitchPost(BLOCKS, 3, 0, 0).isEmpty());
        capture.endOuterMapLoad(false);
    }

    @Test
    void duplicateAllocationPoisonsWithNoLastObservationWin() {
        StitchCapture capture = new StitchCapture();
        capture.beginOuterMapLoad(BLOCKS, 3);
        capture.onStitchBegin(BLOCKS, 3);
        capture.onAllocation(BLOCKS, 3, 64, 32, 3, 16384);
        capture.onAllocation(BLOCKS, 3, 64, 32, 3, 16384); // identical → still poison
        assertTrue(capture.attemptPoisoned());
        assertTrue(capture.onStitchPost(BLOCKS, 3, 0, 3).isEmpty());
        capture.endOuterMapLoad(false);
    }

    @Test
    void changedMipCountAtPostPoisons() {
        StitchCapture capture = new StitchCapture();
        completeAttempt(capture, BLOCKS, 3, 64, 32, 3);
        assertTrue(capture.onStitchPost(BLOCKS, 3, 0, 2).isEmpty());
        assertTrue(capture.attemptPoisoned());
        capture.endOuterMapLoad(false);
    }

    @Test
    void invalidExtentsPoisonTheAttempt() {
        StitchCapture capture = new StitchCapture();
        capture.beginOuterMapLoad(BLOCKS, 3);
        capture.onStitchBegin(BLOCKS, 3);
        capture.onAllocation(BLOCKS, 3, 0, 32, 1, 16384); // nonpositive width
        assertTrue(capture.attemptPoisoned());
        capture.endOuterMapLoad(false);

        capture.beginOuterMapLoad(BLOCKS, 3);
        capture.onStitchBegin(BLOCKS, 3);
        capture.onAllocation(BLOCKS, 3, 64, 32, -1, 16384); // negative mips
        assertTrue(capture.attemptPoisoned());
        capture.endOuterMapLoad(false);

        capture.beginOuterMapLoad(BLOCKS, 3);
        capture.onStitchBegin(BLOCKS, 3);
        capture.onAllocation(BLOCKS, 3, 20480, 32, 1, 16384); // beyond recorded 2D max
        assertTrue(capture.attemptPoisoned());
        capture.endOuterMapLoad(false);
    }

    @Test
    void spriteBeyondCapturedExtentPoisonsAtPost() {
        StitchCapture capture = new StitchCapture();
        completeAttempt(capture, BLOCKS, 3, 64, 32, 1);
        capture.stageSprite(BLOCKS, sprite("overflow", 56, 24)); // 56+16 > 64
        assertTrue(capture.onStitchPost(BLOCKS, 3, 1, 1).isEmpty());
        assertTrue(capture.attemptPoisoned());
        capture.endOuterMapLoad(false);
    }

    @Test
    void stagedSpriteCountMustMatchMapUploadedSprites() {
        StitchCapture capture = new StitchCapture();
        completeAttempt(capture, BLOCKS, 3, 64, 32, 1);
        capture.stageSprite(BLOCKS, sprite("a", 0, 0));
        assertTrue(capture.onStitchPost(BLOCKS, 3, 2, 1).isEmpty()); // staged 1, map says 2
        assertTrue(capture.attemptPoisoned());
        capture.endOuterMapLoad(false);
    }

    @Test
    void otherMapOrEpochNeverExchangesCaptures() {
        StitchCapture capture = new StitchCapture();
        capture.beginOuterMapLoad(BLOCKS, 5);
        capture.onStitchBegin(OTHER, 5); // wrong map while scope owns BLOCKS
        assertTrue(capture.attemptPoisoned());
        capture.endOuterMapLoad(false);

        capture.beginOuterMapLoad(BLOCKS, 5);
        capture.onStitchBegin(BLOCKS, 6); // wrong epoch
        assertTrue(capture.attemptPoisoned());
        capture.endOuterMapLoad(false);

        capture.beginOuterMapLoad(BLOCKS, 5);
        capture.onStitchBegin(BLOCKS, 5);
        capture.onAllocation(OTHER, 5, 64, 32, 1, 16384); // mismatched allocation
        assertTrue(capture.attemptPoisoned());
        capture.endOuterMapLoad(false);

        // A matching attempt rejects Post from another map and from another epoch.
        capture.beginOuterMapLoad(BLOCKS, 5);
        capture.onStitchBegin(BLOCKS, 5);
        capture.onAllocation(BLOCKS, 5, 64, 32, 1, 16384);
        assertTrue(capture.onStitchPost(OTHER, 5, 0, 1).isEmpty()); // wrong map
        assertTrue(capture.attemptPoisoned());
        capture.endOuterMapLoad(false);

        capture.beginOuterMapLoad(BLOCKS, 5);
        capture.onStitchBegin(BLOCKS, 5);
        capture.onAllocation(BLOCKS, 5, 64, 32, 1, 16384);
        assertTrue(capture.onStitchPost(BLOCKS, 6, 0, 1).isEmpty()); // wrong epoch
        assertTrue(capture.attemptPoisoned());
        capture.endOuterMapLoad(false);
    }

    @Test
    void nestedConflictingLoadPoisonsTheOuterAttemptInPlace() {
        StitchCapture capture = new StitchCapture();
        capture.beginOuterMapLoad(BLOCKS, 5);
        capture.beginOuterMapLoad(OTHER, 5); // nested conflicting map load
        assertTrue(capture.attemptPoisoned());
        // The outer scope is neither replaced nor released by the nested work...
        assertTrue(capture.scopeActive());
        capture.onStitchBegin(OTHER, 5);
        capture.onAllocation(OTHER, 5, 32, 32, 1, 16384);
        assertTrue(capture.onStitchPost(OTHER, 5, 0, 1).isEmpty());
        // ...and the outer attempt publishes nothing.
        assertTrue(capture.onStitchPost(BLOCKS, 5, 0, 1).isEmpty());
        capture.endOuterMapLoad(true); // owner exit: poisoned attempt discards everything
        assertFalse(capture.scopeActive());
        assertTrue(capture.lastAccepted().isEmpty());
    }

    // ------------------------------------------------------------ atlas_scopeExceptionalExit

    @Test
    void preListenerThrowBeforeInnerEntryDiscardsAndReleases() {
        StitchCapture capture = new StitchCapture();
        capture.beginOuterMapLoad(BLOCKS, 5);
        capture.onStitchBegin(BLOCKS, 5);
        capture.stageSprite(BLOCKS, sprite("a", 0, 0));
        capture.endOuterMapLoad(false); // the finally after a Pre-listener throw
        assertFalse(capture.scopeActive());
        assertTrue(capture.lastAccepted().isEmpty());
        assertTrue(capture.onStitchPost(BLOCKS, 5, 1, 0).isEmpty()); // unscoped now
    }

    @Test
    void throwAfterAcceptanceStillDiscardsEverything() {
        StitchCapture capture = new StitchCapture();
        completeAttempt(capture, BLOCKS, 5, 64, 32, 2);
        capture.stageSprite(BLOCKS, sprite("a", 0, 0));
        Optional<AtlasDescriptor> accepted = capture.onStitchPost(BLOCKS, 5, 1, 2);
        assertTrue(accepted.isPresent()); // accepted, then a later Post listener throws
        capture.endOuterMapLoad(false);
        assertFalse(capture.scopeActive());
        assertTrue(capture.lastAccepted().isEmpty());
        // The consumed token cannot be reopened by a second Post.
        assertTrue(capture.onStitchPost(BLOCKS, 5, 1, 2).isEmpty());
    }

    @Test
    void normalEndWithoutPostDiscardsEverything() {
        StitchCapture capture = new StitchCapture();
        capture.beginOuterMapLoad(BLOCKS, 5);
        capture.onStitchBegin(BLOCKS, 5);
        capture.onAllocation(BLOCKS, 5, 64, 32, 2, 16384);
        capture.endOuterMapLoad(true); // completed without Post
        assertFalse(capture.scopeActive());
        assertTrue(capture.lastAccepted().isEmpty());
    }

    @Test
    void duplicatePostPoisonsAndNeverRepublishes() {
        StitchCapture capture = new StitchCapture();
        completeAttempt(capture, BLOCKS, 5, 64, 32, 1);
        assertTrue(capture.onStitchPost(BLOCKS, 5, 0, 1).isPresent());
        assertTrue(capture.onStitchPost(BLOCKS, 5, 0, 1).isEmpty()); // consumed once
        assertTrue(capture.attemptPoisoned());
        capture.endOuterMapLoad(false);
        // Even so, the attempt HAD accepted before its failing exit; cleanup discards it.
        assertTrue(capture.lastAccepted().isEmpty());
    }

    // ------------------------------------------------------------ epoch invalidation

    @Test
    void newEpochAttemptInvalidatesPreviousAcceptedDataAtPre() {
        StitchCapture capture = new StitchCapture();
        completeAttempt(capture, BLOCKS, 5, 64, 32, 1);
        assertTrue(capture.onStitchPost(BLOCKS, 5, 0, 1).isPresent());
        capture.endOuterMapLoad(true);
        assertTrue(capture.lastAccepted().isPresent());
        // Resource reload: a new-epoch outer attempt invalidates prior availability.
        Optional<AtlasDescriptor> accepted;
        assertTrue(capture.lastAccepted().isPresent()); // still stale until Pre
        capture.onStitchBegin(BLOCKS, 6); // Pre invalidates previous accepted/pending data
        assertTrue(capture.lastAccepted().isEmpty());
        capture.onAllocation(BLOCKS, 6, 64, 32, 1, 16384);
        accepted = capture.onStitchPost(BLOCKS, 6, 0, 1);
        assertTrue(accepted.isPresent());
        assertEquals(64, accepted.get().width());
        capture.endOuterMapLoad(true);
        assertEquals(64, capture.lastAccepted().get().width());
    }

    // ------------------------------------------------------------ TextureHooks glue

    @Test
    void hooksInstallOnceAndForwardDesignationPolicy() {
        AtomicInteger accepted = new AtomicInteger();
        AtomicReference<Boolean> designated = new AtomicReference<>();
        TextureHooks.install(new TextureCaptureSink() {
            @Override
            public void onStitchAccepted(AtlasDescriptor descriptor, boolean blockItemAtlas) {
                accepted.incrementAndGet();
                designated.set(blockItemAtlas);
            }

            @Override
            public void invalidateStitch(long newResourceReloadEpoch) {
            }

            @Override
            public void associateBase(com.schmaloogium.engine.gl.TextureHandle base,
                                      com.schmaloogium.engine.buffers.BaseAtlasContext association,
                                      long resourceReloadEpoch) {
            }

            @Override
            public void applyAnimationSnapshot(AtlasAnimationSnapshot snapshot) {
            }

            @Override
            public com.schmaloogium.engine.buffers.AtlasId designatedBlockItemAtlas() {
                return null;
            }
        });
        assertThrows(IllegalStateException.class, () -> TextureHooks.install(new TextureCaptureSink() {
            @Override
            public void onStitchAccepted(AtlasDescriptor descriptor, boolean blockItemAtlas) {
            }

            @Override
            public void invalidateStitch(long newResourceReloadEpoch) {
            }

            @Override
            public void associateBase(com.schmaloogium.engine.gl.TextureHandle base,
                                      com.schmaloogium.engine.buffers.BaseAtlasContext association,
                                      long resourceReloadEpoch) {
            }

            @Override
            public void applyAnimationSnapshot(AtlasAnimationSnapshot snapshot) {
            }

            @Override
            public com.schmaloogium.engine.buffers.AtlasId designatedBlockItemAtlas() {
                return null;
            }
        }));
        assertTrue(TextureHooks.isDesignatedBlockItemAtlas(
            "minecraft:textures/atlas/blocks.png"));
        assertFalse(TextureHooks.isDesignatedBlockItemAtlas(
            "minecraft:textures/atlas/particles.png"));

        // Full hook-path attempt: outer → Pre → allocation → Post pushes to the sink.
        TextureHooks.recordCapabilities(16384);
        TextureHooks.beginOuterMapLoad(BLOCKS, 9);
        TextureHooks.onAtlasStitchBegin(BLOCKS, 9);
        TextureHooks.onAtlasStorageDefined(64, 32, 2);
        TextureHooks.onAtlasStitchPost(BLOCKS, 9, 0, 2);
        assertEquals(1, accepted.get());
        assertNotNull(designated.get());
        assertTrue(designated.get());
        TextureHooks.endOuterMapLoad(true);

        // Nested load poisons the owner's attempt without releasing its scope: the
        // owner's own exit is the one that actually closes the machine scope.
        TextureHooks.beginOuterMapLoad(BLOCKS, 10);
        TextureHooks.beginOuterMapLoad(OTHER, 10);
        TextureHooks.onAtlasStitchBegin(BLOCKS, 10);
        TextureHooks.onAtlasStorageDefined(64, 32, 2);
        TextureHooks.onAtlasStitchPost(BLOCKS, 10, 0, 2); // poisoned → no push
        assertEquals(1, accepted.get());
        TextureHooks.endOuterMapLoad(false);  // nested exit: no machine release yet
        TextureHooks.endOuterMapLoad(true);   // owner exit: releases
        assertFalse(TextureHooks.capture().scopeActive());
    }
}
