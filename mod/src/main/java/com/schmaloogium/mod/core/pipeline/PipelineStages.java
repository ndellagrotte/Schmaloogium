// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core.pipeline;

import com.schmaloogium.engine.buffers.BufferFailure;
import com.schmaloogium.engine.buffers.BufferPublicationResult;
import com.schmaloogium.engine.buffers.BufferRuntimeInputs;
import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.pack.DimensionKey;
import com.schmaloogium.engine.pack.PackConfiguration;
import com.schmaloogium.engine.pack.PackLoadResult;
import com.schmaloogium.engine.pack.PackSelection;
import com.schmaloogium.engine.preprocess.MacroContribution;
import com.schmaloogium.engine.registry.ProgramBindingParticipant;
import com.schmaloogium.engine.registry.ProgramRegistryView;
import com.schmaloogium.engine.registry.PublicationResult;
import com.schmaloogium.engine.registry.RegistryBuildFailure;
import com.schmaloogium.engine.registry.RegistryFingerprint;
import com.schmaloogium.engine.uniforms.UniformBuildResult;
import com.schmaloogium.engine.uniforms.UniformConfiguration;
import com.schmaloogium.engine.uniforms.UniformReplayErrorSink;

/**
 * The composition root's stage seam: one method per engine step of the PHASE_7_DOC §4.1
 * transaction, in the order the transaction calls them. {@link EnginePipelineStages} is the
 * only production implementation and maps 1:1 onto the public P3/P4/P5/P6 facades; headless
 * tests script the outcomes. The seam exists because caller-owned P4/P5 candidates mint only
 * over engine internals that {@code :mod} tests may not construct (C-2), not because the
 * engine contracts need adapting.
 */
public interface PipelineStages {

    /** P3: discover-independent load of an already resolved selection. */
    PackLoadResult load(PackSelection selection, EngineOptionData options);

    /** P6: the eight-argument factory over the live platform; the sink is never a no-op. */
    UniformBuildResult uniforms(long initialRegistryGeneration, UniformConfiguration configuration,
                                UniformReplayErrorSink replayErrors);

    /** P4 compile: the first real shader compilation; caller owns the returned candidate. */
    RegistryHandle compile(PackConfiguration configuration, DimensionKey dimension,
                           MacroContribution macroContribution);

    /** P5 plan + create from the detached registry view; caller owns a ready candidate. */
    EstateHandle estate(PackConfiguration configuration, ProgramRegistryView registry,
                        RegistryFingerprint registryFingerprint, BufferRuntimeInputs runtime);

    /** The P4 publisher's current generation (the P6 factory's initial generation). */
    long currentRegistryGeneration();

    /** P4 publish of a ready candidate; the release context is minted immediately inside. */
    PublicationResult publishReady(RegistryHandle.Ready registry, BarrierHandle barrier);

    /** P4 shaders-off publication with a fresh release context. */
    PublicationResult publishOff(RegistryBuildFailure cause);

    /** P5 publish; requires the exact fingerprint the candidate was built against. */
    BufferPublicationResult publishEstate(EstateHandle.Ready estate, RegistryFingerprint accepted);

    /** P5 off publication. */
    BufferPublicationResult publishEstateOff(BufferFailure cause);

    /** A compiled registry: ready (caller-owned until accepted) or shaders-off. */
    sealed interface RegistryHandle permits RegistryHandle.Ready, RegistryHandle.Off {

        non-sealed interface Ready extends RegistryHandle, AutoCloseable {

            /** The detached, publication-independent view (P4 §5.1). */
            ProgramRegistryView view();

            /** Composes the production barrier with exactly one runtime's participants. */
            BarrierOutcome compose(ProgramBindingParticipant samplers,
                                   ProgramBindingParticipant builtIns,
                                   ProgramBindingParticipant customs);

            @Override
            void close();
        }

        record Off(RegistryBuildFailure failure) implements RegistryHandle {
        }
    }

    /** The barrier composition outcome. */
    sealed interface BarrierOutcome permits BarrierOutcome.Ready, BarrierOutcome.Invalid {

        record Ready(BarrierHandle barrier) implements BarrierOutcome {
        }

        record Invalid(String diagnosticId) implements BarrierOutcome {
        }
    }

    /** A composed barrier candidate, caller-owned until accepted. */
    interface BarrierHandle extends AutoCloseable {

        @Override
        void close();
    }

    /** A built estate: ready (caller-owned until accepted), awaiting depth, or off. */
    sealed interface EstateHandle
            permits EstateHandle.Ready, EstateHandle.AwaitingMainDepth, EstateHandle.Off {

        non-sealed interface Ready extends EstateHandle, AutoCloseable {

            @Override
            void close();
        }

        record AwaitingMainDepth(long expectedVersion) implements EstateHandle {
        }

        record Off(BufferFailure failure) implements EstateHandle {
        }
    }
}
