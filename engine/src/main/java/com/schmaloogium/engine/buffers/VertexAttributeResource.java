// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import java.util.Objects;

/** Vertex attribute consumed by one program (PHASE_5_DOC §2.2). */
public record VertexAttributeResource(String program, String name) {

    public VertexAttributeResource {
        Objects.requireNonNull(program, "program");
        if (program.isBlank()) {
            throw new IllegalArgumentException("blank vertex attribute program");
        }
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) {
            throw new IllegalArgumentException("blank vertex attribute name");
        }
    }
}
