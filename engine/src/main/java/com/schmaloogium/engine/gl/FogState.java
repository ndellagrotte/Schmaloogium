// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import java.util.Objects;

/**
 * An enabled fog configuration (PHASE_1_DOC §4.7.4, D-P1-31): mode, linear range,
 * exponential density and fog color. Absence (null) is the disabled state, per
 * {@link StateService#fog}; a present value is always an enabled fog. No GL constants
 * appear in any signature.
 */
public record FogState(FogMode mode, float start, float end, float density,
                       float red, float green, float blue, float alpha) {

    public FogState {
        Objects.requireNonNull(mode, "mode");
    }

    /** Closed engine fog-mode vocabulary; the backend maps these to native values. */
    public enum FogMode {
        LINEAR,
        EXP,
        EXP2
    }
}
