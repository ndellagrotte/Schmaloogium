// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The callback-scoped bound-uniform access (PHASE_4_DOC §4.10). Render-thread and callback
 * scoped: {@code locate} accepts only a non-empty name present in the effective uniform
 * layout and delegates internally without exposing the private handle; blank, undeclared, or
 * out-of-callback use fails before any GL lookup. Returned locations and the cache key may be
 * retained under the generation rule; this access object may not.
 */
public interface BoundProgramUniformAccess {

    ProgramUniformCacheKey cacheKey();

    com.schmaloogium.engine.gl.UniformLocation locate(String exactName);

    BoundProgramActivityToken activityToken();
}
