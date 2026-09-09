// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import com.schmaloogium.engine.diag.EngineDiagnostic;

/**
 * Never-throwing result of {@link ShaderService#validate} (PHASE_1_DOC §4.7.4). Carries
 * {@code success}, the driver log, and an {@link EngineDiagnostic}; {@code diagnostic} is
 * null on success.
 */
public record ValidateResult(boolean success, String log, EngineDiagnostic diagnostic) {
}
