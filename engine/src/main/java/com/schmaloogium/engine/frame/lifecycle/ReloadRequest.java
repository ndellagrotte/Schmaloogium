// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.lifecycle;

import com.schmaloogium.engine.frame.ReloadReason;

import java.util.Objects;

/**
 * One queued reload request (PHASE_7_DOC §4.8). The lifecycle carries the effect; the two
 * flags are independent OR contributions; the cause is diagnostics-only. Immutable — the
 * queue always copies on submit.
 */
public record ReloadRequest(
        ReloadLifecycle lifecycle,
        boolean worldRendererReload,
        boolean resourceReacquire,
        ReloadReason cause) {

    public ReloadRequest {
        Objects.requireNonNull(lifecycle, "lifecycle");
        Objects.requireNonNull(cause, "cause");
    }

    /** The unit effect: everything off (still a valid coalescing baseline). */
    public static ReloadRequest none(ReloadReason cause) {
        return new ReloadRequest(ReloadLifecycle.NONE, false, false, cause);
    }

    /** Merges two requests: max lifecycle, OR flags, latest cause wins. */
    public ReloadRequest merge(ReloadRequest newer) {
        Objects.requireNonNull(newer, "newer");
        return new ReloadRequest(
                ReloadLifecycle.max(this.lifecycle, newer.lifecycle),
                this.worldRendererReload || newer.worldRendererReload,
                this.resourceReacquire || newer.resourceReacquire,
                newer.cause);
    }
}
