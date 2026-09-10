// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * The v0.1 base-layout conditional topology adapter (PHASE_10_DOC §4.6, §9; decision
 * {@code docs/decisions/GEOMETRY_PRIMITIVE_COMPATIBILITY.md}): translate the
 * authenticated category, and convert only an otherwise admitted QUADS submission
 * whose actually active effective geometry input is TRIANGLES. No extension fields and
 * no P9 dependency; no points/lines/adjacency synthesis exists.
 *
 * <p>Category law: NONE preserves the original primitive; TRIANGLES accepts existing
 * triangles/strips/fans and converts QUADS only (QUAD_STRIP and POLYGON match no
 * geometry input primitive — ARB_geometry_shader4 Issue 30); POINTS, LINES,
 * LINES_ADJACENCY and TRIANGLES_ADJACENCY accept only the corresponding GL-compatible
 * original primitive families. An unsupported mismatch rejects before draw and is
 * reported, never retried under another program.
 *
 * <p>Expansion law: all original quad computation, late mutation and translucent
 * ordering happen first; then each canonical quad {@code q=(0,1,2,3)} emits triangles
 * {@code (0,1,3), (1,2,3)} in that order, copying whole vertex records bit-for-bit —
 * IDs, padding, color, lightmap and unknown supported fields — never recomputing
 * attributes per triangle and never modifying the source bytes. The original last
 * vertex3 stays both triangles' last provoking vertex; {@code gl_PrimitiveIDIn} becomes
 * 2q then 2q+1, never the old quad ID. Checked arithmetic bounds q, the destination
 * count (6q) and the destination bytes (6q×stride, 1.5× source) before any allocation;
 * a submission is never split to fit memory.
 */
public final class ConditionalTopology {

    /** The original submitted primitive families the adapter can be asked about. */
    public enum SourcePrimitive {
        POINTS,
        LINES,
        LINE_STRIP,
        LINE_LOOP,
        LINES_ADJACENCY,
        LINE_STRIP_ADJACENCY,
        TRIANGLES,
        TRIANGLE_STRIP,
        TRIANGLE_FAN,
        TRIANGLES_ADJACENCY,
        TRIANGLE_STRIP_ADJACENCY,
        QUADS,
        QUAD_STRIP,
        POLYGON
    }

    /** Why a category/source combination is rejected before draw. */
    public enum TopologyRejection {
        /** The source family is not GL-compatible with the required geometry input. */
        INCOMPATIBLE_FAMILY
    }

    /** The closed submission decision for one source primitive under one geometry input. */
    public sealed interface TopologyDecision {

        /** Submit the original primitive unchanged. */
        record Preserve(SourcePrimitive source) implements TopologyDecision {
        }

        /** QUADS → TRIANGLES conversion required (the v0.1 authorized conversion). */
        record ExpandQuads() implements TopologyDecision {
        }

        /** Unsupported mismatch: reject before draw and report; never silently convert. */
        record Rejected(TopologyRejection reason) implements TopologyDecision {
        }
    }

    /** The closed expansion outcome for one sealed quad source. */
    public sealed interface QuadExpansion {

        /** The derived stream: exactly 6q whole records in {@code (0,1,3), (1,2,3)} order. */
        record Expanded(ByteBuffer derived, int quadCount) implements QuadExpansion {
        }

        /** Pre-allocation rejection: partial quads or checked-arithmetic range overflow. */
        record Rejected(ExpansionRejection reason) implements QuadExpansion {
        }
    }

    /** Why a quad source cannot be expanded. */
    public enum ExpansionRejection {
        /** The range does not contain complete canonical quads. */
        PARTIAL_QUAD,
        /** Checked count/byte arithmetic exceeded the representable native range. */
        RANGE_OVERFLOW
    }

    private ConditionalTopology() {
    }

