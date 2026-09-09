// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed engine vocabulary for {@link FramebufferService#check} results (PHASE_1_DOC
 * §4.7.4): no GL constants appear in any signature; the backend maps driver-reported
 * completeness into this set, with {@link #UNKNOWN} absorbing anything unrecognized.
 */
public enum FramebufferStatus {
    COMPLETE,
    INCOMPLETE_ATTACHMENT,
    INCOMPLETE_MISSING_ATTACHMENT,
    INCOMPLETE_DIMENSIONS,
    INCOMPLETE_FORMATS,
    INCOMPLETE_DRAW_BUFFER,
    INCOMPLETE_READ_BUFFER,
    INCOMPLETE_MULTISAMPLE,
    INCOMPLETE_LAYER_TARGETS,
    UNSUPPORTED,
    UNDEFINED,
    UNKNOWN
}
