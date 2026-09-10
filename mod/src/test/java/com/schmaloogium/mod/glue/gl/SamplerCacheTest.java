// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.gl;

import com.schmaloogium.engine.buffers.BufferResizeReason;
import com.schmaloogium.engine.gl.SamplerKey;
import com.schmaloogium.engine.gl.TextureBorderColor;
import com.schmaloogium.engine.gl.TextureCompareFunction;
import com.schmaloogium.engine.gl.TextureCompareMode;
import com.schmaloogium.engine.gl.TextureMagFilter;
import com.schmaloogium.engine.gl.TextureMinFilter;
import com.schmaloogium.engine.gl.TextureParameters;
import com.schmaloogium.engine.gl.TextureSwizzle;
import com.schmaloogium.engine.gl.TextureWrap;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Headless A1 cache behavior (PHASE_14_DOC §8.1 rows 2/3/5/6, model-observable part):
 * key equality drives interning hit/miss; the complete parameter value is retained per
 * association while only the exact sampler prefix reaches the key; all eight resize
 * reasons invalidate bound-unit knowledge and retire entries; retirement is
 * lease-accounted and deletion is exactly once; normalization and the fixed-function
 * dispatch clear exactly what D-P14-27/D-P14-4 say. Deterministic; no GL context
 * anywhere (the ops fake stands in for the LWJGL3 adapter).
 */
class SamplerCacheTest {

    // ------------------------------------------------------------------ fakes

    /** Recording sampler ops: deterministic names, scripted failures, no GL. */
    private static final class FakeSamplerOps implements SamplerOps {
        final List<String> log = new ArrayList<>();
        final Set<Integer> live = new HashSet<>();
        int nextName = 7;
        int failNextCreates;
        int failNextDeletes;
        int failBindCallIndex = -1;
        private int bindCount;

        int binds() {
            return bindCount;
        }

        long count(String prefix) {
            return log.stream().filter(entry -> entry.startsWith(prefix)).count();
        }

        @Override
        public int create(SamplerKey key) {
            log.add("create");
            if (failNextCreates > 0) {
                failNextCreates--;
                return -1;
            }
            int name = nextName++;
            live.add(name);
            return name;
        }

        @Override
        public boolean bind(int unit, int sampler) {
            int index = bindCount++;
            log.add("bind " + unit + " <- " + sampler);
            return index != failBindCallIndex;
        }

        @Override
        public boolean delete(int sampler) {
            log.add("delete " + sampler);
            if (failNextDeletes > 0) {
                failNextDeletes--;
                return false;
            }
            live.remove(sampler);
            return true;
        }
    }

    // ------------------------------------------------------------------ fixtures

    private static TextureParameters params(TextureMinFilter min, TextureSwizzle swizzle,
                                            int baseLevel) {
        return new TextureParameters(min, TextureMagFilter.NEAREST,
                TextureWrap.REPEAT, TextureWrap.REPEAT, TextureWrap.REPEAT,
                TextureCompareMode.NONE, TextureCompareFunction.NEVER,
                new TextureBorderColor(0, 0, 0, 0), -1000f, 1000f, 0f, 1f,
                baseLevel, 1000, swizzle);
    }

    private static SamplerCache.Handle intern(SamplerCache cache, TextureMinFilter min) {
        return assertInstanceOf(SamplerCache.InternResult.Interned.class,
                cache.intern(SamplerKey.of(params(min, TextureSwizzle.IDENTITY, 0)))).handle();
    }

    private static SamplerCache.EstateIdentity identity(long serial) {
        return new SamplerCache.EstateIdentity(1000 + serial, "source-fp-" + serial, serial);
    }

    // ------------------------------------------------------------------ interning (row 2)

