// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Opaque texture handle (PHASE_1_DOC §4.7.3). {@code non-sealed} because every backend
 * implements it outside this package; see {@link GLHandle} for why that is the only
 * correct modifier.
 *
 * <p>The same type serves engine-created textures and the two foreign classes
 * (ordinary {@link ForeignTextureProvider} values and authenticated
 * {@link BorrowedDepthAttachmentHandle}s), which carry a narrower bind-and-label-only
 * contract (§4.7.3, [D-P1-40]).
 */
public non-sealed interface TextureHandle extends GLHandle {
}
