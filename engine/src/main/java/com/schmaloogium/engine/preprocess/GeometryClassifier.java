// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import com.schmaloogium.engine.diag.EngineDiagnostic;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classifies the geometry source form of one geometry-stage root: core
 * {@code layout(...)} declarations or the legacy GL_ARB_geometry_shader4 form
 * (extension + MAXVERTICES comment directive), per section 4.5.
 */
public final class GeometryClassifier {

    private GeometryClassifier() {
    }

    private static final Pattern LAYOUT = Pattern.compile(
        "^\\s*layout\\s*\\(([^)]*)\\)\\s*(in|out)\\s*;.*$");
    private static final Pattern EXTENSION_STMT = Pattern.compile(
        "^\\s*#?\\s*extension\\s+GL_ARB_geometry_shader4\\s*:\\s*enable.*$");
    private static final Pattern MAXVERTICES_DIRECTIVE = Pattern.compile(
        "^.*MAXVERTICES\\s*:\\s*([0-9]+).*$");
    private static final Pattern INPUT_PRIMITIVE = Pattern.compile(
        "(points|lines|lines_adjacency|triangles|triangles_adjacency)");
    private static final Pattern OUTPUT_PRIMITIVE
        = Pattern.compile("(points|line_strip|triangle_strip)");
    private static final Pattern MAX_VERTICES = Pattern.compile("max_vertices\\s*=\\s*([0-9]+)");

    /** Result: the classified form plus any hard classification diagnostics. */
    public record Classified(GeometrySourceForm form, List<EngineDiagnostic> diagnostics) {

        public Classified {
            diagnostics = List.copyOf(diagnostics);
        }
    }

    public static Classified classify(SourceKey root, String text, SourceId rootSource) {
        List<EngineDiagnostic> diags = new ArrayList<>();
        List<GeometryLayoutDeclaration> declarations = new ArrayList<>();
        String[] lines = text.split("\\n", -1);
        LegacyGeometryConfig legacy = null;
        for (int i = 0; i < lines.length; i++) {
            int lineNo = i + 1;
            String line = CommentStripper.stripComments(lines[i]);
            Matcher extension = EXTENSION_STMT.matcher(line);
            if (extension.matches()) {
                // legacy form: extension statement plus MAXVERTICES comment directive
                continue;
            }
            Matcher maxDirective = MAXVERTICES_DIRECTIVE.matcher(lines[i]);
            if (maxDirective.matches()) {
                int maxVertices = Integer.parseInt(maxDirective.group(1));
                if (maxVertices <= 0 || maxVertices > 1024) {
                    diags.add(diag("schmaloogium.error.geometry.max_vertices", lineNo));
                    continue;
                }
                legacy = new LegacyGeometryConfig(root, LegacyGeometryExtension.GL_ARB_GEOMETRY_SHADER4,
                    maxVertices, new LegacyGeometrySite(root,
                        new SourceSpan(rootSource, 0, 1, lineNo, 1),
                        new SourceSpan(rootSource, 0, 1, lineNo, 1)));
                continue;
            }
            Matcher layout = LAYOUT.matcher(line);
            if (!layout.matches()) {
                continue;
            }
            String body = layout.group(1);
            if (layout.group(2).equals("in")) {
                Matcher primitive = INPUT_PRIMITIVE.matcher(body);
                if (!primitive.find()) {
                    diags.add(diag("schmaloogium.warn.geometry.layout_input", lineNo));
                    continue;
                }
                declarations.add(new GeometryLayoutDeclaration(
                    Optional.of(inputOf(primitive.group(1))), Optional.empty(),
                    OptionalInt.empty(), new AttributedSourceLocation(rootSource, lineNo, 1)));
            } else {
                Matcher primitive = OUTPUT_PRIMITIVE.matcher(body);
                Matcher maxV = MAX_VERTICES.matcher(body);
                if (!primitive.find()) {
                    diags.add(diag("schmaloogium.warn.geometry.layout_output", lineNo));
                    continue;
                }
                if (maxV.find()) {
                    int maxVertices = Integer.parseInt(maxV.group(1));
                    declarations.add(new GeometryLayoutDeclaration(Optional.empty(),
                        Optional.of(outputOf(primitive.group(1))),
                        maxVertices > 0 ? OptionalInt.of(maxVertices) : OptionalInt.empty(),
                        new AttributedSourceLocation(rootSource, lineNo, 1)));
                } else {
                    declarations.add(new GeometryLayoutDeclaration(Optional.empty(),
                        Optional.of(outputOf(primitive.group(1))), OptionalInt.empty(),
                        new AttributedSourceLocation(rootSource, lineNo, 1)));
                }
            }
        }
        if (legacy != null) {
            return new Classified(new GeometrySourceForm.NativeLegacy(legacy,
                effectiveOf(declarations, legacy.maxVertices(), diags), declarations), diags);
        }
        if (declarations.isEmpty()) {
            return new Classified(new GeometrySourceForm.None(), diags);
        }
        GeometryLayout effective = effectiveOf(declarations, defaultMaxVertices(declarations), diags);
        return new Classified(new GeometrySourceForm.CoreLayout(effective, declarations), diags);
    }

