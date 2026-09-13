// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.diff;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The §4.6.5 calibration record: the observed maxima of every measured metric over a set of
 * same-ordinal comparisons, the stated safety factor, and the profile those numbers propose.
 *
 * <p>Rules: each measured threshold ({@code maxDifferingFraction}, {@code maxDelta},
 * {@code maxRmse}, {@code maxClusterArea}, {@code maxClusters}) becomes
 * {@code max(current, ceil(observed × factor))}; {@code channelTolerance} is never changed
 * (it is the L1 definition of a differing pixel, not a measurement, and raising it hides
 * pixels); a zero observation keeps the current starting number but still stamps
 * {@code calibratedOn}, because "measured, floor zero" is a different statement from
 * "unmeasured". The record never lowers a threshold: calibration replaces starting numbers
 * with evidence, it does not tighten a profile on one lucky pair.
 */
public record Calibration(
        TolerancePolicy current,
        List<Observation> observations,
        double factor,
        TolerancePolicy proposed) {

    /** One same-ordinal comparison, diffed at {@code IDENTICAL} so every metric is observed. */
    public record Observation(String key, DiffResult result) {
        public Observation {
            if (key == null || key.isEmpty() || result == null) {
                throw new IllegalArgumentException("observation needs a key and a result");
            }
        }
    }

    public Calibration {
        observations = List.copyOf(observations);
    }

    public static Calibration of(TolerancePolicy current, List<Observation> observations, double factor,
            String calibratedOn) {
        if (current.advisory()) {
            throw new IllegalArgumentException("ADVISORY has no thresholds to calibrate");
        }
        if (!(factor >= 1.0) || !Double.isFinite(factor)) {
            throw new IllegalArgumentException("safety factor must be a finite number >= 1.0");
        }
        if (calibratedOn == null || calibratedOn.isBlank()) {
            throw new IllegalArgumentException("calibratedOn provenance is required");
        }
        double fraction = 0.0;
        int delta = 0;
        double rmse = 0.0;
        int area = 0;
        int clusters = 0;
        for (Observation o : observations) {
            DiffResult r = o.result();
            fraction = Math.max(fraction, r.differingFraction());
            delta = Math.max(delta, r.maxChannelDelta());
            rmse = Math.max(rmse, r.rmse());
            area = Math.max(area, r.largestClusterArea());
            clusters = Math.max(clusters, r.clusterCount());
        }
        TolerancePolicy proposed = new TolerancePolicy(current.name(), false,
            current.channelTolerance(),
            Math.max(current.maxDifferingFraction(), Math.min(1.0, fraction * factor)),
            Math.max(current.maxDelta(), Math.min(255, scaled(delta, factor))),
            Math.max(current.maxRmse(), Math.min(255.0, rmse * factor)),
            Math.max(current.maxClusterArea(), scaled(area, factor)),
            Math.max(current.maxClusters(), scaled(clusters, factor)),
            calibratedOn);
        return new Calibration(current, observations, factor, proposed);
    }

    private static int scaled(int observed, double factor) {
        return (int) Math.ceil(observed * factor);
    }

    /** Observed maxima over all observations, as the summary line prints them. */
    public String observedMaxima() {
        double fraction = 0.0;
        int delta = 0;
        double rmse = 0.0;
        int area = 0;
        int clusters = 0;
        for (Observation o : observations) {
            fraction = Math.max(fraction, o.result().differingFraction());
            delta = Math.max(delta, o.result().maxChannelDelta());
            rmse = Math.max(rmse, o.result().rmse());
            area = Math.max(area, o.result().largestClusterArea());
            clusters = Math.max(clusters, o.result().clusterCount());
        }
        return String.format(Locale.ROOT,
            "differingFraction=%.6f maxDelta=%d rmse=%.4f largestClusterArea=%d clusterCount=%d",
            fraction, delta, rmse, area, clusters);
    }

    /** The human record: every observation, the maxima, the factor and the proposed block. */
    public String report() {
        List<String> lines = new ArrayList<>();
        lines.add("calibration of [profile " + current.name() + "] over " + observations.size()
            + " same-ordinal comparison(s), safety factor " + CalibrationFormat.factor(factor));
        for (Observation o : observations) {
            lines.add("  " + o.key() + ": " + o.result().summary());
        }
        lines.add("  observed maxima: " + observedMaxima());
        lines.add("  channelTolerance stays " + current.channelTolerance() + " (L1 definition, not measured)");
        lines.add("proposed:");
        for (String l : TolerancePolicy.formatProfile(proposed).split("\n")) {
            lines.add("  " + l);
        }
        return String.join("\n", lines);
    }

    /** Whether the proposal changed any threshold (the provenance stamp always changes). */
    public boolean raisedAnyThreshold() {
        return proposed.maxDifferingFraction() != current.maxDifferingFraction()
            || proposed.maxDelta() != current.maxDelta()
            || proposed.maxRmse() != current.maxRmse()
            || proposed.maxClusterArea() != current.maxClusterArea()
            || proposed.maxClusters() != current.maxClusters();
    }

    static final class CalibrationFormat {
        private CalibrationFormat() {
        }

        static String factor(double factor) {
            return factor == Math.rint(factor) ? String.format(Locale.ROOT, "%.1f", factor)
                : Double.toString(factor);
        }
    }
}
