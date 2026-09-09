// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The compiler outcome (PHASE_4_DOC §2.2): a ready candidate for publication, or a closed
 * shaders-off failure with the aggregated cause. Never throws.
 */
public sealed interface RegistryBuildResult {

    record Ready(CompiledRegistryCandidate candidate) implements RegistryBuildResult {
    }

    record ShadersOff(RegistryBuildFailure failure) implements RegistryBuildResult {
    }
}
