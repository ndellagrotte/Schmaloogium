// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

/** How the plan's companion demand was chosen (PHASE_13_DOC §4.1.1). */
public enum CompanionDemandSource {
    /** Both kinds follow the already-chosen preliminary pair. */
    DECLARED_SAMPLERS,
    /** Without optional R4: allocate each enabled kind regardless of linked demand. */
    ALWAYS_ON_FALLBACK,
    /** Inactive pack or fixed-unit incapability gated both kinds off. */
    CAPABILITY_GATED_OFF
}
