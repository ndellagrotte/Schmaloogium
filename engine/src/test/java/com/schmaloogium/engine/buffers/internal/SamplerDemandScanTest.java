// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.schmaloogium.engine.buffers.FixedSamplerPolicies;
import com.schmaloogium.engine.preprocess.DeclaredGlslType;
import com.schmaloogium.engine.preprocess.SampledKind;
import com.schmaloogium.engine.preprocess.TextureDimension;
import com.schmaloogium.engine.registry.BufferDomain;
import com.schmaloogium.engine.registry.BufferRef;
import com.schmaloogium.engine.registry.DrawRouting;
import com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint;
import com.schmaloogium.engine.registry.GeometryInputRequirement;
import com.schmaloogium.engine.registry.PassDescriptor;
import com.schmaloogium.engine.registry.PassIndex;
import com.schmaloogium.engine.registry.PassPopulation;
import com.schmaloogium.engine.registry.PassResourceAccess;
import com.schmaloogium.engine.registry.ProgramOwnBuildDisposition;
import com.schmaloogium.engine.registry.ProgramRegistryView;
import com.schmaloogium.engine.registry.ProgramResolutionProjection;
import com.schmaloogium.engine.registry.ProgramResolutionStatus;
import com.schmaloogium.engine.registry.ProgramSamplerDeclaration;
import com.schmaloogium.engine.registry.ProgramSamplerLayout;
import com.schmaloogium.engine.registry.ProgramSamplerLayoutFingerprint;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.ProgramStateBundle;
import com.schmaloogium.engine.registry.ProgramUniformLayout;
import com.schmaloogium.engine.registry.RegistryFingerprint;
import com.schmaloogium.engine.registry.ResolvedProgramDescriptor;
import com.schmaloogium.engine.registry.SamplerLayoutValidation;
import com.schmaloogium.engine.registry.StageBand;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.registry.StageRegistry;
import com.schmaloogium.engine.registry.StageStep;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Source-scan resource sizing (RESEARCH §4.4): executable programs raise the planned
 * depth/shadow texture counts by what they sample, and the shadow pass by what it writes.
 * Before Task D the P3 minima placeholder sized every pack to one depth texture and no
 * shadow color, so composites sampling depthtex1 or shadowcolor0 degraded to nothing.
 */
class SamplerDemandScanTest {

    private static final DeclaredGlslType.Sampler SAMPLER_2D = new DeclaredGlslType.Sampler(
        SampledKind.FLOAT, TextureDimension.D2, false, false, false);
    private static final StageStep COMPOSITE = new StageStep(StageId.COMPOSITE,
        StageBand.FRAME_END, new PassPopulation.Singleton());
    private static final StageStep SHADOW = new StageStep(StageId.SHADOW, StageBand.SHADOW,
        new PassPopulation.Singleton());

    /** One registry: slot -> (status, sampler names, shadowcolor writes). */
    private static final class Registry implements ProgramRegistryView {
        private final Map<StageStep, List<PassDescriptor>> passes = new LinkedHashMap<>();
        private final Map<ProgramSlotId, ProgramResolutionStatus> statuses = new LinkedHashMap<>();
        private final Map<ProgramSlotId, ResolvedProgramDescriptor> resolved = new LinkedHashMap<>();

