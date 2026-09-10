// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.config.IdMappingInput;
import com.schmaloogium.engine.config.IdMappingParserImpl;
import com.schmaloogium.engine.config.TriState;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.uniforms.HeldItemSample;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** §8.1 tests 12, 13: held-hand resolution and per-draw scope state machine. */
class IdPerDrawHeldTest {

    /** Recording sink capturing only the held tuple. */
    static final class HeldSink implements com.schmaloogium.engine.uniforms.UniformEventSink {
        final List<HeldItemSample> samples = new ArrayList<>();
        int entityId;
        int blockEntityId;

        @Override public void captureGbufferMatrices(long frameId,
                com.schmaloogium.engine.uniforms.Matrix4Value a,
                com.schmaloogium.engine.uniforms.Matrix4Value b) { }
        @Override public void updateCelestial(com.schmaloogium.engine.uniforms.CelestialSample s) { }
        @Override public void updateShadowMatrices(
                com.schmaloogium.engine.uniforms.ShadowMatrixSample s) { }
        @Override public void updateFog(com.schmaloogium.engine.uniforms.FogSample s) { }
        @Override public void updateBlend(com.schmaloogium.engine.uniforms.BlendSample s) { }
        @Override public void updateEntityColor(com.schmaloogium.engine.uniforms.Float4 v) { }
        @Override public void updateEntityId(int value) {
            this.entityId = value;
        }
        @Override public void updateBlockEntityId(int value) {
            this.blockEntityId = value;
        }
        @Override public void updateInstanceId(int value) { }
        @Override public void updateAtlasSize(com.schmaloogium.engine.uniforms.Int2 value) { }
        @Override public void updateHeldItems(HeldItemSample value) {
            samples.add(value);
        }
    }

    private static final IdScopeAdmission ADMISSION = new IdScopeAdmission() {
        @Override public long generation() {
            return 1;
        }

        @Override public long frameId() {
            return 100;
        }

        @Override public Thread ownerThread() {
            return Thread.currentThread();
        }

        @Override public boolean active() {
            return true;
        }

        @Override public boolean shadow() {
            return false;
        }
    };

    @Test
    void heldItemResolver_normalAndOldModes_equalTupleDeduplication() {
        try (PublishedHarness.Published f = PublishedHarness.buildAndPublish(
                IdFixtures.packMappings("100=stone\n", "10=stick\n11=glowstone\n", "", "",
                        IdFixtures.packOrigin()),
                IdFixtures.vanilla(), ModIdSourceSnapshot.empty(),
                new CompatibilityAliasCatalog(1, List.of()), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault())) {
            HeldSink sink = new HeldSink();
            HeldStackValue stick = new HeldStackValue(false, f.itemOrdinal("stick"), 0);
            HeldStackValue glow = new HeldStackValue(false, f.itemOrdinal("glowstone"), 15);
            HeldStackValue empty = HeldStackValue.emptyHand();

            // Normal mode: (main, off) verbatim; non-block stick carries light 0.
            HeldItemResolver normal = new HeldItemResolver(f.runtime().aliases(),
                    new HandLightPolicy(TriState.DEFAULT, TriState.DEFAULT, TriState.FALSE),
                    sink);
            normal.accept(new HeldHandsValue(1, 50, stick, empty));
            assertEquals(new HeldItemSample(1, 50, 10, 0, 0, 0), sink.samples.get(0));
            // Old mode: max(main, off) goes to the primary slot; ids never swap.
            HeldItemResolver old = new HeldItemResolver(f.runtime().aliases(),
                    new HandLightPolicy(TriState.DEFAULT, TriState.DEFAULT, TriState.TRUE),
                    sink);
            old.accept(new HeldHandsValue(1, 51, stick, glow));
            assertEquals(new HeldItemSample(1, 51, 10, 15, 11, 15), sink.samples.get(1));
            // Equal tuple: no further sink event.
            old.accept(new HeldHandsValue(1, 52, stick, glow));
            assertEquals(2, sink.samples.size(), "equal tuple must deduplicate");
        }
    }

