// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.scene;

import com.schmaloogium.conformance.wire.SectionedText;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A named capture clock profile (PHASE_2_DOC §5.1.1): the runner owns it outside the scene
 * grammar, resolves it before plan construction and hashes its explicit values in the plan.
 * Committed in {@code conformance/fixtures/clocks.profile} as {@code [clock <name>]} blocks
 * with exactly {@code frameTimeNanos}, {@code ticksPerFrame} and {@code partialTicks}; no
 * parser supplies defaults.
 */
public record ClockProfile(String name, long frameTimeNanos, int ticksPerFrame, double partialTicks) {

    public ClockProfile {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("clock profile needs a name");
        }
        if (frameTimeNanos <= 0) {
            throw new IllegalArgumentException("frameTimeNanos must be positive");
        }
        if (ticksPerFrame <= 0) {
            throw new IllegalArgumentException("ticksPerFrame must be positive");
        }
        if (!(partialTicks >= 0.0 && partialTicks < 1.0)) {
            throw new IllegalArgumentException("partialTicks must be in [0,1)");
        }
    }

    /** The elapsed seconds the engine receives per accepted frame. */
    public float frameTimeSeconds() {
        double seconds = frameTimeNanos / 1_000_000_000.0;
        float f = (float) seconds;
        if (!(f > 0.0f) || !Float.isFinite(f)) {
            throw new IllegalArgumentException("unrepresentable frame time: " + frameTimeNanos);
        }
        return f;
    }

    public static Map<String, ClockProfile> parseFile(String text) {
        Map<String, ClockProfile> out = new LinkedHashMap<>();
        for (SectionedText.Section s : SectionedText.parse(text)) {
            if (!s.kind().equals("clock") || s.header().size() != 2) {
                throw new IllegalArgumentException("line " + s.line() + ": expected [clock <name>]");
            }
            Map<String, String> e = s.entries();
            if (e.size() != 3 || !e.containsKey("frameTimeNanos") || !e.containsKey("ticksPerFrame")
                    || !e.containsKey("partialTicks")) {
                throw new IllegalArgumentException("[clock " + s.name() + "] needs exactly"
                    + " frameTimeNanos, ticksPerFrame, partialTicks");
            }
            ClockProfile profile;
            try {
                profile = new ClockProfile(s.name(), Long.parseLong(e.get("frameTimeNanos")),
                    Integer.parseInt(e.get("ticksPerFrame")), Double.parseDouble(e.get("partialTicks")));
            } catch (NumberFormatException bad) {
                throw new IllegalArgumentException("[clock " + s.name() + "]: " + bad.getMessage());
            }
            if (out.putIfAbsent(s.name(), profile) != null) {
                throw new IllegalArgumentException("duplicate clock profile " + s.name());
            }
        }
        return out;
    }
}
