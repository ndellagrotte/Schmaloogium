// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors
package com.schmaloogium.engine.preprocess;

import com.schmaloogium.engine.diag.EngineDiagnostic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.pack.NormalizedPackPath;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** End-to-end materializer: transformed text, uniform scan, geometry classification. */
class MaterializedSourceTest {

    private static Map<NormalizedPackPath, byte[]> files(String... nameTextPairs) {
        Map<NormalizedPackPath, byte[]> files = new LinkedHashMap<>();
        for (int i = 0; i < nameTextPairs.length; i += 2) {
            files.put(new NormalizedPackPath(nameTextPairs[i]),
                nameTextPairs[i + 1].getBytes(StandardCharsets.UTF_8));
        }
        return files;
    }

    private static SourceIndex index(Map<NormalizedPackPath, byte[]> files) {
        List<EngineDiagnostic> diags = new ArrayList<>();
        SourceIndex index = SourceIndex.build(files, diags);
        assertEquals(List.of(), reportable(diags));
        return index;
    }

    private static List<String> reportable(List<EngineDiagnostic> diags) {
        return diags.stream().filter(d -> !d.messageKey().endsWith("warn.idmap.disabled"))
            .map(EngineDiagnostic::messageKey).toList();
    }

    private static SourceKey root(SourceIndex index, String program, ShaderSourceStage stage) {
        return index.roots().stream()
            .filter(r -> r.programName().equals(program) && r.stage() == stage)
            .findFirst()
            .orElseThrow();
    }

    @Test
    void materializedTextCarriesIncludesAndUniformCatalog() {
        Map<NormalizedPackPath, byte[]> files = files(
            "shaders/composite.fsh",
            """
            #version 120

            #include "/lib/glow.glsl"

            uniform vec4 tintColor;
            void main() { gl_FragColor = tintColor; }
            """,
            "shaders/lib/glow.glsl",
            """
            uniform float brightness;
            """);
        SourceIndex index = index(files);
        MaterializerImpl materializer = new MaterializerImpl(index, Map.of("MC_VERSION", "11202"),
            120);

        MaterializationResult result = materializer.materialize(root(index, "composite",
            ShaderSourceStage.FRAGMENT), new MacroContribution.Empty(),
            new GeometrySourceRequest.None());

        MaterializedSource source = assertInstanceOf(MaterializationResult.Available.class,
            result).source();
        assertTrue(source.transformedText().contains("uniform float brightness"),
            "include expansion must feed the transformed text");
        assertTrue(source.transformedText().contains("uniform vec4 tintColor"),
            "root declarations must survive");

        List<DeclaredUniform> uniforms = source.declaredUniforms().declarations();
        assertEquals(2, uniforms.size());
        DeclaredUniform brightness = uniforms.stream()
            .filter(u -> u.exactName().equals("brightness")).findFirst().orElseThrow();
        assertInstanceOf(DeclaredGlslType.Scalar.class, brightness.type());
        assertEquals(ScalarKind.FLOAT,
            ((DeclaredGlslType.Scalar) brightness.type()).kind());
        assertEquals("shaders/lib/glow.glsl", brightness.location().source().path().canonicalString(),
            "uniform attributed to the included file");
        DeclaredUniform tint = uniforms.stream()
            .filter(u -> u.exactName().equals("tintColor")).findFirst().orElseThrow();
        assertInstanceOf(DeclaredGlslType.Vector.class, tint.type());
        assertEquals(4, ((DeclaredGlslType.Vector) tint.type()).width());
        assertEquals("shaders/composite.fsh", tint.location().source().path().canonicalString());

        assertTrue(source.language().explicitVersion());
        assertEquals(120, source.language().version());
    }

