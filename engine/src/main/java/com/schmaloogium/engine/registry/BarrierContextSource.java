// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * Phase-4-owned context source (PHASE_4_DOC §4.10). Render-thread-only; Phase 7 calls
 * {@link #beginFrame()} exactly once at frame entry. It monotonically advances a private
 * epoch, permanently retires the prior {@link FrameBarrierContexts}, and returns the sole
 * issuer for that frame.
 */
public interface BarrierContextSource {

    FrameBarrierContexts beginFrame();
}
