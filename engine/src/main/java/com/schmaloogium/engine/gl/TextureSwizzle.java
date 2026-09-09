// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Closed engine swizzle vocabulary (PHASE_1_DOC §4.7.7). {@code IDENTITY} is the
 * color-texture RGBA mapping; depth values use {@code LEGACY_DEPTH_LUMINANCE}
 * consistently (R,R,R,1), through supported swizzle where available and compatibility
 * {@code GL_DEPTH_TEXTURE_MODE=LUMINANCE} otherwise — never RED as a GL2 depth-mode
 * value. {@code IDENTITY} is rejected for depth rather than silently given different
 * cross-profile meanings.
 */
public enum TextureSwizzle {
    IDENTITY,
    LEGACY_DEPTH_LUMINANCE
}
