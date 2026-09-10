// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow;

import com.schmaloogium.engine.buffers.LogicalBuffer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * The Phase-8-local hardware-PCF policy (PHASE_8_DOC §2.2): the shadow depth buffers that
 * carry compare sampling. Construction accepts any duplicate-free set and stores it in
 * canonical {@code (domain, index)} order; domain membership ({@code {shadow depth 0,
 * shadow depth 1}} only — any shadowcolor member is invalid policy) is validated by the
 * plan factory in its listed policy-check order, not here.
 */
public record ShadowPcfPolicy(Set<LogicalBuffer> compareDepthBuffers) {

    private static final Comparator<LogicalBuffer> CANONICAL_ORDER =
            Comparator.comparing((LogicalBuffer buffer) -> buffer.domain())
                    .thenComparingInt(buffer -> buffer.index().value());

    public ShadowPcfPolicy {
        Set<LogicalBuffer> copy = new TreeSet<>(CANONICAL_ORDER);
        for (LogicalBuffer buffer : compareDepthBuffers) {
            copy.add(java.util.Objects.requireNonNull(buffer, "compareDepthBuffers"));
        }
        compareDepthBuffers = Set.copyOf(copy);
    }

    /** The canonical ASCII/logical member list (declared order, ascending). */
    public List<LogicalBuffer> canonicalMembers() {
        return List.copyOf(new ArrayList<>(compareDepthBuffers));
    }
}
