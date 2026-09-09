// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Backend-implemented opaque render-thread lease (PHASE_1_DOC §4.7.4, D-P1-58): obtain
 * ONLY from {@link StateService#lockAlphaBlend}. An unsealed engine interface, like the
 * backend-implemented handle seams — the LWJGL backend in {@code mod.glue} and the
 * recorder in {@code engine.gl.record} each return their own private implementation; no
 * engine-package constructor or factory exists.
 *
 * <p>The implementation retains its issuing device's private lease identity, saved state
 * and consumed flag. Implementing this interface cannot manufacture that private
 * authority, and no operation accepts a caller-supplied lease. Closing a genuine lease is
 * idempotent; a consumed binding is never retried.
 */
public interface AlphaBlendOverride extends AutoCloseable {

    @Override
    void close();
}
