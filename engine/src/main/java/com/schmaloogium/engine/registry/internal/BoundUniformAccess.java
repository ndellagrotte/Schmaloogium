// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.gl.ProgramHandle;
import com.schmaloogium.engine.gl.UniformLocation;
import com.schmaloogium.engine.registry.BoundProgramUniformAccess;
import com.schmaloogium.engine.registry.ProgramUniformCacheKey;
import com.schmaloogium.engine.registry.ProgramUniformLayout;

/**
 * The callback-scoped bound-uniform access (PHASE_4_DOC §4.10). {@code locate} accepts only a
 * non-empty name present in the effective uniform layout, only inside an open callback
 * window, and delegates privately to UniformService without exposing the handle.
 */
final class BoundUniformAccess implements BoundProgramUniformAccess {

    private final BarrierCore core;
    private final ProgramUniformCacheKey cacheKey;
    private final com.schmaloogium.engine.registry.ProgramSlotId provider;
    private final ProgramUniformLayout layout;
    private final ActivityToken token;

    BoundUniformAccess(
            BarrierCore core,
            ProgramUniformCacheKey cacheKey,
            com.schmaloogium.engine.registry.ProgramSlotId provider,
            ProgramUniformLayout layout,
            ActivityToken token) {
        this.core = core;
        this.cacheKey = cacheKey;
        this.provider = provider;
        this.layout = layout;
        this.token = token;
    }

    @Override
    public ProgramUniformCacheKey cacheKey() {
        return cacheKey;
    }

    @Override
    public UniformLocation locate(String exactName) {
        if (!core.callbackWindowOpen()) {
            throw new IllegalStateException("locate outside the participant callback window");
        }
        if (exactName == null || exactName.isEmpty()
            || !layout.declarations().containsKey(exactName)) {
            throw new IllegalArgumentException("blank or undeclared uniform name");
        }
        ProgramHandle handle = core.handleOf(provider);
        return core.uniforms().locate(handle, exactName);
    }

    @Override
    public ActivityToken activityToken() {
        return token;
    }
}
