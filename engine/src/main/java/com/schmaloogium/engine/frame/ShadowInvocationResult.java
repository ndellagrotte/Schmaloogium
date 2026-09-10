// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * The shadow invocation outcome (PHASE_7_DOC §5.1). {@code NotInstalled} is the v0.1
 * implementation state, never a health claim.
 */
public sealed interface ShadowInvocationResult {

    record NotInstalled() implements ShadowInvocationResult {
    }

    record Completed() implements ShadowInvocationResult {
    }

    record Rejected(ShadowRejection reason) implements ShadowInvocationResult {
    }

    record Failed(FailureId failure) implements ShadowInvocationResult {
    }
}
