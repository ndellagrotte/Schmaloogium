// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.config.ProfileName;
import com.schmaloogium.engine.pack.PackConfiguration;
import com.schmaloogium.engine.preprocess.MacroContribution;

import java.util.Objects;
import java.util.Optional;

/**
 * The complete compilation request (PHASE_4_DOC §2.2). {@code profileSelection} is a required
 * non-null wrapper; empty explicitly means no selected profile, not inferred profile intent.
 * The configuration is immutable and carries no hidden selection; §4.7 binds evaluation to
 * this exact pair and no preview OptionState is accepted.
 */
public record RegistryBuildRequest(
        PackConfiguration configuration,
        Optional<ProfileName> profileSelection,
        DimensionKey dimension,
        MacroContribution macroContribution,
        FixedSamplerLayoutPolicy samplerPolicy,
        GLCapabilityProfile capabilities,
        GLDevice device,
        DiagnosticReporter diagnostics) {

    public RegistryBuildRequest {
        Objects.requireNonNull(configuration, "configuration");
        Objects.requireNonNull(profileSelection, "profileSelection");
        Objects.requireNonNull(dimension, "dimension");
        Objects.requireNonNull(macroContribution, "macroContribution");
        Objects.requireNonNull(samplerPolicy, "samplerPolicy");
        Objects.requireNonNull(capabilities, "capabilities");
        Objects.requireNonNull(device, "device");
        Objects.requireNonNull(diagnostics, "diagnostics");
    }
}