        Registry add(StageStep step, String slotName, ProgramResolutionStatus status,
                Set<BufferRef> writes, String... samplers) {
            ProgramSlotId slot = new ProgramSlotId(slotName);
            passes.computeIfAbsent(step, ignored -> new ArrayList<>()).add(new PassDescriptor(
                step, slot, Optional.empty(),
                new PassResourceAccess(Set.of(), writes, Map.of(), Set.of()), Set.of()));
            statuses.put(slot, status);
            List<ProgramSamplerDeclaration> declarations = new ArrayList<>();
            for (int index = 0; index < samplers.length; index++) {
                declarations.add(new ProgramSamplerDeclaration(samplers[index], SAMPLER_2D,
                    index, List.of()));
            }
            resolved.put(slot, new ResolvedProgramDescriptor(slot, slot,
                new ProgramStateBundle(new DrawRouting.AllUsedBuffers(BufferDomain.COLORTEX),
                    Set.of(), 1, Set.of(), Optional.empty(), Optional.empty(),
                    Optional.empty(), Map.of(), Optional.empty(),
                    GeometryInputRequirement.NONE),
                ProgramUniformLayout.empty(),
                new ProgramSamplerLayout.Shader(new ProgramSamplerLayoutFingerprint("fp"),
                    FixedSamplerPolicies.appB3Fingerprint(), step.stage(),
                    Set.of(step.band()), declarations, new SamplerLayoutValidation.Valid()),
                List.of(), List.of()));
            return this;
        }

        @Override
        public StageRegistry stages() {
            return new StageRegistry() {
                @Override
                public List<StageStep> schedule() {
                    return List.copyOf(passes.keySet());
                }

                @Override
                public List<PassDescriptor> passes(StageStep step) {
                    return passes.getOrDefault(step, List.of());
                }

                @Override
                public Optional<PassDescriptor> named(StageStep step, ProgramSlotId id) {
                    return Optional.empty();
                }

                @Override
                public Optional<PassDescriptor> indexed(StageStep step, PassIndex index) {
                    return Optional.empty();
                }

                @Override
                public boolean stepExists(StageStep step) {
                    return passes.containsKey(step);
                }
            };
        }

        @Override
        public Optional<ResolvedProgramDescriptor> resolve(ProgramSlotId requested) {
            return Optional.ofNullable(resolved.get(requested));
        }

        @Override
        public List<ProgramResolutionProjection> resolutions() {
            List<ProgramResolutionProjection> out = new ArrayList<>();
            statuses.forEach((slot, status) -> out.add(new ProgramResolutionProjection(slot,
                status, Optional.empty(), true, ProgramOwnBuildDisposition.SUCCEEDED, "")));
            return out;
        }

        @Override
        public RegistryFingerprint fingerprint() {
            return new RegistryFingerprint("registry-fp");
        }

        @Override
        public FixedSamplerPolicyFingerprint samplerPolicyFingerprint() {
            return FixedSamplerPolicies.appB3Fingerprint();
        }
    }

    @Test
    void samplersRaiseDepthAndShadowDemand() {
        Registry registry = new Registry()
            .add(COMPOSITE, "composite", ProgramResolutionStatus.SOURCED, Set.of(),
                "colortex0", "depthtex1", "shadowcolor0", "shadow")
            .add(COMPOSITE, "composite1", ProgramResolutionStatus.CHAIN, Set.of(),
                "depthtex2", "watershadow");
        BufferPlanner.SamplerDemand demand = BufferPlanner.scanSamplerDemand(registry);
        assertEquals(new BufferPlanner.SamplerDemand(3, 2, 1), demand);
    }

    @Test
    void shadowPassWritesRaiseShadowColorAndImplyShadowDepth() {
        Registry registry = new Registry()
            .add(SHADOW, "shadow", ProgramResolutionStatus.SOURCED,
                Set.of(new BufferRef(BufferDomain.SHADOWCOLOR, 1)), "texture");
        assertEquals(new BufferPlanner.SamplerDemand(0, 1, 2),
            BufferPlanner.scanSamplerDemand(registry));
    }

    @Test
    void absentAndFailedProgramsRequireNothing() {
        Registry registry = new Registry()
            .add(COMPOSITE, "composite5", ProgramResolutionStatus.ABSENT, Set.of(),
                "depthtex2", "shadowcolor1")
            .add(COMPOSITE, "composite6", ProgramResolutionStatus.FAILED, Set.of(),
                "shadowtex1");
        assertEquals(new BufferPlanner.SamplerDemand(0, 0, 0),
            BufferPlanner.scanSamplerDemand(registry));
    }
}
