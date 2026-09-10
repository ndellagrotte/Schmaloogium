// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.gl;

import com.schmaloogium.engine.gl.SamplerKey;

/**
 * Native sampler-object verbs (PHASE_14_DOC §4.1). The seam that keeps
 * {@link SamplerCache} headlessly testable: the LWJGL3 adapter implements it against
 * GL 3.3 {@code glGenSamplers}/{@code glSamplerParameter*}/{@code glBindSampler}, while
 * the mod test source set (which has no LWJGL on its classpath) supplies recording
 * fakes.
 *
 * <p>Render-thread only; render-thread/context guards and error attribution stay with
 * the installing adapter, exactly as every other facade verb ([D-P1-30]). Each mutating
 * implementation polls its own GL error after the call and returns {@code false} on
 * failure, so the cache never records bound-unit knowledge from a failed call
 * (D-P14-4: only successfully completed clears update knowledge).
 */
interface SamplerOps {

    /** Creates and fully parameterizes one native sampler from {@code key} — the whole
     *  sampler-state prefix, losslessly (D-P14-1/D-P14-31). Returns the native name, or
     *  -1 when creation or any parameterization step failed (no partial sampler is
     *  reported). */
    int create(SamplerKey key);

    /** {@code glBindSampler(unit, sampler)}; {@code sampler == 0} clears the unit. */
    boolean bind(int unit, int sampler);

    /** Deletes one native sampler. {@code false} on GL error (the cache retries later). */
    boolean delete(int sampler);
}
