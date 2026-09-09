// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import com.schmaloogium.engine.preprocess.ShaderSourceStage;

import java.util.List;
import java.util.Optional;

/**
 * Candidate-build evidence produced before the detached resolution snapshot is created
 * (PHASE_4_DOC §4.12). Driver logs are sanitized (no source text, no line breaks);
 * source attribution is retained as Phase 3 source-map diagnostic IDs, never pack source
 * text. {@link #samplerLayout()} is present and mandatory for {@code SAMPLER_LAYOUT}
 * failures and absent for earlier stages. {@link #projectionDetail()} is a deterministic,
 * sanitized, non-empty single-line serialization of stage, stable diagnostic ID and
 * sanitized log. Immutable.
 */
public final class ProgramBuildFailure {

    private final ProgramSlotId requested;
    private final Optional<String> sourceStem;
    private final ProgramBuildStage stage;
    private final Optional<ShaderSourceStage> shaderStage;
    private final String sanitizedDriverLog;
    private final List<String> sourceDiagnosticIds;
    private final List<ProgramSlotId> fallbackPath;
    private final String disposition;
    private final String diagnosticId;
    private final Optional<ProgramSamplerLayout> samplerLayout;
    private final String projectionDetail;

    public ProgramBuildFailure(
            ProgramSlotId requested,
            Optional<String> sourceStem,
            ProgramBuildStage stage,
            Optional<ShaderSourceStage> shaderStage,
            String sanitizedDriverLog,
            List<String> sourceDiagnosticIds,
            List<ProgramSlotId> fallbackPath,
            String disposition,
            String diagnosticId,
            Optional<ProgramSamplerLayout> samplerLayout) {
        this.requested = java.util.Objects.requireNonNull(requested, "requested");
        this.sourceStem = sourceStem == null ? Optional.empty() : sourceStem;
        this.stage = java.util.Objects.requireNonNull(stage, "stage");
        this.shaderStage = shaderStage == null ? Optional.empty() : shaderStage;
        this.sanitizedDriverLog = sanitize(sanitizedDriverLog);
        this.sourceDiagnosticIds = List.copyOf(
            sourceDiagnosticIds == null ? List.of() : sourceDiagnosticIds);
        this.fallbackPath = List.copyOf(
            fallbackPath == null ? List.of() : fallbackPath);
        this.disposition = java.util.Objects.requireNonNull(disposition, "disposition");
        this.diagnosticId = requireNonBlank(diagnosticId, "diagnosticId");
        if (stage == ProgramBuildStage.SAMPLER_LAYOUT) {
            this.samplerLayout = Optional.ofNullable(
                java.util.Objects.requireNonNull(samplerLayout, "samplerLayout").orElse(null));
        } else {
            this.samplerLayout = Optional.empty();
        }
        StringBuilder detail = new StringBuilder(stage.name())
            .append('|').append(this.diagnosticId);
        if (!this.sanitizedDriverLog.isEmpty()) {
            detail.append('|').append(this.sanitizedDriverLog);
        }
        this.projectionDetail = detail.toString();
    }

    public ProgramSlotId requested() {
        return requested;
    }

    public Optional<String> sourceStem() {
        return sourceStem;
    }

    public ProgramBuildStage stage() {
        return stage;
    }

    public Optional<ShaderSourceStage> shaderStage() {
        return shaderStage;
    }

    public String sanitizedDriverLog() {
        return sanitizedDriverLog;
    }

    public List<String> sourceDiagnosticIds() {
        return sourceDiagnosticIds;
    }

    public List<ProgramSlotId> fallbackPath() {
        return fallbackPath;
    }

    public String disposition() {
        return disposition;
    }

    public String diagnosticId() {
        return diagnosticId;
    }

    public Optional<ProgramSamplerLayout> samplerLayout() {
        return samplerLayout;
    }

    /** Deterministic, sanitized, non-empty single-line failure detail (§4.12). */
    public String projectionDetail() {
        return projectionDetail;
    }

    @Override
    public String toString() {
        return projectionDetail;
    }

    private static String sanitize(String log) {
        if (log == null) {
            return "";
        }
        String flattened = log.replaceAll("\\s+", " ").trim();
        return flattened.length() > 512 ? flattened.substring(0, 512) : flattened;
    }

    private static String requireNonBlank(String value, String field) {
        java.util.Objects.requireNonNull(value, field);
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(field + " must be non-blank");
        }
        return trimmed;
    }
}
