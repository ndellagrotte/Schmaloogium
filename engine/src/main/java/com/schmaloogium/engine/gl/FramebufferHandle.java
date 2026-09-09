// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Opaque framebuffer handle (PHASE_1_DOC §4.7.3). {@code non-sealed} because every backend
 * implements it outside this package; see {@link GLHandle} for why that is the only
 * correct modifier. Four handle types, not five: there is deliberately no
 * {@code RenderbufferHandle} and no renderbuffer verb anywhere in the facade — every
 * attachment must be a sampleable texture (§4.7.3).
 */
public non-sealed interface FramebufferHandle extends GLHandle {
}
