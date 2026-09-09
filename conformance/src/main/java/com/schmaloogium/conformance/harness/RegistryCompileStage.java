// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.harness;

import com.schmaloogium.conformance.glrec.ScriptedGLDevice;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.registry.SamplerLayoutValidation;
import com.schmaloogium.engine.gl.SamplerUnitAssignment;
import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.pack.PackInspectionResult;
import com.schmaloogium.engine.preprocess.MacroContribution;
import com.schmaloogium.engine.registry.FixedSamplerLayoutPolicy;
import com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint;
import com.schmaloogium.engine.registry.ProgramRegistries;
import com.schmaloogium.engine.registry.ProgramRegistryCompiler;
import com.schmaloogium.engine.registry.ProgramRegistryView;
import com.schmaloogium.engine.registry.ProgramSamplerLayout;
import com.schmaloogium.engine.registry.RegistryBuildRequest;
import com.schmaloogium.engine.registry.RegistryBuildResult;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The owner-backed T0 compile leg (§4.2.1 "programs compile"): drives Phase 4's pure
 * {@link ProgramRegistryCompiler} over the front end's inspected configuration, headless —
 * the GL side is the scripted device, never a live context. {@code profileSelection} is
 * empty (T0 runs the pack with no profile selected) and the macro contribution is the
 * empty reserved set (no Phase 6 feature in a conformance run).
 */
public final class RegistryCompileStage implements CompileStage {

    private static final ProgramRegistryCompiler COMPILER = ProgramRegistries.compiler();

    private final GLCapabilityProfile profile;

    public RegistryCompileStage(GLCapabilityProfile profile) {
        this.profile = java.util.Objects.requireNonNull(profile, "profile");
    }

    @Override
    public CompileEvidence compile(PackInspectionResult.Inspected inspected) {
        ScriptedGLDevice device = new ScriptedGLDevice(profile);
        RegistryBuildResult result = COMPILER.compile(new RegistryBuildRequest(
            inspected.configuration(),
            Optional.empty(),
            DimensionKey.BASE,
            new MacroContribution.Empty(),
            TrivialSamplerPolicy.INSTANCE,
            profile,
            device,
            reason -> { }));
        if (result instanceof RegistryBuildResult.Ready ready) {
            // The detached view is safe after close and never keeps GL state alive.
            ProgramRegistryView view = ready.candidate().view();
            List<CompileStage.SlotRow> rows = new ArrayList<>();
            for (var resolution : view.resolutions()) {
                rows.add(new CompileStage.SlotRow(resolution.slot().packName(),
                    resolution.status().name()));
            }
            ready.candidate().close();
            return CompileEvidence.resolved(rows);
        }
        var failure = ((RegistryBuildResult.ShadersOff) result).failure();
        return CompileEvidence.failed(failure.kind() + " " + failure.diagnosticId()
            + ": " + failure.userMessage());
    }

    /**
     * The conformance stand-in for Phase 5's {@code FixedSamplerPolicies.appB3()}: every
     * band validates and each distinct declared sampler gets the next unit in
     * exact-name UTF-8 order — the simplest policy that satisfies P4's output contract
     * for packs without exotic sampler shapes. Replaced by P5's real policy; P4 rejects
     * malformed output as a typed registry-wide failure, so a pack this policy cannot
     * serve fails T0's compile leg rather than passing spuriously.
     */
    private static final class TrivialSamplerPolicy implements FixedSamplerLayoutPolicy {

        static final TrivialSamplerPolicy INSTANCE = new TrivialSamplerPolicy();
        private static final FixedSamplerPolicyFingerprint FINGERPRINT =
            new FixedSamplerPolicyFingerprint("conformance-trivial-1");

        @Override
        public FixedSamplerPolicyFingerprint fingerprint() {
            return FINGERPRINT;
        }

        @Override
        public SamplerLayoutValidation validate(StageId effectiveStage, StageBand effectiveBand,
                List<com.schmaloogium.engine.registry.ProgramSamplerDeclaration> declarations) {
            return new SamplerLayoutValidation.Valid();
        }

        @Override
        public List<SamplerUnitAssignment> initializationAssignments(
                ProgramSamplerLayout.Shader layout) {
            List<SamplerUnitAssignment> assignments = new ArrayList<>();
            int unit = 0;
            List<String> names = layout.declarations().stream()
                .map(declaration -> declaration.exactName())
                .distinct()
                .sorted()
                .toList();
            for (String name : names) {
                assignments.add(new SamplerUnitAssignment(name, unit++));
            }
            return List.copyOf(assignments);
        }
    }
}