    /** Translates the authenticated category into the submission decision. */
    public static TopologyDecision plan(SourcePrimitive source,
                                        VertexGeometryInput geometryInput) {
        if (geometryInput == null) {
            throw new IllegalArgumentException("geometryInput must not be null");
        }
        return switch (geometryInput) {
            case NONE -> new TopologyDecision.Preserve(source);
            case TRIANGLES -> planTrianglesFamily(source);
            case POINTS -> source == SourcePrimitive.POINTS
                    ? new TopologyDecision.Preserve(source)
                    : rejected();
            case LINES -> switch (source) {
                case LINES, LINE_STRIP, LINE_LOOP -> new TopologyDecision.Preserve(source);
                default -> rejected();
            };
            case LINES_ADJACENCY -> switch (source) {
                case LINES_ADJACENCY, LINE_STRIP_ADJACENCY -> new TopologyDecision.Preserve(source);
                default -> rejected();
            };
            case TRIANGLES_ADJACENCY -> switch (source) {
                case TRIANGLES_ADJACENCY, TRIANGLE_STRIP_ADJACENCY ->
                        new TopologyDecision.Preserve(source);
                default -> rejected();
            };
        };
    }

    private static TopologyDecision planTrianglesFamily(SourcePrimitive source) {
        return switch (source) {
            case TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN -> new TopologyDecision.Preserve(source);
            case QUADS -> new TopologyDecision.ExpandQuads();
            default -> rejected();
        };
    }

    private static TopologyDecision.Rejected rejected() {
        return new TopologyDecision.Rejected(TopologyRejection.INCOMPATIBLE_FAMILY);
    }

    /**
     * Expands {@code vertexCount} whole records of one quad source, starting at
     * {@code byteOffset} in {@code source}, into a fresh little-endian direct buffer of
     * exactly 6q records of {@code strideBytes} each. Validates all ranges and counts
     * before any allocation. Never splits a submission; never mutates the source.
     */
    public static QuadExpansion expandQuads(ByteBuffer source, int byteOffset,
                                            int vertexCount, int strideBytes) {
        if (source == null) {
            throw new IllegalArgumentException("source must not be null");
        }
        if (strideBytes < Classic56Layout.STRIDE_BYTES) {
            throw new IllegalArgumentException("stride below the classic floor: " + strideBytes);
        }
        if (vertexCount < 0 || byteOffset < 0) {
            return new QuadExpansion.Rejected(ExpansionRejection.PARTIAL_QUAD);
        }
        if (vertexCount % 4 != 0) {
            return new QuadExpansion.Rejected(ExpansionRejection.PARTIAL_QUAD);
        }
        long quadCount = vertexCount / 4L;
        long sourceBytes = (long) vertexCount * strideBytes;
        if (byteOffset > source.capacity() || sourceBytes > source.capacity() - byteOffset) {
            return new QuadExpansion.Rejected(ExpansionRejection.RANGE_OVERFLOW);
        }
        long destinationVertices;
        long destinationBytes;
        try {
            destinationVertices = Math.multiplyExact(6L, quadCount);
            destinationBytes = Math.multiplyExact(destinationVertices, strideBytes);
        } catch (ArithmeticException overflow) {
            return new QuadExpansion.Rejected(ExpansionRejection.RANGE_OVERFLOW);
        }
        if (destinationVertices > Integer.MAX_VALUE) {
            return new QuadExpansion.Rejected(ExpansionRejection.RANGE_OVERFLOW);
        }

        ByteBuffer derived = ByteBuffer.allocateDirect((int) destinationBytes)
                .order(ByteOrder.LITTLE_ENDIAN);
        for (long quad = 0; quad < quadCount; quad++) {
            long sourceBase = byteOffset + quad * 4L * strideBytes;
            long destinationBase = quad * 6L * strideBytes;
            copyRecord(source, sourceBase, derived, destinationBase, strideBytes);
            copyRecord(source, sourceBase + strideBytes, derived,
                    destinationBase + strideBytes, strideBytes);
            copyRecord(source, sourceBase + 3L * strideBytes, derived,
                    destinationBase + 2L * strideBytes, strideBytes);
            copyRecord(source, sourceBase + strideBytes, derived,
                    destinationBase + 3L * strideBytes, strideBytes);
            copyRecord(source, sourceBase + 2L * strideBytes, derived,
                    destinationBase + 4L * strideBytes, strideBytes);
            copyRecord(source, sourceBase + 3L * strideBytes, derived,
                    destinationBase + 5L * strideBytes, strideBytes);
        }
        derived.rewind();
        return new QuadExpansion.Expanded(derived, (int) quadCount);
    }

    private static void copyRecord(ByteBuffer source, long sourceOffset,
                                   ByteBuffer destination, long destinationOffset,
                                   int strideBytes) {
        for (int i = 0; i < strideBytes; i++) {
            destination.put((int) destinationOffset + i, source.get((int) sourceOffset + i));
        }
    }
}
