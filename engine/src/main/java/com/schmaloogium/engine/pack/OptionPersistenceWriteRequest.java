// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.config.OptionCatalog;
import com.schmaloogium.engine.config.OptionState;
import com.schmaloogium.engine.diag.DiagnosticReporter;

public record OptionPersistenceWriteRequest(
        PersistenceFileAccess files,
        PackOptionsTarget target,
        OptionCatalog catalog,
        OptionState state,
        DiagnosticReporter diagnostics) {
}
