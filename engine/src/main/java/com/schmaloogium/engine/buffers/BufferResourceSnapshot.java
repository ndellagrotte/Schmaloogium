// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import java.util.Objects;

/** Resource evidence snapshot handed out with plan results (PHASE_5_DOC §2.2). */
public sealed interface BufferResourceSnapshot
        permits BufferResourceSnapshot.Available, BufferResourceSnapshot.Unavailable {

    /** Resources are realized and inspectable. */
    record Available(BufferResourceProjection projection) implements BufferResourceSnapshot {

        public Available {
            Objects.requireNonNull(projection, "projection");
        }
    }

    /** Resources cannot be projected; the reason stays engine-internal. */
    record Unavailable(ResourceProjectionUnavailableReason reason) implements BufferResourceSnapshot {

        public Unavailable {
            Objects.requireNonNull(reason, "reason");
        }
    }
}
