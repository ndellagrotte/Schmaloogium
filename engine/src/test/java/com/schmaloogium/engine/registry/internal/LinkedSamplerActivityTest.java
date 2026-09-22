// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors
package com.schmaloogium.engine.registry.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.buffers.FixedSamplerPolicies;
import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.pack.CompanionOptionMacros;
import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.pack.OsFamily;
import com.schmaloogium.engine.pack.PackDiscoveryRequest;
import com.schmaloogium.engine.pack.PackFrontEnds;
import com.schmaloogium.engine.pack.PackInspectionResult;
import com.schmaloogium.engine.pack.PackLoadRequest;
import com.schmaloogium.engine.pack.PackSelection;
import com.schmaloogium.engine.pack.RendererFeatureData;
import com.schmaloogium.engine.pack.RuntimeIdentityData;
import com.schmaloogium.engine.preprocess.MacroContribution;
import com.schmaloogium.engine.registry.ProgramResolutionStatus;
import com.schmaloogium.engine.registry.ProgramSamplerLayout;
import com.schmaloogium.engine.registry.ProgramSlotId;
import com.schmaloogium.engine.registry.RegistryBuildRequest;
import com.schmaloogium.engine.registry.RegistryBuildResult;
import com.schmaloogium.engine.registry.ProgramRegistryView;
import com.schmaloogium.engine.registry.support.ScriptedGLDevice;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LinkedSamplerActivityTest {
    @TempDir Path root;

    private com.schmaloogium.engine.registry.FixedSamplerLayoutPolicy policy = FixedSamplerPolicies.appB3();

    private ProgramRegistryView compile(String declarations, String expression,
            ScriptedGLDevice device) throws Exception {
        var ready = assertInstanceOf(RegistryBuildResult.Ready.class, build(declarations, expression, device));
        try (var compiled = ready.candidate()) {
            return compiled.view();
        }
    }

    private RegistryBuildResult build(String declarations, String expression,
            ScriptedGLDevice device) throws Exception {
        Path shaders = root.resolve("activity/shaders");
        Files.createDirectories(shaders);
        Files.writeString(shaders.resolve("composite.vsh"),
            "#version 120\nvoid main() { gl_Position = gl_Vertex; }\n");
        Files.writeString(shaders.resolve("composite.fsh"),
            "#version 120\n" + declarations + "\nvoid main() { gl_FragColor = " + expression + "; }\n");
        var frontEnd = PackFrontEnds.create().frontEnd();
        var candidate = frontEnd.discover(new PackDiscoveryRequest(root, d -> {})).candidates()
            .stream().filter(c -> c.displayName().equals("activity")).findFirst().orElseThrow();
        var inspected = assertInstanceOf(PackInspectionResult.Inspected.class,
            frontEnd.inspect(new PackLoadRequest(root, new PackSelection.Filesystem(candidate.id()),
                new RuntimeIdentityData(1, 12, 2, "test", "1", OsFamily.LINUX, Map.of()),
                device.capabilities(), EngineOptionData.empty(), new CompanionOptionMacros(false, false),
                new RendererFeatureData(false, false), null, null, Optional.empty(), d -> {})));
        return RegistryAssembler.compile(
            new RegistryBuildRequest(inspected.configuration(), Optional.empty(), DimensionKey.BASE,
                new MacroContribution.Empty(), policy, device.capabilities(), device, d -> {}));
    }

    @Test
    void inactiveAliasesDoNotRejectButCompleteDeclarationProvenanceSurvives() throws Exception {
        var device = new ScriptedGLDevice();
        device.absentUniforms.addAll(Set.of("normals", "unassignedResource"));
        var view = compile("uniform sampler2D colortex0, normals, unassignedResource;",
            "texture2D(colortex0, vec2(0.5))", device);
        var resolved = view.resolve(new ProgramSlotId("composite")).orElseThrow();
        assertEquals(ProgramResolutionStatus.SOURCED, view.resolutions().stream()
            .filter(r -> r.slot().equals(new ProgramSlotId("composite"))).findFirst().orElseThrow().status());
        assertEquals(Set.of("colortex0", "normals", "unassignedResource"),
            resolved.uniformLayout().declarations().keySet());
        assertEquals(java.util.List.of("colortex0"), assertInstanceOf(ProgramSamplerLayout.Shader.class,
            resolved.samplerLayout()).declarations().stream().map(d -> d.exactName()).toList());
    }

    @Test
    void linkedActiveUnknownResourceStillFailsAndCannotBePublished() throws Exception {
        var device = new ScriptedGLDevice();
        var view = compile("uniform sampler2D unassignedResource;",
            "texture2D(unassignedResource, vec2(0.5))", device);
        var row = view.resolutions().stream().filter(r -> r.slot().equals(new ProgramSlotId("composite")))
            .findFirst().orElseThrow();
        assertEquals(ProgramResolutionStatus.FAILED, row.status());
        assertTrue(row.driverLog().contains("SAMPLER_LAYOUT_UNSUPPORTED"));
        assertTrue(device.samplerUnitsInitialized.isEmpty());
    }

    @Test
    void aggregateBaseNameAbsenceDoesNotDiscardPotentiallyActiveMembers() throws Exception {
        var device = new ScriptedGLDevice();
        device.absentUniforms.add("resourceArray");
        var view = compile("uniform sampler2D resourceArray[2];",
            "texture2D(resourceArray[1], vec2(0.5))", device);
        assertEquals(ProgramResolutionStatus.FAILED, view.resolutions().stream()
            .filter(r -> r.slot().equals(new ProgramSlotId("composite"))).findFirst().orElseThrow().status());
        assertTrue(device.samplerUnitsInitialized.isEmpty());
    }

    @Test
    void activeAliasesWithIncompatibleTypesFailBeforeSamplerInitialization() throws Exception {
        var device = new ScriptedGLDevice();
        var view = compile("uniform sampler2D colortex0; uniform sampler3D gcolor;",
            "texture2D(colortex0, vec2(0.5)) + texture3D(gcolor, vec3(0.5))", device);
        var row = view.resolutions().stream().filter(r -> r.slot().equals(new ProgramSlotId("composite")))
            .findFirst().orElseThrow();
        assertEquals(ProgramResolutionStatus.FAILED, row.status());
        assertTrue(row.driverLog().contains("SAMPLER_UNIT_TYPE_CONFLICT"));
        assertTrue(device.samplerUnitsInitialized.isEmpty());
    }

    @Test
    void policyFailureAfterLinkedProjectionDeletesUnpublishedPrograms() throws Exception {
        var device = new ScriptedGLDevice();
        device.absentUniforms.add("normals");
        policy = new com.schmaloogium.engine.registry.FixedSamplerLayoutPolicy() {
            @Override
            public com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint fingerprint() {
                return FixedSamplerPolicies.appB3Fingerprint();
            }

            @Override
            public com.schmaloogium.engine.registry.SamplerLayoutValidation validate(
                    com.schmaloogium.engine.registry.StageId stage,
                    com.schmaloogium.engine.registry.StageBand band,
                    java.util.List<com.schmaloogium.engine.registry.ProgramSamplerDeclaration> declarations) {
                if (declarations.size() == 1) {
                    throw new IllegalStateException("independent linked-policy failure");
                }
                return FixedSamplerPolicies.appB3().validate(stage, band, declarations);
            }

            @Override
            public java.util.List<com.schmaloogium.engine.gl.SamplerUnitAssignment> initializationAssignments(
                    ProgramSamplerLayout.Shader layout) {
                return FixedSamplerPolicies.appB3().initializationAssignments(layout);
            }
        };
        var result = assertInstanceOf(RegistryBuildResult.ShadersOff.class,
            build("uniform sampler2D colortex0, normals;", "texture2D(colortex0, vec2(0.5))", device));
        assertEquals(com.schmaloogium.engine.registry.RegistryFailureKind.INVALID_SAMPLER_POLICY,
            result.failure().kind());
        assertEquals(1, device.programHandlesDeleted.size());
        assertTrue(device.samplerUnitsInitialized.isEmpty());
    }
}
