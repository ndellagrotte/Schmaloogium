// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Opaque snapshot of the {@link StateService} aspects named at
 * {@link StateService#snapshot} time, for the §G4.6 restore discipline
 * (PHASE_1_DOC §4.7.4). Backend-implemented and device-scoped: only the same backend's
 * {@link StateService#restore} accepts it. It covers only the declared aspects and does
 * NOT save programs (§4.7.4a).
 */
public interface StateSnapshot {
}
