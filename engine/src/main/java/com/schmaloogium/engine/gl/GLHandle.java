// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

/**
 * Marker for every GL object the engine holds. Never an int (PHASE_1_DOC §4.7.3).
 *
 * <p>Sealed — and this is the one sealing that means something: the permits list IS the
 * statement that there are exactly four handle categories and no renderbuffer (§12 item 18).
 * The four leaves are deliberately {@code non-sealed}: backends
 * ({@code mod.glue.Lwjgl3GLDevice}, {@code engine.gl.record.RecordingGLDevice}, mod glue
 * foreign handles) must implement them from outside this package, and a sealed type in the
 * unnamed module cannot admit subtypes from another package. Opaqueness lives in these
 * interfaces exposing no accessor that returns a GL name, plus the bytecode scan
 * ({@code SeamBytecodeTest}, §4.3).
 */
public sealed interface GLHandle permits
        ProgramHandle, ShaderHandle, TextureHandle, FramebufferHandle {
}
