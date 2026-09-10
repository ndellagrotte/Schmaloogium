// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.plan;

import com.schmaloogium.engine.expr.api.CompiledUniform;
import com.schmaloogium.engine.expr.api.CustomExpressionPlan;
import com.schmaloogium.engine.expr.api.ExpressionDiagnostic;
import java.util.List;
import java.util.Objects;

/** Immutable published plan (§4.1): metadata only, owning its executable program
 * privately; thread-safe. */
public final class CompiledPlan implements CustomExpressionPlan {

    private final String fingerprint;
    private final String fixedSchemaVersion;
    private final String contextSchemaVersion;
    private final String backendSemanticId;
    private final List<CompiledUniform> uniforms;
    private final List<ExpressionDiagnostic> diagnostics;
    public final Program program;

    public CompiledPlan(String fingerprint, String fixedSchemaVersion, String contextSchemaVersion,
                        String backendSemanticId, List<CompiledUniform> uniforms,
                        List<ExpressionDiagnostic> diagnostics, Program program) {
        this.fingerprint = Objects.requireNonNull(fingerprint, "fingerprint");
        this.fixedSchemaVersion = Objects.requireNonNull(fixedSchemaVersion, "fixedSchemaVersion");
        this.contextSchemaVersion = Objects.requireNonNull(contextSchemaVersion, "contextSchemaVersion");
        this.backendSemanticId = Objects.requireNonNull(backendSemanticId, "backendSemanticId");
        this.uniforms = List.copyOf(uniforms);
        this.diagnostics = List.copyOf(diagnostics);
        this.program = Objects.requireNonNull(program, "program");
    }

    @Override
    public String fingerprint() {
        return fingerprint;
    }

    @Override
    public String fixedSchemaVersion() {
        return fixedSchemaVersion;
    }

    @Override
    public String contextSchemaVersion() {
        return contextSchemaVersion;
    }

    @Override
    public String backendSemanticId() {
        return backendSemanticId;
    }

    @Override
    public List<CompiledUniform> uniforms() {
        return uniforms;
    }

    @Override
    public List<ExpressionDiagnostic> diagnostics() {
        return diagnostics;
    }
}
