// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

/**
 * Closed attribute-delivery vocabulary (PHASE_10_DOC §2.2). Pure engine value.
 */
public enum Delivery {
    FIXED_FUNCTION,
    FLOAT_VALUE,
    NORMALIZED_FLOAT,
    PADDING
}
