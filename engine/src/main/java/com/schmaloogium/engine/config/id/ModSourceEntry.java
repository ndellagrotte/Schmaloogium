// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.config.MappingKind;
import com.schmaloogium.engine.pack.ImmutableBytes;

/**
 * One bounded, origin-preserving per-mod mapping source (PHASE_9_DOC §4.3). Bytes are the
 * exact file body; the fingerprint is the deterministic SHA-256 over those bytes so a
 * changed mod file is detectable without re-reading anything. Mods contribute block,
 * item and entity files only — never layers.
 */
public record ModSourceEntry(String modId, MappingKind kind, String sourceName,
        ImmutableBytes bytes, String fingerprint) {

    /** Engine-side defense-in-depth re-validation of the glue's per-file bound. */
    public static final int MAX_ENTRY_BYTES = 4 * 1024 * 1024;

    public ModSourceEntry {
        if (modId == null || !modId.matches("[a-z0-9][a-z0-9_.-]{0,63}")) {
            throw new IllegalArgumentException("invalid mod id: " + modId);
        }
        if (kind != MappingKind.BLOCK && kind != MappingKind.ITEM && kind != MappingKind.ENTITY) {
            throw new IllegalArgumentException("mods never contribute " + kind + " mappings");
        }
        if (sourceName == null || sourceName.isEmpty()) {
            throw new IllegalArgumentException("sourceName must be non-empty");
        }
        java.util.Objects.requireNonNull(bytes, "bytes");
        if (bytes.size() > MAX_ENTRY_BYTES) {
            throw new IllegalArgumentException("mod source exceeds the per-file byte bound");
        }
        if (fingerprint == null || fingerprint.isEmpty()) {
            throw new IllegalArgumentException("fingerprint must be non-empty");
        }
    }
}
