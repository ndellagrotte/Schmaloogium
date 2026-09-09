// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import com.schmaloogium.engine.diag.EngineDiagnostic;

/**
 * Never-throwing result of {@link ShaderService#link} (PHASE_1_DOC §4.7.4). Carries
 * {@code success}, the driver log, and an {@link EngineDiagnostic}; {@code diagnostic} is
 * null on success. Under §4.7.4a a failed linked-input metadata acquisition makes the
 * link unsuccessful — never a default-QUADS success — and a failed link publishes no new
 * executable requirement.
 */
public record LinkResult(boolean success, String log, EngineDiagnostic diagnostic) {
}
