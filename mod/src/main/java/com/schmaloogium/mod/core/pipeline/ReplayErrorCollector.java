// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core.pipeline;

import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.log.Logs;
import com.schmaloogium.engine.uniforms.UniformReplayErrorSink;
import com.schmaloogium.engine.uniforms.UniformReplayReport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The P7-owned replay-error observer (PHASE_6_DOC D-P6-27): every report is accepted in
 * full, retained in a bounded ledger and reported once. Nothing is filtered or dropped
 * silently; when the ledger is full the count keeps growing and the log line still fires.
 * The callback does no GL, no provider calls and no lifecycle work.
 */
public final class ReplayErrorCollector implements UniformReplayErrorSink {

    static final int LEDGER_CAPACITY = 256;

    private final String packFingerprint;
    private final DiagnosticReporter diagnostics;
    private final List<UniformReplayReport> ledger = new ArrayList<>();
    private long accepted;

    public ReplayErrorCollector(String packFingerprint, DiagnosticReporter diagnostics) {
        this.packFingerprint = Objects.requireNonNull(packFingerprint, "packFingerprint");
        this.diagnostics = Objects.requireNonNull(diagnostics, "diagnostics");
    }

    @Override
    public void accept(UniformReplayReport report) {
        Objects.requireNonNull(report, "report");
        accepted++;
        if (ledger.size() < LEDGER_CAPACITY) {
            ledger.add(report);
        }
        Logs.channel(LogChannels.UNIFORMS).warn(
                "uniform replay errors for program {} ({} errors; pack {})",
                report.program(), report.errors().size(), packFingerprint);
        diagnostics.report(new EngineDiagnostic(DiagnosticSeverity.WARN, UserChannel.SHADER_GUI,
                "schmaloogium.warn.uniforms.replay",
                List.of(String.valueOf(report.program()), report.errors().size()),
                report.errors().toString(), LogChannels.UNIFORMS));
    }

    /** Every retained report, oldest first. */
    public List<UniformReplayReport> ledger() {
        return Collections.unmodifiableList(ledger);
    }

    /** The count of reports accepted, including those beyond the ledger capacity. */
    public long acceptedCount() {
        return accepted;
    }
}