    @Test
    void perDrawScopes_nestedLifoRestore_overflowWrongThreadAndDrain() {
        try (PublishedHarness.Published f = PublishedHarness.buildAndPublish(
                IdFixtures.packMappings("100=stone\n", "", "30=creeper\n", "",
                        IdFixtures.packOrigin()),
                IdFixtures.vanilla(), ModIdSourceSnapshot.empty(),
                new CompatibilityAliasCatalog(1, List.of()), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault())) {
            PerDrawDynamics perDraw = f.runtime().perDraw();
            assertTrue(perDraw instanceof PerDrawDynamicsImpl);
            // A generation-1 admission matches this publication (generation 1).
            IdScopeResult first = perDraw.enterEntity(ADMISSION, f.entityOrdinal("creeper"));
            assertTrue(first instanceof IdScopeResult.Entered);
            IdScopeToken outer = ((IdScopeResult.Entered) first).token();
            // Nested scope over the same producer: push/restore chain.
            IdScopeResult second = perDraw.enterEntity(ADMISSION, f.entityOrdinal("sheep"));
            assertTrue(second instanceof IdScopeResult.Entered);
            IdScopeToken inner = ((IdScopeResult.Entered) second).token();
            perDraw.leave(inner);
            perDraw.leave(outer);
            // Unassigned but valid ordinal enters with the mapped-or-zero value;
            // out-of-range ordinals are protocol rejections (IndexOutOfBounds).
            IdScopeResult unknown = perDraw.enterBlockEntity(ADMISSION,
                    f.stateOrdinal("oak_door", 5));
            assertTrue(unknown instanceof IdScopeResult.Entered,
                    "unknown ordinals push neutral zero");
            perDraw.leave(((IdScopeResult.Entered) unknown).token());
            // Wrong thread is rejected before any state change.
            Thread other = Thread.ofVirtual().unstarted(() -> { });
            IdScopeAdmission foreign = new IdScopeAdmission() {
                @Override public long generation() {
                    return 1;
                }

                @Override public long frameId() {
                    return 100;
                }

                @Override public Thread ownerThread() {
                    return other;
                }

                @Override public boolean active() {
                    return true;
                }

                @Override public boolean shadow() {
                    return false;
                }
            };
            assertEquals(IdScopeRejection.WRONG_THREAD,
                    ((IdScopeResult.Rejected) perDraw.enterEntity(foreign, 0)).reason());
            // Stale generation is rejected without state change.
            IdScopeAdmission stale = new IdScopeAdmission() {
                @Override public long generation() {
                    return 99;
                }

                @Override public long frameId() {
                    return 100;
                }

                @Override public Thread ownerThread() {
                    return Thread.currentThread();
                }

                @Override public boolean active() {
                    return true;
                }

                @Override public boolean shadow() {
                    return false;
                }
            };
            assertEquals(IdScopeRejection.STALE_GENERATION,
                    ((IdScopeResult.Rejected) perDraw.enterEntity(stale, 0)).reason());
            // Overflow: fixed-capacity stack; producer disables for the frame.
            IdScopeResult.Rejected overflow = null;
            for (int i = 0; i < 256; i++) {
                IdScopeResult result = perDraw.enterEntity(ADMISSION, 0);
                if (result instanceof IdScopeResult.Rejected rejected) {
                    overflow = rejected;
                    break;
                }
            }
            assertTrue(overflow != null, "stack limit must exist");
            assertEquals(IdScopeRejection.STACK_LIMIT, overflow.reason());
            // Drain re-arms both producers and neutralizes outstanding tokens.
            perDraw.resetFrame();
            assertTrue(perDraw.enterEntity(ADMISSION, f.entityOrdinal("creeper"))
                    instanceof IdScopeResult.Entered);
        }
    }
}
