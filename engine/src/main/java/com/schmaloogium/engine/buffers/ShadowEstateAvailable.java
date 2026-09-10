// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * The shadow estate is published and usable; carries its live protocol view.
 */
public record ShadowEstateAvailable(ShadowEstateView view) implements ShadowEstateResult {

    public ShadowEstateAvailable {
        java.util.Objects.requireNonNull(view, "view");
    }
}
