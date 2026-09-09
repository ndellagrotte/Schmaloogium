// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Monotonic buffer-size minima aggregated from active uniform declarations. */
public record BufferMinima(int colorBuffers, int mainDepthTextures,
    int shadowDepthBuffers, int shadowColorBuffers) {
}
