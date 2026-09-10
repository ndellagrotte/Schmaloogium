// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.frame.spi.AtlasBindingEvidence;

/**
 * The P13-side observation port installed by Phase 7's issuer adapter at owner construction
 * (PHASE_13_DOC §4.4, D-P13-43). The adapter translates the opaque P7 evidence into the
 * association/base observation the owner needs and answers lease currentness from its
 * private serial. Public evidence stays opaque; the owner never constructs evidence.
 */
public interface AtlasBindingObserver {

    /** Authenticates one evidence instance. Never returns null. */
    AtlasBindingObservation authenticate(AtlasBindingEvidence evidence);

    /** Whether this currentness token is still the latest authenticated commit. */
    boolean isLatest(Object currentnessToken);
}
