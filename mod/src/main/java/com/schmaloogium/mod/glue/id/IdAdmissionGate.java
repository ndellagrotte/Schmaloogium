// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.id;

import com.schmaloogium.engine.config.id.IdScopeAdmission;

/**
 * The render-thread issuer of {@link IdScopeAdmission}s (PHASE_9_DOC §4.12: "Phase 7
 * issues main admission only from the accepted entity/block scope and shadow admission
 * only ... for the exact current shadow execution"). One admission is live at a time; it
 * is revoked ({@code active() == false}) when its scope closes, so a token minted under it
 * can never enter a later scope. Static because the hooks that need it are static.
 */
public final class IdAdmissionGate {

    private static final class Admission implements IdScopeAdmission {
        private final long generation;
        private final long frameId;
        private final Thread owner;
        private final boolean shadow;
        private volatile boolean active = true;

        Admission(long generation, long frameId, Thread owner, boolean shadow) {
            this.generation = generation;
            this.frameId = frameId;
            this.owner = owner;
            this.shadow = shadow;
        }

        @Override
        public long generation() {
            return generation;
        }

        @Override
        public long frameId() {
            return frameId;
        }

        @Override
        public Thread ownerThread() {
            return owner;
        }

        @Override
        public boolean active() {
            return active;
        }

        @Override
        public boolean shadow() {
            return shadow;
        }
    }

    private static volatile Admission current;

    private IdAdmissionGate() {
    }

    /** Opens the main admission for an accepted entity/block-entity scope. */
    public static IdScopeAdmission openMain(long generation, long frameId) {
        return open(generation, frameId, false);
    }

    /** Opens the shadow admission for the authenticated shadow execution's entity passes. */
    public static IdScopeAdmission openShadow(long generation, long frameId) {
        return open(generation, frameId, true);
    }

    private static IdScopeAdmission open(long generation, long frameId, boolean shadow) {
        Admission previous = current;
        if (previous != null) {
            previous.active = false; // a scope opening over a live one revokes it
        }
        Admission next = new Admission(generation, frameId, Thread.currentThread(), shadow);
        current = next;
        return next;
    }

    /** Revokes the given admission if it is the live one. */
    public static void close(IdScopeAdmission admission) {
        if (admission instanceof Admission a) {
            a.active = false;
            if (current == a) {
                current = null;
            }
        }
    }

    /** Revokes whatever is live (frame end / abort). */
    public static void closeAll() {
        Admission a = current;
        if (a != null) {
            a.active = false;
            current = null;
        }
    }

    /** The live admission or null. */
    public static IdScopeAdmission current() {
        Admission a = current;
        return a != null && a.active ? a : null;
    }
}
