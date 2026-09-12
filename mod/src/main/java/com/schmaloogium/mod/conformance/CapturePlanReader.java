// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.conformance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The deliberately dumb client-side reader of {@code schmaloogium.capture-plan/4}
 * (PHASE_2_DOC §4.5.2): the schema line must match exactly, every line splits at the first
 * {@code " = "}, keys must be strictly ascending and unique, there are no defaults, and a
 * missing key aborts at the first typed read. The rich parser, validator and default
 * resolution stay on the {@code :conformance} side.
 */
public final class CapturePlanReader {

    public static final String SCHEMA_LINE = "schema = schmaloogium.capture-plan/4";

    /** One dense sample pose. */
    public record Pose(double x, double y, double z, double yaw, double pitch) {
    }

    /** One capture block with its dense samples. */
    public record Capture(String kind, String id, String heldMain, String heldOff, int warmupFrames,
            List<Pose> samples, int captureStartSample, int captureSampleCount) {
    }

    private final SortedMap<String, String> entries;
    private final String text;

    private CapturePlanReader(SortedMap<String, String> entries, String text) {
        this.entries = Collections.unmodifiableSortedMap(entries);
        this.text = text;
    }

    public static CapturePlanReader parse(String text) {
        String[] lines = text.split("\n", -1);
        if (lines.length == 0 || !SCHEMA_LINE.equals(lines[0])) {
            throw new IllegalArgumentException("plan schema line must be exactly '" + SCHEMA_LINE + "'");
        }
        if (!text.endsWith("\n")) {
            throw new IllegalArgumentException("plan must end with a newline");
        }
        TreeMap<String, String> entries = new TreeMap<>();
        String previous = null;
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if (line.isEmpty()) {
                continue;
            }
            int split = line.indexOf(" = ");
            if (split <= 0) {
                throw new IllegalArgumentException("plan line " + (i + 1) + " is not 'key = value'");
            }
            String key = line.substring(0, split);
            if (previous != null && key.compareTo(previous) <= 0) {
                throw new IllegalArgumentException("plan keys must be strictly ascending at " + key);
            }
            previous = key;
            entries.put(key, line.substring(split + 3));
        }
        return new CapturePlanReader(entries, text);
    }

    /** The exact plan text (for the run's plan hash). */
    public String text() {
        return text;
    }

    public String planHash() {
        return TreeHash.sha256Hex(text.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    public SortedMap<String, String> entries() {
        return entries;
    }

    public String raw(String key) {
        String v = entries.get(key);
        if (v == null) {
            throw new IllegalArgumentException("plan lacks required key " + key);
        }
        return v;
    }

    public String text(String key) {
        return CanonicalScalars.decodeJson(raw(key));
    }

    public String token(String key) {
        String v = raw(key);
        if (v.isEmpty()) {
            throw new IllegalArgumentException("plan key " + key + " is an empty token");
        }
        return v;
    }

    public boolean bool(String key) {
        return CanonicalScalars.parseBoolean(raw(key));
    }

    public long integer(String key) {
        return CanonicalScalars.parseInt(raw(key));
    }

    public double decimal(String key) {
        return CanonicalScalars.parseDouble(raw(key));
    }

    public int count(String family) {
        return Math.toIntExact(integer(family + ".count"));
    }

    /** A name/value family as an ordered map (JSON-string fields). */
    public Map<String, String> namedMap(String family) {
        Map<String, String> out = new TreeMap<>();
        int n = count(family);
        for (int i = 0; i < n; i++) {
            out.put(text(family + "." + i + ".name"), text(family + "." + i + ".value"));
        }
        return out;
    }

    public List<Capture> captures() {
        int n = count("captures");
        List<Capture> out = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            String p = "captures." + i + ".";
            int samples = count(p + "samples");
            List<Pose> poses = new ArrayList<>(samples);
            for (int s = 0; s < samples; s++) {
                String sp = p + "samples." + s + ".";
                poses.add(new Pose(decimal(sp + "pos.x"), decimal(sp + "pos.y"), decimal(sp + "pos.z"),
                        decimal(sp + "look.yaw"), decimal(sp + "look.pitch")));
            }
            out.add(new Capture(token(p + "kind"), text(p + "id"), text(p + "heldMain"), text(p + "heldOff"),
                    (int) integer(p + "warmupFrames"), poses, (int) integer(p + "captureStartSample"),
                    (int) integer(p + "captureSampleCount")));
        }
        return out;
    }
}
