// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.buffers.BaseAtlasContext;
import com.schmaloogium.engine.buffers.TextureHandleRef;

import java.util.Objects;
import java.util.Optional;

/**
 * Closed authenticated-evidence observation (§4.4): the base association, the observed
 * opaque base handle when available, and the private currentness token. Invalid or stale
 * evidence maps to the dedicated outcomes — never to a forged Unavailable association.
 */
public sealed interface AtlasBindingObservation {

    record Authenticated(BaseAtlasContext association, Optional<TextureHandleRef> base,
                         Object currentnessToken) implements AtlasBindingObservation {
        public Authenticated {
            Objects.requireNonNull(association, "association");
            Objects.requireNonNull(base, "base");
            base.ifPresent(Objects::requireNonNull);
            base = base.isEmpty() ? Optional.empty() : Optional.of(base.get());
            Objects.requireNonNull(currentnessToken, "currentnessToken");
        }

        public static Authenticated of(BaseAtlasContext association,
                                       Optional<TextureHandleRef> base,
                                       Object currentnessToken) {
            return new Authenticated(association, base == null ? Optional.empty() : base,
                currentnessToken);
        }
    }

    record InvalidBase() implements AtlasBindingObservation {
        public static final InvalidBase INSTANCE = new InvalidBase();
    }

    record StaleBase() implements AtlasBindingObservation {
        public static final StaleBase INSTANCE = new StaleBase();
    }
}