    @Test
    void equalKeysInternOneNativeSampler() {
        FakeSamplerOps ops = new FakeSamplerOps();
        SamplerCache cache = new SamplerCache(ops);

        SamplerCache.Handle first = intern(cache, TextureMinFilter.NEAREST);
        assertEquals(1, ops.count("create"));

        // Equal sampler state (the swizzle is object state and never reaches the key),
        // independently constructed: a hit, no new native object.
        SamplerCache.Handle second = assertInstanceOf(SamplerCache.InternResult.Interned.class,
                cache.intern(SamplerKey.of(
                        params(TextureMinFilter.NEAREST, TextureSwizzle.LEGACY_DEPTH_LUMINANCE, 0))))
                .handle();
        assertEquals(first, second);
        assertEquals(1, ops.count("create"), "structural equality must intern deterministically");

        // A different key interns a second sampler.
        SamplerCache.Handle linear = intern(cache, TextureMinFilter.LINEAR);
        assertNotEquals(first, linear);
        assertEquals(2, ops.count("create"));
    }

    @Test
    void failedCreationIsContainedAndRetryable() {
        FakeSamplerOps ops = new FakeSamplerOps();
        ops.failNextCreates = 1;
        SamplerCache cache = new SamplerCache(ops);

        SamplerCache.InternResult.Failed failed = assertInstanceOf(
                SamplerCache.InternResult.Failed.class,
                cache.intern(SamplerKey.of(params(TextureMinFilter.NEAREST, TextureSwizzle.IDENTITY, 0))));
        assertTrue(failed.reason() != null && !failed.reason().isBlank());
        assertTrue(ops.live.isEmpty(), "no partial sampler exists");

        // The failure cached nothing: a retry may succeed (containment then recovery).
        SamplerCache.InternResult.Interned retry = assertInstanceOf(
                SamplerCache.InternResult.Interned.class,
                cache.intern(SamplerKey.of(params(TextureMinFilter.NEAREST, TextureSwizzle.IDENTITY, 0))));
        assertTrue(retry.freshlyCreated());
    }

    // ------------------------------------------------------------------ associations (row 3)

    @Test
    void associationRetainsCompleteParametersWhileOnlyThePrefixReachesTheKey() {
        FakeSamplerOps ops = new FakeSamplerOps();
        SamplerCache cache = new SamplerCache(ops);
        Object subject = new Object();
        TextureParameters complete =
                params(TextureMinFilter.NEAREST, TextureSwizzle.LEGACY_DEPTH_LUMINANCE, 2);

        SamplerCache.Handle handle = intern(cache, TextureMinFilter.NEAREST);
        cache.associate(subject, identity(1), complete, handle);

        SamplerCache.Association association =
                cache.associationFor(subject, identity(1)).orElseThrow();
        // Every P1 field reaches the retained baseline, including object-only state:
        assertEquals(complete, association.parameters());
        assertEquals(TextureSwizzle.LEGACY_DEPTH_LUMINANCE, association.parameters().swizzle());
        assertEquals(2, association.parameters().baseLevel());

        // Object-only change (baseLevel): same key -> same interned handle.
        TextureParameters retargeted =
                params(TextureMinFilter.NEAREST, TextureSwizzle.LEGACY_DEPTH_LUMINANCE, 5);
        assertEquals(SamplerKey.of(complete), SamplerKey.of(retargeted),
                "baseLevel is object state, never key state");
        assertEquals(handle, assertInstanceOf(SamplerCache.InternResult.Interned.class,
                cache.intern(SamplerKey.of(retargeted))).handle());
        assertEquals(1, ops.count("create"));

        // A stale identity is never a hit: the complete baseline write is demanded again.
        assertTrue(cache.associationFor(subject, identity(9)).isEmpty(),
                "equal keys alone do not authenticate a subject");
    }

    @Test
    void associationRejectsRetiredPublications() {
        SamplerCache cache = new SamplerCache(new FakeSamplerOps());
        SamplerCache.Handle handle = intern(cache, TextureMinFilter.NEAREST);
        cache.onResize(BufferResizeReason.REGISTRY_PLAN);
        assertThrows(IllegalArgumentException.class,
                () -> cache.associate(new Object(), identity(1),
                        params(TextureMinFilter.NEAREST, TextureSwizzle.IDENTITY, 0), handle),
                "a retired publication cannot be associated");
        assertThrows(IllegalArgumentException.class, () -> cache.bind(0, handle),
                "a retired publication cannot bind (retired cannot rebind)");
    }

    // ------------------------------------------------------------------ bind cache + clears (rows 6/7)

