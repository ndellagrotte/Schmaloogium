// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.spi;

/**
 * A loader-neutral vanilla-visible state capture (PHASE_7_DOC §5.1). Opaque to the engine:
 * only the {@link FrameRenderPort} implementation that minted a snapshot can restore it.
 * Engine types never appear inside.
 */
public interface StateSnapshot {
}
