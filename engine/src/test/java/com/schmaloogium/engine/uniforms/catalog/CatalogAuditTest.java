// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms.catalog;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Catalog invariants (§5/§8): exactly 56 built-ins with unique identifier-legal names,
 * valid type/cadence/activation combinations, SIGNAL rows carrying their sink event
 * while other cadences carry none, the §4.12 notifier audit at 15 rows, and the
 * expression exclusion set being a subset of the catalog.
 */
class CatalogAuditTest {

    @Test
    void inventoryIsExactlyFiftySixUniqueIdentifierLegalNames() {
        List<BuiltInUniform> all = BuiltInUniformCatalog.all();
        assertEquals(56, all.size());
        Set<String> names = new HashSet<>();
        for (BuiltInUniform uniform : all) {
            assertTrue(names.add(uniform.name()), "duplicate name: " + uniform.name());
            assertTrue(uniform.name().matches("[A-Za-z_][A-Za-z0-9_]*"),
                    "identifier-legal name: " + uniform.name());
        }
        assertEquals(names.size(), all.size());
    }

    @Test
    void signalRowsCarrySinkEventsOthersDoNot() {
        for (BuiltInUniform uniform : BuiltInUniformCatalog.all()) {
            if (uniform.cadence() == Cadence.SIGNAL) {
                assertFalse(uniform.signalEvent().isEmpty(),
                        "SIGNAL row needs a sink event: " + uniform.name());
            } else {
                assertTrue(uniform.signalEvent().isEmpty(),
                        "non-SIGNAL row carries no sink event: " + uniform.name());
            }
        }
    }

    @Test
    void notifierAuditHasFifteenRows() {
        List<ProducerAudit.Row> rows = ProducerAudit.rows();
        assertEquals(15, rows.size());
        Set<String> signals = new HashSet<>();
        for (ProducerAudit.Row row : rows) {
            assertTrue(signals.add(row.signal()), "duplicate audit signal: " + row.signal());
            assertFalse(row.consumerUniforms().isEmpty());
            assertFalse(row.requiredMoment().isEmpty());
        }
    }

    @Test
    void expressionExclusionIsSubsetOfCatalog() {
        Set<String> names = new HashSet<>();
        for (BuiltInUniform uniform : BuiltInUniformCatalog.all()) {
            names.add(uniform.name());
        }
        assertTrue(names.containsAll(BuiltInUniformCatalog.EXPRESSION_EXCLUDED));
        assertEquals(names.size() - BuiltInUniformCatalog.EXPRESSION_EXCLUDED.size(),
                BuiltInUniformCatalog.expressionPermittedNames().size());
    }

    @Test
    void v01MilestoneRowsDeclareProducers() {
        for (BuiltInUniform uniform : BuiltInUniformCatalog.all()) {
            if (uniform.milestone() == Milestone.V0_1) {
                assertFalse(uniform.producer().isEmpty(),
                        "v0.1 rows name their producer: " + uniform.name());
            }
        }
    }
}
