// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.List;
import java.util.Objects;

/** One deterministic compile transaction (§4.1). */
public record CustomExpressionCompileRequest(
        String packConfigurationFingerprint,
        List<CustomExpressionSource> declarations,
        FixedExpressionInputSchema fixedInputs,
        ExpressionContextSchema context,
        String backendSemanticId) {

    public CustomExpressionCompileRequest {
        Objects.requireNonNull(packConfigurationFingerprint, "packConfigurationFingerprint");
        if (packConfigurationFingerprint.isEmpty()) {
            throw new IllegalArgumentException("packConfigurationFingerprint must be non-empty");
        }
        Objects.requireNonNull(declarations, "declarations");
        Objects.requireNonNull(fixedInputs, "fixedInputs");
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(backendSemanticId, "backendSemanticId");
        declarations = List.copyOf(declarations);
    }
}
