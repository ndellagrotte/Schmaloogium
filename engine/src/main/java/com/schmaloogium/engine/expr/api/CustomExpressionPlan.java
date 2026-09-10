// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.List;

/** Immutable, fingerprinted plan metadata; thread-safe and free of program/GL state (§4.1).
 * The private executable graph is owned internally and never exposed. */
public interface CustomExpressionPlan {
    String fingerprint();

    String fixedSchemaVersion();

    String contextSchemaVersion();

    String backendSemanticId();

    /** Valid upload-designated uniforms in original declaration order. */
    List<CompiledUniform> uniforms();

    /** All load diagnostics, in deterministic emission order. */
    List<ExpressionDiagnostic> diagnostics();
}
