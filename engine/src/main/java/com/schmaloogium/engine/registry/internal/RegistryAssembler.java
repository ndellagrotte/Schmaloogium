// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.config.EvaluatedProgramStates;
import com.schmaloogium.engine.config.ProgramStateEvaluationResult;
import com.schmaloogium.engine.config.ProgramStateEvaluator;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.gl.ProgramHandle;
import com.schmaloogium.engine.registry.ClassicProgramCatalog;
import com.schmaloogium.engine.registry.CompiledRegistryCandidate;
import com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint;
import com.schmaloogium.engine.registry.ProgramBuildFailure;
import com.schmaloogium.engine.registry.ProgramBuildStage;
import com.schmaloogium.engine.registry.ProgramOwnBuildDisposition;
import com.schmaloogium.engine.registry.ProgramResolutionProjection;
import com.schmaloogium.engine.registry.ProgramResolutionStatus;
import com.schmaloogium.engine.registry.ProgramSlotDescriptor;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.ProgramSlotKind;
import com.schmaloogium.engine.registry.RegistryBuildFailure;
import com.schmaloogium.engine.registry.RegistryFailureKind;
import com.schmaloogium.engine.registry.RegistryBuildResult;
import com.schmaloogium.engine.registry.RegistryFingerprint;
import com.schmaloogium.engine.registry.StageRegistries;
import com.schmaloogium.engine.pack.DimensionConfiguration;
import com.schmaloogium.engine.registry.FixedSamplerLayoutPolicy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The compile orchestrator (PHASE_4_DOC §4.7/§4.11): validates the request, evaluates the
 * program states for the exact configuration/profile pair, plans and classifies every
 * catalog slot, executes the render-thread GL transaction, folds the sole backup chains,
 * derives the resolution projection, and fingerprints the whole registry. Registry-wide
 * sampler-policy failures abort as ShadersOff before any GL work.
 */
final class RegistryAssembler {

    private RegistryAssembler() {
    }

    static RegistryBuildResult compile(com.schmaloogium.engine.registry.RegistryBuildRequest request) {
        DiagnosticReporter diagnostics = request.diagnostics();
        FixedSamplerLayoutPolicy policy = request.samplerPolicy();
        if (policy == null) {
            return off("INVALID_SAMPLER_POLICY", "sampler policy missing");
        }
        FixedSamplerPolicyFingerprint first = policy.fingerprint();
        FixedSamplerPolicyFingerprint second = policy.fingerprint();
        if (first == null || !first.equals(second)) {
            return off("INVALID_SAMPLER_POLICY", "unstable sampler policy fingerprint");
        }
        DimensionConfiguration dimension = request.configuration().dimensions()
            .get(request.dimension());
        if (dimension == null) {
            return off("INVALID_PROGRAM_STATE", "dimension key not in configuration");
        }
        ProgramStateEvaluationResult evaluated = ProgramStateEvaluator.evaluate(
            request.configuration(),
            request.configuration().options().state(),
            request.profileSelection(),
            diagnostics);
        if (!(evaluated instanceof ProgramStateEvaluationResult.Evaluated evaluation)) {
            return off("INVALID_PROGRAM_STATE", "option state evaluation failed");
        }
        EvaluatedProgramStates states = evaluation.states();

        // Pure planning over the complete classic catalog; no GL yet.
        List<ProgramSlotDescriptor> catalog = ClassicProgramCatalog.rows();
        List<Planner.PlannedSlot> planned = new ArrayList<>(catalog.size());
        List<ProgramBuildFailure> policyFailures = new ArrayList<>();
        for (ProgramSlotDescriptor descriptor : catalog) {
            planned.add(Planner.planSlot(descriptor, dimension, states, request.configuration(),
                request.macroContribution(), policy, request.capabilities(), policyFailures));
        }
        if (!policyFailures.isEmpty()) {
            return new RegistryBuildResult.ShadersOff(new RegistryBuildFailure(
                RegistryFailureKind.INVALID_SAMPLER_POLICY,
                List.copyOf(policyFailures),
                policyFailures.get(0).diagnosticId(),
                "sampler policy rejected the registry build"));
        }

        // Render-thread GL transaction per enabled slot.
        GLDevice device = request.device();
        Map<ProgramSlotId, CompiledProgramBinding> bindings = new LinkedHashMap<>();
        List<ProgramHandle> handles = new ArrayList<>();
        boolean poisoned = false;
        for (Planner.PlannedSlot slot : planned) {
            if (slot.disposition != ProgramOwnBuildDisposition.SUCCEEDED) {
                continue;
            }
            GlProgramBuilder.build(slot, device, bindings, handles);
            if (slot.poisoned) {
                poisoned = true;
                break;
            }
        }
        if (poisoned) {
            for (ProgramHandle handle : handles) {
                device.shaders().delete(handle);
            }
            return off("UNSAFE_STATE", "sampler initialization left the unit selection unrestored");
        }

        // Backup-chain folding and the resolution projection.
        Map<ProgramSlotId, ResolvedCompiledProgramBinding> resolutions = new LinkedHashMap<>();
        List<ProgramResolutionProjection> projections = new ArrayList<>(planned.size());
        for (Planner.PlannedSlot slot : planned) {
            if (slot.descriptor.kind() == ProgramSlotKind.VIRTUAL_FLIP_CONTROL
                || slot.descriptor.kind() == ProgramSlotKind.FIXED_FUNCTION_SENTINEL) {
                projections.add(new ProgramResolutionProjection(
                    slot.descriptor.id(), ProgramResolutionStatus.ABSENT,
                    Optional.empty(), false, slot.disposition, ""));
                continue;
            }
            Walk walk = walk(slot.descriptor.id(), planned, bindings);
            resolutions.put(slot.descriptor.id(),
                new ResolvedCompiledProgramBinding(
                    slot.descriptor.id(), walk.binding, List.copyOf(walk.path)));
            projections.add(project(slot, walk));
        }

        RegistryFingerprint fingerprint = fingerprint(request, dimension, planned,
            projections, first);
        CompiledProgramRegistryImpl registry = new CompiledProgramRegistryImpl(
            StageRegistries.classicG6(),
            catalog,
            resolutions,
            projections,
            fingerprint,
            first,
            device,
            handles,
            new RegistryContexts(),
            ClassicProgramCatalog.SHADOW);
        return new RegistryBuildResult.Ready(new CompiledRegistryCandidate(registry));
    }

