// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.Optional;

/** Per-attachment format/clear/clear-color requirement. */
public record ColorAttachmentRequirement(ColorAttachmentFormat format, boolean clear,
        Optional<Vec4f> clearColorOverride) {

    public ColorAttachmentRequirement {
        java.util.Objects.requireNonNull(format, "format");
        clearColorOverride = clearColorOverride == null ? Optional.empty() : clearColorOverride;
    }
}
