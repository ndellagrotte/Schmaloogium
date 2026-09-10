// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

/** Closed adoption outcome (PHASE_6_DOC §2.2/§4.1). {@code ADOPTED} applies the reset
 *  scope for the supplied reason before returning; {@code ALREADY_CURRENT} is idempotent;
 *  {@code REJECTED_RETIRED_GENERATION} is permanent — a retired runtime returns it for
 *  every generation, even the former current value, and can never be revived. */
public enum RegistryGenerationAdoptionResult {
    ADOPTED,
    ALREADY_CURRENT,
    REJECTED_RETIRED_GENERATION
}
