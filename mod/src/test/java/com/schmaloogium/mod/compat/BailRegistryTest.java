// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.compat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The bail registry mechanism (PHASE_1_DOC §4.10, §8.1.2): evaluation semantics,
 * registration discipline and the latched early veto - all headless, no MC types.
 */
class BailRegistryTest {

    @AfterEach
    void reset() {
        BailRegistry.resetForTests();
    }

    private static CompatCheck check(String id, CompatVerdict verdict) {
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

    private static CompatContext anyContext() {
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
                throw new IllegalStateException("not used by these checks");
            }
        };
    }

    @Test
    void emptyRegistryEvaluatesClean() {
        CompatEvaluation evaluation = BailRegistry.evaluate(anyContext());
        assertFalse(evaluation.shouldBail());
        assertTrue(evaluation.bails().isEmpty());
        assertTrue(evaluation.degradations().isEmpty());
    }

    @Test
    void verdictsAccumulateInRegistrationOrder() {
        BailRegistry.register(check("c-late-ok", new CompatVerdict.Ok()));
        BailRegistry.register(check("c-degrade",
                new CompatVerdict.Degrade("key.degrade", List.of("a"))));
        BailRegistry.register(check("c-bail", new CompatVerdict.Bail("key.bail", List.of("b"))));

        CompatEvaluation evaluation = BailRegistry.evaluate(anyContext());
        assertTrue(evaluation.shouldBail());
        assertEquals(1, evaluation.bails().size());
        assertEquals("key.bail", evaluation.bails().get(0).reasonKey());
        assertEquals(List.of("b"), evaluation.bails().get(0).args());
        assertEquals(1, evaluation.degradations().size());
        assertEquals("key.degrade", evaluation.degradations().get(0).reasonKey());
    }

    @Test
    void throwingCheckIsBailNotCrash() {
        BailRegistry.register(new CompatCheck() {
            @Override
            public String id() {
                return "c-thrower";
            }

            @Override
            public CompatVerdict check(CompatContext ctx) {
                throw new IllegalStateException("cannot decide");
            }
        });

        CompatEvaluation evaluation = BailRegistry.evaluate(anyContext());
        assertTrue(evaluation.shouldBail());
        assertEquals("schmaloogium.compat.check.threw",
                evaluation.bails().get(0).reasonKey());
        assertEquals("c-thrower", evaluation.bails().get(0).args().get(0));
    }

    @Test
    void okVerdictsLeaveNoTrace() {
        BailRegistry.register(check("c-ok", new CompatVerdict.Ok()));
        CompatEvaluation evaluation = BailRegistry.evaluate(anyContext());
        assertFalse(evaluation.shouldBail());
        assertTrue(evaluation.bails().isEmpty());
        assertTrue(evaluation.degradations().isEmpty());
    }

    @Test
    void duplicateRegistrationOfSameObjectIsNoOp() {
        CompatCheck check = check("c-dup", new CompatVerdict.Degrade("d", List.of()));
        BailRegistry.register(check);
        BailRegistry.register(check);

        CompatEvaluation evaluation = BailRegistry.evaluate(anyContext());
        assertEquals(1, evaluation.degradations().size());
    }

    @Test
    void sameIdDifferentCheckFailsRegistration() {
        BailRegistry.register(check("c-collision", new CompatVerdict.Ok()));
        assertThrows(IllegalArgumentException.class,
                () -> BailRegistry.register(check("c-collision", new CompatVerdict.Ok())));
    }
}
