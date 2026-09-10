// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.spi;

import com.schmaloogium.engine.frame.FrameToken;

/**
 * The fullscreen draw seam (PHASE_7_DOC §5.1): snapshots/normalizes/restores vanilla-visible
 * state through the loader glue, binds a typed Phase-5 draw target with the current anaglyph
 * mask, and executes one immutable fullscreen draw. Anaglyph color-mask handling is a
 * mod-side vanilla state operation, deliberately not a Phase-1 facade verb.
 */
public interface FrameRenderPort {

    StateSnapshot snapshotState();

    PortResult normalizeForEngine();

    PortResult bind(com.schmaloogium.engine.buffers.PassDrawTarget target, com.schmaloogium.engine.frame.AnaglyphEye eye);

    PortResult drawFullscreen(FullscreenDraw draw);

    PortResult restore(StateSnapshot snapshot);
}
