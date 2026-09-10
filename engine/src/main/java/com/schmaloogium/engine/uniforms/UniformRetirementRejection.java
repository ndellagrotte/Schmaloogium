// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

/** Closed retirement rejection domain (PHASE_6_DOC §4.14.2): off the render thread, or a
 *  Phase 6 operation/callback in flight. A rejected retirement mutates nothing; the caller
 *  keeps ownership and services alive and retries at the legal boundary. */
public enum UniformRetirementRejection {
    WRONG_THREAD,
    ACTIVE_CALLBACK
}
