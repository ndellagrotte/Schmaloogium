// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.capture;

import com.schmaloogium.conformance.capture.ManifestKeys.KeySpec;
import com.schmaloogium.conformance.capture.ManifestKeys.ScalarType;
import com.schmaloogium.conformance.capture.RunManifest.Value;
import com.schmaloogium.conformance.wire.CanonicalText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * Reads one canonical {@code schmaloogium.run-manifest/4} document into the model
 * (§4.5.2's shared grammar; §4.5.4's block rules). The outer parser splits each line
 * only at the first {@code  = }, decodes exactly one complete JSON string where the key
 * says TEXT (requiring canonical re-encoding), rejects duplicate, unknown-core,
 * non-dense, missing-required or conditional-inconsistent keys, and admits no other
 * major version — there is no {@code /1}–{@code /3} compatibility reader.
 */
public final class RunManifestReader {

    private RunManifestReader() {
    }

    public static RunManifest parse(Reader in) throws IOException {
        BufferedReader lines = new BufferedReader(in);
        String first = lines.readLine();
        if (!RunManifest.SCHEMA_LINE.equals(first)) {
            throw new IllegalArgumentException("first line must be exactly '"
                + RunManifest.SCHEMA_LINE + "' (no compatibility reader for other majors): "
                + first);
        }
        TreeMap<String, Value> entries = new TreeMap<>();
        String previousKey = null;
        String line;
        while ((line = lines.readLine()) != null) {
            if (line.isEmpty()) {
                continue;
            }
            if (line.endsWith("\r")) {
                throw new IllegalArgumentException("CR byte in manifest line " + line);
            }
            int split = line.indexOf(" = ");
            if (split <= 0) {
                throw new IllegalArgumentException("expected 'key = value': " + line);
            }
            String key = line.substring(0, split);
            String raw = line.substring(split + 3);
            CanonicalText.validateKey(key);
            if (previousKey != null && key.compareTo(previousKey) <= 0) {
                throw new IllegalArgumentException("keys must be strictly ascending: "
                    + previousKey + " then " + key);
            }
            previousKey = key;
            entries.put(key, decode(key, raw));
        }
        RunManifest manifest = new RunManifest(entries);
        validateStructure(manifest);
        return manifest;
    }

    public static RunManifest parse(String text) {
        try {
            return parse(new StringReader(text));
        } catch (IOException impossibleOnStringReader) {
            throw new IllegalStateException(impossibleOnStringReader);
        }
    }

    private static Value decode(String key, String raw) {
        if (key.startsWith("x.")) {
            // Extension keys are preserved and reported, never verdict-affecting; their
            // scalar grammar is still the shared one (a TEXT-looking payload must decode).
            return new Value.Token(raw);
        }
        KeySpec spec = ManifestKeys.scalar(key);
        String family = ManifestKeys.familyOf(key);
        ScalarType type;
        if (spec != null) {
            type = spec.type();
        } else if (ManifestKeys.isFamilyCount(key)) {
            type = ScalarType.INT; // <family>.count is the density scalar.
        } else if (family != null) {
            {
                // family.<index>.<field> — the field is everything after the index dot.
                int afterIndex = key.indexOf('.', family.length() + 1);
                String field = key.substring(afterIndex + 1);
                type = ManifestKeys.familyFieldType(family, field);
            }
        } else {
            throw new IllegalArgumentException("unknown core key: " + key);
        }
        try {
            return switch (type) {
                case TEXT -> {
                    String decoded = CanonicalText.decodeJson(raw);
                    CanonicalText.requireCanonicalJson(raw);
                    yield new Value.Text(decoded);
                }
                case BOOL -> new Value.Bool(CanonicalText.parseBoolean(raw));
                case INT -> new Value.Int(CanonicalText.parseInt(raw));
                case DECIMAL -> new Value.Dec(CanonicalText.parseDouble(raw));
                case TOKEN -> {
                    if (raw.isEmpty()) {
                        throw new IllegalArgumentException("empty token: " + key);
                    }
                    yield new Value.Token(raw);
                }
                case HEX64 -> new Value.Token(requireHex(key, raw, 64));
                case HEX128 -> new Value.Token(requireHex(key, raw, 128));
            };
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("key " + key + ": " + e.getMessage(), e);
        }
    }

