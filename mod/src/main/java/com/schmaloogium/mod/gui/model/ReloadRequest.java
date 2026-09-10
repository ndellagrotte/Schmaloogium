// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.Objects;

/**
 * The classified reload request value (PHASE_12_DOC §2.2, §4.7.4): one ordered
 * lifecycle plus two independent additive flags, carried with a diagnostic-only cause.
 * Merging is associative, commutative in effect fields, and idempotent — the property
 * that makes "N rapid clicks produce one reload" hold by construction.
 */
public record ReloadRequest(ReloadLifecycle lifecycle,
                            boolean worldRendererReload,
                            boolean resourceReacquire,
                            ReloadCause cause) {

    public ReloadRequest {
        Objects.requireNonNull(lifecycle, "lifecycle");
        Objects.requireNonNull(cause, "cause");
    }

    /** The unit effect: everything off (still a valid coalescing baseline). */
    public static ReloadRequest none(ReloadCause cause) {
        return new ReloadRequest(ReloadLifecycle.NONE, false, false, cause);
    }

    /** max lifecycle, OR of both flags, latest cause for diagnostics. */
    public static ReloadRequest merge(ReloadRequest a, ReloadRequest b) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");
        return new ReloadRequest(
                ReloadLifecycle.max(a.lifecycle, b.lifecycle),
                a.worldRendererReload || b.worldRendererReload,
                a.resourceReacquire || b.resourceReacquire,
                b.cause);
    }

    public ReloadRequest merge(ReloadRequest newer) {
        return merge(this, newer);
    }
}
