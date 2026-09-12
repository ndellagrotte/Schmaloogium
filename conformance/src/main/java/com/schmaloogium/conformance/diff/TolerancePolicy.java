// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.diff;

import com.schmaloogium.conformance.wire.SectionedText;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A named tolerance profile (PHASE_2_DOC §4.6.3), committed in
 * {@code conformance/fixtures/tolerances.profile}. Every gating profile carries all six
 * numeric thresholds and a {@code calibratedOn} provenance string (empty until §4.6.5 runs);
 * {@code ADVISORY} is the sole report-only variant, forbids thresholds and never yields a
 * verdict. No omission means zero, infinity or a disabled predicate.
 */
public record TolerancePolicy(String name, boolean advisory, int channelTolerance,
        double maxDifferingFraction, int maxDelta, double maxRmse, int maxClusterArea,
        int maxClusters, String calibratedOn) {

    public static final String ADVISORY_NAME = "ADVISORY";
    private static final Set<String> THRESHOLD_KEYS = Set.of("channelTolerance",
        "maxDifferingFraction", "maxDelta", "maxRmse", "maxClusterArea", "maxClusters");

    public TolerancePolicy {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("profile needs a name");
        }
        if (calibratedOn == null) {
            throw new IllegalArgumentException("calibratedOn is required (empty when unmeasured)");
        }
        if (!advisory) {
            if (channelTolerance < 0 || channelTolerance > 255 || maxDelta < 0 || maxDelta > 255) {
                throw new IllegalArgumentException("channelTolerance and maxDelta must be in [0,255]");
            }
            if (!(maxDifferingFraction >= 0.0 && maxDifferingFraction <= 1.0)) {
                throw new IllegalArgumentException("maxDifferingFraction must be finite in [0,1]");
            }
            if (!(maxRmse >= 0.0 && maxRmse <= 255.0)) {
                throw new IllegalArgumentException("maxRmse must be finite in [0,255]");
            }
            if (maxClusterArea < 0 || maxClusters < 0) {
                throw new IllegalArgumentException("cluster thresholds must be non-negative");
            }
        }
    }

    public static TolerancePolicy advisory(String calibratedOn) {
        return new TolerancePolicy(ADVISORY_NAME, true, 0, 0.0, 0, 0.0, 0, 0, calibratedOn);
    }

    public boolean calibrated() {
        return !calibratedOn.isEmpty();
    }

    /** Parses the committed profile file; unknown names are looked up by the caller. */
    public static Map<String, TolerancePolicy> parseFile(String text) {
        Map<String, TolerancePolicy> out = new LinkedHashMap<>();
        for (SectionedText.Section s : SectionedText.parse(text)) {
            if (!s.kind().equals("profile") || s.header().size() != 2) {
                throw new IllegalArgumentException("line " + s.line() + ": expected [profile <name>]");
            }
            Map<String, String> e = s.entries();
            if (!e.containsKey("calibratedOn")) {
                throw new IllegalArgumentException("[profile " + s.name() + "]: calibratedOn is required");
            }
            String calibratedOn = unquote(e.get("calibratedOn"), s.name());
            for (String key : e.keySet()) {
                if (!key.equals("calibratedOn") && !THRESHOLD_KEYS.contains(key)) {
                    throw new IllegalArgumentException("[profile " + s.name() + "]: unknown key " + key);
                }
            }
            TolerancePolicy policy;
            if (s.name().equals(ADVISORY_NAME)) {
                if (e.size() != 1) {
                    throw new IllegalArgumentException("[profile ADVISORY] forbids threshold fields");
                }
                policy = advisory(calibratedOn);
            } else {
                for (String key : THRESHOLD_KEYS) {
                    if (!e.containsKey(key)) {
                        throw new IllegalArgumentException("[profile " + s.name() + "]: missing " + key);
                    }
                }
                try {
                    policy = new TolerancePolicy(s.name(), false,
                        Integer.parseInt(e.get("channelTolerance")),
                        Double.parseDouble(e.get("maxDifferingFraction")),
                        Integer.parseInt(e.get("maxDelta")),
                        Double.parseDouble(e.get("maxRmse")),
                        Integer.parseInt(e.get("maxClusterArea")),
                        Integer.parseInt(e.get("maxClusters")),
                        calibratedOn);
                } catch (NumberFormatException bad) {
                    throw new IllegalArgumentException("[profile " + s.name() + "]: " + bad.getMessage());
                }
            }
            if (out.putIfAbsent(s.name(), policy) != null) {
                throw new IllegalArgumentException("duplicate profile " + s.name());
            }
        }
        return out;
    }

    public static TolerancePolicy require(Map<String, TolerancePolicy> profiles, String name) {
        TolerancePolicy policy = profiles.get(name);
        if (policy == null) {
            throw new IllegalArgumentException("unknown tolerance profile: " + name);
        }
        return policy;
    }

    private static String unquote(String raw, String profile) {
        if (raw.length() < 2 || raw.charAt(0) != '"' || raw.charAt(raw.length() - 1) != '"') {
            throw new IllegalArgumentException("[profile " + profile + "]: calibratedOn must be a"
                + " JSON string (\"\" when unmeasured)");
        }
        return com.schmaloogium.conformance.wire.CanonicalText.decodeJson(raw);
    }
}
