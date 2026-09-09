// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The one SourceMaterializer: expands the root through the include graph, detects
 * the language, preprocesses with the pipeline macros plus any contribution,
 * scans declared uniforms with attributed locations, classifies the geometry form,
 * and validates the caller's geometry request.
 */
public final class MaterializerImpl implements SourceMaterializer {

    private final SourceIndex index;
    private final Map<String, String> macros;
    private final int effectiveVersion;

    public MaterializerImpl(SourceIndex index, Map<String, String> macros, int effectiveVersion) {
        this.index = index;
        this.macros = Map.copyOf(macros);
        this.effectiveVersion = effectiveVersion;
    }

    @Override
    public MaterializationResult materialize(
            SourceKey root, MacroContribution contribution, GeometrySourceRequest geometry) {
        IncludeExpander expander = new IncludeExpander(index);
        IncludeExpander.Result expansion = expander.expand(root);
        if (expansion instanceof IncludeExpander.Result.Failed failed) {
            return new MaterializationResult.Unavailable(root, failed.diagnostics().stream()
                .map(d -> diag(DiagnosticSeverity.ERROR, d.key(), d.detail()))
                .toList());
        }
        List<IncludeExpander.ExpandedLine> lines
            = ((IncludeExpander.Result.Expanded) expansion).lines();
        StringBuilder expandedText = new StringBuilder();
        lines.forEach(l -> expandedText.append(l.text()).append('\n'));

        ShaderLanguage language = LanguageScanner.scan(expandedText.toString(), root.source());
        int version = language.explicitVersion() ? language.version() : effectiveVersion;

        Map<String, String> effectiveMacros = new java.util.LinkedHashMap<>(macros);
        List<EngineDiagnostic> diags = new ArrayList<>(index.diagnostics());
        if (contribution instanceof MacroContribution.DefineCenterDepthSmooth define) {
            effectiveMacros.putIfAbsent("centerDepthSmooth", define.replacementTokens());
        }
        ShaderPreprocessor.Result processed
            = ShaderPreprocessor.process(expandedText.toString(), effectiveMacros, version);
        diags.addAll(processed.diagnostics());
        String transformed = processed.text();

        // declared uniforms with attribution aligned to the expanded stream when the
        // preprocessor preserved the line count, else attributed to the root document
        List<DeclaredUniform> uniforms = new ArrayList<>();
        List<UniformDeclScanner.Finding> findings = UniformDeclScanner.scan(transformed);
        String[] expandedLinesArr = expandedText.toString().split("\n", -1);
        boolean aligned = expandedLinesArr.length == transformed.split("\n", -1).length;
        for (UniformDeclScanner.Finding finding : findings) {
            SourceId source;
            int logicalLine;
            if (aligned) {
                IncludeExpander.ExpandedLine origin = lines.get(finding.line() - 1);
                source = origin.source();
                logicalLine = origin.line();
            } else {
                source = root.source();
                logicalLine = finding.line();
            }
            uniforms.add(new DeclaredUniform(finding.name(), finding.type(),
                root.stage(), new AttributedSourceLocation(source, logicalLine, 1)));
        }

        GeometrySourceForm form;
        if (root.stage() == ShaderSourceStage.GEOMETRY) {
            GeometryClassifier.Classified classified
                = GeometryClassifier.classify(root, expandedText.toString(), root.source());
            form = classified.form();
            diags.addAll(classified.diagnostics());
        } else {
            form = new GeometrySourceForm.None();
        }
        if (!requestSatisfied(geometry, form)) {
            diags.add(diag(DiagnosticSeverity.ERROR, "schmaloogium.error.geometry.request_mismatch",
                root.programName()));
            return new MaterializationResult.Unavailable(root, diags);
        }

        String fingerprint = hexSha256(
            (transformed + "|" + uniforms + "|" + language.version()).getBytes(StandardCharsets.UTF_8));
        return new MaterializationResult.Available(new MaterializedSource(root, transformed,
            sourceMap(), new DeclaredUniformCatalog(
                new MaterializationFingerprint(fingerprint), uniforms),
            language, form, diags, new MaterializationFingerprint(fingerprint)));
    }

    private static boolean requestSatisfied(GeometrySourceRequest request, GeometrySourceForm form) {
        if (request instanceof GeometrySourceRequest.None) {
            return true; // no expectation: native legacy absence is fine even for .gsh
        }
        if (request instanceof GeometrySourceRequest.PreserveNative expected) {
            return form instanceof GeometrySourceForm.NativeLegacy legacy
                && legacy.config().equals(expected.expected());
        }
        return false;
    }

    private SourceMap sourceMap() {
        Map<Integer, SourceId> files = new java.util.LinkedHashMap<>();
        index.fileNumbers().forEach((id, number) -> files.put(number, id));
        return new SourceMap(files, List.of());
    }

    private static String hexSha256(byte[] bytes) {
        try {
            byte[] digest = java.security.MessageDigest.getInstance("SHA-256").digest(bytes);
            StringBuilder hex = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                hex.append(String.format(java.util.Locale.ROOT, "%02x", b));
            }
            return hex.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static EngineDiagnostic diag(DiagnosticSeverity severity, String key, String detail) {
        return new EngineDiagnostic(severity, UserChannel.LOG_ONLY, key, List.of(detail), "",
            "schmaloogium.preprocess");
    }
}
