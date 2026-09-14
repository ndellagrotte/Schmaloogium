// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.id;

import com.schmaloogium.engine.config.id.HandLightPolicy;
import com.schmaloogium.engine.config.id.PublishedIdRuntime;
import com.schmaloogium.engine.uniforms.UniformEventSink;
import com.schmaloogium.engine.vertex.VertexEpoch;

import java.util.Objects;
import java.util.Optional;

/**
 * What the composition root publishes to the hooks after each install (or clears):
 * the P9 runtime with its identity maps and the event sink it writes through, the hand
 * light policy, and the P10 vertex epoch the install admits.
 */
public record IdPublication(Optional<PublishedIdRuntime> runtime, IdIdentityMaps maps,
                            UniformEventSink sink, HandLightPolicy handLight,
                            Optional<VertexEpoch> vertexEpoch,
                            java.util.Set<com.schmaloogium.engine.registry.ExtendedAttribute> declaredAttributes) {

    public IdPublication {
        runtime = runtime == null ? Optional.empty() : runtime;
        maps = maps == null ? IdIdentityMaps.EMPTY : maps;
        handLight = handLight == null ? HandLightPolicy.allDefault() : handLight;
        vertexEpoch = vertexEpoch == null ? Optional.empty() : vertexEpoch;
        declaredAttributes = declaredAttributes == null ? java.util.Set.of() : java.util.Set.copyOf(declaredAttributes);
        Objects.requireNonNull(sink, "sink");
    }

    private static final UniformEventSink NO_SINK = new IdEventSinkRelay();

    /** IDs off, vanilla vertex formats. */
    public static IdPublication none() {
        return new IdPublication(Optional.empty(), IdIdentityMaps.EMPTY, NO_SINK,
                HandLightPolicy.allDefault(), Optional.empty(), java.util.Set.of());
    }
}
