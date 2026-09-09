// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

/** Closed global {@code optionsshaders.txt} codec. */
public interface GlobalShaderOptionsCodec {

    GlobalShaderOptionsReadResult read(GlobalShaderOptionsReadRequest request);

    GlobalShaderOptionsWriteResult write(GlobalShaderOptionsWriteRequest request);
}
