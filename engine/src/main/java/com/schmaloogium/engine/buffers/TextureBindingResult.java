// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/** Closed texture-binding acquisition result. */
public sealed interface TextureBindingResult {
    record Bound(TextureBindingSnapshot snapshot) implements TextureBindingResult {
    }

    record Degraded(TextureBindingDegradation degradation) implements TextureBindingResult {
    }

    record Rejected(TextureBindingRejection reason) implements TextureBindingResult {
    }

    record BackendFailed(BufferFailure failure) implements TextureBindingResult {
    }
}
