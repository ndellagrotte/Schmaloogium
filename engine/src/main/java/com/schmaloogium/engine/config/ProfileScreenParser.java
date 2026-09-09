// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses the profile, screen, and sliders families from the retained preprocessed
 * property stream into the immutable profile/screen/slider models.
 */
public final class ProfileScreenParser {

    private ProfileScreenParser() {
    }

    /** One decoded logical property line: key, value, 1-based physical line. */
    public record Line(String key, String value, int line) {
    }

    public static ProfileModel parseProfile(String name, List<String> tokens) {
        List<ProfileConstraint> constraints = new ArrayList<>();
        List<ProgramDisable> disabled = new ArrayList<>();
        for (String token : tokens) {
            String t = token.trim();
            if (t.isEmpty()) {
                continue;
            }
            boolean negated = t.startsWith("!");
            String body = negated ? t.substring(1) : t;
            if (body.startsWith("program.")) {
                String program = body.substring("program.".length());
                if (program.endsWith(":true")) {
                    program = program.substring(0, program.length() - ":true".length());
                }
                if (negated) {
                    disabled.add(new ProgramDisable(java.util.Optional.empty(), program));
                }
                continue;
            }
            int colon = body.indexOf(':');
            String option = colon < 0 ? body : body.substring(0, colon);
            String value = colon < 0 ? "true" : body.substring(colon + 1);
            if (!negated) {
                constraints.add(new ProfileConstraint(option, optionValue(value)));
            }
        }
        return new ProfileModel(new ProfileName(name), constraints, disabled);
    }

    private static OptionValue optionValue(String value) {
        if (value.equals("true") || value.equals("false")) {
            return new BooleanOptionValue(Boolean.parseBoolean(value));
        }
        return new TextOptionValue(value);
    }

    /** Parses screen entries; bare tokens name options, empty parens are separators. */
    public static List<ScreenEntry> parseScreen(String value) {
        List<ScreenEntry> out = new ArrayList<>();
        for (String token : value.trim().split("\\s+")) {
            if (token.isEmpty()) {
                continue;
            }
            String t = token.endsWith(",") ? token.substring(0, token.length() - 1) : token;
            if (t.equals("<empty>")) {
                out.add(new ScreenEmptyEntry());
                continue;
            }
            if (t.startsWith("[") && t.endsWith("]")) {
                out.add(new ScreenSubscreenEntry(t.substring(1, t.length() - 1)));
            } else if (t.equals("profile")) {
                out.add(new ScreenProfileEntry());
            } else if (t.equals("*") || t.startsWith("*")) {
                out.add(new ScreenAllOptionsEntry());
            } else {
                int colon = t.indexOf(':');
                out.add(new ScreenOptionEntry(colon > 0 ? t.substring(0, colon) : t));
            }
        }
        return out;
    }

    public static List<String> parseSliders(String value) {
        List<String> out = new ArrayList<>();
        for (String token : value.trim().split("\\s+")) {
            if (!token.isEmpty()) {
                out.add(token);
            }
        }
        return out;
    }
}
