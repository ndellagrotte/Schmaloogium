// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex.internal;

/**
 * Primitive per-builder entity-data stack storage (PHASE_10_DOC §2.2, §4.4): exactly
 * two primitive integers per entry, plus scope depth. The stack starts neutral at
 * {@code (0, 0)}; a block push stores exactly the two words of the context's matching
 * Phase 9 lookup result. A scope captures its prior depth and restores that exact prior
 * depth in {@code finally} — success, false return, cancellation or exception alike.
 *
 * <p>Storage grows geometrically from a small primitive array (the observed ten-entry
 * stack is not a limit); an allocation failure propagates and terminates the shader
 * operation safely. An out-of-order pop (a depth greater than the current depth)
 * throws: the calling glue invalidates the product and unwinds to the task boundary.
 * Private to the vertex phase — the task-to-context association itself lives in mod
 * glue, never here.
 */
public final class IdentityStack {

    private static final int INITIAL_ENTRIES = 10;

    private int[] words = new int[INITIAL_ENTRIES * 2];
    private int depth;

    /** The current scope depth; zero is the neutral base. */
    public int depth() {
        return depth;
    }

    /** The current identity word 0 (the packed render-type-and-id word). */
    public int word0() {
        return depth == 0 ? 0 : words[(depth - 1) * 2];
    }

    /** The current identity word 1 (the metadata word). */
    public int word1() {
        return depth == 0 ? 0 : words[(depth - 1) * 2 + 1];
    }

    /** Pushes exactly the two identity words of one block scope. */
    public void push(int word0, int word1) {
        if ((depth + 1) * 2 > words.length) {
            int[] grown = new int[words.length * 2];
            System.arraycopy(words, 0, grown, 0, words.length);
            words = grown;
        }
        words[depth * 2] = word0;
        words[depth * 2 + 1] = word1;
        depth++;
    }

    /**
     * Restores the exact prior scope depth captured before a nested scope. A depth
     * greater than the current depth is an out-of-order pop and throws.
     */
    public void popTo(int priorDepth) {
        if (priorDepth < 0 || priorDepth > depth) {
            throw new IllegalStateException(
                    "out-of-order stack pop: " + priorDepth + " over depth " + depth);
        }
        depth = priorDepth;
    }

    /** Returns the stack to the neutral base for the next unrelated task. */
    public void reset() {
        depth = 0;
    }
}
