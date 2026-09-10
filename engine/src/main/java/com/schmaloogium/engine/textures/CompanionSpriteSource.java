// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.buffers.CompanionKind;

import java.util.Objects;

/**
 * One sprite's companion source for one kind. Discovery is total: every base sprite yields
 * exactly one of these per enabled kind — Resource or DefaultFill, never an absent state
 * (§4.1.2).
 */
public record CompanionSpriteSource(CompanionKind kind, String iconName, CompanionOrigin origin) {
    public CompanionSpriteSource {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(iconName, "iconName");
        if (iconName.isEmpty()) {
            throw new IllegalArgumentException("iconName must be non-empty");
        }
        Objects.requireNonNull(origin, "origin");
    }
}
