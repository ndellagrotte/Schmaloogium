// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.schmaloogium.engine.diag.EngineDiagnostic;

/**
 * The result of one apply/reset attempt. {@code reload} describes the already-submitted
 * effect — views never submit it again; empty on UNCHANGED/REJECTED/FAILED.
 */
public record ApplyOutcome(OptionApplyStatus status, Optional<ReloadRequest> reload,
                           List<EngineDiagnostic> diagnostics) {

    public ApplyOutcome {
        reload = reload == null ? Optional.empty() : reload;
        diagnostics = List.copyOf(diagnostics);
        Objects.requireNonNull(status, "status");
    }

    public static ApplyOutcome unchanged() {
        return new ApplyOutcome(OptionApplyStatus.UNCHANGED, Optional.empty(), List.of());
    }
}
