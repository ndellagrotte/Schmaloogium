// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * The scope-close outcome (PHASE_7_DOC §5.1). {@code Closed} consumes exactly its scope
 * token and reports the disposition the resumed parent context draws under.
 */
public sealed interface ScopeCloseResult {

    /** The scope closed and the parent resumed with the given disposition. */
    record Closed(DrawDisposition resumedParent) implements ScopeCloseResult {
    }

    record Rejected(HookRejection reason) implements ScopeCloseResult {
    }

    record Aborted(FrameAbortReason reason) implements ScopeCloseResult {
    }

    record Failed(FailureId failure) implements ScopeCloseResult {
    }
}
