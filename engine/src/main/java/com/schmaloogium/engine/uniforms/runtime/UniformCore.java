// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.runtime;

import com.schmaloogium.engine.expr.api.BuiltInExpressionView;
import com.schmaloogium.engine.registry.BarrierParticipantResult;
import com.schmaloogium.engine.uniforms.UniformConfiguration;
import com.schmaloogium.engine.registry.BoundProgramActivityToken;
import com.schmaloogium.engine.registry.ProgramUniformCacheKey;
import com.schmaloogium.engine.registry.ProgramUniformLayout;

import java.util.List;
import java.util.Optional;

/**
 * The runtime-internal facade both participants and the immediate-event path execute
 * through (PHASE_6_DOC §4.11/§4.12): retired guards, cache access, batch runs, the
 * built-in expression view and the once-latched warning channel. Not published outside
 * the package; Phase 4's publisher sees only {@code BarrierParticipant} instances.
 */
public interface UniformCore {

    UniformConfiguration configuration();

    /** True after terminal retirement; every guard short-circuits on this. */
    boolean isRetired();

    /** The retired position result for the supplied scope. */
    BarrierParticipantResult retiredResult(String scope);

    /** The per-activation cache, created empty on first use of this key. */
    ProgramCache cacheFor(ProgramUniformCacheKey key, ProgramUniformLayout layout);

    /** The current active pair (cache, key, activity token), or empty before the first
     *  successful activation of an accepted frame. */
    Optional<ActivePair> activePair();

    record ActivePair(
            ProgramUniformCacheKey key,
            ProgramCache cache,
            BoundProgramActivityToken token) {
    }

    /** Runs one §4.11 attempt-batch protocol round; commits on clean drains. */
    BarrierParticipantResult runBatch(ProgramUniformCacheKey key, ProgramCache cache,
            List<UploadCommand> batch, String degradationScope);

    /** The canonical cell value, or null when invalid/absent. */
    UniformValue cellValue(String name);

    /** True when a capture arrived for the latest accepted frame (matrix cells current). */
    boolean matricesCurrentForFrame();

    /** Once-per-runtime-lifetime stable-key warning. */
    void warnOnce(String diagnosticKey, String detail);

    /** Unconditional stable-key warning. */
    void warn(String diagnosticKey, String detail);

    /** The installed custom bridge; the default no-op bridge when none installed. */
    com.schmaloogium.engine.expr.api.CustomUniformBridge customBridge();

    /** The fixed built-in expression view for Phase 11's refresh calls. */
    BuiltInExpressionView expressionView();
}
