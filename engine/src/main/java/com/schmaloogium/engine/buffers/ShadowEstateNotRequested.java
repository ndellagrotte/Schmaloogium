// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/**
 * Ordinary absence of the shadow estate because both shadow minima are zero;
 * not a {@link BufferFailure}.
 */
public record ShadowEstateNotRequested(long estateGeneration) implements ShadowEstateResult {
}
