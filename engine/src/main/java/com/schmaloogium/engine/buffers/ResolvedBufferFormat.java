// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.gl.ColorInternalFormat;
import com.schmaloogium.engine.gl.DepthAttachmentFormat;

import java.util.Objects;

/** Resolved GPU format for one buffer role: color or depth (PHASE_5_DOC §2.2). */
public sealed interface ResolvedBufferFormat permits ResolvedBufferFormat.Color, ResolvedBufferFormat.Depth {

    /** Resolved color internal format. */
    record Color(ColorInternalFormat value) implements ResolvedBufferFormat {

        public Color {
            Objects.requireNonNull(value, "value");
        }
    }

    /** Resolved depth attachment format. */
    record Depth(DepthAttachmentFormat value) implements ResolvedBufferFormat {

        public Depth {
            Objects.requireNonNull(value, "value");
        }
    }
}