    @Test
    void bindCacheSkipsProvenRedundantBinds() {
        FakeSamplerOps ops = new FakeSamplerOps();
        SamplerCache cache = new SamplerCache(ops);
        SamplerCache.Handle handle = intern(cache, TextureMinFilter.NEAREST);

        assertTrue(cache.bind(3, handle));
        assertEquals(1, ops.binds());
        assertTrue(cache.bind(3, handle), "proven redundant: same live handle known on unit 3");
        assertEquals(1, ops.binds());
        assertTrue(cache.bind(4, handle));
        assertEquals(2, ops.binds());

        assertTrue(cache.clearUnit(3));
        assertEquals(1, ops.count("bind 3 <- 0"));
        assertTrue(cache.clearUnit(3), "proven zero on unit 3");
        assertEquals(1, ops.count("bind 3 <- 0"));
    }

    @Test
    void failedBindInvalidatesUnitKnowledgeAndRetries() {
        FakeSamplerOps ops = new FakeSamplerOps();
        SamplerCache cache = new SamplerCache(ops);
        SamplerCache.Handle handle = intern(cache, TextureMinFilter.NEAREST);

        ops.failBindCallIndex = 0;
        assertFalse(cache.bind(2, handle));
        assertEquals(1, ops.binds());

        // Knowledge is unknown after the failure: the retry issues the native bind again.
        assertTrue(cache.bind(2, handle));
        assertEquals(2, ops.binds());
    }

    @Test
    void prepareUnitsClearsExactlyTheUnoccupiedRows() {
        FakeSamplerOps ops = new FakeSamplerOps();
        SamplerCache cache = new SamplerCache(ops);
        SamplerCache.Handle handle = intern(cache, TextureMinFilter.NEAREST);
        assertTrue(cache.bind(1, handle));
        assertTrue(cache.bind(4, handle));
        int bindsBefore = ops.binds();

        assertTrue(cache.prepareUnits((1 << 1) | (1 << 4)));
        assertEquals(14, ops.binds() - bindsBefore, "every unoccupied unit of sixteen is cleared");
        assertEquals(0, ops.count("bind 1 <- 0"), "occupied units are untouched");
        assertEquals(0, ops.count("bind 4 <- 0"));
    }

    @Test
    void prepareUnitsRejectsNonSixteenBitMasks() {
        SamplerCache cache = new SamplerCache(new FakeSamplerOps());
        assertThrows(IllegalArgumentException.class, () -> cache.prepareUnits(0x10000));
        assertThrows(IllegalArgumentException.class, () -> cache.prepareUnits(-1));
    }

    @Test
    void fixedFunctionDispatchClearsAllSixteenThenOnlyWhatIsUnknown() {
        FakeSamplerOps ops = new FakeSamplerOps();
        SamplerCache cache = new SamplerCache(ops);
        SamplerCache.Handle handle = intern(cache, TextureMinFilter.NEAREST);

        assertTrue(cache.clearAllUnits());
        assertEquals(16, ops.binds(), "unknown knowledge is cleared, never skipped (D-P14-4)");
        assertTrue(cache.clearAllUnits(), "all proven zero: the second dispatch issues nothing");
        assertEquals(16, ops.binds());

        long unit2ZerosBefore = ops.count("bind 2 <- 0");
        assertTrue(cache.bind(2, handle));
        assertTrue(cache.clearAllUnits());
        assertEquals(18, ops.binds(), "only unit 2 lost its proven-zero knowledge");
        assertEquals(unit2ZerosBefore + 1, ops.count("bind 2 <- 0"));
    }

    // ------------------------------------------------------------------ resize + retirement (row 5)

