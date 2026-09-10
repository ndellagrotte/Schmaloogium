// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.eval;

import com.schmaloogium.engine.expr.plan.Program;

/** Backend seam (§2.1): validates that a compiled program is buildable by the typed-AST
 * interpreter. A future compiled backend implements the same seam and must match the same
 * golden bits (§4.6). */
public final class InterpreterBackend {

    private InterpreterBackend() {}

    public static void build(Program program) {
        for (int slot : program.order) {
            if (program.roots[slot] == null) {
                throw new IllegalStateException("backend invariant: no root for ordered slot " + slot);
            }
            for (int dep : program.deps[slot]) {
                if (dep < 0 || dep >= program.slotCount) {
                    throw new IllegalStateException("backend invariant: dependency out of range");
                }
            }
            Program.Site[] sites = program.smoothSites[slot];
            if (sites != null) {
                for (Program.Site site : sites) {
                    if (site.cellIndex() < 0) {
                        throw new IllegalStateException("backend invariant: smooth cell unassigned");
                    }
                }
            }
        }
    }
}
