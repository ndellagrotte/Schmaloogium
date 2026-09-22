// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import com.schmaloogium.engine.diag.DiagnosticSeverity;
import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.diag.UserChannel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.anarres.cpp.Feature;
import org.anarres.cpp.LexerException;
import org.anarres.cpp.Preprocessor;
import org.anarres.cpp.StringLexerSource;
import org.anarres.cpp.Token;

/**
 * The jcpp wrapper for shader sources: immutable text in, processed text out,
 * comment retention on, every library exception converted to a diagnostic.
 */
public final class ShaderPreprocessor {

    // Match comments first so directive-looking text inside them is never transported.
    // Within a directive, consume entire block comments so suffix tokens and physical
    // newlines survive together; a slash inside a comment is not a directive boundary.
    private static final Pattern DRIVER_DIRECTIVE = Pattern.compile(
        "/\\*[\\s\\S]*?\\*/|//[^\\r\\n]*|#[\\t ]*(version|extension)\\b"
            + "(?:[^/\\r\\n]|/\\*[\\s\\S]*?\\*/|/(?![/*]))*(?://[^\\r\\n]*)?");

    private ShaderPreprocessor() {
    }

    /** Processes expanded text with the supplied macros; never throws for pack input. */
    public static Result process(String expandedText, Map<String, String> macros,
            int effectiveVersion) {
        List<EngineDiagnostic> diags = new ArrayList<>();
        StringBuilder sb = new StringBuilder(expandedText.length());
        String markerPrefix = "_schmaloogium_driver_directive_";
        while (expandedText.contains(markerPrefix)) markerPrefix += "_";
        Map<String, String> driverDirectives = new java.util.HashMap<>();
        Matcher directives = DRIVER_DIRECTIVE.matcher(expandedText);
        StringBuilder cppInput = new StringBuilder(expandedText.length());
        while (directives.find()) {
            if (directives.group(1) == null) continue;
            // One marker per physical line keeps __LINE__ and inactive-branch
            // whitespace unchanged. Each active marker restores only its own line.
            StringBuilder markers = new StringBuilder();
            for (String physicalLine : directives.group().split("\n", -1)) {
                if (!markers.isEmpty()) markers.append('\n');
                String marker = markerPrefix + driverDirectives.size();
                driverDirectives.put(marker, physicalLine);
                markers.append("#pragma ").append(marker);
            }
            directives.appendReplacement(cppInput, Matcher.quoteReplacement(markers.toString()));
        }
        directives.appendTail(cppInput);
        // jcpp understands C directives, not GLSL #version/#extension. Its pragma hook
        // runs only in active branches, preserving both driver text and conditionality
        // without inventing a version or hoisting an inactive extension.
        try (Preprocessor pp = new Preprocessor() {
            @Override
            protected void pragma(Token name, List<Token> value) throws IOException, LexerException {
                String directive = driverDirectives.get(name.getText());
                if (directive != null) {
                    sb.append(directive);
                } else {
                    super.pragma(name, value);
                }
            }
        }) {
            pp.addFeature(Feature.KEEPCOMMENTS);
            pp.setListener(new org.anarres.cpp.DefaultPreprocessorListener() {
                @Override
                public void handleWarning(org.anarres.cpp.Source source, int line, int column,
                        String msg) throws LexerException {
                    diags.add(diag("schmaloogium.warn.preprocess.cpp_warning", msg));
                }

                @Override
                public void handleError(org.anarres.cpp.Source source, int line, int column,
                        String msg) throws LexerException {
                    diags.add(new EngineDiagnostic(DiagnosticSeverity.ERROR, UserChannel.LOG_ONLY,
                        "schmaloogium.error.preprocess.failed", List.of(), msg,
                        "schmaloogium.preprocess"));
                }
            });
            // reserved GLSL intrinsics first so pack macros cannot shadow them
            try {
                pp.addMacro("__VERSION__", Integer.toString(effectiveVersion));
                pp.addMacro("GL_core_profile", "1");
                pp.addMacro("GL_compatibility_profile", "1");
            } catch (org.anarres.cpp.LexerException e) {
                throw new IllegalStateException("intrinsic macro installation failed", e);
            }
            for (Map.Entry<String, String> e : macros.entrySet()) {
                if (e.getValue().isEmpty()) {
                    pp.addMacro(new org.anarres.cpp.Macro(e.getKey()));
                } else {
                    pp.addMacro(e.getKey(), e.getValue());
                }
            }
            pp.addInput(new StringLexerSource(cppInput.toString(), true));
            while (true) {
                Token tok = pp.token();
                if (tok == null || tok.getType() == Token.EOF) {
                    break;
                }
                sb.append(tok.getText());
            }
        } catch (LexerException | IOException e) {
            diags.add(new EngineDiagnostic(DiagnosticSeverity.ERROR, UserChannel.LOG_ONLY,
                "schmaloogium.error.preprocess.failed", List.of(), String.valueOf(e.getMessage()),
                "schmaloogium.preprocess"));
        }
        return new Result(sb.toString(), List.copyOf(diags));
    }

    public record Result(String text, List<EngineDiagnostic> diagnostics) {
    }

    private static EngineDiagnostic diag(String key, String detail) {
        return new EngineDiagnostic(DiagnosticSeverity.WARN, UserChannel.LOG_ONLY, key,
            List.of(), detail, "schmaloogium.preprocess");
    }
}
