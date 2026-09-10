// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.gl.TextureHandle;

import java.util.Objects;

/**
 * One color attachment row of a pass snapshot (PHASE_5_DOC §2.2).
 */
public record ColorAttachment(
        int outputOrdinal,
        int framebufferAttachment,
        LogicalBuffer logicalBuffer,
        TextureHandle physicalTexture) {

    public ColorAttachment {
        logicalBuffer = Objects.requireNonNull(logicalBuffer, "logicalBuffer");
        physicalTexture = Objects.requireNonNull(physicalTexture, "physicalTexture");
    }
}
