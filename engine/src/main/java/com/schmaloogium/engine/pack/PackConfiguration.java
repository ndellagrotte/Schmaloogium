// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.config.ProfileName;

import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.pack.PackIdentity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.schmaloogium.engine.config.CustomExpressionDecl;
import com.schmaloogium.engine.config.IdMappingInput;
import com.schmaloogium.engine.config.MacroConfiguration;
import com.schmaloogium.engine.config.OptionConfiguration;
import com.schmaloogium.engine.config.ProgramStateEvaluationResult;
import com.schmaloogium.engine.config.ProgramStateEvaluator;
import com.schmaloogium.engine.config.ResourceRequirements;
import com.schmaloogium.engine.config.ShaderPropertiesModel;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.preprocess.SourceCatalog;

/** The single validated downstream truth at the current schema. */
public record PackConfiguration(
        int schemaVersion,
        PackIdentity pack,
        CompatibilityStatus compatibility,
        Map<DimensionKey, DimensionConfiguration> dimensions,
        SourceCatalog sources,
        PackAssetSnapshot assets,
        OptionConfiguration options,
        MacroConfiguration macros,
        ShaderPropertiesModel properties,
        ResourceRequirements resources,
        IdMappingInput idMappings,
        List<EngineDiagnostic> diagnostics,
        ConfigurationFingerprint fingerprint) {

    public PackConfiguration {
        java.util.SortedMap<DimensionKey, DimensionConfiguration> ordered =
            new java.util.TreeMap<>();
        ordered.putAll(java.util.Objects.requireNonNull(dimensions, "dimensions"));
        dimensions = java.util.Collections.unmodifiableSortedMap(ordered);
        java.util.Objects.requireNonNull(pack, "pack");
        java.util.Objects.requireNonNull(compatibility, "compatibility");
        java.util.Objects.requireNonNull(sources, "sources");
        java.util.Objects.requireNonNull(assets, "assets");
        if (assets.pack() != null && !assets.pack().equals(pack)) {
            throw new IllegalArgumentException("assets from a foreign load snapshot");
        }
        java.util.Objects.requireNonNull(options, "options");
        java.util.Objects.requireNonNull(macros, "macros");
        java.util.Objects.requireNonNull(properties, "properties");
        java.util.Objects.requireNonNull(resources, "resources");
        java.util.Objects.requireNonNull(idMappings, "idMappings");
        if (idMappings.schemaVersion() != schemaVersion) {
            throw new IllegalArgumentException("nested idMappings schema mismatch");
        }
        diagnostics = List.copyOf(diagnostics);
        java.util.Objects.requireNonNull(fingerprint, "fingerprint");
    }

    public ProgramStateEvaluationResult evaluateProgramStates(
            Optional<ProfileName> selectedProfile, DiagnosticReporter diagnostics) {
        return ProgramStateEvaluator.evaluate(this, options.state(), selectedProfile, diagnostics);
    }

    /** Named immutable projection of the properties custom-expression list. */
    public List<CustomExpressionDecl> customExpressions() {
        return properties().customExpressions();
    }
}