    @Test
    void geometryCoreLayoutClassifiedAndLegacyDirectiveHonored() {
        Map<NormalizedPackPath, byte[]> files = files(
            "shaders/composite.gsh",
            """
            #version 120
            layout(points) in;
            layout(triangle_strip, max_vertices=4) out;
            void main() { }
            """);
        SourceIndex index = index(files);
        MaterializerImpl materializer = new MaterializerImpl(index, Map.of(), 120);

        MaterializedSource source = assertInstanceOf(MaterializationResult.Available.class,
            materializer.materialize(root(index, "composite", ShaderSourceStage.GEOMETRY),
                new MacroContribution.Empty(), new GeometrySourceRequest.None())).source();

        GeometrySourceForm.CoreLayout core = assertInstanceOf(GeometrySourceForm.CoreLayout.class,
            source.geometry());
        assertEquals(GeometryInputPrimitive.POINTS, core.effective().input());
        assertEquals(GeometryOutputPrimitive.TRIANGLE_STRIP, core.effective().output());
        assertEquals(4, core.effective().maxVertices());
        assertEquals(2, core.declarations().size());
    }

    @Test
    void legacyMaxVerticesDirectiveProducesNativeLegacyForm() {
        Map<NormalizedPackPath, byte[]> files = files(
            "shaders/composite.gsh",
            """
            #version 120
            // MAXVERTICES:6
            #extension GL_ARB_geometry_shader4 : enable
            void main() { }
            """);
        SourceIndex index = index(files);
        MaterializerImpl materializer = new MaterializerImpl(index, Map.of(), 120);
        SourceKey geometryRoot = root(index, "composite", ShaderSourceStage.GEOMETRY);

        MaterializedSource source = assertInstanceOf(MaterializationResult.Available.class,
            materializer.materialize(geometryRoot, new MacroContribution.Empty(),
                new GeometrySourceRequest.None())).source();

        GeometrySourceForm.NativeLegacy legacy = assertInstanceOf(
            GeometrySourceForm.NativeLegacy.class, source.geometry());
        assertEquals(6, legacy.config().maxVertices());
        assertEquals(LegacyGeometryExtension.GL_ARB_GEOMETRY_SHADER4,
            legacy.config().extension());

        // a PreserveNative request expecting this exact config is satisfied
        MaterializationResult matched = materializer.materialize(geometryRoot,
            new MacroContribution.Empty(),
            new GeometrySourceRequest.PreserveNative(legacy.config()));
        assertInstanceOf(MaterializationResult.Available.class, matched);

        // a request expecting a different legacy config fails with a diagnostic
        LegacyGeometryConfig other = new LegacyGeometryConfig(geometryRoot,
            LegacyGeometryExtension.GL_ARB_GEOMETRY_SHADER4, 9, legacy.config().site());
        MaterializationResult mismatched = materializer.materialize(geometryRoot,
            new MacroContribution.Empty(), new GeometrySourceRequest.PreserveNative(other));
        MaterializationResult.Unavailable unavailable =
            assertInstanceOf(MaterializationResult.Unavailable.class, mismatched);
        assertTrue(unavailable.diagnostics().stream()
            .anyMatch(d -> d.messageKey().equals("schmaloogium.error.geometry.request_mismatch")));
    }

    @Test
    void missingIncludeSurfacesAsWarningNotFailure() {
        Map<NormalizedPackPath, byte[]> files = files(
            "shaders/composite.fsh",
            """
            #version 120
            #include "/lib/missing.glsl"
            """);
        List<EngineDiagnostic> diags = new ArrayList<>();
        SourceIndex index = SourceIndex.build(files, diags);
        MaterializerImpl materializer = new MaterializerImpl(index, Map.of(), 120);

        // a missing include is a warning on the index: materialization still succeeds
        MaterializedSource source = assertInstanceOf(MaterializationResult.Available.class,
            materializer.materialize(root(index, "composite", ShaderSourceStage.FRAGMENT),
                new MacroContribution.Empty(), new GeometrySourceRequest.None())).source();
        assertTrue(source.diagnostics().stream()
                .anyMatch(d -> d.severity() == com.schmaloogium.engine.diag.DiagnosticSeverity.WARN
                    && d.messageKey().contains("include")),
            "the missing-include warning must travel with the materialized source");
    }
}
