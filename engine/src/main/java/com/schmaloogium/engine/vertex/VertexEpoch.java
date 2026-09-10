// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

/**
 * Independent vertex-publication identity (PHASE_10_DOC §2.2, §5.1). {@code serial}
 * changes on every geometry-affecting publication, including off/on and resource reload
 * with unchanged alias bytes; {@code worldEpoch} prevents an old world/chunk coordinate
 * from matching a newly loaded world; {@code idGeneration} is Phase 9's independent
 * generation, not Phase 4's registry generation. The layout fingerprint identifies
 * immutable layout content, never a credential.
 *
 * <p>Equality of epochs is <em>necessary, never sufficient</em> for product admission:
 * {@link MeshAdmission} additionally compares the complete input-plan identity, so
 * identical epochs with a changed participation or pointer set still reject.
 */
public record VertexEpoch(long serial, long worldEpoch, long idGeneration,
                          String layoutFingerprint) {

    public VertexEpoch {
        if (layoutFingerprint == null) {
            throw new IllegalArgumentException("layoutFingerprint must not be null");
        }
    }
}
