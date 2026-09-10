// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.registry.BufferDomain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Immutable, duplicate-free shadow mipmap request over typed shadow buffers
 * ({@code SHADOWTEX}/{@code SHADOWCOLOR}), stored in canonical {@link LogicalBuffer} order
 * ({@link BufferDomain} declaration order, then {@link BufferIndex}); construction rejects
 * duplicates and out-of-domain buffers.
 */
public record ShadowMipmapPolicy(List<LogicalBuffer> buffers) {

    private static final Comparator<LogicalBuffer> CANONICAL_ORDER =
            Comparator.comparing((LogicalBuffer buffer) -> buffer.domain())
                    .thenComparingInt(buffer -> buffer.index().value());

    public ShadowMipmapPolicy {
        List<LogicalBuffer> requested =
                List.copyOf(java.util.Objects.requireNonNull(buffers, "buffers"));
        if (requested.size() != Set.copyOf(requested).size()) {
            throw new IllegalArgumentException("duplicate buffers in shadow mipmap policy");
        }
        for (LogicalBuffer buffer : requested) {
            BufferDomain domain = java.util.Objects.requireNonNull(buffer, "buffer").domain();
            if (domain != BufferDomain.SHADOWTEX && domain != BufferDomain.SHADOWCOLOR) {
                throw new IllegalArgumentException(
                        "shadow mipmap policy requires SHADOWTEX or SHADOWCOLOR, got " + domain);
            }
        }
        List<LogicalBuffer> ordered = new ArrayList<>(requested);
        ordered.sort(CANONICAL_ORDER);
        buffers = List.copyOf(ordered);
    }
}
