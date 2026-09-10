// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import java.util.Objects;

/** Instanced draw demand of one program (PHASE_5_DOC §2.2). */
public record InstanceResource(String program, int count) {

    public InstanceResource {
        Objects.requireNonNull(program, "program");
        if (program.isBlank()) {
            throw new IllegalArgumentException("blank instance resource program");
        }
        if (count < 1) {
            throw new IllegalArgumentException("instance count below one: " + count);
        }
    }
}
