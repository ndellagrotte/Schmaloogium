// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.conformance.capture;

import com.schmaloogium.conformance.capture.RunManifest.Value;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The run-manifest key universe (§4.5.4 + D-P2-50): which scalar keys exist, their
 * types, which are required, and the exact member set of every dense family. Unknown
 * core keys reject; {@code x.<producer>.*} extension keys are preserved and reported
 * but never affect a verdict.
 *
 * <p>Producers for the {@code resources} and {@code hooks} row projections (P5/P7) and
 * the {@code images} block land with the live-capture leg: their keys are registered so
 * the grammar rejects misspellings, but their cross-record completeness is validated by
 * the owning stage, not here — a named deferral, not a relaxed rule.
 */
final class ManifestKeys {

    enum ScalarType {
        BOOL(Value.Bool.class), INT(Value.Int.class), DECIMAL(Value.Dec.class),
        TEXT(Value.Text.class), TOKEN(Value.Token.class), HEX64(Value.Token.class),
        HEX128(Value.Token.class);

        final Class<? extends Value> valueClass;

        ScalarType(Class<? extends Value> valueClass) {
            this.valueClass = valueClass;
        }
    }

    record KeySpec(ScalarType type, boolean required) {
    }

    static final Set<String> EXIT_STATUSES = Set.of("COMPLETE", "FAILED", "SKIPPED");
    static final Set<String> COMPAT_VERDICTS = Set.of("Continue", "Bail", "NOT_REACHED");
    static final Set<String> SEVERITIES = Set.of("INFO", "WARN", "ERROR", "FATAL");
    static final Set<String> CHANNELS = Set.of("CHAT", "SHADER_GUI", "LOG_ONLY");
    static final Set<String> RESTORATIONS = Set.of("NOT_REACHED", "PENDING", "RESTORED", "FAILED");
    static final Set<String> PHASES = Set.of("PREPARATION", "WARMUP", "SAMPLE");
    static final Set<String> CAPTURE_KINDS = Set.of("SHOT", "PATH");

    private static final Map<String, KeySpec> SCALARS = buildScalars();

    /** family prefix → exact member field set (rows must carry every member). */
    private static final Map<String, Set<String>> FAMILIES = buildFamilies();

