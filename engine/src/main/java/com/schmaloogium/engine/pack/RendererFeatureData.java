// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

/** Companion-atlas availability; fingerprinted but never a companion-emission gate. */
public record RendererFeatureData(
        boolean normalMapAvailable,
        boolean specularMapAvailable) {
}
