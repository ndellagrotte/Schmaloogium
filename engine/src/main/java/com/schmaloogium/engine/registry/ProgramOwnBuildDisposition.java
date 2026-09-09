// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The requested slot's own-build classification, decided strictly before fallback
 * (PHASE_4_DOC §4.6, D-P4-31). The field always describes the requested slot, never the
 * effective provider or worst ancestor. No {@code PENDING}/null/default disposition exists.
 */
public enum ProgramOwnBuildDisposition {
    /** Virtual/fixed catalog sentinels: no own build, {@code sourcePresent=false}. */
    NOT_APPLICABLE,
    /** Real slot with no selected source stages; never filled from base sources. */
    NO_SOURCE,
    /** Present source intentionally disabled by Phase 3's evaluated state. */
    DISABLED,
    /** Present enabled slot whose complete own planning/build validated successfully. */
    SUCCEEDED,
    /** Present enabled slot whose own build failed at any candidate-local stage. */
    FAILED
}
