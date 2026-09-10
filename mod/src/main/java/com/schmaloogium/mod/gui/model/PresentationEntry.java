// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.List;
import java.util.Optional;

/**
 * Sealed presentation-entry sum (PHASE_12_DOC §2.2). Every record is a fully resolved,
 * deeply immutable snapshot: labels localized, values decorated, tooltips split. A view
 * makes no contract decision — it renders these and reports intents back.
 */
public sealed interface PresentationEntry {

    /** A switch option rendering its boolean value. */
    record SwitchOption(OptionId id, String label, boolean value, Tooltip tooltip,
                        boolean interactive) implements PresentationEntry {
    }

    /** A value option cycling over the published allowed list. */
    record ValueOption(OptionId id, String label, String rawValue, String displayValue,
                       List<String> allowedValues, int valueIndex, Tooltip tooltip,
                       boolean interactive) implements PresentationEntry {

        public ValueOption {
            allowedValues = List.copyOf(allowedValues);
        }
    }

    /**
     * A discrete slider over the ordered allowed list: exactly
     * {@code allowedValues.size()} stops, handle at {@code valueIndex}; no interpolation
     * and no numeric parsing ever ({@link com.schmaloogium.mod.gui.model.PresentationEntry}
     * §4.4.3, D-P12-5).
     */
    record SliderOption(OptionId id, String label, String rawValue, String displayValue,
                        List<String> allowedValues, int valueIndex, Tooltip tooltip,
                        boolean interactive) implements PresentationEntry {

        public SliderOption {
            allowedValues = List.copyOf(allowedValues);
        }
    }

    /**
     * The literal {@code <profile>} row: one grid slot; current is the display selection
     * (pending explicit when present, else inferred). No profiles in the pack renders
     * {@code applicable=false} with a prepared no-profiles label and availability.
     */
    record ProfileCycle(String label, Optional<com.schmaloogium.engine.config.ProfileName> current,
                        Tooltip tooltip, boolean applicable,
                        OptionActionAvailability availability) implements PresentationEntry {

        public ProfileCycle {
            current = current == null ? Optional.empty() : current;
            java.util.Objects.requireNonNull(availability, "availability");
        }
    }

    /**
     * A subscreen link. Unresolved targets (no declared screen of that name) keep their
     * grid cell and render disabled ({@code resolved=false}, D-P12-6), never dropped.
     */
    record SubScreenLink(ScreenId target, String label, Tooltip tooltip,
                         boolean resolved) implements PresentationEntry {
    }

    /** The {@code <empty>} layout slot: occupies a cell, never interactive. */
    record Blank() implements PresentationEntry {
    }
}
