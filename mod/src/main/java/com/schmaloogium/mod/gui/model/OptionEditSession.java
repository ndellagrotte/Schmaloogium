// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

/**
 * The edit-session contract (PHASE_12_DOC §2.2, §4.4/§4.5): pending changes over a
 * committed baseline, apply/discard/reset with the persistence timing of §4.7.2, and
 * presenter-owned availability. Confined to the client thread; implementations are
 * `.internal`.
 */
public interface OptionEditSession {

    OptionPresentationModel present(ScreenId screen);

    /** Toggles a switch option. */
    void toggle(OptionId id);

    /** Cycles a value or slider option; step is +1/-1 with floorMod wrap. */
    void cycle(OptionId id, int step);

    /** Selects an allowed-list index directly (slider stops, list picks). */
    void setValueIndex(OptionId id, int index);

    /** Advances the profile selection in declaration order, wrapping. */
    void cycleProfile();

    /** Commit-needed: an option delta or a changed explicit profile intent. */
    boolean isDirty();

    /** Differing option values, plus one iff the profile intent differs. */
    int pendingChangeCount();

    /** Accepts options and profile intent, then submits exactly one reload. */
    ApplyOutcome apply();

    /** Restores preview and pending selection from the baselines; writes nothing. */
    void discard();

    /** Resets the preview to pack defaults and clears the explicit selection. */
    ApplyOutcome resetToPackDefaults();
}
