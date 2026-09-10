// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

/** Decoded independent user preference pair feeding the preliminary macro producer. */
public record PreliminaryCompanionDemand(
        boolean packActive, boolean fixedUnitCapabilityAvailable,
        boolean normalMapEnabled, boolean specularMapEnabled) {
}
