// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Flip buffer identity; no program-name variant. */
public record FlipBufferKey(ColorAttachmentKey attachment) {

    public FlipBufferKey {
        java.util.Objects.requireNonNull(attachment, "attachment");
    }
}