    private static RegistryBuildResult.ShadersOff off(String diagnosticId, String detail) {
        return new RegistryBuildResult.ShadersOff(new RegistryBuildFailure(
            RegistryFailureKind.INVALID_PROGRAM_STATE,
            List.of(),
            diagnosticId,
            diagnosticId + ": " + detail));
    }

    static final class Walk {
        List<ProgramSlotId> path = new ArrayList<>();
        CompiledProgramBinding binding;
        List<ProgramBuildFailure> failures = new ArrayList<>();
    }

    /** The sole backup-chain walk with per-slot memoization over the planned list. */
    static Walk walk(
            ProgramSlotId slot,
            List<Planner.PlannedSlot> planned,
            Map<ProgramSlotId, CompiledProgramBinding> bindings) {
        Planner.PlannedSlot own = byId(planned, slot);
        Walk walk = new Walk();
        walk.path.add(slot);
        if (own != null && own.disposition == ProgramOwnBuildDisposition.SUCCEEDED) {
            walk.binding = bindings.get(slot);
            return walk;
        }
        if (own != null && own.disposition == ProgramOwnBuildDisposition.FAILED) {
            walk.failures.add(own.failure);
        }
        Optional<ProgramSlotId> fallback = own == null
            ? Optional.empty()
            : own.descriptor.fallback();
        if (fallback.isPresent()) {
            Walk child = walk(fallback.get(), planned, bindings);
            List<ProgramSlotId> path = new ArrayList<>(walk.path);
            path.addAll(child.path);
            walk.path = path;
            List<ProgramBuildFailure> failures = new ArrayList<>(walk.failures);
            failures.addAll(child.failures);
            walk.failures = failures;
            walk.binding = child.binding;
            return walk;
        }
        // Root terminal: fixed function with the canonical default bundle.
        com.schmaloogium.engine.registry.ProgramStateBundle defaults =
            defaultBundle();
        walk.binding = new CompiledProgramBinding.FixedFunction(slot, defaults);
        return walk;
    }

