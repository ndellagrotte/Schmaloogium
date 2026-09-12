// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.wire;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The hand-written sectioned {@code key = value} grammar of PHASE_2_DOC §4.3.1, shared by
 * scene files, the tolerance profile file, the clock profile file and the pack registry:
 * {@code #} to end of line is a comment; {@code [name]} or {@code [kind name]} opens a
 * section; blank lines are insignificant; a key may appear once per section; a value is
 * the trimmed text after the first {@code =}. Sections keep file order.
 */
public final class SectionedText {

    /** One section: its header words (e.g. {@code ["shot", "main"]}) and ordered entries. */
    public record Section(List<String> header, int line, Map<String, String> entries) {

        public String kind() {
            return header.get(0);
        }

        public String name() {
            return header.size() > 1 ? header.get(1) : "";
        }
    }

    private SectionedText() {
    }

    public static List<Section> parse(String text) {
        List<Section> sections = new ArrayList<>();
        Section current = null;
        String[] lines = text.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int hash = line.indexOf('#');
            if (hash >= 0) {
                line = line.substring(0, hash);
            }
            line = line.strip();
            if (line.isEmpty()) {
                continue;
            }
            if (line.startsWith("[")) {
                if (!line.endsWith("]")) {
                    throw new IllegalArgumentException("line " + (i + 1) + ": unterminated section header");
                }
                String[] words = line.substring(1, line.length() - 1).strip().split("\\s+");
                if (words.length == 0 || words[0].isEmpty() || words.length > 2) {
                    throw new IllegalArgumentException("line " + (i + 1) + ": malformed section header");
                }
                current = new Section(List.of(words), i + 1, new LinkedHashMap<>());
                sections.add(current);
                continue;
            }
            int eq = line.indexOf('=');
            if (eq <= 0) {
                throw new IllegalArgumentException("line " + (i + 1) + ": expected 'key = value'");
            }
            if (current == null) {
                throw new IllegalArgumentException("line " + (i + 1) + ": entry before any section");
            }
            String key = line.substring(0, eq).strip();
            String value = line.substring(eq + 1).strip();
            if (current.entries().putIfAbsent(key, value) != null) {
                throw new IllegalArgumentException("line " + (i + 1) + ": duplicate key '" + key
                    + "' in section [" + String.join(" ", current.header()) + "]");
            }
        }
        return sections;
    }
}
