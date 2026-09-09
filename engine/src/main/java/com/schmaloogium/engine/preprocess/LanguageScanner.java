// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Detects the GLSL language of one root: #version and #extension directives. */
public final class LanguageScanner {

    private LanguageScanner() {
    }

    private static final Pattern VERSION = Pattern.compile(
        "^\\s*#version\\s+([0-9]+)\\s*(core|compatibility)?\\s*$");
    private static final Pattern EXTENSION = Pattern.compile(
        "^\\s*#extension\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*:\\s*(enable|require|warn|disable)\\s*$");

    public static ShaderLanguage scan(String text, SourceId rootSource) {
        Integer version = null;
        Optional<GlslProfile> profile = Optional.empty();
        List<ShaderExtensionDirective> extensions = new ArrayList<>();
        String[] lines = text.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            Matcher m = VERSION.matcher(lines[i]);
            if (m.matches()) {
                try {
                    version = Integer.parseInt(m.group(1));
                    profile = Optional.ofNullable(m.group(2))
                        .map(p -> p.equals("core") ? GlslProfile.CORE : GlslProfile.COMPATIBILITY);
                } catch (NumberFormatException ignored) {
                    // retain implicit language on a malformed version
                }
                continue;
            }
            Matcher ext = EXTENSION.matcher(lines[i]);
            if (ext.matches()) {
                extensions.add(new ShaderExtensionDirective(ext.group(1),
                    behaviorOf(ext.group(2)), new AttributedSourceLocation(
                        rootSource, i + 1, 1)));
            }
        }
        if (version == null) {
            return ShaderLanguage.implicit110();
        }
        return new ShaderLanguage(version, true, profile, extensions);
    }

    private static ExtensionBehavior behaviorOf(String token) {
        return switch (token) {
            case "enable" -> ExtensionBehavior.ENABLE;
            case "require" -> ExtensionBehavior.REQUIRE;
            case "warn" -> ExtensionBehavior.WARN;
            default -> ExtensionBehavior.DISABLE;
        };
    }
}
