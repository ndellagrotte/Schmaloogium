// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.spi;

import com.schmaloogium.engine.frame.FinalizedFrame;

/**
 * Fired exactly once per finalized frame, after the final pass completed and before
 * presentation (H-CAPTURE-01); the observer receives the frozen summary. Absence disables
 * the observation — never a capture failure.
 */
public interface FrameCompletionObserver {

    void beforePresent(FinalizedFrame frame);
}
