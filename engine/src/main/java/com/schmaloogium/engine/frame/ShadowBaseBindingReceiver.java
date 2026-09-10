// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

import com.schmaloogium.engine.frame.spi.AtlasBindingEvidence;
import com.schmaloogium.engine.frame.spi.SignalResult;

/**
 * The D-P7-73 actual-base refresh receiver (PHASE_7_DOC §5.1): called by the binding
 * observer when the base texture changes while a shadow execution is open.
 */
public interface ShadowBaseBindingReceiver {

    SignalResult refreshBaseBinding(AtlasBindingEvidence evidence);
}
