// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Depth-readback tier (R-P14→P1-2, PHASE_1_DOC §4.7.8; PHASE_14_DOC §2.1):
 * PBO_FENCE | SYNCHRONOUS. SYNCHRONOUS is the faithful v0.1 path through
 * {@link FramebufferService#readDepthPixel}; the async PBO+fence variant is the deferred
 * modernization (§4.7.4's absent-verbs table).
 */
public enum AsyncReadbackTier {
    PBO_FENCE,
    SYNCHRONOUS
}
