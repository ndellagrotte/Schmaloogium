// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.config.OptionCatalog;
import com.schmaloogium.engine.config.OptionState;

import com.schmaloogium.engine.diag.DiagnosticReporter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Engine-issued sealed option catalog; consumers cannot mint one. */
public sealed interface OptionCatalog permits OptionCatalogValue {

    List<OptionDefinition> definitions();

    Optional<OptionDefinition> find(String name);

    OptionState defaultState();

    OptionStateResult constructState(
        Map<String, OptionValue> values, DiagnosticReporter diagnostics);

    OptionStateResult updateState(
        OptionState baseline, String name, OptionValue value, DiagnosticReporter diagnostics);

    OptionStateValidation validate(OptionState candidate);

    InternalOptionCaptureResult captureInternalOptions(
        OptionState state, DiagnosticReporter diagnostics);
}
