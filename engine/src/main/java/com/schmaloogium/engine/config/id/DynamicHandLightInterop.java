// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

/**
 * Optional interop with an explicitly installed external dynamic-lights provider
 * (PHASE_9_DOC §4.11). When {@code dynamicHandLight=false}, glue asks a recognized bridge
 * to suppress that provider's contribution during the Phase 7 hand scope. Phase 9 never
 * changes world light, creates a light source, or treats the static held-light uniforms
 * as dynamic lights; absence or failure of the bridge is a feature-level diagnostic and
 * no-op.
 */
@FunctionalInterface
public interface DynamicHandLightInterop {

    /**
     * Requests suppression for the current hand scope.
     *
     * @return true when a recognized provider acknowledged the suppression request
     */
    boolean suppressDuringHandScope();
}
