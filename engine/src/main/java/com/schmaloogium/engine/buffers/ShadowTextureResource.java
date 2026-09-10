// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/** Sampling parameter set for one shadow texture (PHASE_5_DOC §2.2). */
public record ShadowTextureResource(boolean hardwareFiltering, boolean mipmap, boolean nearest) {
}
