// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/** GL capability limit, with its wire name (PHASE_5_DOC §2.2). */
public enum CapabilityLimit {
    MAX_DRAW_BUFFERS("maxDrawBuffers"),
    MAX_COLOR_ATTACHMENTS("maxColorAttachments"),
    MAX_TEXTURE_IMAGE_UNITS("maxTextureImageUnits");

    private final String wireName;

    CapabilityLimit(String wireName) {
        this.wireName = wireName;
    }

    public String wireName() {
        return wireName;
    }
}
