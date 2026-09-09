// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed depth-transfer layout vocabulary (PHASE_1_DOC §4.7.7a, D-P1-63):
 * respectively 4-byte float depth or 4-byte packed 24/8 words. Internal allocation
 * vocabulary, not pack-facing App B.4 additions.
 */
public enum DepthTransferLayout {
    DEPTH_COMPONENT_FLOAT,
    DEPTH_STENCIL_UNSIGNED_INT_24_8
}
