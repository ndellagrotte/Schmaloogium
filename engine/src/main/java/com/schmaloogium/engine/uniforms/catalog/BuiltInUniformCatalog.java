// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.catalog;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The complete built-in inventory (PHASE_6_DOC §4.4): all 56 Appendix D uniforms with
 * exact names, types, cadences, activation policies, producers and milestones. This is
 * the doc's Appendix D realization — the RESEARCH Appendix D authority — and the source
 * of the §4.13 fixed-expression input schema minus its conservative exclusion union. The
 * catalog deliberately contains no sampler-name/unit table (R7-10).
 */
public final class BuiltInUniformCatalog {

    /** The conservative §4.13 exclusion union: the whole D.4 table plus fogMode/fogColor. */
    public static final Set<String> EXPRESSION_EXCLUDED = Set.of(
            "entityColor", "entityId", "blockEntityId", "blendFunc", "instanceId",
            "fogMode", "fogColor");

    private static final List<BuiltInUniform> ALL = List.of(
            // ---- §4.4.1 held item and player ----
            row("heldItemId", BuiltInUniformType.INT, Cadence.SIGNAL, ActivationKind.SKIP_EQUAL,
                    "held items (Phase 9 values via Phase 7 hook)", Milestone.V0_3,
                    "updateHeldItems"),
            row("heldBlockLightValue", BuiltInUniformType.INT, Cadence.SIGNAL,
                    ActivationKind.SKIP_EQUAL,
                    "held items (Phase 9 values via Phase 7 hook)", Milestone.V0_3,
                    "updateHeldItems"),
            row("heldItemId2", BuiltInUniformType.INT, Cadence.SIGNAL, ActivationKind.SKIP_EQUAL,
                    "held items (Phase 9 values via Phase 7 hook)", Milestone.V0_3,
                    "updateHeldItems"),
            row("heldBlockLightValue2", BuiltInUniformType.INT, Cadence.SIGNAL,
                    ActivationKind.SKIP_EQUAL,
                    "held items (Phase 9 values via Phase 7 hook)", Milestone.V0_3,
                    "updateHeldItems"),
            row("wetness", BuiltInUniformType.FLOAT, Cadence.PER_FRAME, ActivationKind.SKIP_EQUAL,
                    "tick rain target + frame EMA (cadence engine)", Milestone.V0_1, ""),
            row("eyeAltitude", BuiltInUniformType.FLOAT, Cadence.PER_FRAME,
                    ActivationKind.SKIP_EQUAL, "frame provider", Milestone.V0_1, ""),
            row("eyeBrightness", BuiltInUniformType.IVEC2, Cadence.PER_FRAME,
                    ActivationKind.SKIP_EQUAL, "frame provider", Milestone.V0_1, ""),
            row("eyeBrightnessSmooth", BuiltInUniformType.IVEC2, Cadence.PER_FRAME,
                    ActivationKind.SKIP_EQUAL, "frame provider + vector EMA", Milestone.V0_1, ""),
            row("isEyeInWater", BuiltInUniformType.INT, Cadence.PER_FRAME,
                    ActivationKind.SKIP_EQUAL, "frame provider", Milestone.V0_1, ""),
            row("nightVision", BuiltInUniformType.FLOAT, Cadence.PER_FRAME,
                    ActivationKind.SKIP_EQUAL, "frame provider", Milestone.V0_1, ""),
            row("blindness", BuiltInUniformType.FLOAT, Cadence.PER_FRAME,
                    ActivationKind.SKIP_EQUAL, "frame provider", Milestone.V0_1, ""),
            row("screenBrightness", BuiltInUniformType.FLOAT, Cadence.PER_FRAME,
                    ActivationKind.SKIP_EQUAL, "frame provider", Milestone.V0_1, ""),
            row("hideGUI", BuiltInUniformType.INT, Cadence.PER_FRAME, ActivationKind.SKIP_EQUAL,
                    "frame provider", Milestone.V0_1, ""),
            // ---- §4.4.2 world, time, weather ----
            row("worldTime", BuiltInUniformType.INT, Cadence.PER_TICK, ActivationKind.SKIP_EQUAL,
                    "tick provider", Milestone.V0_1, ""),
            row("worldDay", BuiltInUniformType.INT, Cadence.PER_TICK, ActivationKind.SKIP_EQUAL,
                    "tick provider", Milestone.V0_1, ""),
            row("moonPhase", BuiltInUniformType.INT, Cadence.PER_TICK, ActivationKind.SKIP_EQUAL,
                    "tick provider", Milestone.V0_1, ""),
            row("frameCounter", BuiltInUniformType.INT, Cadence.PER_FRAME,
                    ActivationKind.SKIP_EQUAL, "cadence engine", Milestone.V0_1, ""),
            row("frameTime", BuiltInUniformType.FLOAT, Cadence.PER_FRAME,
                    ActivationKind.SKIP_EQUAL, "frame clock provider (FrameBeginInput)",
                    Milestone.V0_1, ""),
            row("frameTimeCounter", BuiltInUniformType.FLOAT, Cadence.PER_FRAME,
                    ActivationKind.SKIP_EQUAL, "cadence engine", Milestone.V0_1, ""),
            row("sunAngle", BuiltInUniformType.FLOAT, Cadence.PER_FRAME,
                    ActivationKind.SKIP_EQUAL, "frame provider", Milestone.V0_1, ""),
            row("shadowAngle", BuiltInUniformType.FLOAT, Cadence.PER_FRAME,
                    ActivationKind.SKIP_EQUAL, "frame provider; shadow policy at v0.2",
                    Milestone.V0_2, ""),
            row("rainStrength", BuiltInUniformType.FLOAT, Cadence.PER_TICK,
                    ActivationKind.SKIP_EQUAL, "tick provider", Milestone.V0_1, ""),
            row("fogMode", BuiltInUniformType.INT, Cadence.SIGNAL,
                    ActivationKind.IMMEDIATE_IF_ACTIVE, "fog signal + frame fallback",
                    Milestone.V0_1, "updateFog"),
            row("fogDensity", BuiltInUniformType.FLOAT, Cadence.SIGNAL,
                    ActivationKind.IMMEDIATE_IF_ACTIVE, "fog signal + frame fallback",
                    Milestone.V0_1, "updateFog"),
            row("fogColor", BuiltInUniformType.VEC3, Cadence.SIGNAL,
                    ActivationKind.IMMEDIATE_IF_ACTIVE, "fog signal + frame fallback",
                    Milestone.V0_1, "updateFog"),
            row("skyColor", BuiltInUniformType.VEC3, Cadence.PER_FRAME,
                    ActivationKind.SKIP_EQUAL, "frame provider", Milestone.V0_1, ""),
            // ---- §4.4.3 camera, matrices, screen ----
            row("aspectRatio", BuiltInUniformType.FLOAT, Cadence.PER_FRAME,
                    ActivationKind.SKIP_EQUAL, "frame-begin input", Milestone.V0_1, ""),
            row("viewWidth", BuiltInUniformType.FLOAT, Cadence.PER_FRAME,
                    ActivationKind.SKIP_EQUAL, "frame-begin input", Milestone.V0_1, ""),
            row("viewHeight", BuiltInUniformType.FLOAT, Cadence.PER_FRAME,
                    ActivationKind.SKIP_EQUAL, "frame-begin input", Milestone.V0_1, ""),
            row("near", BuiltInUniformType.FLOAT, Cadence.ONCE, ActivationKind.SKIP_EQUAL,
                    "once provider (fixed 0.05)", Milestone.V0_1, ""),
            row("far", BuiltInUniformType.FLOAT, Cadence.PER_FRAME, ActivationKind.SKIP_EQUAL,
                    "frame provider (render distance x 16)", Milestone.V0_1, ""),
            row("sunPosition", BuiltInUniformType.VEC3, Cadence.SIGNAL,
                    ActivationKind.IMMEDIATE_IF_ACTIVE, "celestial signal (Phase 8 values)",
                    Milestone.V0_2, "updateCelestial"),
            row("moonPosition", BuiltInUniformType.VEC3, Cadence.SIGNAL,
                    ActivationKind.IMMEDIATE_IF_ACTIVE, "celestial signal (Phase 8 values)",
                    Milestone.V0_2, "updateCelestial"),
            row("shadowLightPosition", BuiltInUniformType.VEC3, Cadence.SIGNAL,
                    ActivationKind.IMMEDIATE_IF_ACTIVE, "celestial/shadow signal (Phase 8 values)",
                    Milestone.V0_2, "updateCelestial"),
            row("upPosition", BuiltInUniformType.VEC3, Cadence.SIGNAL,
                    ActivationKind.IMMEDIATE_IF_ACTIVE, "celestial signal (Phase 8 values)",
                    Milestone.V0_2, "updateCelestial"),
            row("cameraPosition", BuiltInUniformType.VEC3, Cadence.PER_FRAME,
                    ActivationKind.SKIP_EQUAL, "frame provider", Milestone.V0_1, ""),
            row("previousCameraPosition", BuiltInUniformType.VEC3, Cadence.PER_FRAME,
                    ActivationKind.SKIP_EQUAL, "temporal store at frame begin", Milestone.V0_1, ""),
            row("gbufferModelView", BuiltInUniformType.MAT4, Cadence.SIGNAL,
                    ActivationKind.ALWAYS_UPLOAD, "gbuffer matrix capture (Phase 7 post-camera)",
                    Milestone.V0_1, "captureGbufferMatrices"),
            row("gbufferModelViewInverse", BuiltInUniformType.MAT4, Cadence.SIGNAL,
                    ActivationKind.ALWAYS_UPLOAD, "matrix engine", Milestone.V0_1,
                    "captureGbufferMatrices"),
            row("gbufferPreviousModelView", BuiltInUniformType.MAT4, Cadence.PER_FRAME,
                    ActivationKind.ALWAYS_UPLOAD, "temporal matrix store", Milestone.V0_1, ""),
            row("gbufferProjection", BuiltInUniformType.MAT4, Cadence.SIGNAL,
                    ActivationKind.ALWAYS_UPLOAD, "gbuffer matrix capture (Phase 7 post-camera)",
                    Milestone.V0_1, "captureGbufferMatrices"),
            row("gbufferProjectionInverse", BuiltInUniformType.MAT4, Cadence.SIGNAL,
                    ActivationKind.ALWAYS_UPLOAD, "matrix engine", Milestone.V0_1,
                    "captureGbufferMatrices"),
            row("gbufferPreviousProjection", BuiltInUniformType.MAT4, Cadence.PER_FRAME,
                    ActivationKind.ALWAYS_UPLOAD, "temporal matrix store", Milestone.V0_1, ""),
            row("shadowProjection", BuiltInUniformType.MAT4, Cadence.SIGNAL,
                    ActivationKind.ALWAYS_UPLOAD, "shadow camera signal (Phase 8)",
                    Milestone.V0_2, "updateShadowMatrices"),
            row("shadowProjectionInverse", BuiltInUniformType.MAT4, Cadence.SIGNAL,
                    ActivationKind.ALWAYS_UPLOAD, "matrix engine", Milestone.V0_2,
                    "updateShadowMatrices"),
            row("shadowModelView", BuiltInUniformType.MAT4, Cadence.SIGNAL,
                    ActivationKind.ALWAYS_UPLOAD, "shadow camera signal (Phase 8)",
                    Milestone.V0_2, "updateShadowMatrices"),
            row("shadowModelViewInverse", BuiltInUniformType.MAT4, Cadence.SIGNAL,
                    ActivationKind.ALWAYS_UPLOAD, "matrix engine", Milestone.V0_2,
                    "updateShadowMatrices"),
            row("centerDepthSmooth", BuiltInUniformType.FLOAT, Cadence.PER_FRAME,
                    ActivationKind.SKIP_EQUAL, "sync center-depth source at frame begin",
                    Milestone.V0_1, ""),
            row("atlasSize", BuiltInUniformType.IVEC2, Cadence.SIGNAL,
                    ActivationKind.IMMEDIATE_IF_ACTIVE,
                    "authenticated atlas bind (Phase 7 adapter + Phase 13 value)", Milestone.V0_5,
                    "updateAtlasSize"),
            row("terrainTextureSize", BuiltInUniformType.IVEC2, Cadence.ONCE,
                    ActivationKind.SKIP_EQUAL, "once neutral provider (documented unused)",
                    Milestone.V0_1, ""),
            row("terrainIconSize", BuiltInUniformType.INT, Cadence.ONCE,
                    ActivationKind.SKIP_EQUAL, "once neutral provider (documented unused)",
                    Milestone.V0_1, ""),
            // ---- §4.4.4 per-draw dynamics ----
            row("entityColor", BuiltInUniformType.VEC4, Cadence.SIGNAL,
                    ActivationKind.IMMEDIATE_IF_ACTIVE,
                    "entity color (Phase 7 hurt/flash push/pop)", Milestone.V0_1,
                    "updateEntityColor"),
            row("entityId", BuiltInUniformType.INT, Cadence.SIGNAL,
                    ActivationKind.IMMEDIATE_IF_ACTIVE,
                    "fallback/current entity (Phase 9 values via Phase 7 hook)", Milestone.V0_3,
                    "updateEntityId"),
            row("blockEntityId", BuiltInUniformType.INT, Cadence.SIGNAL,
                    ActivationKind.IMMEDIATE_IF_ACTIVE,
                    "current block entity (Phase 9 values via Phase 7 hook)", Milestone.V0_3,
                    "updateBlockEntityId"),
            row("blendFunc", BuiltInUniformType.IVEC4, Cadence.SIGNAL,
                    ActivationKind.IMMEDIATE_IF_ACTIVE,
                    "blend enable/factors (Phase 7 + P1 effective state + Phase 4 descriptor)",
                    Milestone.V0_1, "updateBlend"),
            row("instanceId", BuiltInUniformType.INT, Cadence.SIGNAL,
                    ActivationKind.IMMEDIATE_IF_ACTIVE,
                    "instance draw loop (Phase 7)", Milestone.V0_5, "updateInstanceId"));

    private static final Map<String, BuiltInUniform> BY_NAME = ALL.stream()
            .collect(Collectors.toUnmodifiableMap(BuiltInUniform::name, Function.identity()));

    private BuiltInUniformCatalog() {
    }

    /** Every row, in the canonical catalog order (group/declaration order of §4.4). */
    public static List<BuiltInUniform> all() {
        return ALL;
    }

    /** The row for one exact name, or empty when the name is not a built-in. */
    public static Optional<BuiltInUniform> byName(String exactName) {
        return Optional.ofNullable(BY_NAME.get(exactName));
    }

    /** Names admitted to the §4.13 fixed-expression input schema: everything except the
     *  conservative exclusion union. */
    public static Set<String> expressionPermittedNames() {
        return ALL.stream()
                .map(BuiltInUniform::name)
                .filter(name -> !EXPRESSION_EXCLUDED.contains(name))
                .collect(Collectors.toUnmodifiableSet());
    }

    private static BuiltInUniform row(String name, BuiltInUniformType type, Cadence cadence,
            ActivationKind activation, String producer, Milestone milestone, String signalEvent) {
        return new BuiltInUniform(name, type, cadence, activation, producer, milestone,
                signalEvent);
    }
}
