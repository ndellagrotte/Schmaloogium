// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.glrec;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A bounded, deterministic call log — the same shape obligation [D-P2-4] and
 * PHASE_1_DOC §4.7.5 put on {@code GLCallLog.render()}: no timestamps, no identity hash
 * codes, no iteration-order dependence, oldest-to-newest order, oldest entries dropped
 * once the ring is full (with the drop count retained so a truncated log is never
 * mistaken for a complete one).
 */
public final class ScriptedCallLog {

    /** One recorded facade call; {@code detail} carries error annotations. */
    public record Call(String op, String subject, String detail) {

        @Override
        public String toString() {
            return op + "(" + subject + ")" + (detail.isEmpty() ? "" : " " + detail);
        }
    }

    private final ArrayDeque<Call> ring;
    private final int capacity;
    private long droppedCallCount;

    public ScriptedCallLog() {
        this(4096);
    }

    public ScriptedCallLog(int capacity) {
        this.capacity = capacity;
        this.ring = new ArrayDeque<>(capacity);
    }

    void record(String op, String subject, String detail) {
        if (ring.size() == capacity) {
            ring.pollFirst();
            droppedCallCount++;
        }
        ring.addLast(new Call(op, subject, detail));
    }

    /** Calls recorded so far, oldest first, exactly as recorded. */
    public List<Call> calls() {
        return List.copyOf(ring);
    }

    public long droppedCallCount() {
        return droppedCallCount;
    }

    /** Deterministic transcript: one line per call, plus a drop notice when truncated. */
    public String render() {
        StringBuilder out = new StringBuilder();
        for (Call call : ring) {
            out.append(call).append('\n');
        }
        if (droppedCallCount > 0) {
            out.append("; ").append(droppedCallCount).append(" earlier calls dropped\n");
        }
        return out.toString();
    }
}
