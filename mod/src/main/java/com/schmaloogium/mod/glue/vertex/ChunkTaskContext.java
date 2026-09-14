// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.vertex;

import com.schmaloogium.engine.config.id.BlockStampResult;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * The per-task context a chunk compile task carries (PHASE_10_DOC §4.4): the vertex
 * publication captured once at task start (so a swap mid-task never changes the layout
 * the task writes) and the block identity stack the per-block render scopes push. Lives in
 * a plain {@link ThreadLocal} installed by H10-TASK and detached in its finally; never an
 * inheritable thread-local, never a render-thread stack (§4.4 :686).
 */
public final class ChunkTaskContext {

    private static final ThreadLocal<ChunkTaskContext> CURRENT = new ThreadLocal<>();
    private static final BlockStampResult NEUTRAL = new BlockStampResult.Absent(0, 0);

    private final VertexEpochs.Publication publication;
    private final Deque<BlockStampResult> stamps = new ArrayDeque<>();

    private ChunkTaskContext(VertexEpochs.Publication publication) {
        this.publication = publication;
    }

    /** Installs the task context over the current publication; returns the previous (nested guard). */
    public static ChunkTaskContext install() {
        ChunkTaskContext previous = CURRENT.get();
        CURRENT.set(new ChunkTaskContext(VertexEpochs.current()));
        return previous;
    }

    /** Detaches the context, restoring the previous one (null clears). */
    public static void detach(ChunkTaskContext previous) {
        if (previous == null) {
            CURRENT.remove();
        } else {
            CURRENT.set(previous);
        }
    }

    public static ChunkTaskContext current() {
        return CURRENT.get();
    }

    /** True when this task builds extended records. */
    public boolean extended() {
        return publication != null;
    }

    public VertexEpochs.Publication publication() {
        return publication;
    }

    public void pushStamp(BlockStampResult stamp) {
        stamps.push(stamp == null ? NEUTRAL : stamp);
    }

    public void popStamp() {
        stamps.poll();
    }

    /** The innermost block scope's stamp, neutral outside every scope. */
    public BlockStampResult stamp() {
        BlockStampResult top = stamps.peek();
        return top == null ? NEUTRAL : top;
    }

    public int depth() {
        return stamps.size();
    }

    public void resetStamps() {
        stamps.clear();
    }
}
