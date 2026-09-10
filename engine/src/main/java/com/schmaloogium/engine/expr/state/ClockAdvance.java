// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.state;

/** One controller-clock observation (§4.7): either the new controller seconds or a stable
 * rejection reason for a provider-protocol failure before any mutation. */
public sealed interface ClockAdvance {

    record Advanced(double controllerSeconds) implements ClockAdvance {
    }

    record Rejected(String reason) implements ClockAdvance {
    }
}
