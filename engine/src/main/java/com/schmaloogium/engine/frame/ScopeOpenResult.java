// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * The scope-open outcome (PHASE_7_DOC §5.1). Same closed tail as {@link FrameStepResult}
 * plus the disposition the vanilla caller must honor inside the scope.
 */
public sealed interface ScopeOpenResult {

    /** The scope is open; the caller draws according to the disposition. */
    record Opened(ScopeToken scope, DrawDisposition draw) implements ScopeOpenResult {
    }

    record Rejected(HookRejection reason) implements ScopeOpenResult {
    }

    record Aborted(FrameAbortReason reason) implements ScopeOpenResult {
    }

    record Failed(FailureId failure) implements ScopeOpenResult {
    }
}
