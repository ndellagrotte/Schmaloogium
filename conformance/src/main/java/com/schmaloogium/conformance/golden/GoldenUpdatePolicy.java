// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.golden;

import java.util.Locale;

/**
 * The golden update workflow's opt-in switch (§4.11.5): a normal test run compares
 * only; {@code ./gradlew :conformance:test -PupdateGoldens} rewrites the committed
 * goldens and then fails, so an update can never be an accident and the resulting diff
 * always gets human review. The Gradle property reaches the JVM as the
 * {@code schmaloogium.conformance.updateGoldens} system property (see
 * {@code conformance/build.gradle}).
 */
public enum GoldenUpdatePolicy {

    /** Compare against the committed golden and fail on any difference. Default. */
    COMPARE_ONLY,

    /** Rewrite the committed golden from current behaviour, then report the change so
     *  the build can fail after writing (the caller owns the fail step). */
    UPDATE_AND_FAIL;

    private static final String PROPERTY = "schmaloogium.conformance.updateGoldens";

    public static GoldenUpdatePolicy fromSystemProperty() {
        String raw = System.getProperty(PROPERTY, "false").trim().toLowerCase(Locale.ROOT);
        return switch (raw) {
            case "true" -> UPDATE_AND_FAIL;
            case "false" -> COMPARE_ONLY;
            default -> throw new IllegalArgumentException(
                PROPERTY + " must be true or false: " + raw);
        };
    }
}