    private static String requireHex(String key, String raw, int digits) {
        if (raw.length() != digits || raw.chars().anyMatch(c ->
                !((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f')))) {
            throw new IllegalArgumentException("expected exactly " + digits
                + " lowercase hex digits: " + key);
        }
        return raw;
    }

    // ------------------------------------------------------------------
    // Structural validation
    // ------------------------------------------------------------------

    private static void validateStructure(RunManifest manifest) {
        for (String key : ManifestKeys.scalarKeySet()) {
            if (ManifestKeys.scalar(key).required() && !manifest.has(key)) {
                throw new IllegalArgumentException("missing required key: " + key);
            }
        }
        checkEnums(manifest);
        checkConditionalPresence(manifest);
        checkPackScalars(manifest);
        checkDiagnosticsRows(manifest);
        for (String family : new String[] {"environment.mods", "environment.resourcePacks",
                "pack.options", "pack.engineOptions", "programs", "captures", "frames",
                "gl_errors", "diagnostics", "images", "timing.steps", "hooks.rows",
                "hooks.subreports"}) {
            requireDense(manifest, family);
        }
        checkTimingAvailability(manifest);
    }

    private static void checkEnums(RunManifest manifest) {
        requireEnum(manifest, "run.exitStatus", ManifestKeys.EXIT_STATUSES);
        requireEnum(manifest, "run.compatVerdict", ManifestKeys.COMPAT_VERDICTS);
        requireEnum(manifest, "pack.acquisitionMode", Set.of("MODRINTH", "MANUAL"));
        requireEnum(manifest, "timing.restoration", ManifestKeys.RESTORATIONS);
    }

    private static void requireEnum(RunManifest manifest, String key, Set<String> domain) {
        if (manifest.has(key) && !domain.contains(manifest.token(key))) {
            throw new IllegalArgumentException(key + " must be one of " + domain + ": "
                + manifest.token(key));
        }
    }

    private static void checkConditionalPresence(RunManifest manifest) {
        boolean glAvailable = manifest.bool("gl.available");
        boolean hasProfile = manifest.has("gl.profile_text");
        if (glAvailable && !hasProfile) {
            throw new IllegalArgumentException("gl.available=true requires gl.profile_text");
        }
        if (!glAvailable && hasProfile) {
            throw new IllegalArgumentException("gl.available=false forbids gl.profile_text");
        }
    }

    private static void checkPackScalars(RunManifest manifest) {
        if (manifest.text("pack.id").isEmpty()) {
            throw new IllegalArgumentException("pack.id must be non-empty");
        }
        if (manifest.text("pack.version").isEmpty()) {
            throw new IllegalArgumentException("pack.version must be non-empty");
        }
        if (manifest.text("pack.licence").isEmpty()) {
            throw new IllegalArgumentException("pack.licence must be non-empty");
        }
    }

    private static void checkDiagnosticsRows(RunManifest manifest) {
        for (RunManifest.Row row : manifest.family("diagnostics")) {
            requireEnum(row, "severity", ManifestKeys.SEVERITIES);
            requireEnum(row, "channel", ManifestKeys.CHANNELS);
            if (row.text("code").isEmpty()) {
                throw new IllegalArgumentException("diagnostics." + row.index() + ".code"
                    + " must be a non-empty JSON string");
            }
            long line = row.integer("line");
            if (line < 0) {
                throw new IllegalArgumentException("diagnostics." + row.index() + ".line"
                    + " must be non-negative");
            }
            if (line > 0 && row.text("file").isEmpty()) {
                throw new IllegalArgumentException("diagnostics." + row.index() + ".line"
                    + " positive requires a non-empty file");
            }
        }
    }

    private static void requireEnum(RunManifest.Row row, String field, Set<String> domain) {
        if (!domain.contains(row.token(field))) {
            throw new IllegalArgumentException("diagnostics." + row.index() + "." + field
                + " must be one of " + domain + ": " + row.token(field));
        }
    }

    private static void requireDense(RunManifest manifest, String family) {
        Set<Integer> seen = new HashSet<>();
        for (String key : manifest.entries().keySet()) {
            if (ManifestKeys.familyOf(key) != null && ManifestKeys.familyOf(key).equals(family)) {
                String rest = key.substring(family.length() + 1);
                seen.add(Integer.parseInt(rest.substring(0, rest.indexOf('.'))));
            }
        }
        Set<String> members = ManifestKeys.familyMembers(family);
        boolean enforceMembers = ManifestKeys.readerEnforcesMembers(family);
        int count = manifest.familyCount(family);
        if (seen.isEmpty()) {
            if (count != 0) {
                throw new IllegalArgumentException(family + ".count=" + count
                    + " with no rows");
            }
            return;
        }
        for (int i = 0; i < count; i++) {
            if (!seen.contains(i)) {
                throw new IllegalArgumentException("dense family " + family + " misses index " + i);
            }
        }
        if (seen.stream().anyMatch(i -> i >= count)) {
            throw new IllegalArgumentException("dense family " + family
                + " has rows beyond count=" + count);
        }
        if (!enforceMembers) {
            return;
        }
        for (int i = 0; i < count; i++) {
            Set<String> present = new HashSet<>();
            for (String key : manifest.entries().keySet()) {
                if (key.startsWith(family + "." + i + ".")) {
                    present.add(key.substring((family + "." + i + ".").length()));
                }
            }
            if (!present.equals(members)) {
                throw new IllegalArgumentException("row " + family + "." + i
                    + " member set mismatch; expected exactly " + members + ", got " + present);
            }
        }
    }

    private static void checkTimingAvailability(RunManifest manifest) {
        boolean available = manifest.bool("timing.available");
        boolean hasOrigin = manifest.has("timing.origin.checkpointId");
        if (available && !hasOrigin) {
            throw new IllegalArgumentException("timing.available=true requires timing.origin.*");
        }
        if (!available) {
            if (hasOrigin) {
                throw new IllegalArgumentException("timing.available=false forbids timing.origin.*");
            }
            if (manifest.bool("timing.complete")) {
                throw new IllegalArgumentException("timing.available=false requires complete=false");
            }
            if (manifest.integer("timing.steps.count") != 0) {
                throw new IllegalArgumentException("timing.available=false requires steps.count=0");
            }
        }
    }
}
