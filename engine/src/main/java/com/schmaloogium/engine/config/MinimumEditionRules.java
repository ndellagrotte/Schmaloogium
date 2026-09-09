// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;
import java.util.ArrayList;
import java.util.List;

import java.util.Locale;

/** D-P3-52 minimum-edition key/value grammar, canonical edition order, and comparison. */
public final class MinimumEditionRules {

    private MinimumEditionRules() {
    }

    /** Parses {@code version.<suffix>} rules; null on malformed input. */
    public static MinimumEditionRule parse(String key, String value) {
        if (!key.startsWith("version.")) {
            return null;
        }
        String suffix = key.substring("version.".length());
        if (!suffix.matches("([1-9][0-9]*)\\.(0|[1-9][0-9]*)(?:\\.(0|[1-9][0-9]*))?")) {
            return null;
        }
        String[] parts = suffix.split("\\.");
        for (String part : parts) {
            if (Long.parseLong(part) > Integer.MAX_VALUE) {
                return null;
            }
        }
        if (!value.matches("[A-Za-z0-9]+(?:[._-][A-Za-z0-9]+)*")
                || value.length() < 1 || value.length() > 64) {
            return null;
        }
        return new MinimumEditionRule(suffix, value);
    }

    /** Canonical edition form: ASCII upper-case, leading zeroes stripped per digit run. */
    public static String canonicalEdition(String edition) {
        StringBuilder sb = new StringBuilder(edition.length());
        for (String run : splitRuns(edition.toUpperCase(Locale.ROOT))) {
            sb.append(run);
        }
        return sb.toString();
    }

    private static List<String> splitRuns(String upper) {
        List<String> out = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean digitRun = false;
        for (int i = 0; i < upper.length(); i++) {
            char c = upper.charAt(i);
            boolean digit = c >= '0' && c <= '9';
            if (digit && digitRun) {
                if (!(current.length() == 1 && current.charAt(0) == '0')) {
                    current.append(c);
                } else if (c != '0') {
                    current.setCharAt(0, c);
                }
            } else {
                if (current.length() > 0) {
                    out.add(current.toString());
                }
                current = new StringBuilder();
                current.append(c);
                digitRun = digit;
            }
        }
        if (current.length() > 0) {
            out.add(current.toString());
        }
        return out;
    }

    /**
     * Locale-independent total order: equal-kind tokens compare by kind; unlike kinds
     * rank separator before digit before letter; shorter sequences sort first.
     */
    public static int compareEditions(String a, String b) {
        List<String> ta = tokens(canonicalEdition(a));
        List<String> tb = tokens(canonicalEdition(b));
        int n = Math.min(ta.size(), tb.size());
        for (int i = 0; i < n; i++) {
            int cmp = compareTokens(ta.get(i), tb.get(i));
            if (cmp != 0) {
                return cmp;
            }
        }
        return Integer.compare(ta.size(), tb.size());
    }

    private static List<String> tokens(String canonical) {
        List<String> out = new ArrayList<>();
        java.util.regex.Matcher m
            = java.util.regex.Pattern.compile("[A-Z]+|[0-9]+|[._-]").matcher(canonical);
        while (m.find()) {
            out.add(m.group());
        }
        return out;
    }

    private static int kindRank(String token) {
        char c = token.charAt(0);
        if (c == '.' || c == '_' || c == '-') {
            return 0;
        }
        return Character.isDigit(c) ? 1 : 2;
    }

    private static int compareTokens(String a, String b) {
        int ra = kindRank(a);
        int rb = kindRank(b);
        if (ra != rb) {
            return Integer.compare(ra, rb);
        }
        if (ra == 1) {
            return Long.compare(Long.parseLong(a), Long.parseLong(b));
        }
        return a.compareTo(b);
    }
}
