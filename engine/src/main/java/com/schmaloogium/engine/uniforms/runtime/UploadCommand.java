// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.runtime;

import com.schmaloogium.engine.gl.UniformLocation;

import java.util.Objects;

/**
 * One attempted-batch entry (PHASE_6_DOC §4.11): uniform name, typed value, cached
 * location and the upload operation. Only commands that will actually be attempted enter
 * a batch; the list itself is the immutable snapshot executed by the §4.11 protocol.
 * Immutable.
 */
public record UploadCommand(String name, UploadKind kind, UniformValue value,
        UniformLocation location) {

    public UploadCommand {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(location, "location");
    }
}
