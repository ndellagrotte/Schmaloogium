// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.vertex;

import com.schmaloogium.engine.log.Log;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.log.Logs;
import com.schmaloogium.engine.registry.ExtendedAttribute;
import com.schmaloogium.engine.vertex.Classic56Layout;
import com.schmaloogium.engine.vertex.ClassicVertexCodec;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Set;

/**
 * {@code -Dschmaloogium.debug.probeBuffers=true}: reads back, right before the first
 * extended draw, the generic pointer state of locations 10/11/12 and the fixed-function
 * strides, and decodes the first quad of the bound VBO through the classic codec — the
 * GL-side truth the Task D/E lessons demand before theorising.
 */
final class VertexGlProbe {

    private static final Log LOG = Logs.channel(LogChannels.FRAME);

    private VertexGlProbe() {
    }

    static void probeExtendedDraw(int vertexCount, Set<ExtendedAttribute> declared) {
        try {
            StringBuilder sb = new StringBuilder();
            for (ExtendedAttribute a : ExtendedAttribute.values()) {
                int loc = a.location();
                sb.append(loc).append(':').append(a.name().toLowerCase())
                        .append("[enabled=").append(GL20.glGetVertexAttribi(loc, GL20.GL_VERTEX_ATTRIB_ARRAY_ENABLED))
                        .append(" size=").append(GL20.glGetVertexAttribi(loc, GL20.GL_VERTEX_ATTRIB_ARRAY_SIZE))
                        .append(" type=0x").append(Integer.toHexString(GL20.glGetVertexAttribi(loc, GL20.GL_VERTEX_ATTRIB_ARRAY_TYPE)))
                        .append(" normalized=").append(GL20.glGetVertexAttribi(loc, GL20.GL_VERTEX_ATTRIB_ARRAY_NORMALIZED))
                        .append(" stride=").append(GL20.glGetVertexAttribi(loc, GL20.GL_VERTEX_ATTRIB_ARRAY_STRIDE))
                        .append("] ");
            }
            int vertexStride = GL11.glGetInteger(GL11.GL_VERTEX_ARRAY_STRIDE);
            int colorStride = GL11.glGetInteger(GL11.GL_COLOR_ARRAY_STRIDE);
            int normalEnabled = GL11.glIsEnabled(GL11.GL_NORMAL_ARRAY) ? 1 : 0;
            int normalStride = GL11.glGetInteger(GL11.GL_NORMAL_ARRAY_STRIDE);
            int program = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
            String quad = "(no vertices)";
            if (vertexCount >= 4) {
                ByteBuffer bytes = ByteBuffer.allocateDirect(4 * Classic56Layout.STRIDE_BYTES)
                        .order(ByteOrder.nativeOrder());
                GL15.glGetBufferSubData(GL15.GL_ARRAY_BUFFER, 0, bytes);
                StringBuilder q = new StringBuilder();
                for (int v = 0; v < 4; v++) {
                    int base = v * Classic56Layout.STRIDE_BYTES;
                    q.append("v").append(v)
                            .append(" pos=").append(Arrays.toString(ClassicVertexCodec.getPosition(bytes, base)))
                            .append(" uv=").append(Arrays.toString(ClassicVertexCodec.getUv0(bytes, base)))
                            .append(" n=").append(Arrays.toString(ClassicVertexCodec.getNormalBytes(bytes, base)))
                            .append(" mid=").append(Arrays.toString(ClassicVertexCodec.getMidTexCoord(bytes, base)))
                            .append(" tan=").append(Arrays.toString(ClassicVertexCodec.getTangentShorts(bytes, base)))
                            .append(" ent=").append(Arrays.toString(ClassicVertexCodec.getIdentityComponents(bytes, base)))
                            .append("; ");
                }
                quad = q.toString();
            }
            LOG.info("H10-PROBE first extended draw: program {} declared {} generic {} ff[vertexStride={} colorStride={} normal={}/{}] firstQuad {}",
                    program, declared, sb.toString().trim(), vertexStride, colorStride, normalEnabled,
                    normalStride, quad);
        } catch (RuntimeException e) {
            LOG.warn("H10-PROBE failed: {}", e.toString());
        } finally {
            while (GL11.glGetError() != GL11.GL_NO_ERROR) {
                // probe queries are never charged to the pass
            }
        }
    }
}
