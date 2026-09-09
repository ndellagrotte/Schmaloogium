// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import com.schmaloogium.engine.config.ProgramKey;
import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.pack.NormalizedPackPath;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

/** Source index include-graph resolution: roots, edges, cycles, expansion order. */
class SourceIndexIncludeTest {

    private static Map<NormalizedPackPath, byte[]> files(String... kv) {
        Map<NormalizedPackPath, byte[]> m = new TreeMap<>(NormalizedPackPath.ORDER);
        for (int i = 0; i < kv.length; i += 2) {
            m.put(new NormalizedPackPath(kv[i]), bytes(kv[i + 1]));
        }
        return m;
    }

    private static byte[] bytes(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void rootsAndExecutableProgramsAreDiscovered() {
        var files = files(
            "shaders/composite.vsh", "void main() {}\n",
            "shaders/composite.fsh", "void main() {}\n",
            "shaders/lib/common.glsh", "int shared;\n",
            "shaders/shaders.properties", "screen=<empty>\n");
        List<EngineDiagnostic> diags = new ArrayList<>();
        SourceIndex index = SourceIndex.build(files, diags);
        assertTrue(diags.isEmpty());
        assertEquals(2, index.roots().size());
        assertEquals(1, index.executablePrograms().size());
        ProgramKey composite = index.executablePrograms().iterator().next();
        assertEquals("composite", composite.programName());
        assertEquals(DimensionKey.BASE, composite.dimension());
    }

    @Test
    void includeEdgesLinkRootToLibrary() {
        var files = files(
            "shaders/composite.fsh",
            "#include \"/lib/common.glsh\"\nvoid main() {}\n",
            "shaders/lib/common.glsh", "int shared;\n");
        List<EngineDiagnostic> diags = new ArrayList<>();
        SourceIndex index = SourceIndex.build(files, diags);
        assertEquals(1, index.includeEdges().size());
        assertEquals(1, index.includeEdges().get(0).logicalLine());
        IncludeEdge edge = index.includeEdges().get(0);
        assertTrue(edge.including().path().canonicalString().endsWith("composite.fsh"));
        assertEquals("shaders/lib/common.glsh", edge.requested().canonicalString());
        assertTrue(edge.included().isPresent());
        assertTrue(edge.included().get().path().canonicalString().endsWith("lib/common.glsh"));
    }

    @Test
    void missingIncludeProducesDiagnosticNotFailure() {
        var files = files(
            "shaders/composite.fsh",
            "#include \"/absent.glsh\"\nvoid main() {}\n");
        List<EngineDiagnostic> diags = new ArrayList<>();
        SourceIndex index = SourceIndex.build(files, diags);
        assertEquals(1, index.diagnostics().size());
        assertEquals(DiagnosticSeverity.WARN, index.diagnostics().get(0).severity());
        assertFalse(index.roots().isEmpty());
    }

    @Test
    void includeCycleIsDetectedAndReported() {
        var files = files(
            "shaders/composite.fsh",
            "#include \"/lib/a.glsh\"\nvoid main() {}\n",
            "shaders/lib/a.glsh", "#include \"/lib/b.glsh\"\n",
            "shaders/lib/b.glsh", "#include \"/lib/a.glsh\"\n");
        List<EngineDiagnostic> diags = new ArrayList<>();
        SourceIndex index = SourceIndex.build(files, diags);
        assertTrue(index.diagnostics().stream()
                .anyMatch(d -> d.messageKey().contains("cycle")),
            "cycle must produce a cycle diagnostic");
    }

    @Test
    void expansionVisitsIncludesInOrderWithLineDirectives() {
        var files = files(
            "shaders/composite.fsh",
            "#include \"/lib/common.glsh\"\nvoid main() { shared(); }\n",
            "shaders/lib/common.glsh", "void shared() {}\n");
        List<EngineDiagnostic> diags = new ArrayList<>();
        SourceIndex index = SourceIndex.build(files, diags);
        SourceKey root = index.roots().get(0);
        IncludeExpander expander = new IncludeExpander(index);
        IncludeExpander.Result result = expander.expand(root);
        assertInstanceOf(IncludeExpander.Result.Expanded.class, result);
        List<IncludeExpander.ExpandedLine> lines
            = ((IncludeExpander.Result.Expanded) result).lines();
        String joined = lines.stream().map(IncludeExpander.ExpandedLine::text)
            .reduce("", (a, b) -> a + "\n" + b);
        assertTrue(joined.contains("void shared() {}"));
        assertTrue(joined.contains("void main() { shared(); }"));
        // the expansion interleaves line directives for attribution
        assertTrue(joined.contains("#line"));
        lines.forEach(l -> assertNotNull(l.source()));
    }

    @Test
    void worldOverrideRootsLandInOverrideDimension() {
        var files = files(
            "shaders/world0/composite.fsh", "void main() {}\n",
            "shaders/composite.fsh", "void main() {}\n");
        List<EngineDiagnostic> diags = new ArrayList<>();
        SourceIndex index = SourceIndex.build(files, diags);
        assertEquals(2, index.roots().size());
        long override = index.roots().stream()
            .filter(k -> !k.dimension().equals(DimensionKey.BASE)).count();
        assertEquals(1, override);
    }
}
