// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * The authenticated producer participation and item→block mapping table
 * (PHASE_10_DOC §4.2 D-P10-30, §4.6 D-P10-29). Participation derives only from the
 * authenticated completed-producer semantics — never from occupied bytes, a
 * caller-provided set or per-vertex written masks:
 *
 * <ul>
 *   <li>{@link #BLOCK} — the completed block-model producer: POSITION/COLOR/UV0/UV1
 *       plus the established generated NORMAL. UV1 participates through the real
 *       four-value brightness writer, never zero initialization.</li>
 *   <li>{@link #ITEM} — partial ingress only, never a final participation: an
 *       authenticated ITEM quad supplies POSITION/COLOR/UV0 and its source normal into
 *       a BLOCK builder; the builder retains BLOCK's immutable final mask throughout
 *       (D-P10-30).</li>
 *   <li>{@link #OLDMODEL} — textured model quads: POSITION/UV0/NORMAL, never
 *       COLOR/UV1; uncolored OLDMODEL keeps those absent and inherited.</li>
 * </ul>
 */
public enum VertexProducer {

    BLOCK(Set.of(ConventionalInput.POSITION, ConventionalInput.COLOR,
            ConventionalInput.UV0, ConventionalInput.UV1, ConventionalInput.NORMAL)),
    ITEM(Set.of(ConventionalInput.POSITION, ConventionalInput.COLOR,
            ConventionalInput.UV0, ConventionalInput.NORMAL)),
    OLDMODEL(Set.of(ConventionalInput.POSITION, ConventionalInput.UV0,
            ConventionalInput.NORMAL));

    /**
     * The brightness completion discharges BLOCK's pending UV1 obligation for exactly
     * the four destination indices of the appended quad (D-P10-30).
     */
    public static final int UV1_COMPLETION_VERTICES = 4;

    private final Set<ConventionalInput> participation;

    VertexProducer(Set<ConventionalInput> participation) {
        this.participation = Set.copyOf(participation);
    }

    /**
     * The authenticated source participation of this producer. For {@link #ITEM} this
     * is the partial ingress supply, not a draw mask; the final participation always
     * comes from the completed producer.
     */
    public Set<ConventionalInput> participation() {
        return participation;
    }

    /**
     * The D-P10-30 item→block ingress table: source semantic → CLASSIC_56 destination
     * semantic. ITEM supplies its semantics by usage — its own vanilla normal offset is
     * irrelevant — and {@link ClassicSemantic#UV1} is deliberately absent: the BLOCK
     * builder's UV1 completes only through the real {@code func_178962_a(IIII)}
     * brightness writer, never from the ITEM descriptor or zero initialization.
     */
    public static Map<ClassicSemantic, ClassicSemantic> itemToBlockIngress() {
        Map<ClassicSemantic, ClassicSemantic> table = new EnumMap<>(ClassicSemantic.class);
        table.put(ClassicSemantic.POSITION, ClassicSemantic.POSITION);
        table.put(ClassicSemantic.COLOR, ClassicSemantic.COLOR);
        table.put(ClassicSemantic.UV0, ClassicSemantic.UV0);
        table.put(ClassicSemantic.NORMAL, ClassicSemantic.NORMAL);
        return java.util.Collections.unmodifiableMap(table);
    }
}
