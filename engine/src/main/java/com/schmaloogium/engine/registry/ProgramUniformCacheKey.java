// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The equality-based uniform cache key (PHASE_4_DOC §4.10). Stable for every activation of
 * the same effective provider/layout in one published generation; fallback children share the
 * provider's key.
 */
public record ProgramUniformCacheKey(
        long registryGeneration,
        ProgramSlotId effectiveProvider,
        ProgramUniformLayoutFingerprint linkedLayout) {

    public ProgramUniformCacheKey {
        java.util.Objects.requireNonNull(effectiveProvider, "effectiveProvider");
        java.util.Objects.requireNonNull(linkedLayout, "linkedLayout");
    }
}
