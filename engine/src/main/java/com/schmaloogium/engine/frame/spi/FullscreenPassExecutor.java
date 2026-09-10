// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.spi;

import com.schmaloogium.engine.frame.FailureId;
import com.schmaloogium.engine.frame.FrameToken;

/**
 * The deferred/composite/final execution surface (PHASE_7_DOC §5.1): one transaction per
 * raster descriptor, render-thread confined.
 */
public interface FullscreenPassExecutor {

    FullscreenExecutionResult execute(FrameToken frame, FullscreenDraw draw);
}
