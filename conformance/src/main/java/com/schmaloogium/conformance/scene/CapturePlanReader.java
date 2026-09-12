// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.scene;

import com.schmaloogium.conformance.wire.FlatDocument;
import com.schmaloogium.conformance.wire.Hashes;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The strict conformance-side reader of {@code schmaloogium.capture-plan/4}: exact schema
 * line, every required scalar present and typed, dense families with exactly their member
 * sets, no unknown key, closed enums, and the capture window/sample arithmetic of §4.3.4.
 * Re-rendering a parsed plan is byte-identical. The client's reader is a separate, dumber
 * one in {@code :mod}; this one is what authenticates a retained plan against
 * {@code run.planHash}.
 */
public final class CapturePlanReader {

    private CapturePlanReader() {
    }

    public static CapturePlan parse(String text) {
        FlatDocument doc = FlatDocument.parse(text, CapturePlan.SCHEMA_LINE);
        Set<String> consumed = new HashSet<>();
        for (Map.Entry<String, String> e : CapturePlan.SCALARS.entrySet()) {
            check(doc, e.getKey(), e.getValue());
            consumed.add(e.getKey());
        }
        for (Map.Entry<String, Map<String, String>> fam : CapturePlan.FAMILIES.entrySet()) {
            int count = doc.count(fam.getKey());
            for (int i = 0; i < count; i++) {
                for (Map.Entry<String, String> field : fam.getValue().entrySet()) {
                    String key = fam.getKey() + "." + i + "." + field.getKey();
                    check(doc, key, field.getValue());
                    consumed.add(key);
                }
            }
        }
        int captures = doc.count("captures");
        if (captures <= 0) {
            throw new IllegalArgumentException("captures.count must be positive");
        }
        for (int i = 0; i < captures; i++) {
            String p = "captures." + i + ".";
            String kind = doc.token(p + "kind");
            if (!CapturePlan.KINDS.contains(kind)) {
                throw new IllegalArgumentException(p + "kind must be SHOT|PATH: " + kind);
            }
            long samples = doc.integer(p + "samples.count");
            long start = doc.integer(p + "captureStartSample");
            long window = doc.integer(p + "captureSampleCount");
            if (samples <= 0 || start < 0 || window <= 0 || start + window > samples) {
                throw new IllegalArgumentException(p + " capture window outside the sample range");
            }
            if (kind.equals("PATH") && (samples < 2 || window < 2)) {
                throw new IllegalArgumentException(p + " PATH needs at least two samples in the window");
            }
            if (kind.equals("SHOT") && (start != 0 || window != samples)) {
                throw new IllegalArgumentException(p + " SHOT window must cover every sample");
            }
            if (doc.integer(p + "warmupFrames") < 0) {
                throw new IllegalArgumentException(p + "warmupFrames must be non-negative");
            }
            for (int s = 0; s < samples; s++) {
                for (Map.Entry<String, String> f : CapturePlan.SAMPLE_FIELDS.entrySet()) {
                    String key = p + "samples." + s + "." + f.getKey();
                    check(doc, key, f.getValue());
                    consumed.add(key);
                }
            }
        }
        if (!CapturePlan.ACQUISITION_MODES.contains(doc.token("pack.acquisitionMode"))) {
            throw new IllegalArgumentException("pack.acquisitionMode must be MODRINTH|MANUAL");
        }
        if (doc.text("pack.id").isEmpty() || doc.text("pack.version").isEmpty()
                || doc.text("pack.licence").isEmpty()) {
            throw new IllegalArgumentException("pack id, version and licence must be non-empty");
        }
        long ticksPerFrame = doc.integer("clock.ticksPerFrame");
        if (ticksPerFrame <= 0 || ticksPerFrame > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("clock.ticksPerFrame must be a positive int");
        }
        if (doc.integer("clock.frameTimeNanos") <= 0) {
            throw new IllegalArgumentException("clock.frameTimeNanos must be positive");
        }
        double partial = doc.decimal("clock.partialTicks");
        if (!(partial >= 0.0 && partial < 1.0)) {
            throw new IllegalArgumentException("clock.partialTicks must be in [0,1)");
        }
        if (doc.integer("world.prepTicks") < 0 || doc.integer("world.prepTicks") % ticksPerFrame != 0) {
            throw new IllegalArgumentException("world.prepTicks must be non-negative and divisible"
                + " by ticksPerFrame");
        }
        for (String key : doc.entries().keySet()) {
            if (!consumed.contains(key)) {
                throw new IllegalArgumentException("unknown key: " + key);
            }
        }
        CapturePlan plan = new CapturePlan(doc);
        plan.totalSteps();
        return plan;
    }

    private static void check(FlatDocument doc, String key, String type) {
        if (!doc.has(key)) {
            throw new IllegalArgumentException("missing required key: " + key);
        }
        try {
            switch (type) {
                case "T" -> doc.text(key);
                case "K" -> doc.token(key);
                case "B" -> doc.bool(key);
                case "I" -> doc.integer(key);
                case "D" -> doc.decimal(key);
                case "H64" -> requireHex(doc.raw(key), 64);
                case "H128" -> requireHex(doc.raw(key), 128);
                default -> throw new IllegalStateException(type);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("key " + key + ": " + e.getMessage(), e);
        }
    }

    private static void requireHex(String raw, int digits) {
        if (!Hashes.isHex(raw, digits)) {
            throw new IllegalArgumentException("expected exactly " + digits + " lowercase hex digits");
        }
    }
}
