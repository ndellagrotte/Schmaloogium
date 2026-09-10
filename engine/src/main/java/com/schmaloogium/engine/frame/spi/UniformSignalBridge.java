// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.spi;

import com.schmaloogium.engine.frame.CameraSnapshot;
import com.schmaloogium.engine.frame.FrameBeginSignal;
import com.schmaloogium.engine.frame.FrameToken;

/**
 * The uniform-signal bridge (PHASE_7_DOC §5.1): the sole route from the catalogued hooks to
 * Phase 6's typed event sink. The frame and camera calls establish the current signal
 * identity; event calls are authenticated against it. Each accepted event forwards its
 * payload verbatim to the Phase-6 sink (Celestial→updateCelestial, Fog→updateFog,
 * Blend→updateBlend) with no resampling.
 */
public interface UniformSignalBridge {

    SignalResult frame(FrameBeginSignal signal);

    SignalResult camera(FrameToken token, CameraSnapshot camera);

    SignalResult event(FrameToken token, UniformSignal signal);
}
