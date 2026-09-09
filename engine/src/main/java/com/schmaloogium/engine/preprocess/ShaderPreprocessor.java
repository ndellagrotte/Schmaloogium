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

    private ShaderPreprocessor() {
    }

    /** Processes expanded text with the supplied macros; never throws for pack input. */
    public static Result process(String expandedText, Map<String, String> macros,
            int effectiveVersion) {
        List<EngineDiagnostic> diags = new ArrayList<>();
        StringBuilder sb = new StringBuilder(expandedText.length());
        try (Preprocessor pp = new Preprocessor()) {
            pp.addFeature(Feature.KEEPCOMMENTS);
            pp.setListener(new org.anarres.cpp.DefaultPreprocessorListener() {
                @Override
                public void handleWarning(org.anarres.cpp.Source source, int line, int column,
                        String msg) throws LexerException {
                    diags.add(diag("schmaloogium.warn.preprocess.cpp_warning", msg));
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
            pp.addInput(new StringLexerSource(expandedText, true));
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
