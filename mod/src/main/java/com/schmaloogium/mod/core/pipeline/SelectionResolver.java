// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core.pipeline;

import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.pack.FilesystemCandidateReference;
import com.schmaloogium.engine.pack.FilesystemCandidateResolution;
import com.schmaloogium.engine.pack.PackCandidate;
import com.schmaloogium.engine.pack.PackCandidateStatus;
import com.schmaloogium.engine.pack.PackDiscoveryRequest;
import com.schmaloogium.engine.pack.PackDiscoveryResult;
import com.schmaloogium.engine.pack.PackFrontEnd;
import com.schmaloogium.engine.pack.PackSelection;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Turns the durable {@code shaderPack} value into a P3 selection by re-discovering and
 * re-resolving on every call (PHASE_3_DOC §5: a {@code PackCandidateId} is generation
 * identity, so only the {@link FilesystemCandidateReference} is ever carried). Every
 * non-resolvable outcome degrades to {@link PackSelection.Off} with one reported reason —
 * the same decode the selection GUI performs on restore.
 */
public final class SelectionResolver implements Supplier<PackSelection> {

    private final PackFrontEnd frontEnd;
    private final Path shaderpacksDirectory;
    private final Supplier<String> durableSelection;
    private final DiagnosticReporter diagnostics;

    public SelectionResolver(PackFrontEnd frontEnd, Path shaderpacksDirectory,
                             Supplier<String> durableSelection, DiagnosticReporter diagnostics) {
        this.frontEnd = Objects.requireNonNull(frontEnd, "frontEnd");
        this.shaderpacksDirectory = Objects.requireNonNull(shaderpacksDirectory, "shaderpacksDirectory");
        this.durableSelection = Objects.requireNonNull(durableSelection, "durableSelection");
        this.diagnostics = Objects.requireNonNull(diagnostics, "diagnostics");
    }

    @Override
    public PackSelection get() {
        String durable = durableSelection.get();
        if (durable == null || durable.isBlank() || durable.equals("off")) {
            return new PackSelection.Off();
        }
        if (durable.equals("(internal)")) {
            // No InternalPackSource exists on the mod side at v0.1; report and stay off.
            diagnostics.report(new EngineDiagnostic(DiagnosticSeverity.WARN, UserChannel.LOG_ONLY,
                    "schmaloogium.warn.pipeline.internalUnavailable", List.of(), durable,
                    LogChannels.PACK));
            return new PackSelection.Off();
        }
        FilesystemCandidateReference reference;
        try {
            reference = new FilesystemCandidateReference(durable);
        } catch (RuntimeException e) {
            return keptOff(durable, "malformed durable reference: " + e.getMessage());
        }
        PackDiscoveryResult discovery = frontEnd.discover(
                new PackDiscoveryRequest(shaderpacksDirectory, diagnostics));
        FilesystemCandidateResolution resolution =
                frontEnd.resolveFilesystemCandidate(reference, discovery);
        if (!(resolution instanceof FilesystemCandidateResolution.Resolved resolved)) {
            return keptOff(durable, resolution.getClass().getSimpleName());
        }
        PackCandidate candidate = discovery.candidates().stream()
                .filter(c -> c.id().equals(resolved.candidate()))
                .findFirst()
                .orElse(null);
        if (candidate == null || candidate.status() != PackCandidateStatus.AVAILABLE) {
            return keptOff(durable, candidate == null ? "candidate vanished"
                    : candidate.status().name());
        }
        return new PackSelection.Filesystem(resolved.candidate());
    }

    private PackSelection keptOff(String durable, String reason) {
        diagnostics.report(new EngineDiagnostic(DiagnosticSeverity.WARN, UserChannel.CHAT,
                "schmaloogium.gui.restoreKeptOff", List.of(reason), durable, LogChannels.PACK));
        return new PackSelection.Off();
    }
}
