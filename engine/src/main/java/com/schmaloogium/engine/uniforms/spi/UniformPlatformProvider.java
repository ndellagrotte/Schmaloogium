// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.spi;

import com.schmaloogium.engine.uniforms.Float3;
import com.schmaloogium.engine.uniforms.Int2;

/**
 * The loader-neutral platform sampling seam (PHASE_6_DOC §4.2), implemented by
 * {@code mod.glue} and scripted in headless tests. Intentionally coarse enough to sample
 * related Minecraft values once per acquisition boundary; never invoked from
 * {@code afterBind}. Production implementations are render-thread only.
 */
public interface UniformPlatformProvider {

    /** Configuration/world-provider install; called once at runtime construction. */
    OnceUniformSample sampleOnce();

    /** Per-tick acquisition; called only when the logical tick differs from the last
     *  sampled tick. The returned identities must equal the call arguments. */
    TickUniformSample sampleTick(long worldEpoch, long logicalTick);

    /** Per-frame acquisition; called exactly once per accepted frame with a request the
     *  runtime constructs from the accepted {@code FrameBeginInput}. */
    FrameUniformSample sampleFrame(FrameSampleRequest request);
}
