// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

import java.util.List;

/**
 * The immutable 56-byte / 14-int classic vertex layout (PHASE_10_DOC §4.1). Every
 * decoder, pointer builder and writer uses the named fields below; only this
 * declaration and the byte-oracle fixtures contain the classic offsets. The two
 * padding fields exist so the decomposition is gapless: padding is always written
 * zero and is never a fourth identity component or an enabled FF index.
 *
 * <p>Identity delivery is the floating-input bit-pattern contract:
 * {@code glVertexAttribPointer(10, 3, GL_SHORT, false, 56, 48)} — never an
 * integer-input pointer and never normalized shorts. {@code mc_Entity} therefore
 * carries {@link Delivery#FLOAT_VALUE} over signed-short storage, and 65535 and −1
 * both store {@code 0xffff}, observed as −1.0.
 */
public final class Classic56Layout {

    /** The immutable classic stride in bytes. */
    public static final int STRIDE_BYTES = 56;

    /** Ordinary and extended element offsets (PHASE_10_DOC §4.1 table). */
    public static final int OFFSET_POSITION = ClassicSemantic.POSITION.byteOffset();
    public static final int OFFSET_COLOR = ClassicSemantic.COLOR.byteOffset();
    public static final int OFFSET_UV0 = ClassicSemantic.UV0.byteOffset();
    public static final int OFFSET_UV1 = ClassicSemantic.UV1.byteOffset();
    public static final int OFFSET_NORMAL = ClassicSemantic.NORMAL.byteOffset();
    public static final int OFFSET_MID_TEX_COORD = 32;
    public static final int OFFSET_TANGENT = 40;
    public static final int OFFSET_IDENTITY = 48;

    private static final List<VertexField> FIELDS = List.of(
            new VertexField("position", 0, 3, StorageType.FLOAT32, Delivery.FIXED_FUNCTION),
            new VertexField("color", 12, 4, StorageType.UINT8, Delivery.FIXED_FUNCTION),
            new VertexField("uv0", 16, 2, StorageType.FLOAT32, Delivery.FIXED_FUNCTION),
            new VertexField("lightmap", 24, 2, StorageType.INT16, Delivery.FIXED_FUNCTION),
            new VertexField("normal", 28, 3, StorageType.INT8, Delivery.FIXED_FUNCTION),
            new VertexField("paddingNormal", 31, 1, StorageType.UINT8, Delivery.PADDING),
            new VertexField("mc_midTexCoord", 32, 2, StorageType.FLOAT32, Delivery.FLOAT_VALUE),
            new VertexField("at_tangent", 40, 4, StorageType.INT16, Delivery.NORMALIZED_FLOAT),
            new VertexField("mc_Entity", 48, 3, StorageType.INT16, Delivery.FLOAT_VALUE),
            new VertexField("paddingIdentity", 54, 2, StorageType.UINT8, Delivery.PADDING));

    private static final VertexLayout LAYOUT = new VertexLayout(
            "CLASSIC_56", STRIDE_BYTES, FIELDS, LayoutFingerprints.of(STRIDE_BYTES, FIELDS));

    private Classic56Layout() {
    }

    /** The immutable CLASSIC_56 layout; always 56 bytes (PHASE_10_DOC §5.1). */
    public static VertexLayout layout() {
        return LAYOUT;
    }

    /** The ordered classic field descriptors; callers must not assume this list is mutable. */
    public static List<VertexField> fields() {
        return FIELDS;
    }

    /** The named field, or {@code null} when the layout does not carry it. */
    public static VertexField field(VertexLayout layout, String name) {
        for (VertexField field : layout.fields()) {
            if (field.name().equals(name)) {
                return field;
            }
        }
        return null;
    }

    /** A builder seeded with the complete classic floor, for post-v0.5 growth layouts. */
    public static VertexLayoutBuilder grow() {
        return VertexLayoutBuilder.seeded(FIELDS, STRIDE_BYTES);
    }
}
