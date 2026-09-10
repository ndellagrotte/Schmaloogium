// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Result of a {@code shadow()} estate query: available, not requested, or unavailable
 * (PHASE_5_DOC §4.10).
 */
public sealed interface ShadowEstateResult
        permits ShadowEstateAvailable, ShadowEstateNotRequested, ShadowEstateUnavailable {
}
