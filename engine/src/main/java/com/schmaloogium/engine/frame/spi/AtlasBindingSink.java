// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame.spi;

/**
 * The authenticated atlas adapter sink (PHASE_7_DOC §5.1). The sink validates issuer,
 * render thread, current composition/epoch, then latest serial; invalid, foreign or stale
 * evidence is rejected without mutation. Consumption prevents duplicate signal dispatch,
 * not a same-serial lease authentication.
 */
public interface AtlasBindingSink {

    SignalResult currentBinding(AtlasBindingEvidence evidence);
}
