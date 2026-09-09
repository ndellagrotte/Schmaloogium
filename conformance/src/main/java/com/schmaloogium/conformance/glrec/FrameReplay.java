// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.glrec;

import java.util.ArrayList;
import java.util.List;

/**
 * Frame-windowed replay assertions — the harness's headless stand-in for the Phase 1
 * {@code ReplayAssertions} shape. The driver slices the {@link ScriptedCallLog} into
 * one window per frame; this class asserts the "stable frame loop" half of T0
 * mechanically: every frame window issues exactly the same call sequence as the first,
 * in order, with the same subjects — the fixed, time-independent behavior §4.3.4's
 * dense samples and §5.1.1's controlled clock demand from a conforming loop.
 */
public final class FrameReplay {

    private final List<List<ScriptedCallLog.Call>> frames = new ArrayList<>();

    /** Records one frame's call window (already sliced from the log by the driver). */
    public void addWindow(List<ScriptedCallLog.Call> window) {
        frames.add(List.copyOf(window));
    }

    public int frameCount() {
        return frames.size();
    }

    /** Every frame after the first must replay the first exactly; the returned list
     *  names each mismatch (empty when the loop is stable). */
    public List<String> assertStableLoop() {
        List<String> mismatches = new ArrayList<>();
        if (frames.size() < 2) {
            mismatches.add("a stable-loop replay needs at least two frames, got "
                + frames.size());
            return mismatches;
        }
        List<ScriptedCallLog.Call> reference = frames.get(0);
        for (int f = 1; f < frames.size(); f++) {
            List<ScriptedCallLog.Call> frame = frames.get(f);
            if (frame.size() != reference.size()) {
                mismatches.add("frame " + f + " issued " + frame.size() + " calls, frame 0 issued "
                    + reference.size());
                continue;
            }
            for (int i = 0; i < frame.size(); i++) {
                if (!frame.get(i).equals(reference.get(i))) {
                    mismatches.add("frame " + f + " call " + i + " was " + frame.get(i)
                        + ", frame 0 issued " + reference.get(i));
                }
            }
        }
        return mismatches;
    }
}