    private static Map<String, KeySpec> buildScalars() {
        var m = new java.util.HashMap<String, KeySpec>();
        // run (§4.5.4)
        req(m, "run.id", ScalarType.TOKEN);
        req(m, "run.sceneId", ScalarType.TOKEN);
        req(m, "run.sceneHash", ScalarType.HEX64);
        req(m, "run.planHash", ScalarType.HEX64);
        req(m, "run.exitStatus", ScalarType.TOKEN);
        req(m, "run.failureReason", ScalarType.TEXT);
        req(m, "run.uncaughtException", ScalarType.TEXT);
        req(m, "run.compatVerdict", ScalarType.TOKEN);
        req(m, "run.shadersActiveThroughout", ScalarType.BOOL);
        req(m, "run.hangCeilingMillis", ScalarType.INT);
        req(m, "run.timedOut", ScalarType.BOOL);
        opt(m, "run.startedAt", ScalarType.TEXT);
        opt(m, "run.endedAt", ScalarType.TEXT);
        // front end
        req(m, "frontEnd.completed", ScalarType.BOOL);
        req(m, "frontEnd.packConfigurationProduced", ScalarType.BOOL);
        // environment
        req(m, "environment.os", ScalarType.TOKEN);
        req(m, "environment.jvm", ScalarType.TOKEN);
        req(m, "environment.minecraftVersion", ScalarType.TOKEN);
        req(m, "environment.cleanroomVersion", ScalarType.TOKEN);
        req(m, "environment.worldSha256", ScalarType.HEX64);
        req(m, "environment.modSetSha256", ScalarType.HEX64);
        req(m, "environment.subjectModId", ScalarType.TOKEN);
        req(m, "environment.subjectJarSha256", ScalarType.HEX64);
        req(m, "environment.externalModSetSha256", ScalarType.HEX64);
        req(m, "environment.worldGenerationSha256", ScalarType.HEX64);
        // clock (§5.1.1 semantics; validation there)
        req(m, "clock.frameTimeNanos", ScalarType.INT);
        req(m, "clock.ticksPerFrame", ScalarType.INT);
        req(m, "clock.partialTicks", ScalarType.DECIMAL);
        // gl
        req(m, "gl.available", ScalarType.BOOL);
        opt(m, "gl.profile_text", ScalarType.TEXT);
        // pack (all JSON strings; content rules at decode)
        req(m, "pack.id", ScalarType.TEXT);
        req(m, "pack.version", ScalarType.TEXT);
        req(m, "pack.acquisitionMode", ScalarType.TOKEN);
        req(m, "pack.archiveSha512", ScalarType.HEX128);
        req(m, "pack.licence", ScalarType.TEXT);
        req(m, "pack.optionStateSha256", ScalarType.HEX64);
        // availability
        req(m, "resources.available", ScalarType.BOOL);
        req(m, "hooks.available", ScalarType.BOOL);
        // resources (§4.5.4 + P5 §4.1.1): conditional on resources.available, checked by the reader
        opt(m, "resources.evidence_stage", ScalarType.TOKEN);
        opt(m, "resources.depthTextures.count", ScalarType.INT);
        opt(m, "resources.shadow.depthTextures", ScalarType.INT);
        opt(m, "resources.shadow.colorTextures", ScalarType.INT);
        opt(m, "resources.shadow.resolution", ScalarType.INT);
        opt(m, "resources.centerDepthSmooth.enabled", ScalarType.BOOL);
        opt(m, "resources.noise.resolution", ScalarType.INT);
        opt(m, "resources.capabilityGate", ScalarType.TOKEN);
        // timing (D-P2-50 core)
        req(m, "timing.available", ScalarType.BOOL);
        req(m, "timing.complete", ScalarType.BOOL);
        req(m, "timing.failureReason", ScalarType.TEXT);
        req(m, "timing.restoration", ScalarType.TOKEN);
        req(m, "timing.identitiesUnchanged", ScalarType.BOOL);
        req(m, "timing.steps.count", ScalarType.INT);
        opt(m, "timing.origin.checkpointId", ScalarType.TEXT);
        opt(m, "timing.origin.clockStep", ScalarType.INT);
        opt(m, "timing.origin.worldTick", ScalarType.INT);
        opt(m, "timing.origin.logicalTick", ScalarType.INT);
        opt(m, "timing.origin.animationTick", ScalarType.INT);
        opt(m, "timing.origin.clientTicks", ScalarType.INT);
        opt(m, "timing.origin.serverTicks", ScalarType.INT);
        opt(m, "timing.origin.acceptedFrames", ScalarType.INT);
        opt(m, "timing.origin.finalizedFrames", ScalarType.INT);
        opt(m, "timing.origin.frameCounter", ScalarType.INT);
        opt(m, "timing.origin.frameTimeCounter", ScalarType.DECIMAL);
        opt(m, "timing.origin.quiescent", ScalarType.BOOL);
        opt(m, "timing.origin.freshRuntime", ScalarType.BOOL);
        opt(m, "timing.origin.frameTimingAbsent", ScalarType.BOOL);
        return Map.copyOf(m);
    }

    private static void req(Map<String, KeySpec> m, String key, ScalarType type) {
        m.put(key, new KeySpec(type, true));
    }

    private static void opt(Map<String, KeySpec> m, String key, ScalarType type) {
        m.put(key, new KeySpec(type, false));
    }

