// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.compat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Early-bail contract (PHASE_1_DOC §4.10, §8.1.2): only the early subset runs at the
 * early point, a {@code Bail} there latches for the whole session, and the latch
 * outranks every later evaluation - before engine bootstrap there is no undo
 * (D-P1-50). Headless: registry transitions recorded, no MC types.
 */
class EarlyBailContractTest {

    private static final EarlyCompatContext EARLY_CTX = binaryName -> false;

    @BeforeEach
    @AfterEach
    void reset() {
        BailRegistry.resetForTests();
    }

    private static EarlyCompatCheck earlyCheck(String id, CompatVerdict earlyVerdict) {
        return new EarlyCompatCheck() {
            @Override
            public CompatVerdict checkEarly(EarlyCompatContext ctx) {
                return earlyVerdict;
            }

            @Override
            public String id() {
                return id;
            }

            @Override
            public CompatVerdict check(CompatContext ctx) {
                return new CompatVerdict.Ok();
            }
        };
    }

    private static CompatCheck runtimeCheck(String id, CompatVerdict verdict) {
        return new CompatCheck() {
            @Override
            public String id() {
                return id;
            }

            @Override
            public CompatVerdict check(CompatContext ctx) {
                return verdict;
            }
        };
    }

    private static CompatContext okRuntimeContext() {
        return new CompatContext() {
            @Override
            public boolean isModLoaded(String modId) {
                return false;
            }

            @Override
            public boolean isClassPresent(String binaryName) {
                return false;
            }

            @Override
            public com.schmaloogium.engine.gl.GLCapabilityProfile capabilities() {
                throw new IllegalStateException("not used");
            }
        };
    }

    @Test
    void earlyEvaluationRunsOnlyEarlySubset() {
        BailRegistry.register(earlyCheck("early-ok", new CompatVerdict.Ok()));
        // A runtime-only check that would bail must NOT influence the early point.
        BailRegistry.register(runtimeCheck("runtime-only", new CompatVerdict.Bail("late", List.of())));

        CompatEvaluation early = BailRegistry.evaluateEarly(EARLY_CTX);
        assertFalse(early.shouldBail());
        assertTrue(early.bails().isEmpty());
    }

    @Test
    void earlyBailLatchesForTheSession() {
        BailRegistry.register(earlyCheck("early-veto", new CompatVerdict.Bail("veto.key", List.of("x"))));

        assertTrue(BailRegistry.evaluateEarly(EARLY_CTX).shouldBail());

        // A later runtime evaluation inherits the latch even though every check is Ok.
        CompatEvaluation later = BailRegistry.evaluate(okRuntimeContext());
        assertTrue(later.shouldBail());
        assertEquals("veto.key", later.bails().get(0).reasonKey());
        assertEquals(List.of("x"), later.bails().get(0).args());
    }

    @Test
    void cleanEarlyEvaluationDoesNotLatch() {
        BailRegistry.register(earlyCheck("early-ok", new CompatVerdict.Ok()));

        assertFalse(BailRegistry.evaluateEarly(EARLY_CTX).shouldBail());
        assertFalse(BailRegistry.evaluate(okRuntimeContext()).shouldBail());
    }

    @Test
    void throwingEarlyCheckLatchesAsBail() {
        BailRegistry.register(new EarlyCompatCheck() {
            @Override
            public CompatVerdict checkEarly(EarlyCompatContext ctx) {
                throw new IllegalStateException("probe exploded");
            }

            @Override
            public String id() {
                return "early-thrower";
            }

            @Override
            public CompatVerdict check(CompatContext ctx) {
                return new CompatVerdict.Ok();
            }
        });

        CompatEvaluation early = BailRegistry.evaluateEarly(EARLY_CTX);
        assertTrue(early.shouldBail());
        assertEquals("schmaloogium.compat.check.threw", early.bails().get(0).reasonKey());

        // The synthetic verdict latches like any other early bail.
        assertTrue(BailRegistry.evaluate(okRuntimeContext()).shouldBail());
    }

    @Test
    void earlyOrderFollowsRegistrationOrder() {
        BailRegistry.register(earlyCheck("e-first",
                new CompatVerdict.Degrade("d1", List.of())));
        BailRegistry.register(earlyCheck("e-second",
                new CompatVerdict.Bail("b2", List.of())));

        CompatEvaluation early = BailRegistry.evaluateEarly(EARLY_CTX);
        assertTrue(early.shouldBail());
        // First verdict in evaluation order is the first registered check's.
        assertEquals("d1", early.degradations().get(0).reasonKey());
        assertEquals("b2", early.bails().get(0).reasonKey());
    }
}
