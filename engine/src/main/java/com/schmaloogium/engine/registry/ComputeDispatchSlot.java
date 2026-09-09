// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * One compute dispatch slot of a pass descriptor (PHASE_4_DOC §2.2). Dormant at v0.1: the
 * slots contain no source, work-group size, image/SSBO binding or dispatch function — that
 * information is intentionally impossible to construct before G8/S2 (§4.3). A non-gbuffers
 * raster pass may own {@link Primary} and {@link Companion} descriptors; every gbuffers
 * descriptor with a non-empty set is rejected at construction.
 */
public sealed interface ComputeDispatchSlot {

    /** The unsuffixed {@code .csh} dispatch. */
    record Primary() implements ComputeDispatchSlot {
    }

    /** One suffixed {@code _<suffix>.csh} dispatch; suffix is {@code 'a'}…{@code 'z'}. */
    record Companion(char suffix) implements ComputeDispatchSlot {

        public Companion {
            if (suffix < 'a' || suffix > 'z') {
                throw new IllegalArgumentException("compute companion suffix outside a..z: " + suffix);
            }
        }
    }
}
