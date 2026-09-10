// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.plan;

import com.schmaloogium.engine.expr.api.DeclarationKind;
import com.schmaloogium.engine.expr.api.ExpressionType;
import com.schmaloogium.engine.expr.api.FixedInputKind;
import com.schmaloogium.engine.expr.type.TypedNode;
import java.util.List;
import java.util.Map;

/** Implementation-private executable plan model (§2.1: nothing here is contract-visible).
 * Slots are dense definition ordinals; {@code order} is the precomputed prerequisite-first
 * sequence with source-ordinal tie breaks (§4.5). */
public final class Program {

    /** One smooth call site bound to its plan-wide cell. */
    public record Site(long key, int cellIndex) {}

    public final int slotCount;
    public final String[] names;
    public final DeclarationKind[] kinds;
    public final ExpressionType[] types;
    public final com.schmaloogium.engine.config.SourceAttribution[] attributions;
    public final String[] raws;

    /** Valid, retained definitions in evaluation order (prerequisites first). */
    public final int[] order;
    /** Valid uniform slots in original declaration order. */
    public final int[] uniformOrder;

    public final TypedNode[] roots;
    public final int[][] deps;
    /** Transitive reverse-reader closure within valid slots; used for runtime disabling. */
    public final int[][] readerClosure;
    public final Site[][] smoothSites;
    public final boolean[] usesRandom;
    public final int[] sourceOrdinals;

    public final String[] inputNames;
    public final FixedInputKind[] inputKinds;
    public final Map<String, Integer> inputIndex;

    public Program(int slotCount, String[] names, DeclarationKind[] kinds, ExpressionType[] types,
                   com.schmaloogium.engine.config.SourceAttribution[] attributions, String[] raws,
                   int[] sourceOrdinals,
                   int[] order, int[] uniformOrder, TypedNode[] roots, int[][] deps,
                   int[][] readerClosure, Site[][] smoothSites, boolean[] usesRandom,
                   String[] inputNames, FixedInputKind[] inputKinds, Map<String, Integer> inputIndex) {
        this.slotCount = slotCount;
        this.names = names;
        this.kinds = kinds;
        this.types = types;
        this.attributions = attributions;
        this.raws = raws;
        this.sourceOrdinals = sourceOrdinals;
        this.order = order;
        this.uniformOrder = uniformOrder;
        this.roots = roots;
        this.deps = deps;
        this.readerClosure = readerClosure;
        this.smoothSites = smoothSites;
        this.usesRandom = usesRandom;
        this.inputNames = inputNames;
        this.inputKinds = inputKinds;
        this.inputIndex = inputIndex;
    }

    public int smoothCellCount() {
        int total = 0;
        for (Site[] sites : smoothSites) {
            total += sites == null ? 0 : sites.length;
        }
        return total;
    }

    public List<Integer> orderList() {
        return List.of(java.util.Arrays.stream(order).boxed().toArray(Integer[]::new));
    }
}