    private static Map<String, Set<String>> buildFamilies() {
        var m = new java.util.HashMap<String, Set<String>>();
        m.put("environment.mods", Set.of("id", "sha256"));
        m.put("environment.resourcePacks", Set.of("id", "sha256"));
        m.put("pack.options", Set.of("name", "value"));
        m.put("pack.engineOptions", Set.of("name", "value"));
        m.put("programs", Set.of("slot", "status", "from", "sourcePresent", "ownBuild", "driverLog"));
        m.put("captures", Set.of("kind", "id", "plannedWarmupFrames", "actualWarmupFrames",
            "plannedSamples", "actualSamples", "captureStartSample", "captureSampleCount"));
        m.put("frames", Set.of("captureKind", "captureId", "sampleOrdinal", "captured",
            "plannedCurrent.posX", "plannedCurrent.posY", "plannedCurrent.posZ",
            "plannedCurrent.yaw", "plannedCurrent.pitch",
            "plannedPrevious.posX", "plannedPrevious.posY", "plannedPrevious.posZ",
            "plannedPrevious.yaw", "plannedPrevious.pitch",
            "actualCurrent.posX", "actualCurrent.posY", "actualCurrent.posZ",
            "actualCurrent.yaw", "actualCurrent.pitch",
            "actualPrevious.posX", "actualPrevious.posY", "actualPrevious.posZ",
            "actualPrevious.yaw", "actualPrevious.pitch",
            "worldTick", "partialTicks", "frameCounter", "logicalTick", "animationTick",
            "smoothingTimeTicks", "frameTimeSeconds", "frameTimeCounter", "clockStep",
            "entityCount", "durationMillis"));
        m.put("gl_errors", Set.of("op", "subject", "kind", "detail", "attributed"));
        m.put("diagnostics", Set.of("code", "severity", "channel", "file", "line"));
        m.put("images", Set.of("captureKind", "captureId", "sampleOrdinal", "path",
            "width", "height", "pixelSha256"));
        m.put("timing.steps", Set.of("phase", "captureIndex", "ordinal", "validation",
            "registryGeneration", "frameId", "worldEpoch", "logicalTick",
            "smoothingTimeTicks", "frameTimeSeconds", "frameCounter", "frameTimeCounter",
            "worldTick", "animationTick", "partialTicks", "clockStep", "clientTicks",
            "serverTicks", "acceptedFrames", "finalizedFrames"));
        // resources families (§4.5.4): colour rows vary by evidence stage / clear policy, so
        // their member set is checked by the reader's variant rule, not here.
        m.put("resources.colorBuffers", Set.of("requested_format", "clear", "clear_policy",
            "clear_color_r", "clear_color_g", "clear_color_b", "clear_color_a", "allocated_format",
            "allocation_origin"));
        m.put("resources.shadow.depth", Set.of("hardwareFiltering", "mipmap", "nearest"));
        m.put("resources.shadow.color", Set.of("hardwareFiltering", "mipmap", "nearest"));
        m.put("resources.vertexAttributes", Set.of("program", "name"));
        m.put("resources.instances", Set.of("program", "count"));
        m.put("resources.capabilityShortfalls", Set.of("limit", "required", "available"));
        // Deferred producers: keys known, cross-record completeness owned by their stage.
        m.put("hooks.rows", Set.of("catalogId", "target", "expectedCount", "actualCount",
            "classes.count", "fallback"));
        m.put("hooks.subreports", Set.of("ownerPhase", "canonicalFingerprint", "featureEnabled",
            "rows.count"));
        return Map.copyOf(m);
    }

