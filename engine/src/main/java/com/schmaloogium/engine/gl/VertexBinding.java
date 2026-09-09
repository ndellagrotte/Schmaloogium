// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Opaque, backend-implemented marker for one LIFO render-thread restoration obligation
 * (PHASE_1_DOC §4.7.6): the public type is not authority, and {@code restore} accepts
 * only the same device's live top binding — null, forged, wrong-device, out-of-order,
 * already-closed or wrong-thread calls fail before GL.
 */
public interface VertexBinding {
}
