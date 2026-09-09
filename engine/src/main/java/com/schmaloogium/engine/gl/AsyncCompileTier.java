// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Shader-compilation execution tier (R-P14→P1-2, PHASE_1_DOC §4.7.8; PHASE_14_DOC
 * §2.1): SHARED_CONTEXT | INLINE. INLINE is the synchronous reference-faithful default
 * (D-P14-17); SHARED_CONTEXT additionally requires the driver-policy allowlist to admit
 * it (default deny, D-P14-12) — the executor mechanism itself is a separately tracked
 * post-v0.5 request.
 */
public enum AsyncCompileTier {
    SHARED_CONTEXT,
    INLINE
}
