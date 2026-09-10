// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.compat.vertex;

import com.schmaloogium.mod.compat.CompatCheck;
import com.schmaloogium.mod.compat.CompatContext;
import com.schmaloogium.mod.compat.CompatVerdict;

import java.util.List;

/**
 * The Phase 10 chunk-renderer coexistence check (PHASE_10_DOC §4.9; OQ-5 policy):
 * v0.3 detect-and-bail — never integrate, never patch the other renderer. Installed
 * through Phase 1's {@code BailRegistry.register(CompatCheck)}; the registry's
 * aggregation and session latch own the decision, and a thrown or indeterminate probe
 * is the registry's business to convert to Bail (a check that cannot decide is not
 * evidence of compatibility). No competing registry, no warn-and-continue mode.
 *
 * <p>Probe table (§4.9): the actual {@code celeritas} mod id is the required positive
 * probe; {@code embeddium}/{@code celeritas_shaders} are retained candidates whose
 * absence does not clear Pintonium; the two exact renderer class names are
 * conservative presence probes that never initialize the classes. The
 * {@code org.embeddedt.embeddium}/{@code org.taumc.celeritas} roots are investigation
 * signatures only — package strings are never passed to {@code isClassPresent}.
 * {@code nothirium}/{@code vintagium} are provisional catalog entries pending OQ-5
 * artifact validation. Evaluation happens at Phase 1's three points; a class-only
 * positive may conservatively reject an inert bundled renderer — that false-positive
 * risk is the explicit OQ-5 measurement, preferable to corrupting vertex input state.
 */
public final class ChunkRendererCompatCheck implements CompatCheck {

    /** The fixed check id registered with the Phase 1 bail registry (PHASE_10_DOC §5.1). */
    public static final String CHECK_ID = "schmaloogium.chunk_renderer";

    /** The diagnostic reason key (PHASE_10_DOC §4.9); args[0] is the renderer name. */
    public static final String REASON_KEY = "schmaloogium.compat.chunk_renderer_replaced";

    private record Family(String[] modIds, String[] exactClasses, String rendererName) {
    }

    private static final Family CELERITAS = new Family(
            new String[] {"celeritas", "embeddium", "celeritas_shaders"},
            new String[] {
                    "org.taumc.celeritas.impl.render.terrain.CeleritasWorldRenderer",
                    "org.taumc.celeritas.CeleritasVintage"},
            "Celeritas");
    private static final Family NOTHIRIUM = new Family(
            new String[] {"nothirium"}, new String[0], "Nothirium");
    private static final Family VINTAGIUM = new Family(
            new String[] {"vintagium"}, new String[0], "Vintagium");

    private static final List<Family> FAMILIES = List.of(CELERITAS, NOTHIRIUM, VINTAGIUM);

    /** Investigation signatures; never probed through {@code isClassPresent} (§4.9). */
    public static final List<String> INVESTIGATION_SIGNATURES =
            List.of("org.embeddedt.embeddium", "org.taumc.celeritas");

    @Override
    public String id() {
        return CHECK_ID;
    }

    @Override
    public CompatVerdict check(CompatContext ctx) {
        if (ctx == null) {
            throw new IllegalArgumentException("context must not be null");
        }
        for (Family family : FAMILIES) {
            for (String modId : family.modIds()) {
                if (ctx.isModLoaded(modId)) {
                    return bail(family.rendererName());
                }
            }
            for (String className : family.exactClasses()) {
                if (ctx.isClassPresent(className)) {
                    return bail(family.rendererName());
                }
            }
        }
        return new CompatVerdict.Ok();
    }

    private static CompatVerdict.Bail bail(String rendererName) {
        return new CompatVerdict.Bail(REASON_KEY, List.of(rendererName));
    }
}