    private static GeometryLayout effectiveOf(List<GeometryLayoutDeclaration> declarations,
            int fallbackMaxVertices, List<EngineDiagnostic> diags) {
        Optional<GeometryInputPrimitive> input = Optional.empty();
        Optional<GeometryOutputPrimitive> output = Optional.empty();
        int maxVertices = fallbackMaxVertices;
        for (GeometryLayoutDeclaration d : declarations) {
            if (d.input().isPresent()) {
                input = d.input();
            }
            if (d.output().isPresent()) {
                output = d.output();
            }
            if (d.maxVertices().isPresent()) {
                maxVertices = d.maxVertices().getAsInt();
            }
        }
        if (input.isEmpty() || output.isEmpty()) {
            diags.add(diag("schmaloogium.error.geometry.incomplete_layout", 1));
        }
        return new GeometryLayout(input.orElse(GeometryInputPrimitive.TRIANGLES),
            output.orElse(GeometryOutputPrimitive.TRIANGLE_STRIP), maxVertices);
    }

    private static int defaultMaxVertices(List<GeometryLayoutDeclaration> declarations) {
        for (GeometryLayoutDeclaration d : declarations) {
            if (d.maxVertices().isPresent()) {
                return d.maxVertices().getAsInt();
            }
        }
        return 4;
    }

    private static GeometryInputPrimitive inputOf(String token) {
        return switch (token) {
            case "points" -> GeometryInputPrimitive.POINTS;
            case "lines" -> GeometryInputPrimitive.LINES;
            case "lines_adjacency" -> GeometryInputPrimitive.LINES_ADJACENCY;
            case "triangles" -> GeometryInputPrimitive.TRIANGLES;
            case "triangles_adjacency" -> GeometryInputPrimitive.TRIANGLES_ADJACENCY;
            default -> GeometryInputPrimitive.TRIANGLES;
        };
    }

    private static GeometryOutputPrimitive outputOf(String token) {
        return switch (token) {
            case "points" -> GeometryOutputPrimitive.POINTS;
            case "line_strip" -> GeometryOutputPrimitive.LINE_STRIP;
            case "triangle_strip" -> GeometryOutputPrimitive.TRIANGLE_STRIP;
            default -> GeometryOutputPrimitive.TRIANGLE_STRIP;
        };
    }

    private static EngineDiagnostic diag(String key, int line) {
        return new EngineDiagnostic(com.schmaloogium.engine.diag.DiagnosticSeverity.WARN,
            com.schmaloogium.engine.diag.UserChannel.LOG_ONLY, key, List.of(line), "",
            "schmaloogium.preprocess");
    }
}
