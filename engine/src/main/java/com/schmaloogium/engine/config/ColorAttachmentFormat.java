// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

/** Attachment format algebra: unsized default vs explicit. */
public sealed interface ColorAttachmentFormat
        permits ColorAttachmentFormat.DefaultRgba, ColorAttachmentFormat.Explicit {

    /** Unsized plain-RGBA baseline; distinct from explicit {@code RGBA8}. */
    record DefaultRgba() implements ColorAttachmentFormat {
    }

    /** One explicit 37-value internal format. */
    record Explicit(ColorInternalFormat format) implements ColorAttachmentFormat {

        public Explicit {
            java.util.Objects.requireNonNull(format, "format");
        }
    }
}
