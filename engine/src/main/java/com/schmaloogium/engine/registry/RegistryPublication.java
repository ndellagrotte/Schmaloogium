// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The publication input (PHASE_4_DOC §2.2): a ready pair of caller-owned candidates, or an
 * explicit shaders-off publication carrying the cause.
 */
public sealed interface RegistryPublication {

    record Ready(CompiledRegistryCandidate registry, BarrierPublicationCandidate barrier)
            implements RegistryPublication {
    }

    record ShadersOff(RegistryBuildFailure cause) implements RegistryPublication {
    }
}
