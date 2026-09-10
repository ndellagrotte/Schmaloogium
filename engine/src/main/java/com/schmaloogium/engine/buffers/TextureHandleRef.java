// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.gl.TextureHandle;

/** Closed ownership view over a texture handle: Phase 13-owned or borrowed from a producer. */
public sealed interface TextureHandleRef {
    record Owned(TextureHandle handle) implements TextureHandleRef {
    }

    record Borrowed(TextureHandle handle) implements TextureHandleRef {
    }
}
