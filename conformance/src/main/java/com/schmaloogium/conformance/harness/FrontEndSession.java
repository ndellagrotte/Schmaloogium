// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.harness;

import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.pack.CompanionOptionMacros;
import com.schmaloogium.engine.pack.PackCandidate;
import com.schmaloogium.engine.pack.PackCandidateId;
import com.schmaloogium.engine.pack.PackCandidateKind;
import com.schmaloogium.engine.pack.PackDiscoveryRequest;
import com.schmaloogium.engine.pack.PackDiscoveryResult;
import com.schmaloogium.engine.pack.PackFrontEnd;
import com.schmaloogium.engine.pack.PackFrontEnds;
import com.schmaloogium.engine.pack.PackInspectionResult;
import com.schmaloogium.engine.pack.PackLoadRequest;
import com.schmaloogium.engine.pack.PackSelection;
import com.schmaloogium.engine.pack.PackCandidateStatus;
import com.schmaloogium.engine.pack.RendererFeatureData;
import com.schmaloogium.engine.pack.RuntimeIdentityData;
import com.schmaloogium.engine.pack.OsFamily;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Drives one front end over one fixture pack (§4.11.4's exact boundary): fresh
 * discovery, canonical durable-reference resolution, memory persistence, then
 * {@code frontEnd.inspect(PackLoadRequest)} with every non-Off field. Both the golden
 * run and the harness T0 leg consume this session; neither touches parser-private code.
 */
public final class FrontEndSession {

    /** Harness identity for runtime macros; MC tuple is fixed at (1,12,2). */
    public static final String ENGINE_EDITION = "schmaloogium-conformance";
    public static final String ENGINE_VERSION = "0.1.0-dev";

    private final PackFrontEnd frontEnd = PackFrontEnds.create().frontEnd();
    private final List<EngineDiagnostic> diagnostics = new ArrayList<>();
    private final DiagnosticReporter reporter = diagnostics::add;

    public PackFrontEnd frontEnd() {
        return frontEnd;
    }

    public List<EngineDiagnostic> diagnostics() {
        return List.copyOf(diagnostics);
    }

    /** Fresh discovery over {@code shaderpacksDir}; the returned list is in discovery order. */
    public PackDiscoveryResult discover(Path shaderpacksDir) {
        return frontEnd.discover(new PackDiscoveryRequest(shaderpacksDir, reporter));
    }

    /** Resolves one directory/archive candidate by display name, ignoring the two
     *  sentinels. */
    public PackCandidateId resolveCandidate(Path shaderpacksDir, String displayName) {
        PackDiscoveryResult discovery = discover(shaderpacksDir);
        return discovery.candidates().stream()
            .filter(candidate -> candidate.kind() != PackCandidateKind.OFF
                && candidate.kind() != PackCandidateKind.INTERNAL)
            .filter(candidate -> candidate.displayName().equals(displayName))
            .filter(candidate -> candidate.status() == PackCandidateStatus.AVAILABLE)
            .map(PackCandidate::id)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("no available candidate named "
                + displayName + " under " + shaderpacksDir));
    }

    /** Inspects one discovered candidate with a complete, non-null request. */
    public PackInspectionResult inspect(Path shaderpacksDir, PackCandidateId candidateId,
            GLCapabilityProfile profile) {
        Objects.requireNonNull(shaderpacksDir, "shaderpacksDir");
        Objects.requireNonNull(candidateId, "candidateId");
        Objects.requireNonNull(profile, "profile");
        PackLoadRequest request = new PackLoadRequest(
            shaderpacksDir,
            new PackSelection.Filesystem(candidateId),
            new RuntimeIdentityData(1, 12, 2, ENGINE_EDITION, ENGINE_VERSION,
                OsFamily.LINUX, java.util.Map.of()),
            profile,
            EngineOptionData.empty(),
            new CompanionOptionMacros(false, false),
            new RendererFeatureData(false, false),
            null, // sealed engine interface: engine owns the impl; inspect() never reads it
            null,
            Optional.empty(),
            reporter);
        return frontEnd.inspect(request);
    }

    /** Inspects the internal selection over a caller-supplied bounded provider. */
    public PackInspectionResult inspectInternal(com.schmaloogium.engine.pack.InternalPackSource source,
            GLCapabilityProfile profile) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(profile, "profile");
        PackLoadRequest request = new PackLoadRequest(
            Path.of("(none)"),
            new PackSelection.Internal(),
            new RuntimeIdentityData(1, 12, 2, ENGINE_EDITION, ENGINE_VERSION,
                OsFamily.LINUX, java.util.Map.of()),
            profile,
            EngineOptionData.empty(),
            new CompanionOptionMacros(false, false),
            new RendererFeatureData(false, false),
            null, // sealed engine interface: engine owns the impl; inspect() never reads it
            source,
            Optional.empty(),
            reporter);
        return frontEnd.inspect(request);
    }
}
