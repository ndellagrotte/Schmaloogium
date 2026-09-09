// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * The composite/final full-screen pass primitive (PHASE_1_DOC §4.7.4). It delegates the
 * PRIMITIVE only: it establishes no draw state, and the caller is responsible for the
 * composite state block through {@link StateService}.
 *
 * <p>{@code const int countInstances = N} on a COMPOSITE/DEFERRED program is NOT served
 * by an instanced verb — there is none, deliberately ([D-P1-33]): it is a caller-side
 * loop over this primitive with an {@code instanceId} upload between copies. On a
 * gbuffers/shadow program the directive re-renders vanilla geometry and never reaches
 * this verb at all.
 */
public interface DrawService {

    /** The backend uses its active linked input requirement: TRIANGLES forces the
     *  retained four-vertex triangle strip even where QUADS is available; non-geometry
     *  retains QUADS preference (§4.7.4a, D-P1-48). The engine never expresses that
     *  backend choice. */
    void fullscreenQuad();
}
