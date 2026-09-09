// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import com.schmaloogium.engine.diag.EngineDiagnostic;

/**
 * Never-throwing result of {@link ShaderService#compile} (PHASE_1_DOC §4.7.4): a checked
 * exception crossing the facade would make §G2.4 rung 3's "delete and report" a
 * control-flow problem instead of a data problem. Carries {@code success}, the driver
 * log, and an {@link EngineDiagnostic}; {@code diagnostic} is null on success.
 */
public record CompileResult(boolean success, String log, EngineDiagnostic diagnostic) {
}
