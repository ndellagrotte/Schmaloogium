// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * Registry-wide build failure kinds (PHASE_4_DOC §4.12). The aggregate means no complete
 * registry is publishable; the final pack-wide disposition is {@code ShadersOff}.
 * {@code INVALID_PROGRAM_STATE} is the registry-wide reduction of Phase 3's
 * {@code InvalidState} — not a per-program FAILED/DISABLED.
 */
public enum RegistryFailureKind {
    NO_REQUIRED_TERMINAL,
    CAPABILITY,
    INVALID_PROGRAM_STATE,
    INVALID_SAMPLER_POLICY,
    UNSAFE_STATE,
    UNEXPECTED_BACKEND
}
