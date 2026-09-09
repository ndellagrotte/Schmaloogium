// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.anarres.cpp.Feature;
import org.anarres.cpp.Preprocessor;
import org.anarres.cpp.StringLexerSource;
import org.anarres.cpp.Token;

/**
 * The lossless properties-safe preprocessing adapter: supported directive lines run
 * through jcpp, every data-line '#' rides in a protected printable token, and no
 * trim or '#' stripping ever touches a value (D-P3 directive posture).
 */
public final class PropertiesPreprocessor {

    private static final Pattern DIRECTIVE = Pattern.compile(
        "^#[ \\t]*(ifdef|ifndef|if|elif|else|endif|define|undef)\\b.*");
    private static final String TOKEN_OPEN = "@@SCHMPROT";
    private static final String TOKEN_CLOSE = "@@";
    private static final Pattern TOKEN_PATTERN = Pattern.compile("@@SCHMPROT(\\d+)@@");

    private PropertiesPreprocessor() {
    }

    public static String preprocess(String text, Map<String, String> macros) {
        StringBuilder processed = new StringBuilder(text.length());
        List<String> protectedTokens = new ArrayList<>();
        for (String line : text.split("\\n", -1)) {
            String crlf = line.endsWith("\\r") ? "\\r" : "";
            String body = line.endsWith("\\r") ? line.substring(0, line.length() - 1) : line;
            String stripped = body.stripLeading();
            if (stripped.startsWith("#") && DIRECTIVE.matcher(stripped).matches()) {
                processed.append(body).append(crlf).append('\n');
                continue;
            }
            processed.append(guardHashes(body, protectedTokens)).append(crlf).append('\n');
        }
        String jcppOut = runJcpp(processed.toString(), macros);
        return restore(jcppOut, protectedTokens);
    }

    private static String guardHashes(String body, List<String> protectedTokens) {
        if (body.indexOf('#') < 0) {
            return body;
        }
        StringBuilder sb = new StringBuilder(body.length());
        for (int i = 0; i < body.length(); i++) {
            char c = body.charAt(i);
            if (c == '#') {
                sb.append(TOKEN_OPEN).append(protectedTokens.size()).append(TOKEN_CLOSE);
                protectedTokens.add("#");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String restore(String text, List<String> protectedTokens) {
        if (!text.contains(TOKEN_OPEN)) {
            return text;
        }
        StringBuilder out = new StringBuilder(text.length());
        Matcher m = TOKEN_PATTERN.matcher(text);
        int last = 0;
        while (m.find()) {
            out.append(text, last, m.start());
            int idx = Integer.parseInt(m.group(1));
            out.append(idx < protectedTokens.size() ? protectedTokens.get(idx) : "");
            last = m.end();
        }
        out.append(text.substring(last));
        return out.toString();
    }

    private static String runJcpp(String text, Map<String, String> macros) {
        StringBuilder sb = new StringBuilder(text.length());
        try (Preprocessor pp = new Preprocessor()) {
            pp.addFeature(Feature.KEEPCOMMENTS);
            for (Map.Entry<String, String> e : macros.entrySet()) {
                if (e.getValue().isEmpty()) {
                    pp.addMacro(new org.anarres.cpp.Macro(e.getKey()));
                } else {
                    pp.addMacro(e.getKey(), e.getValue());
                }
            }
            pp.addInput(new StringLexerSource(text, true));
            while (true) {
                Token tok = pp.token();
                if (tok == null || tok.getType() == Token.EOF) {
                    break;
                }
                sb.append(tok.getText());
            }
        } catch (Exception e) {
            return text; // malformed directives are ignored line-locally by callers
        }
        return sb.toString();
    }
}
