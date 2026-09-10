// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.config.IdMappingInput;
import com.schmaloogium.engine.diag.DiagnosticReporter;

/**
 * The immutable, no-publication build request (PHASE_9_DOC §2.2). The mapping input must
 * carry the current Phase 3 schema — every other version is rejected before derivation,
 * never inferred into an upgrade. Mod sources enter as bounded bytes and are parsed with
 * the exact published parser environment through the Phase 3 parser the builder was
 * created with.
 */
public record IdBuildRequest(
        IdMappingInput mappings,
        IdRegistrySnapshot registries,
        ModIdSourceSnapshot modSources,
        CompatibilityAliasCatalog aliases,
        LegacyTagCatalog tags,
        HandLightPolicy handLightPolicy,
        DiagnosticReporter diagnostics) {

    public IdBuildRequest {
        java.util.Objects.requireNonNull(mappings, "mappings");
        java.util.Objects.requireNonNull(registries, "registries");
        java.util.Objects.requireNonNull(modSources, "modSources");
        java.util.Objects.requireNonNull(aliases, "aliases");
        java.util.Objects.requireNonNull(tags, "tags");
        java.util.Objects.requireNonNull(handLightPolicy, "handLightPolicy");
        java.util.Objects.requireNonNull(diagnostics, "diagnostics");
    }
}
