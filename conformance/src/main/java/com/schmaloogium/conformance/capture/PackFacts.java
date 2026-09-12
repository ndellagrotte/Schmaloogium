// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.capture;

import com.schmaloogium.conformance.harness.FrontEndSession;
import com.schmaloogium.engine.config.BooleanOptionValue;
import com.schmaloogium.engine.config.OptionValue;
import com.schmaloogium.engine.config.SmoothingConstants;
import com.schmaloogium.engine.config.TextOptionValue;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.pack.PackCandidate;
import com.schmaloogium.engine.pack.PackCandidateKind;
import com.schmaloogium.engine.pack.PackCandidateStatus;
import com.schmaloogium.engine.pack.PackDiscoveryResult;
import com.schmaloogium.engine.pack.PackInspectionResult;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

/**
 * Headless P3 facts the plan needs from the pack itself: the complete default option state
 * (the effective map at v0.1, where no scene pins options) and the largest declared smoothing
 * half-life for §4.4's warm-up floor. Obtained through the real front end over the verified
 * archive's directory, never by parsing shader text here.
 */
public record PackFacts(Map<String, String> packOptionDefaults, double largestHalflifeTicks) {

    /** Mirror of the mod's eight executable engine settings and their defaults
     *  ({@code EngineSettingsController.baseline()}); drift fails closed because the runner
     *  requires the client's accepted map to equal this plan map byte-for-byte. */
    public static Map<String, String> engineOptionDefaults() {
        Map<String, String> m = new TreeMap<>();
        m.put("normalMapEnabled", "true");
        m.put("specularMapEnabled", "true");
        m.put("renderResMul", "1.0");
        m.put("shadowResMul", "1.0");
        m.put("handDepthMul", "0.125");
        m.put("oldHandLight", "default");
        m.put("oldLighting", "default");
        m.put("antialiasingLevel", "0");
        return m;
    }

    public static PackFacts inspect(Path archive, GLCapabilityProfile profile) {
        FrontEndSession session = new FrontEndSession();
        Path dir = archive.getParent();
        PackDiscoveryResult discovery = session.discover(dir);
        String archiveName = archive.getFileName().toString();
        PackCandidate candidate = discovery.candidates().stream()
            .filter(c -> c.kind() != PackCandidateKind.OFF && c.kind() != PackCandidateKind.INTERNAL)
            .filter(c -> c.displayName().equals(archiveName)
                || (archiveName.endsWith(".zip")
                    && c.displayName().equals(archiveName.substring(0, archiveName.length() - 4))))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("front end did not discover " + archiveName
                + " under " + dir + "; candidates: " + discovery.candidates().stream()
                    .map(PackCandidate::displayName).toList()));
        if (candidate.status() != PackCandidateStatus.AVAILABLE) {
            throw new IllegalStateException("pack " + archiveName + " is " + candidate.status());
        }
        PackInspectionResult result = session.inspect(dir, candidate.id(), profile);
        if (!(result instanceof PackInspectionResult.Inspected inspected)) {
            throw new IllegalStateException("front end could not inspect " + archiveName + ": "
                + result.getClass().getSimpleName() + " " + session.diagnostics());
        }
        Map<String, String> defaults = new TreeMap<>();
        for (Map.Entry<String, OptionValue> e : inspected.configuration().options().catalog()
                .defaultState().values().entrySet()) {
            defaults.put(e.getKey(), render(e.getValue()));
        }
        SmoothingConstants s = inspected.configuration().resources().smoothing();
        double largest = Math.max(Math.max(s.wetnessHalfLifeTicks(), s.drynessHalfLifeTicks()),
            Math.max(s.eyeBrightnessHalfLifeTicks(), s.centerDepthHalfLifeTicks()));
        return new PackFacts(defaults, largest);
    }

    /** The owner encoding of an option value: {@code true|false} or the exact text. */
    public static String render(OptionValue value) {
        if (value instanceof BooleanOptionValue b) {
            return b.value() ? "true" : "false";
        }
        if (value instanceof TextOptionValue t) {
            return t.value();
        }
        throw new IllegalArgumentException("unknown option value " + value);
    }
}
