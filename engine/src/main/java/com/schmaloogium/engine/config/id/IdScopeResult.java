// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/** Closed per-draw scope entry result (PHASE_9_DOC §4.12). */
public sealed interface IdScopeResult {

    /** The scope opened; only this exact token may close it. */
    record Entered(IdScopeToken token) implements IdScopeResult {
    }

    /** The scope did not open; no value changed and nothing needs restoring. */
    record Rejected(IdScopeRejection reason) implements IdScopeResult {
    }
}
