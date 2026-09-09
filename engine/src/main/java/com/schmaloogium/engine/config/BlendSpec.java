// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.Optional;

/** {@code blend} state; {@code Off} differs from absence. */
public sealed interface BlendSpec {
    record Off() implements BlendSpec {}
    record Enabled(
        BlendFactor sourceColor,
        BlendFactor destinationColor,
        Optional<BlendAlphaFactors> alpha) implements BlendSpec {

        public Enabled {
            java.util.Objects.requireNonNull(sourceColor, "sourceColor");
            java.util.Objects.requireNonNull(destinationColor, "destinationColor");
            alpha = alpha == null ? Optional.empty() : alpha;
        }
    }
}
