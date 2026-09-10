// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/**
 * Opaque, single-close scope token (PHASE_9_DOC §4.12). The token authenticates the
 * stack kind, depth, admission kind, frame and generation; only the exact LIFO token can
 * close its scope, and drain invalidates every outstanding token. Glue carries tokens
 * call-locally and never stores them for later replay.
 */
public final class IdScopeToken {

    /** Which balanced stack the token belongs to. */
    public enum Kind { ENTITY, BLOCK_ENTITY }

    private final Kind kind;
    private final int depth;
    private final long generation;
    private final long frameId;
    private final long epoch;
    private final boolean shadow;
    private boolean closed;

    IdScopeToken(Kind kind, int depth, long generation, long frameId, long epoch,
            boolean shadow) {
        this.kind = kind;
        this.depth = depth;
        this.generation = generation;
        this.frameId = frameId;
        this.epoch = epoch;
        this.shadow = shadow;
    }

    public Kind kind() {
        return kind;
    }

    public int depth() {
        return depth;
    }

    public long generation() {
        return generation;
    }

    public long frameId() {
        return frameId;
    }

    public boolean shadow() {
        return shadow;
    }

    long epoch() {
        return epoch;
    }

    boolean isClosed() {
        return closed;
    }

    void markClosed() {
        closed = true;
    }

    @Override
    public String toString() {
        return "IdScopeToken[" + kind + " depth=" + depth + " gen=" + generation
                + " frame=" + frameId + " epoch=" + epoch
                + (shadow ? " shadow" : " main") + (closed ? " closed" : "") + "]";
    }
}
