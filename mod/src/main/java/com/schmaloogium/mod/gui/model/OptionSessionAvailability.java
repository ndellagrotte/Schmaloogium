// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.gui.model;

import java.util.Objects;

/**
 * Session-level action availability, rebuilt fresh on every {@code present(screen)}
 * (D-P12-38): mutation (edits), reset, apply and done each carry their own gate and
 * prepared reason. Views render these flags verbatim and never infer gates from
 * dirty/count or any other proxy.
 */
public record OptionSessionAvailability(
        OptionActionAvailability mutation,
        OptionActionAvailability reset,
        OptionActionAvailability apply,
        OptionActionAvailability done) {

    public OptionSessionAvailability {
        Objects.requireNonNull(mutation, "mutation");
        Objects.requireNonNull(reset, "reset");
        Objects.requireNonNull(apply, "apply");
        Objects.requireNonNull(done, "done");
    }
}
