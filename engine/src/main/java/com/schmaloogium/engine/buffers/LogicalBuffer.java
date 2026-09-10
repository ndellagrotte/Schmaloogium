// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.registry.BufferDomain;

import java.util.Objects;

/**
 * Domain-scoped logical buffer identity (PHASE_5_DOC §2.2): a registry
 * {@link BufferDomain} plus its {@link BufferIndex}.
 */
public record LogicalBuffer(BufferDomain domain, BufferIndex index) {

    public LogicalBuffer {
        Objects.requireNonNull(domain, "domain");
        Objects.requireNonNull(index, "index");
    }
}
