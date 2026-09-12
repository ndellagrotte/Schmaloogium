// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.StateAspect;
import com.schmaloogium.engine.gl.StateSnapshot;

import java.util.Map;

/**
 * The device's shadow-state snapshot ([D-P1-52]): the captured actual values of the
 * requested aspects. Restore writes each captured aspect through the device's GLSM
 * sanctioned path - the backend restorer reads its own captured values, never GLSM's
 * private map, so no state-shadow workarounds occur during restore.
 */
final class Lwjgl3StateSnapshot implements StateSnapshot {

    private final Lwjgl3GLDevice device;
    private final Map<StateAspect, Object> captured;

    Lwjgl3StateSnapshot(Lwjgl3GLDevice device, Map<StateAspect, Object> captured) {
        this.device = device;
        // Null values encode "disabled" for BLEND/ALPHA_TEST/FOG; Map.copyOf rejects them.
        this.captured = java.util.Collections.unmodifiableMap(new java.util.EnumMap<>(captured));
    }

    Lwjgl3GLDevice device() {
        return device;
    }

    void restore() {
        for (Map.Entry<StateAspect, Object> e : captured.entrySet()) {
            device.stateWrite(e.getKey(), e.getValue());
        }
    }
}
