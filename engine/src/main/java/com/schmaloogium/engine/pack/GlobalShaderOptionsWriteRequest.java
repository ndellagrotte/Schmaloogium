// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.diag.DiagnosticReporter;

public record GlobalShaderOptionsWriteRequest(
        PersistenceFileAccess files,
        GlobalOptionsTarget target,
        EngineOptionData values,
        DiagnosticReporter diagnostics) {
}
