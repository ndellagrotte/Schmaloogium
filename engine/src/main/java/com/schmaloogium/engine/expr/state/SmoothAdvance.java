// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.state;

/** One smooth advance attempt (§4.7): the resulting value, or a diagnostic kind name
 * ({@code DOMAIN} or {@code NON_FINITE}) plus message when the transition is rejected. */
public record SmoothAdvance(float value, String errorKind, String errorMessage) {

    public static SmoothAdvance of(float value) {
        return new SmoothAdvance(value, null, null);
    }

    public static SmoothAdvance rejected(String kind, String message) {
        return new SmoothAdvance(0.0f, kind, message);
    }

    public boolean failed() {
        return errorKind != null;
    }
}
