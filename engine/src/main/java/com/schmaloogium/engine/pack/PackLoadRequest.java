// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import java.nio.file.Path;

import com.schmaloogium.engine.pack.CompanionOptionMacros;

import java.util.Optional;

import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.config.InternalOptionSnapshot;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.gl.GLCapabilityProfile;

public record PackLoadRequest(
        Path shaderpacksDirectory,
        PackSelection selection,
        RuntimeIdentityData runtimeIdentity,
        GLCapabilityProfile capabilities,
        EngineOptionData engineOptions,
        CompanionOptionMacros companionOptionMacros,
        RendererFeatureData rendererFeatures,
        PersistenceFileAccess persistenceFiles,
        InternalPackSource internalPackSource,
        Optional<InternalOptionSnapshot> internalOptions,
        DiagnosticReporter diagnostics) {
}