    @Test
    void everyResizeReasonForgetsKnowledgeAndRetiresEntries() {
        for (BufferResizeReason reason : BufferResizeReason.values()) {
            FakeSamplerOps ops = new FakeSamplerOps();
            SamplerCache cache = new SamplerCache(ops);
            SamplerCache.Handle handle = intern(cache, TextureMinFilter.NEAREST);
            assertTrue(cache.bind(5, handle));
            assertTrue(cache.clearAllUnits()); // one dispatch proves every unit zero
            int bindsBefore = ops.binds();
            assertTrue(cache.clearAllUnits());
            assertEquals(16, 16 - (ops.binds() - bindsBefore),
                    "after one dispatch all sixteen units are proven zero");
            cache.onResize(reason);

            // Knowledge forgotten: the next dispatch clears all sixteen again.
            bindsBefore = ops.binds();
            assertTrue(cache.clearAllUnits());
            assertEquals(16, ops.binds() - bindsBefore,
                    () -> reason + " must invalidate bound-unit knowledge");
            // The retired publication can never bind again; the same key interns fresh.
            assertThrows(IllegalArgumentException.class, () -> cache.bind(5, handle),
                    () -> reason + " must retire publications");
            SamplerCache.InternResult.Interned fresh = assertInstanceOf(
                    SamplerCache.InternResult.Interned.class,
                    cache.intern(SamplerKey.of(
                            params(TextureMinFilter.NEAREST, TextureSwizzle.IDENTITY, 0))));
            assertNotEquals(handle, fresh.handle(),
                    () -> reason + " must not revive the retired entry");
            assertTrue(fresh.freshlyCreated());
        }
        assertEquals(8, BufferResizeReason.values().length, "the estate transaction has eight reasons");
    }

    @Test
    void retirementIsLeaseAccountedAndDeletionIsExactlyOnce() {
        FakeSamplerOps ops = new FakeSamplerOps();
        SamplerCache cache = new SamplerCache(ops);
        SamplerCache.Handle handle = intern(cache, TextureMinFilter.NEAREST);
        int name = ops.live.iterator().next();

        assertThrows(IllegalArgumentException.class, () -> cache.release(handle),
                "release without retain is unbalanced");

        cache.retain(handle);
        cache.onResize(BufferResizeReason.PACK_CONFIGURATION);
        cache.drainRetired();
        assertTrue(ops.live.contains(name), "an outstanding lease prevents deletion");
        assertEquals(0, ops.count("delete"));
        assertEquals(SamplerCache.State.RETIRING, handle.state());

        cache.release(handle);

        assertEquals(SamplerCache.State.DELETED, handle.state());
        assertEquals(1, ops.count("delete"), "deletion is exactly once");
        assertTrue(!ops.live.contains(name));
        assertThrows(IllegalArgumentException.class, () -> cache.release(handle),
                "a deleted handle can never be released again");
        cache.drainRetired();
        assertEquals(1, ops.count("delete"), "a deleted handle is never revisited");
        assertThrows(IllegalArgumentException.class, () -> cache.retain(handle),
                "a deleted handle is no longer referenceable");
    }

    @Test
    void failedNativeDeleteRetriesOnTheNextDrain() {
        FakeSamplerOps ops = new FakeSamplerOps();
        SamplerCache cache = new SamplerCache(ops);
        SamplerCache.Handle handle = intern(cache, TextureMinFilter.NEAREST);
        int name = ops.live.iterator().next();

        cache.onResize(BufferResizeReason.DISPLAY_EXTENT);
        ops.failNextDeletes = 1;
        cache.drainRetired();
        assertEquals(1, ops.count("delete"));
        assertEquals(SamplerCache.State.RETIRING, handle.state(),
                "an uncertain native delete keeps the lifetime ending");
        assertTrue(ops.live.contains(name));

        cache.drainRetired();
        assertEquals(SamplerCache.State.DELETED, handle.state());
        assertEquals(2, ops.count("delete"));
    }

    @Test
    void resizeRebindUsesFreshNativeAndNeverRevivesDeletedNames() {
        FakeSamplerOps ops = new FakeSamplerOps();
        SamplerCache cache = new SamplerCache(ops);
        SamplerCache.Handle first = intern(cache, TextureMinFilter.NEAREST);
        int firstName = ops.live.iterator().next();

        cache.onResize(BufferResizeReason.SHADOW_RESOLUTION);
        cache.drainRetired();
        SamplerCache.Handle second = intern(cache, TextureMinFilter.NEAREST);
        assertTrue(cache.bind(0, second));
        assertAll(
                () -> assertNotEquals(firstName, second.name()),
                () -> assertTrue(ops.live.contains(second.name())),
                () -> assertTrue(!ops.live.contains(firstName), "the deleted name never returns"));
    }
}
