// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.vertex;

import net.minecraft.client.renderer.BufferBuilder;

/**
 * The render-thread hand-off between {@code VertexBufferUploader.draw} (which resets the
 * builder before uploading) and {@code VertexBuffer.bufferData} (which only sees bytes):
 * the descriptor of the product being uploaded (H10-UPLOAD-DESCRIPTOR / H10-VBO-UPLOAD).
 */
public final class VertexUploads {

    /** What the builder being uploaded carried. */
    public record Pending(boolean extended, long serial) {
    }

    private static Pending pending;

    private VertexUploads() {
    }

    public static void begin(BufferBuilder builder) {
        boolean extended = Block56Format.isExtended(builder.getVertexFormat());
        long serial = builder instanceof BuilderSidecar sidecar ? sidecar.schmaloogium$serial() : 0L;
        if (extended && serial == 0L) {
            VertexEpochs.Publication current = VertexEpochs.current();
            serial = current == null ? 0L : current.epoch().serial(); // a resorted state copy
        }
        pending = new Pending(extended, serial);
    }

    public static void end() {
        pending = null;
    }

    /** The descriptor of the upload in progress, or null outside one. */
    public static Pending pending() {
        return pending;
    }
}
