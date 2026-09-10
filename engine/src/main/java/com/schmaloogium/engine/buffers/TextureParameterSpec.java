// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

import com.schmaloogium.engine.gl.TextureMagFilter;
import com.schmaloogium.engine.gl.TextureMinFilter;
import com.schmaloogium.engine.gl.TextureWrap;

/** The exact D-P13-27 texture parameter triple. */
public record TextureParameterSpec(TextureMinFilter minFilter, TextureMagFilter magFilter,
                                   TextureWrap wrap) {

    public TextureParameterSpec {
        java.util.Objects.requireNonNull(minFilter, "minFilter");
        java.util.Objects.requireNonNull(magFilter, "magFilter");
        java.util.Objects.requireNonNull(wrap, "wrap");
    }
}
