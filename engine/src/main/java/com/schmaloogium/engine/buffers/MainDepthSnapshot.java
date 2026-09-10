// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.gl.BorrowedDepthAttachmentHandle;
import com.schmaloogium.engine.gl.DepthAttachmentFormat;

/**
 * Closed main-depth snapshot. {@code version} changes whenever the underlying texture identity,
 * format, extent, or availability changes; it is equality-only.
 */
public sealed interface MainDepthSnapshot {
    record Available(long version, BorrowedDepthAttachmentHandle texture,
                     DepthAttachmentFormat format, Extent2i extent)
        implements MainDepthSnapshot {
    }

    record Unavailable(long version, String diagnosticId) implements MainDepthSnapshot {
    }
}
