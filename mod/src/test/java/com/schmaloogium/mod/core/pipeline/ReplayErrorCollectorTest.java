// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core.pipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.UserChannel;
import com.schmaloogium.engine.gl.GLError;
import com.schmaloogium.engine.gl.GLErrorKind;
import com.schmaloogium.engine.gl.ReplayAwareGLError;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.ProgramUniformCacheKey;
import com.schmaloogium.engine.registry.ProgramUniformLayoutFingerprint;
import com.schmaloogium.engine.uniforms.UniformReplayReport;
import com.schmaloogium.mod.core.pipeline.PipelineFixtures.CollectingDiagnostics;

import org.junit.jupiter.api.Test;

import java.util.List;

/** D-P6-27: every report is accepted, retained and surfaced; nothing is filtered. */
class ReplayErrorCollectorTest {

    @Test
    void everyReport_isRetainedAndReported() {
        CollectingDiagnostics diagnostics = new CollectingDiagnostics();
        ReplayErrorCollector collector = new ReplayErrorCollector("fp", diagnostics);
        int reports = ReplayErrorCollector.LEDGER_CAPACITY + 3;
        for (int i = 0; i < reports; i++) {
            collector.accept(report(i));
        }
        assertEquals(reports, collector.acceptedCount());
        assertEquals(ReplayErrorCollector.LEDGER_CAPACITY, collector.ledger().size());
        assertEquals(reports, diagnostics.reports.size());
        assertEquals(DiagnosticSeverity.WARN, diagnostics.reports.get(0).severity());
        assertEquals(UserChannel.SHADER_GUI, diagnostics.reports.get(0).channel());
        assertEquals("schmaloogium.warn.uniforms.replay", diagnostics.reports.get(0).messageKey());
    }

    private static UniformReplayReport report(int i) {
        return new UniformReplayReport(
                new ProgramUniformCacheKey(1L, new ProgramSlotId("composite"),
                        new ProgramUniformLayoutFingerprint("layout-" + i)),
                List.of(new ReplayAwareGLError(new GLError("op", "subject",
                        GLErrorKind.INVALID_OPERATION, "detail"), true)));
    }
}
