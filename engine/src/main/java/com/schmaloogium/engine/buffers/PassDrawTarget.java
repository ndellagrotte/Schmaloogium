// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.gl.FramebufferHandle;

/**
 * Where a pass snapshot draws: an engine-owned framebuffer or the platform screen
 * (PHASE_5_DOC §2.2); SCREEN carries no framebuffer handle.
 */
public sealed interface PassDrawTarget {

    record EngineFramebuffer(FramebufferHandle framebuffer) implements PassDrawTarget {
    }

    enum Screen implements PassDrawTarget {
        INSTANCE
    }
}
