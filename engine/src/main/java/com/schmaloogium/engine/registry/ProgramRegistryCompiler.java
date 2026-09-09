// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The pure registry compiler (PHASE_4_DOC §2.2). Render-thread-only; never throws — all
 * build failures arrive as {@link RegistryBuildResult.ShadersOff} with the aggregated cause.
 */
public interface ProgramRegistryCompiler {

    RegistryBuildResult compile(RegistryBuildRequest request);
}
