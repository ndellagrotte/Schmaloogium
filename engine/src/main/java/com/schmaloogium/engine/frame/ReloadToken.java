// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

/**
 * The reload token (PHASE_7_DOC §5.1). Issued by {@link ShaderReloadController#request};
 * Accepted and Coalesced tokens are polled identically — no expiry, no supersession
 * history. Unissued or unknown tokens answer {@link ReloadStatus.Recorded.Unknown} exactly.
 */
public record ReloadToken(long value) {

    public ReloadToken {
        if (value < 0) {
            throw new IllegalArgumentException("reload token must be non-negative: " + value);
        }
    }
}
