// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

import com.schmaloogium.engine.buffers.FixedSamplerResolver;
import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.registry.ProgramBindingParticipant;
import com.schmaloogium.engine.uniforms.spi.CenterDepthSource;
import com.schmaloogium.engine.uniforms.spi.UniformPlatformProvider;
import com.schmaloogium.engine.expr.api.CustomUniformBridge;
import com.schmaloogium.engine.expr.api.FixedExpressionInputSchema;

import java.util.Optional;

/**
 * One live uniform runtime (PHASE_6_DOC §2.2). The factory transfers sole lifecycle
 * ownership to the caller; the runtime retains its borrowed service references until
 * successful {@link #retire} and owns only its caches, snapshots and participants. The
 * runtime owns no program handle: Phase 4 binds its retained program and invokes the
 * three participants in sampler → built-in → custom order.
 */
public interface UniformRuntime {

    /** Atomic replacement-generation adoption handshake (§4.1/§4.14.1); render thread. */
    RegistryGenerationAdoptionResult adoptRegistryGeneration(
            long registryGeneration, UniformResetReason reason);

    /** The immutable exact-name input schema (§4.13); available immediately after
     *  construction, stable per runtime, independent of frame/program/provider/location. */
    FixedExpressionInputSchema fixedExpressionInputSchema();

    /** Frame begin (§4.6); render thread only. */
    FrameBeginResult beginFrame(FrameBeginInput input);

    /** The accepted-frame timing query (D-P6-25); render-thread confined — a wrong-thread
     *  call throws {@code IllegalStateException} before reading state, even after
     *  retirement. */
    Optional<UniformFrameTiming> frameTiming(long registryGeneration, long frameId);

    /** The typed event sink (§4.12); the same guarded instance for the runtime's life. */
    UniformEventSink events();

    /** First barrier position: fixed sampler integer re-point (§4.9). */
    ProgramBindingParticipant samplerParticipant();

    /** Second barrier position: built-in refresh (§4.3/§4.10/§4.11). */
    ProgramBindingParticipant builtInParticipant();

    /** Third barrier position: custom-expression refresh (§4.13). */
    ProgramBindingParticipant customParticipant();

    /** The reserved Phase 3 macro contributor — deliberately empty for every
     *  configuration (§4.8, D-P6-1). Pure metadata, never resurrection. */
    com.schmaloogium.engine.preprocess.MacroContributor centerDepthMacroContributor();

    /** Installs the one custom-uniform bridge (§4.13); composition thread only, after
     *  construction and before the first {@code beginFrame} or participant activation. */
    void installCustomUniformBridge(CustomUniformBridge bridge);

    /** Direct world-epoch reset (§4.14.1); render thread, accepts only WORLD_EPOCH. */
    void reset(UniformResetReason reason);

    /** Terminal, non-GL retirement (§4.14.2, R7-11); render thread only. */
    UniformRetirementResult retire(UniformRetirementReason reason);
}
