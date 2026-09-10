// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.frame;

import com.schmaloogium.engine.buffers.Extent2i;

/**
 * The one-frame summary returned by a winning {@code finish} and carried into capture views
 * (PHASE_7_DOC §5.1). Immutable; the driver mints it after the Phase-5 commit succeeded.
 */
public record FinalizedFrame(
        long frameId,
        PipelineIdentity identity,
        PipelineVersion version,
        Extent2i extent,
        AnaglyphEye eye,
        FrameReadiness readiness) {

    public FinalizedFrame {
        java.util.Objects.requireNonNull(identity, "identity");
        java.util.Objects.requireNonNull(version, "version");
        java.util.Objects.requireNonNull(extent, "extent");
        java.util.Objects.requireNonNull(eye, "eye");
        java.util.Objects.requireNonNull(readiness, "readiness");
    }
}
