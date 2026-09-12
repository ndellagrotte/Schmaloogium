// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.diff;

import java.util.List;

/**
 * The three-level findings of one comparison (§4.6.2) and, for a gating profile, the
 * verdict with every failed predicate named. {@code advisory} results carry metrics and
 * no verdict.
 */
public record DiffResult(
        String profile,
        boolean advisory,
        int width,
        int height,
        long unmaskedPixels,
        long differingPixels,
        double differingFraction,
        int maxChannelDelta,
        double rmse,
        int clusterCount,
        int largestClusterArea,
        double maskedFraction,
        List<String> failedPredicates) {

    public DiffResult {
        failedPredicates = List.copyOf(failedPredicates);
    }

    /** True only for a gating profile with every L2/L3 predicate satisfied. */
    public boolean passed() {
        return !advisory && failedPredicates.isEmpty();
    }

    public String summary() {
        return String.format(java.util.Locale.ROOT,
            "%s: differing=%d/%d (%.6f) maxDelta=%d rmse=%.4f clusters=%d largest=%d masked=%.4f%s",
            profile, differingPixels, unmaskedPixels, differingFraction, maxChannelDelta, rmse,
            clusterCount, largestClusterArea, maskedFraction,
            advisory ? " [advisory, no verdict]" : (passed() ? " PASS" : " FAIL " + failedPredicates));
    }
}
