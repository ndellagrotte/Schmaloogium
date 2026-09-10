// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * The reload request outcome (PHASE_7_DOC §5.1). A {@code Coalesced} token is polled
 * exactly like an Accepted one; rejection is mutation-free.
 */
public sealed interface ReloadResult {

    record Accepted(ReloadToken token) implements ReloadResult {
    }

    record Coalesced(ReloadToken token) implements ReloadResult {
    }

    record Rejected(ReloadRejection reason) implements ReloadResult {
    }
}
