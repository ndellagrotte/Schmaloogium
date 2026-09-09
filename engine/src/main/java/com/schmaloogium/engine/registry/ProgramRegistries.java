// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import com.schmaloogium.engine.registry.internal.ProductionAssembler;
import com.schmaloogium.engine.registry.internal.RegistryCompilerImpl;
import com.schmaloogium.engine.registry.internal.RegistryContexts;
import com.schmaloogium.engine.registry.internal.RegistryPublisherImpl;

/**
 * The Phase-4 access facade: the pure compiler, the atomic publisher over a private context
 * source, and the production barrier composer (PHASE_4_DOC §2.2/§4.10/§4.11).
 */
public final class ProgramRegistries {

    private ProgramRegistries() {
    }

    public static ProgramRegistryCompiler compiler() {
        return new RegistryCompilerImpl();
    }

    /** The initial snapshot is an empty shaders-off publication at generation zero. */
    public static ProgramRegistryPublisher publisher() {
        return new RegistryPublisherImpl(new RegistryContexts(), 0L);
    }

    public static ProductionBarrierComposer productionComposer() {
        return ProductionAssembler.create();
    }
}
