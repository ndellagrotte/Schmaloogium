// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.harness;

import com.schmaloogium.conformance.golden.GoldenComparer;
import com.schmaloogium.conformance.golden.GoldenDocument;
import com.schmaloogium.conformance.golden.GoldenProjectionAdapter;
import com.schmaloogium.conformance.golden.GoldenUpdatePolicy;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.pack.PackCandidateId;
import com.schmaloogium.engine.pack.PackInspectionResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * The headless golden run over one fixture pack (§4.11): inspect through the
 * {@link FrontEndSession} boundary, project through
 * {@link GoldenProjectionAdapter}, then either compare against the committed golden
 * (default) or — only under the opt-in {@code -PupdateGoldens} policy — rewrite the
 * committed file and report the change so the caller can fail the build afterwards
 * (§4.11.5: an update can never be an accident of a normal run).
 */
public final class FrontEndGoldenRun {

    public enum Kind { MATCHED, DIFFERENT, UPDATED, FAILED }

    public record Result(Kind kind, List<GoldenComparer.Difference> differences,
        String committedRender, String producedRender, List<EngineDiagnostic> loadDiagnostics) {
    }

    private static final GoldenProjectionAdapter ADAPTER = new GoldenProjectionAdapter();

    private FrontEndGoldenRun() {
    }

    public static Result run(FrontEndSession session, Path shaderpacksDir,
            PackCandidateId candidateId, GoldenProjectionAdapter.GoldenInputs inputs,
            GLCapabilityProfile profile, GoldenUpdatePolicy policy, Path committedGolden) {
        PackInspectionResult result = session.inspect(shaderpacksDir, candidateId, profile);
        if (!(result instanceof PackInspectionResult.Inspected inspected)) {
            return new Result(Kind.FAILED, List.of(), "", "", session.diagnostics());
        }
        GoldenDocument produced = ADAPTER.project(inspected, inputs);
        return finish(committedGolden, policy, produced, session.diagnostics());
    }

    public static Result runInternal(FrontEndSession session,
            com.schmaloogium.engine.pack.InternalPackSource source,
            GoldenProjectionAdapter.GoldenInputs inputs, GLCapabilityProfile profile,
            GoldenUpdatePolicy policy, Path committedGolden) {
        PackInspectionResult result = session.inspectInternal(source, profile);
        if (!(result instanceof PackInspectionResult.Inspected inspected)) {
            return new Result(Kind.FAILED, List.of(), "", "", session.diagnostics());
        }
        GoldenDocument produced = ADAPTER.project(inspected, inputs);
        return finish(committedGolden, policy, produced, session.diagnostics());
    }

    private static Result finish(Path committedGolden, GoldenUpdatePolicy policy,
            GoldenDocument produced, List<EngineDiagnostic> loadDiagnostics) {
        String producedRender = produced.render();
        if (policy == GoldenUpdatePolicy.UPDATE_AND_FAIL) {
            String committedRender = readOrNull(committedGolden);
            List<GoldenComparer.Difference> diffs = committedRender == null
                ? List.of(new GoldenComparer.Difference(
                    GoldenComparer.Difference.Kind.MISSING_SECTION, "", "",
                    "<file absent>", ""))
                : GoldenComparer.compare(GoldenDocument.parse(committedRender), produced);
            write(committedGolden, producedRender);
            return new Result(Kind.UPDATED, diffs, committedRender, producedRender,
                loadDiagnostics);
        }
        String committedRender = readOrNull(committedGolden);
        if (committedRender == null) {
            return new Result(Kind.DIFFERENT,
                List.of(new GoldenComparer.Difference(
                    GoldenComparer.Difference.Kind.MISSING_SECTION, "", "",
                    "<file absent>", "")),
                "", producedRender, loadDiagnostics);
        }
        List<GoldenComparer.Difference> diffs =
            GoldenComparer.compare(GoldenDocument.parse(committedRender), produced);
        return new Result(diffs.isEmpty() ? Kind.MATCHED : Kind.DIFFERENT, diffs,
            committedRender, producedRender, loadDiagnostics);
    }
    private static String readOrNull(Path path) {
        if (!Files.isRegularFile(path)) {
            return null;
        }
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("unreadable committed golden: " + path, e);
        }
    }

    private static void write(Path path, String render) {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.writeString(path, render, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("cannot write golden: " + path, e);
        }
    }
}
