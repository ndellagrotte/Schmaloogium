// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed depth-attachment format domain (PHASE_1_DOC §4.7.7a; P5 §4.8's exact two-value
 * domain): depth-only, or the packed DEPTH24_STENCIL8 object attachable at both depth and
 * stencil points. {@code DEPTH24_STENCIL8} is the exact format
 * {@link FramebufferService#attachDepthStencil} requires of an authenticated borrowed
 * depth handle.
 */
public enum DepthAttachmentFormat {
    DEPTH_COMPONENT,
    DEPTH24_STENCIL8
}
