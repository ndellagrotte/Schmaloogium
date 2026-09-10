// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers;

/** Closed atlas context of a candidate's base texture (D-P5-50). */
public sealed interface BaseAtlasContext {
    record Atlas(AtlasId atlas) implements BaseAtlasContext {
    }

    record NonAtlas() implements BaseAtlasContext {
    }

    record Unavailable() implements BaseAtlasContext {
    }
}
