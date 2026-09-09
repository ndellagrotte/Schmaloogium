// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.List;

/** One schema-versioned ID-mapping file input. */
public record IdMappingFileInput(
        MappingKind kind,
        MappingFileState state,
        List<MappingRule> ordinaryRules,
        List<MappingRule> forced11300Rules,
        IdMappingFileFingerprint fingerprint) {

    public IdMappingFileInput {
        java.util.Objects.requireNonNull(kind, "kind");
        java.util.Objects.requireNonNull(state, "state");
        ordinaryRules = List.copyOf(java.util.Objects.requireNonNull(ordinaryRules, "ordinaryRules"));
        forced11300Rules = List.copyOf(java.util.Objects.requireNonNull(forced11300Rules, "forced11300Rules"));
        java.util.Objects.requireNonNull(fingerprint, "fingerprint");
    }
}
