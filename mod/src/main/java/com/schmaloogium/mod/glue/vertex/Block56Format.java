// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.vertex;

import com.schmaloogium.engine.vertex.Classic56Layout;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

/**
 * The Forge projection of the classic 56-byte record (PHASE_10_DOC §4.1 :403-409): the
 * five conventional semantics as fixed-function elements at their classic offsets and the
 * 24 extension bytes as PADDING, so Forge's {@code preDraw/postDraw} never enables an
 * array behind the plan's back and {@code nextVertexFormatIndex} skips them. Generic
 * pointers for {@code mc_midTexCoord@32}, {@code at_tangent@40} and {@code mc_Entity@48}
 * are issued only through the P1 facade. An immutable object distinct from
 * {@link DefaultVertexFormats#BLOCK}, which is never mutated (§4.8 step 4).
 */
public final class Block56Format {

    /** Position, colour, uv0, lightmap, normal, 1 B pad, 16 B pad, 8 B pad = 56 bytes. */
    public static final VertexFormat BLOCK_56 = build();

    /** Element index of each conventional semantic in {@link #BLOCK_56}. */
    public static final int POSITION_INDEX = 0;
    public static final int COLOR_INDEX = 1;
    public static final int UV0_INDEX = 2;
    public static final int UV1_INDEX = 3;
    public static final int NORMAL_INDEX = 4;

    private Block56Format() {
    }

    private static VertexFormat build() {
        VertexFormat format = new VertexFormat();
        format.addElement(DefaultVertexFormats.POSITION_3F);
        format.addElement(DefaultVertexFormats.COLOR_4UB);
        format.addElement(DefaultVertexFormats.TEX_2F);
        format.addElement(DefaultVertexFormats.TEX_2S);
        format.addElement(DefaultVertexFormats.NORMAL_3B);
        format.addElement(DefaultVertexFormats.PADDING_1B);
        format.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT,
                VertexFormatElement.EnumUsage.PADDING, 4)); // mc_midTexCoord@32, at_tangent@40
        format.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT,
                VertexFormatElement.EnumUsage.PADDING, 2)); // mc_Entity@48 + 2 B pad
        if (format.getSize() != Classic56Layout.STRIDE_BYTES
                || format.getUvOffsetById(1) != Classic56Layout.OFFSET_UV1
                || format.getNormalOffset() != Classic56Layout.OFFSET_NORMAL
                || format.getColorOffset() != Classic56Layout.OFFSET_COLOR) {
            throw new IllegalStateException("BLOCK_56 projection does not match Classic56Layout: "
                    + format.getSize());
        }
        return format;
    }

    /** True for the projection and any copy of it ({@code VertexFormat.equals} is structural). */
    public static boolean isExtended(VertexFormat format) {
        return format == BLOCK_56
                || (format != null && format.getSize() == Classic56Layout.STRIDE_BYTES
                        && BLOCK_56.equals(format));
    }
}
