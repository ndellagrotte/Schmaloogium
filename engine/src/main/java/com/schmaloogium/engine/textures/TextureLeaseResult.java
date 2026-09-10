// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.buffers.TextureOverlayLease;

import java.util.Objects;

/** Closed lease outcome (§2.2): the acquired immutable view or the rejection reason. */
public sealed interface TextureLeaseResult {
    record Acquired(TextureOverlayLease lease) implements TextureLeaseResult {
        public Acquired {
            Objects.requireNonNull(lease, "lease");
        }
    }

    record Rejected(TextureLeaseRejection reason) implements TextureLeaseResult {
        public Rejected {
            Objects.requireNonNull(reason, "reason");
        }
    }
}
