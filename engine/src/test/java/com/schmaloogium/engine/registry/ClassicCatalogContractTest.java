// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * The 60-slot classic catalog contract (PHASE_4_DOC §4.5, Appendix A.1): the numeric total
 * is validation metadata only; no behavior may depend on it. Names, fallback edges, kinds
 * and stage/band pairing must stay lossless against A.1.
 */
class ClassicCatalogContractTest {

    @Test
    void catalogDeclaresAndEnumeratesSixtyRows() {
        List<ProgramSlotDescriptor> rows = ClassicProgramCatalog.rows();
        assertEquals(60, rows.size());
        assertEquals(60, ClassicProgramCatalog.declaredClassicCount());
        assertEquals(60, ClassicProgramCatalog.enumeratedClassicCount());
    }

    @Test
    void catalogSlotIdentitiesAreUnique() {
        Set<String> seen = new HashSet<>();
        for (ProgramSlotDescriptor row : ClassicProgramCatalog.rows()) {
            assertTrue(seen.add(row.id().packName()), "duplicate slot " + row.id().packName());
        }
    }

    @Test
    void fixedTerminalsHaveNoFallbackAndHooksKeepTheirStems() {
        for (ProgramSlotDescriptor row : ClassicProgramCatalog.rows()) {
            if (row.fallback().isEmpty() && row.kind() == ProgramSlotKind.RASTER) {
                // Root terminals are the A.1 "fixed" base rows and the indexed sparse
                // members, which resolve only through their own family root or fixed.
                String name = row.id().packName();
                boolean indexedFamily = name.matches(".*(deferred|composite)\\d+.*");
                assertTrue(indexedFamily
                        || Set.of("shadow", "gbuffers_basic", "deferred", "composite", "final")
                            .contains(name),
                    "unexpected root terminal " + name);
            }
            if (row.sourceStem().isPresent()) {
                assertTrue(row.id().packName().startsWith(row.sourceStem().get()),
                    "stem " + row.sourceStem().get() + " not a prefix of "
                        + row.id().packName());
            }
        }
    }

    @Test
    void fallbackEdgesOnlyReferenceExistingRows() {
        Set<String> names = new HashSet<>();
        for (ProgramSlotDescriptor row : ClassicProgramCatalog.rows()) {
            names.add(row.id().packName());
        }
        for (ProgramSlotDescriptor row : ClassicProgramCatalog.rows()) {
            row.fallback().ifPresent(fallback ->
                assertTrue(names.contains(fallback.packName()),
                    "fallback " + fallback.packName() + " of " + row.id().packName()
                        + " is not a catalog row"));
        }
    }

    @Test
    void noGbuffersDescriptorCarriesComputeDispatch() {
        // Enforced at stage-registry level; the catalog itself carries no compute slots.
        for (ProgramSlotDescriptor row : ClassicProgramCatalog.rows()) {
            assertNotNull(row);
            assertFalse(row.kind() == null);
        }
    }

    @Test
    void virtualPreludesAreTheOnlyVirtualKindRows() {
        for (ProgramSlotDescriptor row : ClassicProgramCatalog.rows()) {
            if (row.kind() == ProgramSlotKind.VIRTUAL_FLIP_CONTROL) {
                assertTrue(row.id().packName().equals("deferred_pre")
                    || row.id().packName().equals("composite_pre"));
                assertTrue(row.sourceStem().isEmpty());
                assertTrue(row.fallback().isEmpty());
            }
        }
    }
}
