// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.Objects;

/** The fourteen view-entity booleans exposed to expressions (Appendix F.6). */
public record ViewEntityFlags(
        boolean isAlive, boolean isBurning, boolean isChild, boolean isGlowing,
        boolean isHurt, boolean isInLava, boolean isInWater, boolean isInvisible,
        boolean isOnGround, boolean isRidden, boolean isRiding, boolean isSneaking,
        boolean isSprinting, boolean isWet) {

    public ViewEntityFlags {
        Objects.requireNonNull(this);
    }
}
