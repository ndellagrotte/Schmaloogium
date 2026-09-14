// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.vertex;

import com.schmaloogium.engine.config.id.AliasLookup;
import com.schmaloogium.engine.config.id.BlockStampResult;
import com.schmaloogium.engine.log.Log;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.log.Logs;
import com.schmaloogium.engine.vertex.Classic56Layout;
import com.schmaloogium.engine.vertex.QuadAttributeWriter;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;

import java.nio.ByteBuffer;

/**
 * The builder-side ingress (PHASE_10_DOC §4.2–4.4) behind the {@code BufferBuilder},
 * {@code BlockRendererDispatcher} and {@code BlockModelRenderer} mixins: format
 * substitution at {@code begin}, block identity scopes, typed 7→14 int expansion for the
 * vanilla model paths, and quad finalization (normal, midpoint, tangent, identity) once
 * a quad's four records are complete. Runs on chunk workers: no GL, no render-thread state.
 */
public final class VertexIngress {

    private static final Log LOG = Logs.channel(LogChannels.FRAME);
    private static final int GL_QUADS = 7;
    private static final int BLOCK_INTS = 7;
    private static final int EXTENDED_INTS = Classic56Layout.STRIDE_BYTES / 4;
    private static volatile boolean firstStampLogged;
    private static volatile boolean partialQuadLogged;
    private static volatile boolean oddArrayLogged;

    private VertexIngress() {
    }

    /** H10-BEGIN: the format a builder actually installs for this {@code begin}. */
    public static VertexFormat beginFormat(VertexFormat requested) {
        if (requested != DefaultVertexFormats.BLOCK) {
            return requested;
        }
        ChunkTaskContext context = ChunkTaskContext.current();
        return context != null && context.extended() ? Block56Format.BLOCK_56 : requested;
    }

    /** After a successful {@code begin}: initialize the sidecar from the installed format. */
    public static void beginInit(BuilderSidecar builder, VertexFormat installed) {
        ChunkTaskContext context = ChunkTaskContext.current();
        boolean extended = context != null && context.extended() && Block56Format.isExtended(installed);
        builder.schmaloogium$setExtended(extended, extended ? context.publication().epoch().serial() : 0L);
        builder.schmaloogium$setFinalizedThrough(0);
    }

    /** H10-RESET: the builder is reused; nothing extended survives. */
    public static void reset(BuilderSidecar builder) {
        builder.schmaloogium$setExtended(false, 0L);
        builder.schmaloogium$setFinalizedThrough(0);
    }

    /** H10-BLOCK enter: the identity words of the block about to render. */
    public static void blockEnter(IBlockState state) {
        ChunkTaskContext context = ChunkTaskContext.current();
        if (context == null || !context.extended()) {
            return;
        }
        VertexEpochs.Publication publication = context.publication();
        BlockStampResult stamp = null;
        int ordinal = publication.maps().stateOrdinal(state);
        AliasLookup aliases = publication.aliases();
        if (ordinal >= 0 && aliases != null) {
            try {
                stamp = aliases.mcEntity(ordinal);
            } catch (RuntimeException retired) {
                stamp = null; // a retired lookup stamps neutral; the epoch swap rebuilds anyway
            }
        }
        if (stamp == null) {
            int renderType = state == null ? 0 : state.getRenderType().ordinal() & 0xffff;
            stamp = new BlockStampResult.Absent(renderType << 16, 0);
        }
        context.pushStamp(stamp);
        if (!firstStampLogged) {
            firstStampLogged = true;
            LOG.info("H10-BLOCK-01 first stamp: {} ordinal {} -> {}", state, ordinal, stamp);
        }
    }

    /** H10-BLOCK exit. */
    public static void blockExit() {
        ChunkTaskContext context = ChunkTaskContext.current();
        if (context != null) {
            context.popStamp();
        }
    }

    /**
     * H10-MODEL-ARRAY (typed): the vanilla model paths append {@code BakedQuad} data in the
     * 7-int BLOCK record; under the extended format each record widens to 14 ints (the
     * first seven identical, the extension zero) before the ordinary append.
     */
    public static void addBlockFormatQuads(BufferBuilder buffer, int[] data) {
        if (!(buffer instanceof BuilderSidecar sidecar) || !sidecar.schmaloogium$extended()) {
            buffer.addVertexData(data);
            return;
        }
        if (data.length % BLOCK_INTS != 0) {
            if (!oddArrayLogged) {
                oddArrayLogged = true;
                LOG.warn("H10-MODEL-ARRAY: {}-int quad data is not BLOCK records; appended unexpanded", data.length);
            }
            buffer.addVertexData(data);
            return;
        }
        int vertices = data.length / BLOCK_INTS;
        int[] expanded = new int[vertices * EXTENDED_INTS];
        for (int v = 0; v < vertices; v++) {
            System.arraycopy(data, v * BLOCK_INTS, expanded, v * EXTENDED_INTS, BLOCK_INTS);
        }
        buffer.addVertexData(expanded);
    }

    /**
     * Finalizes every complete quad not yet finalized (after {@code endVertex}, the untyped
     * bulk appends and the typed expansion): derives normal/midpoint/tangent from the four
     * records and stamps the innermost block identity. Quads are grouped from vertex zero
     * of the {@code begin} (§4.3 "primitive boundaries").
     */
    public static void finalizeCompleteQuads(BuilderSidecar builder) {
        if (!builder.schmaloogium$extended() || builder.schmaloogium$drawMode() != GL_QUADS) {
            return;
        }
        int count = builder.schmaloogium$vertexCount();
        int done = builder.schmaloogium$finalizedThrough();
        int complete = count - (count % 4);
        if (complete <= done) {
            return;
        }
        ChunkTaskContext context = ChunkTaskContext.current();
        BlockStampResult stamp = context == null ? new BlockStampResult.Absent(0, 0) : context.stamp();
        ByteBuffer bytes = builder.schmaloogium$byteBuffer();
        for (int base = done; base < complete; base += 4) {
            try {
                QuadAttributeWriter.finalizeQuad(bytes, base * Classic56Layout.STRIDE_BYTES,
                        Classic56Layout.STRIDE_BYTES, stamp);
            } catch (IllegalArgumentException malformed) {
                // Non-finite stored positions: the quad keeps zero attributes (§4.3).
            }
        }
        builder.schmaloogium$setFinalizedThrough(complete);
    }

    /** H10-BLOCK exit / seal check: a partial quad at a boundary is malformed (logged once). */
    public static void notePartialQuadAtBoundary(BuilderSidecar builder, String boundary) {
        if (builder.schmaloogium$extended() && builder.schmaloogium$vertexCount() % 4 != 0
                && !partialQuadLogged) {
            partialQuadLogged = true;
            LOG.warn("H10-SEAL: partial quad at {} ({} vertices)", boundary, builder.schmaloogium$vertexCount());
        }
    }
}
