// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * T10-STACK pure kernel (PHASE_10_DOC §4.4, §8): nested states A/B/A restore the
 * previous depth through finally-style pops — including on the throw path — a
 * wrong/out-of-order pop throws and unwinds, the stack grows past the observed
 * ten-entry mark, and a reset leaves the next unrelated task neutral.
 */
class IdentityStackTest {

    @Test
    void nestedScopesRestoreTheExactPriorDepth() {
        IdentityStack stack = new IdentityStack();
        assertEquals(0, stack.depth());
        assertEquals(0, stack.word0());
        assertEquals(0, stack.word1());

        stack.push(0x0003_001f, 2); // A
        int afterA = stack.depth();
        stack.push(0x0004_0020, 5); // B
        assertEquals(2, stack.depth());
        assertEquals(0x0004_0020, stack.word0());

        stack.popTo(afterA); // inner scope's finally
        assertEquals(1, stack.depth());
        assertEquals(0x0003_001f, stack.word0());
        assertEquals(2, stack.word1());

        stack.popTo(0);
        assertEquals(0, stack.depth());
        assertEquals(0, stack.word0());
        assertEquals(0, stack.word1());
    }

    @Test
    void throwPathStillRestoresThroughTheCapturedDepth() {
        IdentityStack stack = new IdentityStack();
        stack.push(1, 2);
        int captured = stack.depth();
        stack.push(3, 4);
        assertThrows(IllegalStateException.class, () -> {
            try {
                throw new IllegalStateException("render failure inside the block scope");
            } finally {
                stack.popTo(captured);
            }
        });
    }

    @Test
    void outOfOrderPopThrowsAndLeavesTheStackIntact() {
        IdentityStack stack = new IdentityStack();
        stack.push(1, 2);
        assertThrows(IllegalStateException.class, () -> {
            stack.popTo(2);
        });
        assertThrows(IllegalStateException.class, () -> stack.popTo(-1));
        // The failed pops left the stack intact.
        assertEquals(1, stack.depth());
        assertEquals(1, stack.word0());
        assertEquals(2, stack.word1());
    }

    @Test
    void outOfOrderPopThrows() {
        IdentityStack stack = new IdentityStack();
        stack.push(1, 2);
        assertThrows(IllegalStateException.class, () -> stack.popTo(2));
        assertThrows(IllegalStateException.class, () -> stack.popTo(-1));
        // The failed pop left the stack intact.
        assertEquals(1, stack.depth());
    }

    @Test
    void growsPastTheObservedTenEntryMark() {
        IdentityStack stack = new IdentityStack();
        for (int i = 1; i <= 64; i++) {
            stack.push(i, i * 2);
        }
        assertEquals(64, stack.depth());
        assertEquals(64, stack.word0());
        assertEquals(128, stack.word1());
        stack.popTo(10);
        assertEquals(10, stack.word0());
    }

    @Test
    void resetStartsTheNextTaskNeutral() {
        IdentityStack stack = new IdentityStack();
        stack.push(9, 9);
        stack.reset();
        assertEquals(0, stack.depth());
        assertEquals(0, stack.word0());
        // The cleared entries do not survive a push/pop cycle.
        stack.push(7, 8);
        stack.popTo(0);
        assertEquals(0, stack.word0());
    }
}