    /** Row-field types for family members (grammar level; completeness is per-family). */
    static ScalarType familyFieldType(String family, String field) {
        if (family.equals("hooks.rows") && field.startsWith("classes.") && !field.equals("classes.count")) {
            return ScalarType.TOKEN; // dense CORE|FEATURE|OBSERVER|DEFERRED members
        }
        if (family.startsWith("resources.")) {
            return switch (field) {
                case "requested_format", "allocated_format", "program", "name" -> ScalarType.TEXT;
                case "clear", "hardwareFiltering", "mipmap", "nearest" -> ScalarType.BOOL;
                case "clear_policy", "allocation_origin", "limit" -> ScalarType.TOKEN;
                case "clear_color_r", "clear_color_g", "clear_color_b", "clear_color_a" -> ScalarType.DECIMAL;
                default -> ScalarType.INT;
            };
        }
        return switch (field) {
            case "id", "name", "value", "from", "driverLog", "captureId", "subject",
                "detail", "code", "file", "path" -> ScalarType.TEXT;
            case "slot", "status", "kind", "op", "phase", "validation", "captureKind",
                "severity", "channel", "fallback", "disposition", "target" -> ScalarType.TOKEN;
            case "sourcePresent", "captured", "attributed", "featureEnabled" ->
                ScalarType.BOOL;
            case "ownBuild" -> ScalarType.TOKEN; // NOT_APPLICABLE|NO_SOURCE|DISABLED|SUCCEEDED|FAILED
            case "requested_format", "clear", "clear_policy", "allocated_format",
                "allocation_origin", "catalogId", "ownerPhase", "canonicalFingerprint" ->
                ScalarType.TEXT;
            case "plannedCurrent.posX", "plannedCurrent.posY", "plannedCurrent.posZ",
                "plannedCurrent.yaw", "plannedCurrent.pitch",
                "plannedPrevious.posX", "plannedPrevious.posY", "plannedPrevious.posZ",
                "plannedPrevious.yaw", "plannedPrevious.pitch",
                "actualCurrent.posX", "actualCurrent.posY", "actualCurrent.posZ",
                "actualCurrent.yaw", "actualCurrent.pitch",
                "actualPrevious.posX", "actualPrevious.posY", "actualPrevious.posZ",
                "actualPrevious.yaw", "actualPrevious.pitch" -> ScalarType.DECIMAL;
            default -> decimalOrInt(family, field);
        };
    }

    private static ScalarType decimalOrInt(String family, String field) {
        return switch (field) {
            case "partialTicks", "frameTimeSeconds", "frameTimeCounter", "smoothingTimeTicks" -> ScalarType.DECIMAL;
            case "sha256", "pixelSha256" -> ScalarType.HEX64;
            default -> ScalarType.INT;
        };
    }

    static KeySpec scalar(String key) {
        return SCALARS.get(key);
    }

    /** True when {@code key} is a {@code <family>.count} density scalar. */
    static boolean isFamilyCount(String key) {
        int dot = key.lastIndexOf('.');
        return dot > 0 && key.endsWith(".count") && FAMILIES.containsKey(key.substring(0, dot));
    }

    /** Returns the family prefix when {@code key} is a dense row field, else null. */
    static String familyOf(String key) {
        for (String family : FAMILIES.keySet()) {
            if (key.startsWith(family + ".") && !key.equals(family + ".count")) {
                String rest = key.substring(family.length() + 1);
                int dot = rest.indexOf('.');
                if (dot > 0 && rest.substring(0, dot).chars().allMatch(c -> c >= '0' && c <= '9')) {
                    return family;
                }
            }
        }
        return null;
    }

    static Set<String> familyMembers(String family) {
        return FAMILIES.get(family);
    }

    /** Families whose member completeness is enforced by the reader. */
    static boolean readerEnforcesMembers(String family) {
        return !family.equals("hooks.rows") && !family.equals("hooks.subreports")
            && !family.equals("resources.colorBuffers");
    }

    static final Set<String> EVIDENCE_STAGES = Set.of("PLANNED", "REALIZED");
    static final Set<String> CLEAR_POLICIES = Set.of("FOG_RGB_ALPHA_ONE", "CONSTANT");
    static final Set<String> ALLOCATION_ORIGINS = Set.of("REQUESTED", "RGBA_FALLBACK");
    static final Set<String> CAPABILITY_GATES = Set.of("OK", "SHORTFALL");
    static final Set<String> CAPABILITY_LIMITS = Set.of("maxDrawBuffers", "maxColorAttachments",
        "maxTextureImageUnits");
    static final Set<String> RESOURCE_FAMILIES = Set.of("resources.colorBuffers", "resources.shadow.depth",
        "resources.shadow.color", "resources.vertexAttributes", "resources.instances",
        "resources.capabilityShortfalls");

    static Set<String> scalarKeySet() {
        return SCALARS.keySet();
    }

    private ManifestKeys() {
    }
}
