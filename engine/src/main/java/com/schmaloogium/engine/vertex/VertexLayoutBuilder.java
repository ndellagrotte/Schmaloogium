// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Layout/producer extensibility infrastructure (PHASE_10_DOC §4.10, §9 v0.3 row):
 * appends aligned named fields, validates no overlap, and derives the stride and the
 * content fingerprint. Existing consumers keep iterating descriptors and semantic
 * names — none may assume 14 ints outside the {@code CLASSIC_56} declaration and its
 * classic oracles. Implicit alignment gaps stay non-FF padding owned by the
 * descriptors, never enabled inputs.
 */
public final class VertexLayoutBuilder {

    private final List<VertexField> fields = new ArrayList<>();
    private final Set<String> names = new HashSet<>();
    private int cursor;

    private VertexLayoutBuilder() {
    }

    /** Seeds a builder with existing sequential fields continuing at {@code startOffset}. */
    public static VertexLayoutBuilder seeded(List<VertexField> seed, int startOffset) {
        VertexLayoutBuilder builder = new VertexLayoutBuilder();
        int previousEnd = 0;
        for (VertexField field : seed) {
            builder.validateField(field);
            if (field.byteOffset() < previousEnd) {
                throw new IllegalArgumentException("seed field overlaps its predecessor: "
                        + field.name());
            }
            previousEnd = field.byteOffset()
                    + field.components() * storageSize(field.storage());
            builder.fields.add(field);
            builder.names.add(field.name());
        }
        builder.cursor = Math.max(startOffset, previousEnd);
        return builder;
    }

    /** Appends one named field at the next alignment boundary of its storage. */
    public VertexLayoutBuilder append(String name, int components, StorageType storage,
                                      Delivery delivery) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name must not be null or empty");
        }
        if (components < 1 || components > 4) {
            throw new IllegalArgumentException(
                    "components must be within 1..4: " + components);
        }
        if (storage == null || delivery == null) {
            throw new IllegalArgumentException("storage and delivery must not be null");
        }
        if (!names.add(name)) {
            throw new IllegalArgumentException("duplicate field name: " + name);
        }
        int alignment = storageSize(storage);
        int padding = (alignment - (cursor % alignment)) % alignment;
        if (padding > 0) {
            fields.add(new VertexField("padding@" + cursor, cursor, padding,
                    StorageType.UINT8, Delivery.PADDING));
            cursor += padding;
        }
        fields.add(new VertexField(name, cursor, components, storage, delivery));
        cursor += components * alignment;
        return this;
    }

    /** Appends an explicit named reserved run (always zero, never an enabled input). */
    public VertexLayoutBuilder reserve(String name, int bytes) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name must not be null or empty");
        }
        if (bytes < 1) {
            throw new IllegalArgumentException("reserved bytes must be positive: " + bytes);
        }
        if (!names.add(name)) {
            throw new IllegalArgumentException("duplicate field name: " + name);
        }
        fields.add(new VertexField(name, cursor, bytes, StorageType.UINT8, Delivery.PADDING));
        cursor += bytes;
        return this;
    }

    /** The stride the layout would build to right now. */
    public int strideBytes() {
        return cursor;
    }

    /** Builds the immutable layout with a content fingerprint over fields and stride. */
    public VertexLayout build(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id must not be null or empty");
        }
        List<VertexField> built = List.copyOf(fields);
        return new VertexLayout(id, cursor, built, LayoutFingerprints.of(cursor, built));
    }

    private void validateField(VertexField field) {
        if (field.components() < 1 || field.components() > 4) {
            throw new IllegalArgumentException(
                    "field components must be within 1..4: " + field.name());
        }
        if (field.byteOffset() < 0) {
            throw new IllegalArgumentException(
                    "field offset must be nonnegative: " + field.name());
        }
    }

    private static int storageSize(StorageType storage) {
        return switch (storage) {
            case FLOAT32 -> 4;
            case UINT8, INT8 -> 1;
            case INT16 -> 2;
        };
    }
}
