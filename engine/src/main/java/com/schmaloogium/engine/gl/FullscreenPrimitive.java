// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed selected-route vocabulary for the fullscreen composite primitive
 * (PHASE_1_DOC §4.7.4a recorder parity, D-P1-48): what the backend actually submitted for
 * {@link DrawService#fullscreenQuad}. {@code TRIANGLES} input always uses the retained
 * four-vertex triangle-strip implementation, including on QUADS-capable profiles;
 * {@code NO_GEOMETRY} keeps QUADS when supported. The engine never expresses that backend
 * choice — it is metadata/log vocabulary only.
 */
public enum FullscreenPrimitive {
    QUADS,
    TRIANGLE_STRIP
}
