// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.spi;

/**
 * The synchronous center-depth seam (PHASE_6_DOC §4.2/§4.8, D-P6-1). The {@code mod.glue}
 * implementation uses Phase 1's synchronous {@code FramebufferService.readDepthPixel};
 * headless tests script answers. Called at most once per accepted frame and only when the
 * configuration requires center depth; the read observes the completed prior image before
 * any resize/clear.
 */
public interface CenterDepthSource {

    CenterDepthResult readCenter(CenterDepthRequest request);
}
