// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.frame.spi.AtlasBindingEvidence;

import java.util.Objects;

/**
 * Where the engine holds the installed {@link AtlasBindingObserver} (§4.4/D-P13-43): the
 * same shape as {@code engine.gl.ForeignTextures} — a Phase 7-implemented SPI installed by
 * the issuer adapter at owner construction. Before install — and in every headless test
 * that does not script one — {@link #active()} rejects every evidence and answers no token
 * is latest, so leases fail closed with {@code INVALID_BASE_BINDING}.
 */
public final class AtlasBindingObservers {

    private static final AtlasBindingObserver ABSENT = new AtlasBindingObserver() {
        @Override
        public AtlasBindingObservation authenticate(AtlasBindingEvidence evidence) {
            Objects.requireNonNull(evidence, "evidence");
            return new AtlasBindingObservation.InvalidBase();
        }

        @Override
        public boolean isLatest(Object currentnessToken) {
            return false;
        }
    };

    private static volatile AtlasBindingObserver active = ABSENT;

    private AtlasBindingObservers() {
    }

    /** Installs the observer. Called by Phase 7's issuer adapter, once. */
    public static void install(AtlasBindingObserver observer) {
        active = Objects.requireNonNull(observer, "observer");
    }

    /** The installed observer; before install, rejects every binding. */
    public static AtlasBindingObserver active() {
        return active;
    }
}
