// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T10-LAYOUT layout vectors (PHASE_10_DOC §4.1, §8): the CLASSIC_56 field table,
 * semantic destinations, gapless decomposition, fingerprint content identity, and the
 * fixed correspondence between Phase 4's published rows and the declared names.
 */
class Classic56LayoutTest {

    @Test
    void classicLayoutIsExactlyFiftySixBytes() {
        VertexLayout layout = Classic56Layout.layout();
        assertEquals("CLASSIC_56", layout.id());
        assertEquals(56, layout.strideBytes());
        assertEquals(56, Classic56Layout.STRIDE_BYTES);
    }

    @Test
    void fieldTableMatchesTheSectionFourOneTable() {
        VertexLayout layout = Classic56Layout.layout();
        assertField(layout, "position", 0, 3, StorageType.FLOAT32, Delivery.FIXED_FUNCTION);
        assertField(layout, "color", 12, 4, StorageType.UINT8, Delivery.FIXED_FUNCTION);
        assertField(layout, "uv0", 16, 2, StorageType.FLOAT32, Delivery.FIXED_FUNCTION);
        assertField(layout, "lightmap", 24, 2, StorageType.INT16, Delivery.FIXED_FUNCTION);
        assertField(layout, "normal", 28, 3, StorageType.INT8, Delivery.FIXED_FUNCTION);
        assertField(layout, "paddingNormal", 31, 1, StorageType.UINT8, Delivery.PADDING);
        assertField(layout, "mc_midTexCoord", 32, 2, StorageType.FLOAT32, Delivery.FLOAT_VALUE);
        assertField(layout, "at_tangent", 40, 4, StorageType.INT16, Delivery.NORMALIZED_FLOAT);
        assertField(layout, "mc_Entity", 48, 3, StorageType.INT16, Delivery.FLOAT_VALUE);
        assertField(layout, "paddingIdentity", 54, 2, StorageType.UINT8, Delivery.PADDING);
    }

    @Test
    void identityIsFloatingDeliveryNotNormalizedNotInteger() {
        VertexField identity = Classic56Layout.field(Classic56Layout.layout(), "mc_Entity");
        assertEquals(Delivery.FLOAT_VALUE, identity.delivery());
        assertEquals(StorageType.INT16, identity.storage());
        // at_tangent is the only normalized pointer of the classic floor.
        assertEquals(Delivery.NORMALIZED_FLOAT,
                Classic56Layout.field(Classic56Layout.layout(), "at_tangent").delivery());
        assertEquals(Delivery.FLOAT_VALUE,
                Classic56Layout.field(Classic56Layout.layout(), "mc_midTexCoord").delivery());
    }

    @Test
    void decompositionIsGaplessAndWithinStride() {
        int expectedOffset = 0;
        for (VertexField field : Classic56Layout.fields()) {
            assertEquals(expectedOffset, field.byteOffset(), field.name());
            expectedOffset = field.byteOffset()
                    + field.components() * switch (field.storage()) {
                case FLOAT32 -> 4;
                case UINT8, INT8 -> 1;
                case INT16 -> 2;
            };
        }
        assertEquals(Classic56Layout.STRIDE_BYTES, expectedOffset);
    }

    @Test
    void fingerprintIdentifiesContentAndChangesWithIt() {
        VertexLayout classic = Classic56Layout.layout();
        VertexLayout again = Classic56Layout.grow().build("RENAMED");
        // Same content, different id: identical fingerprint — it identifies content.
        assertEquals(classic.fingerprint(), again.fingerprint());

        VertexLayout grown = Classic56Layout.grow()
                .append("at_midBlock", 3, StorageType.INT8, Delivery.FLOAT_VALUE)
                .build("CLASSIC_56_GROWN");
        assertNotEquals(classic.fingerprint(), grown.fingerprint());
        assertNotEquals(grown.fingerprint(), new Object());
        assertTrue(grown.fingerprint().length() == 64);
    }

    @Test
    void semanticDestinationsMatchTheClassicTable() {
        assertEquals(0, ClassicSemantic.POSITION.byteOffset());
        assertEquals(12, ClassicSemantic.COLOR.byteOffset());
        assertEquals(16, ClassicSemantic.UV0.byteOffset());
        assertEquals(24, ClassicSemantic.UV1.byteOffset());
        assertEquals(28, ClassicSemantic.NORMAL.byteOffset());
        assertEquals(12, ClassicSemantic.POSITION.byteSize());
        assertEquals(3, ClassicSemantic.NORMAL.byteSize());
    }

    @Test
    void missingFieldNameYieldsNull() {
        assertNull(Classic56Layout.field(Classic56Layout.layout(), "at_midBlock"));
    }

    private static void assertField(VertexLayout layout, String name, int offset,
                                    int components, StorageType storage, Delivery delivery) {
        VertexField field = Classic56Layout.field(layout, name);
        assertEquals(offset, field.byteOffset(), name);
        assertEquals(components, field.components(), name);
        assertEquals(storage, field.storage(), name);
        assertEquals(delivery, field.delivery(), name);
    }
}
