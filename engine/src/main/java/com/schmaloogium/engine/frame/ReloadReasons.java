// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

import java.util.Objects;
import java.util.Set;

/**
 * Closed reload trigger vocabulary (PHASE_7_DOC §5.1). A cause set travels with each
 * request; the newest request's set supersedes older ones rather than accumulating
 * resource/geometry flags.
 */

/** Immutable set carrier for {@link ReloadReason}. */
public record ReloadReasons(Set<ReloadReason> values) {

    public ReloadReasons {
        values = Set.copyOf(Objects.requireNonNull(values, "values"));
    }

    /** An empty reason set is invalid for submitted requests (contract-wide rule). */
    public boolean isEmpty() {
        return values.isEmpty();
    }
}