    static com.schmaloogium.engine.registry.ProgramStateBundle defaultBundle() {
        return new com.schmaloogium.engine.registry.ProgramStateBundle(
            new com.schmaloogium.engine.registry.DrawRouting.AllUsedBuffers(
                com.schmaloogium.engine.registry.BufferDomain.COLORTEX),
            java.util.Set.of(),
            1,
            java.util.Set.of(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Map.of(),
            Optional.empty(),
            com.schmaloogium.engine.registry.GeometryInputRequirement.NONE);
    }

    static ProgramResolutionProjection project(Planner.PlannedSlot slot, Walk walk) {
        ProgramOwnBuildDisposition own = slot.disposition;
        if (own == ProgramOwnBuildDisposition.SUCCEEDED) {
            return new ProgramResolutionProjection(
                slot.descriptor.id(), ProgramResolutionStatus.SOURCED,
                Optional.empty(), true, own, "");
        }
        StringBuilder driverLog = new StringBuilder();
        for (ProgramBuildFailure failure : walk.failures) {
            if (driverLog.length() > 0) {
                driverLog.append("; ");
            }
            driverLog.append(failure.projectionDetail());
        }
        if (walk.binding instanceof CompiledProgramBinding.ShaderProgram) {
            return new ProgramResolutionProjection(
                slot.descriptor.id(), ProgramResolutionStatus.CHAIN,
                Optional.of(walk.binding.provider()), slot.sourcePresent, own,
                driverLog.toString());
        }
        if (!walk.failures.isEmpty()) {
            return new ProgramResolutionProjection(
                slot.descriptor.id(), ProgramResolutionStatus.FAILED,
                Optional.empty(), slot.sourcePresent, own, driverLog.toString());
        }
        return new ProgramResolutionProjection(
            slot.descriptor.id(), ProgramResolutionStatus.ABSENT,
            Optional.empty(), slot.sourcePresent, own, "");
    }

    private static Planner.PlannedSlot byId(
            List<Planner.PlannedSlot> planned, ProgramSlotId id) {
        for (Planner.PlannedSlot slot : planned) {
            if (slot.descriptor.id().equals(id)) {
                return slot;
            }
        }
        return null;
    }

    /** The registry-wide fingerprint (§4.11): identity, snapshot, layouts, evidence. */
    private static RegistryFingerprint fingerprint(
            com.schmaloogium.engine.registry.RegistryBuildRequest request,
            DimensionConfiguration dimension,
            List<Planner.PlannedSlot> planned,
            List<ProgramResolutionProjection> projections,
            FixedSamplerPolicyFingerprint policy) {
        List<byte[]> parts = new ArrayList<>();
        parts.add(CanonicalFraming.atom(CanonicalFraming.REGISTRY_DOMAIN));
        parts.add(CanonicalFraming.atom(request.configuration().fingerprint().value()));
        parts.add(CanonicalFraming.atom(request.profileSelection()
            .map(p -> p.value()).orElse("")));
        parts.add(CanonicalFraming.atom(String.valueOf(request.dimension().legacyId().orElse(-1))));
        parts.add(CanonicalFraming.atom(macroText(request.macroContribution())));
        parts.add(CanonicalFraming.atom(policy.value()));
        parts.add(CanonicalFraming.atom(request.capabilities().maxDrawBuffers()));
        parts.add(CanonicalFraming.atom(request.capabilities().maxColorAttachments()));
        parts.add(CanonicalFraming.atom(request.capabilities().maxVertexAttribs()));
        parts.add(CanonicalFraming.seqOfStrings(
            request.capabilities().extensions().stream().sorted().toList()));
        for (Planner.PlannedSlot slot : planned) {
            parts.add(CanonicalFraming.atom(slot.descriptor.id().packName()));
            parts.add(CanonicalFraming.atom(slot.disposition.name()));
            if (slot.disposition == ProgramOwnBuildDisposition.SUCCEEDED) {
                for (Planner.MaterializedStage stage : slot.materialized) {
                    parts.add(CanonicalFraming.atom(
                        stage.source().fingerprint().value()));
                }
                parts.add(CanonicalFraming.atom(
                    slot.uniformLayout.fingerprint().value()));
                parts.add(CanonicalFraming.atom(
                    slot.samplerLayout.fingerprint().value()));
            }
            if (slot.failure != null) {
                parts.add(CanonicalFraming.atom(slot.failure.projectionDetail()));
            }
        }
        for (ProgramResolutionProjection row : projections) {
            parts.add(CanonicalFraming.atom(row.status().name()));
            parts.add(CanonicalFraming.atom(row.from().map(f -> f.packName()).orElse("")));
            parts.add(CanonicalFraming.atom(row.driverLog()));
        }
        return new RegistryFingerprint(CanonicalFraming.sha256Hex(parts.toArray(new byte[0][])));
    }

    private static String macroText(com.schmaloogium.engine.preprocess.MacroContribution contribution) {
        if (contribution instanceof com.schmaloogium.engine.preprocess.MacroContribution.Empty) {
            return "Empty";
        }
        var defined = (com.schmaloogium.engine.preprocess.MacroContribution.DefineCenterDepthSmooth)
            contribution;
        return "DefineCenterDepthSmooth:" + defined.replacementTokens();
    }
}
