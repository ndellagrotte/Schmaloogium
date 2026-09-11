// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.schmaloogium.engine.config.EngineOptionData;

/**
 * The global engine-settings domain (PHASE_12_DOC §4.6.2/§4.6.3): the seven canonical
 * keys over the {@code optionsshaders.txt} global file, exact-value intents with
 * presenter rechecks, write-through through the global codec on every accepted change,
 * and the durable {@code shaderPack} selection riding the same file (D-P12-22). The
 * reserved eighth key {@code antialiasingLevel} has no GUI entry and no intent
 * (D-P12-35); unknown-safe keys survive every round-trip untouched.
 */
public final class EngineSettingsController {

    /** Read/write port over the global file (the global codec, mod-side). */
    public interface GlobalsIo {

        EngineOptionData read();

        boolean writeCommitted(EngineOptionData values);
    }

    /** Owner-published behavior/ladder gates; the presenter owns interactivity. */
    public interface Gates {

        /** True when the setting's behavior owner has shipped and a ladder exists. */
        boolean interactive(String key);

        /** The ordered UI ladder; empty when the owner has not published choices. */
        List<String> ladder(String key);
    }

    /** The v0.4 gate set: every control inert, decoded values preserved verbatim. */
    public static Gates inertGates() {
        return new Gates() {
            @Override
            public boolean interactive(String key) {
                return false;
            }

            @Override
            public List<String> ladder(String key) {
                return List.of();
            }
        };
    }

    /** The canonical absent-file baseline in Phase-3 known-key order. */
    public static EngineOptionData baseline() {
        return new EngineOptionData(Map.of(
                "normalMapEnabled", "true",
                "specularMapEnabled", "true",
                "renderResMul", "1.0",
                "shadowResMul", "1.0",
                "handDepthMul", "0.125",
                "oldHandLight", "default",
                "oldLighting", "default",
                "antialiasingLevel", "0"));
    }

    private static final String SHADER_PACK_KEY = "shaderPack";
    private static final List<String> TOGGLE_KEYS =
            List.of("normalMapEnabled", "specularMapEnabled");
    private static final List<String> TRI_STATE_KEYS =
            List.of("oldHandLight", "oldLighting");
    private static final List<String> CHOICE_KEYS =
            List.of("renderResMul", "shadowResMul", "handDepthMul");
    private final GlobalsIo io;
    private final Gates gates;
    private final GuiText text;
    private EngineOptionData committed;
    private final Map<String, String> pendingOverlay = new LinkedHashMap<>();
    private final com.schmaloogium.engine.diag.DiagnosticReporter diagnostics;

    public EngineSettingsController(GlobalsIo io, Gates gates, GuiText text,
                                    com.schmaloogium.engine.diag.DiagnosticReporter diagnostics) {
        this.io = Objects.requireNonNull(io, "io");
        this.gates = Objects.requireNonNull(gates, "gates");
        this.text = Objects.requireNonNull(text, "text");
        this.committed = io.read();
        this.diagnostics = diagnostics;
    }

    // ------------------------------------------------------------------- view model

    /** All seven entries, always, in canonical table order (D-P12-32). */
    public EngineSettingsModel model() {
        List<EngineSettingEntry> entries = new ArrayList<>(7);
        entries.add(toggleEntry("normalMapEnabled"));
        entries.add(toggleEntry("specularMapEnabled"));
        entries.add(choiceEntry("renderResMul"));
        entries.add(choiceEntry("shadowResMul"));
        entries.add(choiceEntry("handDepthMul"));
        entries.add(triStateEntry("oldHandLight"));
        entries.add(triStateEntry("oldLighting"));
        return new EngineSettingsModel(entries);
    }

    private String label(String key) {
        return text.gui("schmaloogium.gui.setting." + key);
    }

    private EngineSettingEntry toggleEntry(String key) {
        return new EngineSettingEntry.Toggle(key, label(key),
                current(key).equals("true"), Tooltip.EMPTY, gates.interactive(key));
    }

    private EngineSettingEntry choiceEntry(String key) {
        List<String> ladder = List.copyOf(gates.ladder(key));
        String raw = current(key);
        return new EngineSettingEntry.Choice(key, label(key), raw, ladder,
                ladder.indexOf(raw), Tooltip.EMPTY, gates.interactive(key) && !ladder.isEmpty());
    }

    private EngineSettingEntry triStateEntry(String key) {
        return new EngineSettingEntry.TriState(key, label(key),
                TriStateValue.fromWire(current(key)), Tooltip.EMPTY, gates.interactive(key));
    }

    private String current(String key) {
        String pending = pendingOverlay.get(key);
        if (pending != null) {
            return pending;
        }
        String committedValue = committed.values().get(key);
        if (committedValue != null) {
            return committedValue;
        }
        return key.equals(SHADER_PACK_KEY) ? "off" : baselineValue(key);
    }


