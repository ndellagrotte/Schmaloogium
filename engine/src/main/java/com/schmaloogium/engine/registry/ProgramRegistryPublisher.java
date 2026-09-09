// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The atomic registry publisher (PHASE_4_DOC §2.2). Render-thread-only. {@code publish}
 * increments generation exactly once for every accepted replacement, including accepted
 * shaders-off and forced RecoveredOff; pre-release rejection and failed candidate
 * compilation alone do not mutate publication state.
 */
public interface ProgramRegistryPublisher {

    PublishedRegistry current();

    PublicationResult publish(RegistryPublication publication, BarrierContext releaseContext);
}
