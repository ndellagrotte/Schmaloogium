// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/**
 * One immutable published runtime (PHASE_9_DOC §2.2/§4.1). Generation and fingerprint
 * identify every borrow; the hot lookups are final primitive-array readers. Retirement
 * keeps the object alive until Phase 7's frame/build borrows end and it is closed —
 * no new borrow may target it once {@code current()} stops returning it.
 */
public interface PublishedIdRuntime extends AutoCloseable {

    /** The monotonically increasing positive publication generation. */
    long generation();

    /** The complete derived identity of this publication. */
    IdRuntimeFingerprint fingerprint();

    /** The Phase 10 alias lookup stamped with this generation. */
    AliasLookup aliases();

    /** The resolved custom render-layer decision table. */
    RenderLayerLookup renderLayers();

    /** The balanced per-draw scope state machine for this generation. */
    PerDrawDynamics perDraw();

    @Override
    void close();
}
