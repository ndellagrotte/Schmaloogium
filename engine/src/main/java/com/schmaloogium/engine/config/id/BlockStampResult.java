// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/**
 * The exact two-word {@code mc_Entity} payload Phase 10 stamps (PHASE_9_DOC §4.10):
 * {@code packedWord = (renderType & 0xffff) << 16 | (aliasedBlockId & 0xffff)} and
 * {@code metadata = legacyMetadata & 0xffff}. The representable 16-bit alias domain is
 * interpreted by low-bit pattern over {@code -32768..65535}; a full alias outside that
 * domain is never truncated silently — {@link Unrepresentable} carries a zero low word,
 * keeps the validated render type and metadata, and was reported once per rule at build
 * time.
 */
public sealed interface BlockStampResult {

    /** The packed word with the aliased low ID, plus state metadata. */
    record Present(int packedRenderTypeAndId, int metadata) implements BlockStampResult {
    }

    /** No alias for this state: zero low word, validated render type and metadata. */
    record Absent(int packedRenderTypeAndZero, int metadata) implements BlockStampResult {
    }

    /** Alias outside the 16-bit domain: zero low word, validated render type and metadata. */
    record Unrepresentable(int packedRenderTypeAndZero, int metadata)
            implements BlockStampResult {
    }
}
