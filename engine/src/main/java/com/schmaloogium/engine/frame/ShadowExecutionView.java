// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

import com.schmaloogium.engine.frame.spi.AtlasBindingEvidence;
import com.schmaloogium.engine.frame.spi.SignalResult;

/**
 * The one live dynamic-extent shadow credential (PHASE_7_DOC §5.1). Lends the current base
 * binding and accepts the D-P7-73 refresh receiver. Invalidated by the bridge's close
 * before it returns; a closed view answers nothing.
 */
public interface ShadowExecutionView {

    /** The current base binding; only legal on a valid live execution. */
    AtlasBindingEvidence currentBaseBinding();

    /** Installs the one base-binding refresh receiver (D-P7-73). */
    SignalResult installBaseBindingReceiver(ShadowBaseBindingReceiver receiver);
}
