// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.Objects;
import java.util.Optional;

import com.schmaloogium.engine.config.ProfileName;

/**
 * The pending explicit-profile-selection summary (D-P12-37): present iff the pending
 * explicit selection differs from the committed one. {@code selection} is the exact
 * pending named identity — empty means a cleared (explicit none) intent, never Custom.
 * {@code displayText} is prepared by the presenter in the model's frozen locale; views
 * render it verbatim and never reconstruct it.
 */
public record PendingProfileSummary(Optional<ProfileName> selection, String displayText) {

    public PendingProfileSummary {
        selection = selection == null ? Optional.empty() : selection;
        Objects.requireNonNull(displayText, "displayText");
    }
}
