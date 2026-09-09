// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import com.schmaloogium.engine.config.AlphaFunction;
import com.schmaloogium.engine.config.AlphaTestSpec;
import com.schmaloogium.engine.config.BlendAlphaFactors;
import com.schmaloogium.engine.config.BlendFactor;
import com.schmaloogium.engine.config.BlendSpec;
import com.schmaloogium.engine.gl.AlphaTestState;
import com.schmaloogium.engine.gl.BlendState;

import java.util.Optional;

/**
 * The lossless Phase3-spec to Phase1-state adaptation used by the barrier lock (PHASE_4_DOC
 * §4.10 step 6). Absent means do not lock that aspect; explicit Off locks it disabled.
 */
final class StateAdapters {

    private StateAdapters() {
    }

    static Optional<AlphaTestState> alpha(Optional<AlphaTestSpec> spec) {
        if (spec.isEmpty()) {
            return Optional.empty();
        }
        if (spec.get() instanceof AlphaTestSpec.Off) {
            return Optional.empty();
        }
        AlphaTestSpec.Enabled enabled = (AlphaTestSpec.Enabled) spec.get();
        return Optional.of(new AlphaTestState(
            AlphaTestState.AlphaTestFunction.valueOf(enabled.function().name()),
            enabled.reference()));
    }

    static Optional<BlendState> blend(Optional<BlendSpec> spec) {
        if (spec.isEmpty()) {
            return Optional.empty();
        }
        if (spec.get() instanceof BlendSpec.Off) {
            return Optional.empty();
        }
        BlendSpec.Enabled enabled = (BlendSpec.Enabled) spec.get();
        Optional<BlendAlphaFactors> alpha = enabled.alpha();
        return Optional.of(new BlendState(
            factor(enabled.sourceColor()),
            factor(enabled.destinationColor()),
            alpha.map(factors -> factor(factors.source()))
                .orElse(factor(enabled.destinationColor())),
            alpha.map(factors -> factor(factors.destination()))
                .orElse(factor(enabled.destinationColor()))));
    }

    private static BlendState.BlendFactor factor(BlendFactor factor) {
        return BlendState.BlendFactor.valueOf(factor.name());
    }

    @SuppressWarnings("unused")
    private static AlphaTestState.AlphaTestFunction function(AlphaFunction function) {
        return AlphaTestState.AlphaTestFunction.valueOf(function.name());
    }
}
