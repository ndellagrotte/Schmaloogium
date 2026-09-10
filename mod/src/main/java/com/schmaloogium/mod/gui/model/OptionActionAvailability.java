// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.Objects;
import java.util.Optional;

/**
 * Presenter-owned availability of one action: enabled, with a prepared disabled reason
 * present iff disabled (present-empty reason stays disabled, D-P12-38).
 */
public record OptionActionAvailability(boolean enabled, Optional<String> disabledReason) {

    /** Shared enabled instance (the accessor {@code enabled()} cannot be a factory). */
    public static final OptionActionAvailability ENABLED =
            new OptionActionAvailability(true, Optional.empty());

    public OptionActionAvailability {
        disabledReason = disabledReason == null ? Optional.empty() : disabledReason;
        Objects.requireNonNull(disabledReason, "disabledReason");
    }


    public static OptionActionAvailability disabled(String reason) {
        return new OptionActionAvailability(false, Optional.ofNullable(reason));
    }
}
