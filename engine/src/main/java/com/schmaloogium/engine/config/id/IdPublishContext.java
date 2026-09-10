// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/**
 * Render-thread safe-boundary evidence supplied by Phase 7 with publish/deactivate calls
 * (PHASE_9_DOC §4.1, §5.3). The stage is a short protocol tag of the boundary (for
 * example the post-texture publication point of the coordinated rebuild transaction);
 * it is provenance, never a capability.
 */
public record IdPublishContext(String stage) {

    public IdPublishContext {
        if (stage == null || stage.isEmpty()) {
            throw new IllegalArgumentException("publication stage must be non-empty");
        }
    }
}
