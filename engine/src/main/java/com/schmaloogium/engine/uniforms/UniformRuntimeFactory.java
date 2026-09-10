// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

import com.schmaloogium.engine.buffers.FixedSamplerResolver;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.uniforms.spi.CenterDepthSource;
import com.schmaloogium.engine.uniforms.spi.UniformPlatformProvider;

/**
 * The exact eight-argument factory (PHASE_6_DOC §2.2). All inputs are required: a missing
 * sampler resolver or replay observer — or any other missing service — rejects
 * construction through {@link UniformBuildResult.Failure} before GL or callbacks; there
 * is no old overload, default observer or no-op fallback.
 *
 * <p>{@code samplerResolver} is the borrowed pure service from Phase 5's
 * {@code FixedSamplerPolicies.resolver()}, paired with the same table/schema/fingerprint
 * as the {@code FixedSamplerPolicies.appB3()} policy supplied to Phase 4 compilation
 * (R7-10). {@code replayErrors} is the Phase-7-supplied observer (D-P6-27).
 */
public interface UniformRuntimeFactory {

    UniformBuildResult create(
            long initialRegistryGeneration,
            UniformConfiguration configuration,
            FixedSamplerResolver samplerResolver,
            UniformPlatformProvider platform,
            CenterDepthSource centerDepth,
            GLDevice gl,
            DiagnosticReporter diagnostics,
            UniformReplayErrorSink replayErrors);

    /** The engine's single production factory. */
    static UniformRuntimeFactory factory() {
        return com.schmaloogium.engine.uniforms.runtime.UniformRuntimeImpl.FACTORY;
    }
}