    private String baselineValue(String key) {
        return Optional.ofNullable(baseline().values().get(key))
                .orElseThrow(() -> new IllegalStateException("no baseline for " + key));
    }

    // --------------------------------------------------------------------- intents

    /** Exact-value toggle intent; only the two toggle keys are legal. */
    public ApplyOutcome setEngineToggle(String key, boolean value) {
        if (!TOGGLE_KEYS.contains(key) || !gates.interactive(key)) {
            return rejected();
        }
        String token = value ? "true" : "false";
        if (current(key).equals(token) && pendingOverlay.isEmpty()) {
            return ApplyOutcome.unchanged();
        }
        return accept(key, token);
    }

    /** Exact-value tri-state intent; only the two old-light keys are legal. */
    public ApplyOutcome setEngineTriState(String key, TriStateValue value) {
        if (!TRI_STATE_KEYS.contains(key) || !gates.interactive(key)) {
            return rejected();
        }
        if (current(key).equals(value.wire()) && pendingOverlay.isEmpty()) {
            return ApplyOutcome.unchanged();
        }
        return accept(key, value.wire());
    }

    /** Exact-token choice intent; only the three multipliers, token in the ladder. */
    public ApplyOutcome setEngineChoice(String key, String rawValue) {
        if (!CHOICE_KEYS.contains(key)) {
            return rejected();
        }
        List<String> ladder = gates.ladder(key);
        if (!gates.interactive(key) || !ladder.contains(rawValue)) {
            return rejected();
        }
        if (current(key).equals(rawValue) && pendingOverlay.isEmpty()) {
            return ApplyOutcome.unchanged();
        }
        return accept(key, rawValue);
    }

    // ------------------------------------------------------- durable pack selection

    /** The committed engine option values (the composition root's load input). */
    public EngineOptionData committed() {
        return committed;
    }

    /** The current durable selection string; absent file reads as {@code off}. */
    public String shaderPack() {
        return current(SHADER_PACK_KEY);
    }

    /** Persists a durable selection ({@code off} | {@code (internal)} | reference). */
    public boolean setShaderPack(String durableValue) {
        if (!durableValue.equals("off") && !durableValue.equals("(internal)")
                && !durableValue.startsWith("d:") && !durableValue.startsWith("a:")) {
            return false;
        }
        return acceptRaw(SHADER_PACK_KEY, durableValue);
    }

    // ------------------------------------------------------------------- write path

    /** Global pending overlay → validation → all-entry write-through (§4.6.2). */
    private ApplyOutcome accept(String key, String token) {
        Map<String, String> merged = new LinkedHashMap<>(committed.values());
        merged.putAll(pendingOverlay);
        merged.put(key, token);
        if (!io.writeCommitted(new EngineOptionData(merged))) {
            // Failed write: keep the pending value for the same-intent retry.
            pendingOverlay.put(key, token);
            return new ApplyOutcome(OptionApplyStatus.FAILED, Optional.empty(), List.of());
        }
        pendingOverlay.clear();
        committed = new EngineOptionData(merged);
        return persisted(triggerFor(key));
    }

    private boolean acceptRaw(String key, String token) {
        Map<String, String> merged = new LinkedHashMap<>(committed.values());
        merged.put(key, token);
        if (!io.writeCommitted(new EngineOptionData(merged))) {
            return false;
        }
        committed = new EngineOptionData(merged);
        return true;
    }

    private ReloadRequest triggerFor(String key) {
        if (TOGGLE_KEYS.contains(key)) {
            return ReloadTrigger.ENGINE_NORMAL_OR_SPECULAR_MAP.request();
        }
        if (key.equals("renderResMul") || key.equals("shadowResMul")) {
            return ReloadTrigger.ENGINE_RENDER_OR_SHADOW_RES.request();
        }
        if (key.equals("handDepthMul")) {
            return ReloadTrigger.ENGINE_HAND_DEPTH.request();
        }
        if (key.equals("oldHandLight")) {
            return ReloadTrigger.ENGINE_OLD_HAND_LIGHT.request();
        }
        if (key.equals("oldLighting")) {
            return ReloadTrigger.ENGINE_OLD_LIGHTING.request();
        }
        return ReloadRequest.none(ReloadCause.ENGINE_SETTING);
    }

    private ApplyOutcome persisted(ReloadRequest request) {
        ReloadCoordinator.submitOrReportInert(request, diagnostics);
        return new ApplyOutcome(OptionApplyStatus.PERSISTED, Optional.of(request), List.of());
    }

    private ApplyOutcome rejected() {
        return new ApplyOutcome(OptionApplyStatus.REJECTED, Optional.empty(), List.of());
    }
}
