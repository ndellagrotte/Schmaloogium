// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.golden;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Golden comparison (§4.11.5's review substrate): every difference names its section,
 * key, expected and actual value, so the human reviewing an update can explain each
 * line. Equal documents yield an empty diff.
 */
public final class GoldenComparer {

    /** One difference; {@code key} is empty for whole-section differences. */
    public record Difference(Kind kind, String section, String key, String expected, String actual) {

        public enum Kind { CHANGED_VALUE, MISSING_SECTION, EXTRA_SECTION }

        @Override
        public String toString() {
            return switch (kind) {
                case CHANGED_VALUE -> "[" + section + "] " + key + ": expected <" + expected
                    + "> actual <" + actual + ">";
                case MISSING_SECTION -> "[" + section + "] missing from committed golden";
                case EXTRA_SECTION -> "[" + section + "] absent from produced golden";
            };
        }
    }

    private GoldenComparer() {
    }

    /** Compares the produced document against the committed expectation. */
    public static List<Difference> compare(GoldenDocument committed, GoldenDocument produced) {
        List<Difference> diffs = new ArrayList<>();
        // The header is part of the contract: pack identity, configuration fingerprint,
        // profile and projection version all pin the run (§4.11.1).
        for (Map.Entry<String, String> field : committed.header().entrySet()) {
            String actual = produced.header().get(field.getKey());
            if (actual == null) {
                diffs.add(new Difference(Difference.Kind.CHANGED_VALUE, "",
                    field.getKey(), field.getValue(), "<absent>"));
            } else if (!actual.equals(field.getValue())) {
                diffs.add(new Difference(Difference.Kind.CHANGED_VALUE, "",
                    field.getKey(), field.getValue(), actual));
            }
        }
        for (String key : produced.header().keySet()) {
            if (!committed.header().containsKey(key)) {
                diffs.add(new Difference(Difference.Kind.CHANGED_VALUE, "",
                    key, "<absent>", produced.header().get(key)));
            }
        }
        for (Map.Entry<String, SortedMap<String, String>> s : committed.sections().entrySet()) {
            SortedMap<String, String> actualRows = produced.sections().get(s.getKey());
            if (actualRows == null || actualRows.isEmpty()) {
                diffs.add(new Difference(Difference.Kind.MISSING_SECTION, s.getKey(), "", "", ""));
                continue;
            }
            for (Map.Entry<String, String> row : s.getValue().entrySet()) {
                String actual = actualRows.get(row.getKey());
                if (actual == null) {
                    diffs.add(new Difference(Difference.Kind.CHANGED_VALUE, s.getKey(),
                        row.getKey(), row.getValue(), "<absent>"));
                } else if (!actual.equals(row.getValue())) {
                    diffs.add(new Difference(Difference.Kind.CHANGED_VALUE, s.getKey(),
                        row.getKey(), row.getValue(), actual));
                }
            }
        }
        for (String name : produced.sections().keySet()) {
            SortedMap<String, String> rows = produced.sections().get(name);
            if (rows != null && !rows.isEmpty() && committed.sections().get(name) == null) {
                diffs.add(new Difference(Difference.Kind.EXTRA_SECTION, name, "", "", ""));
            }
        }
        return diffs;
    }
}
