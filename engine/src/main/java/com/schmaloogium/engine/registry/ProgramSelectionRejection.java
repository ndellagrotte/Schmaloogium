// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The ordered selection rejections (PHASE_4_DOC §4.10); first failure wins. Phase 5 maps
 * these respectively to INVALID_PROGRAM_SELECTION, STALE_REGISTRY_GENERATION,
 * PROGRAM_SELECTION_MISMATCH, PROGRAM_SELECTION_MISMATCH, SAMPLER_LAYOUT_MISMATCH.
 */
public enum ProgramSelectionRejection {
    INVALID_ISSUER,
    STALE_GENERATION,
    STALE_CONTEXT,
    WRONG_STAGE_BAND,
    PROVIDER_LAYOUT_MISMATCH
}
