// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import com.schmaloogium.engine.pack.PackCandidateId;

/**
 * The closed intent set a pack-selection view may report (PHASE_12_DOC §2.2). The
 * controller rechecks availability on every call, including stale callbacks from kept
 * widgets; a rejected engine intent returns {@code REJECTED} without any state change,
 * write or queued reload.
 */
public interface PackSelectionActions {

    /** Selects a row; always re-runs discovery first (D-P12-8). */
    void selectCandidate(PackCandidateId candidate);

    /** Explicit re-discovery. */
    void refresh();

    /** Opens the shaderpacks folder for the current selection. */
    void openPackFolder();

    /** Opens the options screen; only meaningful for a loadable non-Off row. */
    void openOptions();

    ApplyOutcome setEngineToggle(String key, boolean value);

    ApplyOutcome setEngineTriState(String key, TriStateValue value);

    ApplyOutcome setEngineChoice(String key, String rawValue);

    /** Closes the selection screen. */
    void close();
}
