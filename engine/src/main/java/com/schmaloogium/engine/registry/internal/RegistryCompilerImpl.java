// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.registry.ProgramRegistryCompiler;
import com.schmaloogium.engine.registry.RegistryBuildRequest;
import com.schmaloogium.engine.registry.RegistryBuildResult;

/**
 * The package-private compiler implementation; delegates the whole pipeline to the
 * assembler (PHASE_4_DOC §4.7/§4.11).
 */
public final class RegistryCompilerImpl implements ProgramRegistryCompiler {

    @Override
    public RegistryBuildResult compile(RegistryBuildRequest request) {
        return RegistryAssembler.compile(request);
    }
}
