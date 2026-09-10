// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import com.schmaloogium.engine.buffers.TextureOverlayLease;
import com.schmaloogium.engine.buffers.TextureOverlayPublicationId;

import java.util.Optional;

/**
 * The shadowtex binding source seam (PHASE_8_DOC §4.2 step 8): the expected overlay
 * publication id and the separately acquired overlay lease. v0.2 rides P13's
 * publication; until the frame context carries the lease pair, production wires
 * {@link #absent()}, which selects the documented binding-suppression path — Completed
 * with a diagnostic, zero texture binds, no transfer, abort without flips or mipmaps.
 * Tests script real sources to exercise the Bound/Degraded/Rejected/BackendFailed
 * branches and shadowtex0/1 parity.
 */
public interface ShadowBindingSource {

    /** The expected accepted overlay publication for this frame, if any. */
    Optional<TextureOverlayPublicationId> expectedOverlay();

    /**
     * Acquires the one overlay lease for this invocation; empty selects suppression.
     * The invocation owns the lease only until a Bound result transfers ownership into
     * the binding snapshot; cleanup closes exactly the current owner.
     */
    Optional<TextureOverlayLease> acquire();

    /** The production default until P13's lease pair lands in the frame context. */
    static ShadowBindingSource absent() {
        return Absent.INSTANCE;
    }

    /** Holder for the absent source. */
    final class Absent implements ShadowBindingSource {
        private static final Absent INSTANCE = new Absent();

        private Absent() {
        }

        @Override
        public Optional<TextureOverlayPublicationId> expectedOverlay() {
            return Optional.empty();
        }

        @Override
        public Optional<TextureOverlayLease> acquire() {
            return Optional.empty();
        }
    }
}
