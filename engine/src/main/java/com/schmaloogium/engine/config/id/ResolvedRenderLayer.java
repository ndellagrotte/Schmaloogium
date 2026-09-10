// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.config.RequestedRenderLayer;

/** The four resolved custom render layers (PHASE_9_DOC §2.2/§4.9). */
public enum ResolvedRenderLayer {
    SOLID, CUTOUT, CUTOUT_MIPPED, TRANSLUCENT;

    /** The resolved layer for one requested layer rule value. */
    public static ResolvedRenderLayer of(RequestedRenderLayer requested) {
        return switch (requested) {
            case SOLID -> SOLID;
            case CUTOUT -> CUTOUT;
            case CUTOUT_MIPPED -> CUTOUT_MIPPED;
            case TRANSLUCENT -> TRANSLUCENT;
        };
    }
}
