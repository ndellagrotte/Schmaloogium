// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.gl.VertexSource;

import java.nio.ByteBuffer;

/**
 * Issuance of authentic vertex sources for the LWJGL backend (Task F; the P10 adapter the
 * v0.1 facade anticipated). A source carries the issuer token privately, so only sources
 * minted here pass {@code vertexInputs().bind}. Ranges are {@code [offset, offset+length)}
 * of the underlying object: a borrowed VBO's range is measured from the start of the
 * buffer object currently bound to {@code GL_ARRAY_BUFFER}; a client range's from the
 * start of the given buffer's backing memory (the caller passes the same buffer to the
 * draw).
 */
public final class VertexSources {

    private VertexSources() {
    }

    /** The buffer object the caller has bound; {@code byteLength} bytes hold {@code vertexCount} records. */
    public static VertexSource.BorrowedVbo borrowedVbo(GLDevice device, long byteLength, int vertexCount) {
        return new Lwjgl3VertexInputService.IssuedBorrowedVbo(owner(device),
                Lwjgl3VertexInputService.IssuedKind.BORROWED_VBO, 0L, byteLength, vertexCount, null);
    }

    /** Client memory: pointers address {@code bytes} from {@code byteOffset}. */
    public static VertexSource.ClientRange clientRange(GLDevice device, ByteBuffer bytes,
                                                       long byteOffset, long byteLength,
                                                       int vertexCount) {
        return new Lwjgl3VertexInputService.IssuedClientRange(owner(device),
                Lwjgl3VertexInputService.IssuedKind.CLIENT_RANGE, byteOffset, byteLength,
                vertexCount, bytes);
    }

    /** A replay guard scope for a compiled display list (no pointers; current values only). */
    public static VertexSource.DisplayListReplay displayListReplay(GLDevice device) {
        return new Lwjgl3VertexInputService.IssuedDisplayListReplay(owner(device),
                Lwjgl3VertexInputService.IssuedKind.DISPLAY_LIST_REPLAY, 0L, 0L, 0, null);
    }

    /** Retires a source so any later bind is {@code STALE_SOURCE}. */
    public static void retire(VertexSource source) {
        if (source instanceof Lwjgl3VertexInputService.IssuedSource issued) {
            issued.retire();
        }
    }

    private static Lwjgl3GLDevice owner(GLDevice device) {
        if (!(device instanceof Lwjgl3GLDevice lwjgl)) {
            throw new IllegalArgumentException("vertex sources are issued for the LWJGL device only");
        }
        return lwjgl;
    }
}
