// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.spi;

/**
 * The raw blend factors exactly as vanilla's GlStateManager reports them
 * (PHASE_7_DOC §5.1). The glue translates observed SourceFactor/DestFactor enums into the
 * full integers; the Phase-6 participant does its own do-nothing translation.
 */
public record BlendStateValue(boolean enabled, int srcRgb, int dstRgb, int srcAlpha, int dstAlpha) {
}
