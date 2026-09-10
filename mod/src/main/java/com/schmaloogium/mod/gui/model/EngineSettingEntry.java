// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.List;
import java.util.Objects;

import com.schmaloogium.engine.diag.EngineDiagnostic;

/**
 * The global settings entry sum (PHASE_12_DOC §2.2). {@code key} is the stable
 * optionsshaders.txt-equivalent key; lists are immutable; {@code rawValue} of a choice
 * is the exact decoded owner text, never reconstructed from the index.
 */
public sealed interface EngineSettingEntry {

    /** The stable canonical key. */
    String key();

    /** Presenter-owned behavior/ladder availability; false keeps the row inert. */
    boolean interactive();

    /** Exact {@code true}/{@code false} toggle (normalMapEnabled, specularMapEnabled). */
    record Toggle(String key, String label, boolean value, Tooltip tooltip,
                  boolean interactive) implements EngineSettingEntry {

        public Toggle {
            Objects.requireNonNull(key, "key");
            Objects.requireNonNull(label, "label");
            Objects.requireNonNull(tooltip, "tooltip");
        }
    }

    /**
     * Exact {@code default}/{@code true}/{@code false} tri-state (oldHandLight,
     * oldLighting); {@code DEFAULT} defers to the pack's App F.1 flag (D-P12-10).
     */
    record TriState(String key, String label, TriStateValue value, Tooltip tooltip,
                    boolean interactive) implements EngineSettingEntry {

        public TriState {
            Objects.requireNonNull(key, "key");
            Objects.requireNonNull(label, "label");
            Objects.requireNonNull(value, "value");
            Objects.requireNonNull(tooltip, "tooltip");
        }
    }

    /**
     * A choice over an owner-published ordered ladder (renderResMul, shadowResMul,
     * handDepthMul). {@code rawValue} is the exact decoded token;
     * {@code valueIndex} is its index in {@code allowedValues} or exactly {@code -1}
     * (including the empty-ladder case) — never rewritten on open.
     */
    record Choice(String key, String label, String rawValue, List<String> allowedValues,
                  int valueIndex, Tooltip tooltip, boolean interactive)
            implements EngineSettingEntry {

        public Choice {
            Objects.requireNonNull(key, "key");
            Objects.requireNonNull(label, "label");
            Objects.requireNonNull(rawValue, "rawValue");
            allowedValues = List.copyOf(allowedValues);
            Objects.requireNonNull(tooltip, "tooltip");
        }
    }
}
