// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/** Effective status of one slot's completed fallback walk (PHASE_4_DOC §4.6). */
public enum ProgramResolutionStatus {
    /** The requested slot's own shader compiled successfully. */
    SOURCED,
    /** An effective ancestor shader is used; {@code from} names the provider. */
    CHAIN,
    /** Nothing selected and no unmasked build failure remains. */
    ABSENT,
    /** Nothing selected and the completed walk hit at least one unmasked failed slot. */
    FAILED
}
