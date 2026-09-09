// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.config.OptionCatalog;
import com.schmaloogium.engine.config.OptionState;
import com.schmaloogium.engine.diag.DiagnosticReporter;

public record OptionPersistenceReadRequest(
        PersistenceFileAccess files,
        PackOptionsTarget target,
        OptionCatalog catalog,
        OptionState baseline,
        DiagnosticReporter diagnostics) {

    public OptionPersistenceReadRequest {
        // null fields are validated by the codec's typed request validation, not here.
    }
}
