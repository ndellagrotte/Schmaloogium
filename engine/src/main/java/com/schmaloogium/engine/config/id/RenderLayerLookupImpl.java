// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import java.util.Optional;

/** Primitive-array layer decision reader over one publication (PHASE_9_DOC §4.9). */
final class RenderLayerLookupImpl implements RenderLayerLookup {

    private final PublishedIdRuntimeImpl owner;
    private final ResolvedRenderLayer[] layers;

    RenderLayerLookupImpl(PublishedIdRuntimeImpl owner, IdResolutionTables tables) {
        this.owner = owner;
        this.layers = tables.layers;
    }

    @Override
    public Optional<ResolvedRenderLayer> layer(int blockStateOrdinal) {
        owner.checkOpen();
        if (blockStateOrdinal < 0 || blockStateOrdinal >= layers.length) {
            throw new IndexOutOfBoundsException("protocol rejection: state ordinal "
                    + blockStateOrdinal + " outside 0.." + layers.length);
        }
        return Optional.ofNullable(layers[blockStateOrdinal]);
    }
}
