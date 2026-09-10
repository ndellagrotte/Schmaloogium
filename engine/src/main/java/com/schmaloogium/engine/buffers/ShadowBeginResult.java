// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Result of beginPass: the sole acquired pass snapshot or a protocol rejection.
 */
public sealed interface ShadowBeginResult {

    record Acquired(ShadowPassSnapshot snapshot) implements ShadowBeginResult {

        public Acquired {
            java.util.Objects.requireNonNull(snapshot, "snapshot");
        }
    }

    record Rejected(ShadowProtocolRejection reason) implements ShadowBeginResult {

        public Rejected {
            java.util.Objects.requireNonNull(reason, "reason");
        }
    }
}
