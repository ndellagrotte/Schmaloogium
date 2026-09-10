// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.pack.PackConfiguration;
import com.schmaloogium.engine.registry.ProgramRegistryView;
import com.schmaloogium.engine.registry.RegistryFingerprint;

import java.util.Objects;

/** Everything the estate builder needs (PHASE_5_DOC §2.2); all non-null. */
public record BufferBuildRequest(PackConfiguration configuration, ProgramRegistryView registry,
        RegistryFingerprint registryFingerprint, GLCapabilityProfile capabilities, BufferRuntimeInputs runtime,
        MainDepthSource mainDepth, GLDevice device, DiagnosticReporter diagnostics) {

    public BufferBuildRequest {
        Objects.requireNonNull(configuration, "configuration");
        Objects.requireNonNull(registry, "registry");
        Objects.requireNonNull(registryFingerprint, "registryFingerprint");
        Objects.requireNonNull(capabilities, "capabilities");
        Objects.requireNonNull(runtime, "runtime");
        Objects.requireNonNull(mainDepth, "mainDepth");
        Objects.requireNonNull(device, "device");
        Objects.requireNonNull(diagnostics, "diagnostics");
    }
}
