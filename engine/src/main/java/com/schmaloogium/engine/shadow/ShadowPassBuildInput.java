// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import java.util.Objects;

/**
 * The Phase-8 pass factory input (PHASE_8_DOC §2.2): ready plan plus the borrowed
 * collaborators for one publication. The registry fingerprint pairs the plan with the
 * construction-time registry; UniformRuntime, world port and diagnostics are retained
 * until the enclosing Phase 7 pipeline publication closes.
 */
public record ShadowPassBuildInput(
        ShadowPlan plan,
        com.schmaloogium.engine.registry.RegistryFingerprint registryFingerprint,
        com.schmaloogium.engine.uniforms.UniformRuntime uniforms,
        ShadowWorldPort world,
        ShadowBindingSource bindings,
        java.util.function.BooleanSupplier renderThread,
        com.schmaloogium.engine.diag.DiagnosticReporter reporter) {

    public ShadowPassBuildInput {
        Objects.requireNonNull(plan, "plan");
        Objects.requireNonNull(registryFingerprint, "registryFingerprint");
        Objects.requireNonNull(uniforms, "uniforms");
        Objects.requireNonNull(world, "world");
        Objects.requireNonNull(bindings, "bindings");
        renderThread = renderThread == null ? () -> true : renderThread;
    }
}
