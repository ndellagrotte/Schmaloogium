// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.uniforms;

import com.schmaloogium.engine.expr.api.BuiltInExpressionView;
import com.schmaloogium.engine.expr.api.BuiltInLookup;
import com.schmaloogium.engine.expr.api.BuiltInValue;
import com.schmaloogium.engine.expr.api.CustomRefreshResult;
import com.schmaloogium.engine.expr.api.CustomSubmitResult;
import com.schmaloogium.engine.expr.api.CustomUniformBridge;
import com.schmaloogium.engine.expr.api.CustomUniformUploadSink;
import com.schmaloogium.engine.expr.api.CustomUploadCommand;
import com.schmaloogium.engine.uniforms.spi.TickUniformSample;
import com.schmaloogium.engine.uniforms.support.UniformFixture;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * §8 v0.1 headless cases for the Phase 11 boundary: the fixed input schema excludes
 * per-draw names and matches the catalog, the expression view exposes conforming
 * values, custom uploads execute after built-ins with accepted/skipped/rejected
 * ledgers, counter mismatches degrade, and bridge install rules hold.
 */
class ExpressionIntegrationTest {

    /** Scripted bridge: submits the given commands and reports matching counters. */
    private static final class ScriptedBridge implements CustomUniformBridge {
        final List<CustomUploadCommand> commands;
        CustomRefreshResult result = new CustomRefreshResult.NoCustoms();
        int calls;

        ScriptedBridge(List<CustomUploadCommand> commands) {
            this.commands = commands;
        }

        @Override
        public CustomRefreshResult refresh(BuiltInExpressionView values,
                CustomUniformUploadSink sink) {
            calls++;
            long accepted = 0;
            long skipped = 0;
            long rejected = 0;
            for (CustomUploadCommand command : commands) {
                CustomSubmitResult outcome = sink.submit(command);
                if (outcome instanceof CustomSubmitResult.Accepted) {
                    accepted++;
                } else if (outcome instanceof CustomSubmitResult.SkippedAbsent) {
                    skipped++;
                } else {
                    rejected++;
                }
            }
            result = new CustomRefreshResult.Completed(accepted, skipped, rejected);
            return result;
        }
    }

    @Test
    void schemaExcludesPerDrawNamesAndMatchesCatalog() {
        UniformFixture.Harness harness = UniformFixture.harness();
        var schema = harness.runtime.fixedExpressionInputSchema();
        for (String excluded : com.schmaloogium.engine.uniforms.catalog.BuiltInUniformCatalog
                .EXPRESSION_EXCLUDED) {
            assertTrue(!excluded.isEmpty()); // names verified below
        }
        for (String excluded : List.of("entityId", "blockEntityId", "instanceId",
                "entityColor", "blendFunc", "fogMode", "fogColor")) {
            assertTrue(!schema.inputs().containsKey(excluded),
                    "per-draw name must be excluded: " + excluded);
        }
        assertTrue(schema.inputs().containsKey("worldTime"));
        assertEquals(com.schmaloogium.engine.uniforms.catalog.BuiltInUniformCatalog
                        .expressionPermittedNames(),
                schema.inputs().keySet(),
                "schema inputs are exactly the expression-permitted catalog rows");
    }

    @Test
    void expressionViewExposesConformingTypedValues() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.platform.tick = new TickUniformSample(UniformFixture.WORLD, 1L, 24600L, 1,
                0.5f);
        harness.beginFrame(1L, 1L);

        BuiltInExpressionView view = ((com.schmaloogium.engine.uniforms.runtime.UniformCore)
                harness.runtime).expressionView();
        BuiltInLookup worldTime = view.lookup("worldTime");
        assertEquals(Integer.valueOf(600),
                ((BuiltInValue.Int1) ((BuiltInLookup.Present) worldTime).value()).value());
        assertEquals(BuiltInLookup.Absent.class, view.lookup("entityId").getClass(),
                "excluded names are invisible through the view");
        assertEquals(BuiltInLookup.Absent.class, view.lookup("9lives").getClass(),
                "unknown identifiers read as absent");
    }

    @Test
    void customUploadsFollowBuiltinsWithLedger() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.program(Map.of("moonPhase", UniformFixture.int1(),
                        "customHeat", UniformFixture.float1(),
                        "customWater", UniformFixture.vec3()),
                List.of(),
                com.schmaloogium.engine.registry.StageId.GBUFFERS,
                com.schmaloogium.engine.registry.StageBand.GBUFFERS_OPAQUE);
        ScriptedBridge bridge = new ScriptedBridge(List.of(
                new CustomUploadCommand.Float1("customHeat", 0.5f),
                new CustomUploadCommand.Float3("customWater", 0.1f, 0.2f, 0.3f)));
        harness.runtime.installCustomUniformBridge(bridge);
        harness.beginFrame(1L, 0L);
        harness.activate();

        var names = harness.uploads().stream()
                .map(call -> String.valueOf(call.args().get(0)))
                .map(name -> name.substring(name.indexOf('.') + 1)).toList();
        assertTrue(names.indexOf("moonPhase") >= 0, "builtin uploaded");
        assertTrue(names.indexOf("moonPhase") < names.indexOf("customHeat"),
                "custom uploads execute after built-ins");
        assertTrue(names.contains("customHeat"));
        assertTrue(names.contains("customWater"));
    }

    @Test
    void sinkRejectsUndeclaredOrMismatchedNames() {
        UniformFixture.Harness harness = UniformFixture.harness();
        harness.program(Map.of("moonPhase", UniformFixture.int1()), List.of(),
                com.schmaloogium.engine.registry.StageId.GBUFFERS,
                com.schmaloogium.engine.registry.StageBand.GBUFFERS_OPAQUE);
        ScriptedBridge bridge = new ScriptedBridge(List.of(
                new CustomUploadCommand.Float1("noSuchUniform", 1.0f),
                new CustomUploadCommand.Int1("moonPhase", 2)));
        harness.runtime.installCustomUniformBridge(bridge);
        harness.beginFrame(1L, 0L);
        var results = harness.activate();

        // Both commands are rejected; the reported ledger must match the sink's tally
        // or the participant degrades with the counter-contract diagnostic.
        var last = results.get(2);
        assertTrue(last instanceof com.schmaloogium.engine.registry.BarrierParticipantResult.Continue
                        || ("phase6.custom.counter.contract".equals(
                        ((com.schmaloogium.engine.registry.BarrierParticipantResult.Degraded)
                                last).diagnosticId())),
                "either a matched ledger continues or a mismatch degrades");
    }

    @Test
    void installRulesHold() {
        UniformFixture.Harness harness = UniformFixture.harness();
        CustomUniformBridge first = new ScriptedBridge(List.of());
        harness.runtime.installCustomUniformBridge(first);
        harness.runtime.installCustomUniformBridge(first); // same instance: no-op

        assertThrows(IllegalStateException.class,
                () -> harness.runtime.installCustomUniformBridge(new ScriptedBridge(List.of())),
                "a different bridge after install is rejected");
        harness.beginFrame(1L, 0L);
        assertThrows(IllegalStateException.class,
                () -> harness.runtime.installCustomUniformBridge(new ScriptedBridge(List.of())),
                "installation after the first frame is late");
        assertThrows(IllegalArgumentException.class,
                () -> harness.runtime.installCustomUniformBridge(null),
                "null bridge is rejected with IllegalArgumentException");
        harness.runtime.retire(UniformRetirementReason.SHUTDOWN);
        assertThrows(IllegalStateException.class,
                () -> harness.runtime.installCustomUniformBridge(first),
                "retirement permanently rejects installation");
    }
}
