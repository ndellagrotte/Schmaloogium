// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * A platform-owned, sampleable main-depth texture (PHASE_1_DOC §4.7.3). A narrower
 * {@link TextureHandle}, not a fifth handle category.
 *
 * <p>The public marker is necessary but never sufficient permission: each backend
 * authenticates that it issued the concrete value for this GLDevice/context before
 * accepting it at the narrow borrowed-depth call sites
 * ({@link FramebufferService#attachDepth}, {@link FramebufferService#attachDepthStencil};
 * bind and label are also legal). Merely implementing this interface is a forgery and is
 * rejected before any GL call, attachment change or recorder mutation ([D-P1-40]).
 */
public interface BorrowedDepthAttachmentHandle extends TextureHandle {
}
