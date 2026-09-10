// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.config.ColorAttachmentFormat;

import java.util.Objects;
import java.util.Optional;

/** One color buffer resource: requested format, realized allocation, clear behavior (PHASE_5_DOC §2.2). */
public record ColorBufferResource(ColorAttachmentFormat requestedFormat,
        Optional<RealizedColorAllocation> allocation, boolean clear, ResourceClearPolicy clearPolicy) {

    public ColorBufferResource {
        requestedFormat = Objects.requireNonNull(requestedFormat, "requestedFormat");
        allocation = allocation == null ? Optional.empty() : allocation;
        clearPolicy = Objects.requireNonNull(clearPolicy, "clearPolicy");
    }
}
