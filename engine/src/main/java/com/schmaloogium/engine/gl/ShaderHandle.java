// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Opaque shader handle (PHASE_1_DOC §4.7.3). {@code non-sealed} because every backend
 * implements it outside this package; see {@link GLHandle} for why that is the only
 * correct modifier.
 */
public non-sealed interface ShaderHandle extends GLHandle {
}
