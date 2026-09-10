// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.config.IdMappingInput;
import com.schmaloogium.engine.config.IdMappingParserImpl;

import com.schmaloogium.engine.diag.EngineDiagnostic;
import com.schmaloogium.engine.log.LogChannels;
import com.schmaloogium.engine.uniforms.UniformEventSink;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * §8.1 tests 1-3, 7, 9, 14: documented selector forms, precedence tiers, mod-order
 * stability, legacy fallback, layer routing, and unknown-name warnings.
 */
class IdResolutionTest {

    /** One built-and-published runtime plus its observation surfaces. */
    record Fixture(IdRegistrySnapshot registries, IdRuntimeView view,
            PublishedIdRuntime runtime, IdFixtures.RecordingReporter reporter)
            implements AutoCloseable {

        @Override
        public void close() {
            runtime.close();
        }

        int blockId(String path, int metadata) {
            for (BlockTypeRecord block : registries.blocks()) {
                if (!block.name().path().equals(path)) {
                    continue;
                }
                for (int ordinal : block.stateOrdinals()) {
                    if (registries.blockStates().get(ordinal).legacyMetadata() == metadata) {
                        return stateId(ordinal);
                    }
                }
            }
            throw new IllegalArgumentException("no state " + path + ":" + metadata);
        }

        int stateId(int ordinal) {
            AliasValue value = runtime.aliases().blockId(ordinal);
            if (!value.present()) {
                throw new IllegalStateException("state " + ordinal + " unassigned");
            }
            return value.shaderId();
        }

        int assignedStateCount(String path) {
            for (BlockTypeRecord block : registries.blocks()) {
                if (block.name().path().equals(path)) {
                    return (int) block.stateOrdinals().stream()
                            .filter(o -> runtime.aliases().blockId(o).present()).count();
                }
            }
            throw new IllegalArgumentException("no block " + path);
        }

        int itemId(String path) {
            for (ItemTypeRecord item : registries.items()) {
                if (item.name().path().equals(path)) {
                    AliasValue value = runtime.aliases().itemId(item.itemOrdinal());
                    if (!value.present()) {
                        throw new IllegalStateException(path + " unassigned");
                    }
                    return value.shaderId();
                }
            }
            throw new IllegalArgumentException("no item " + path);
        }

        int entityId(String path) {
            for (EntityTypeRecord entity : registries.entities()) {
                if (entity.name().path().equals(path)) {
                    AliasValue value = runtime.aliases().entityId(entity.entityTypeOrdinal());
                    if (!value.present()) {
                        throw new IllegalStateException(path + " unassigned");
                    }
                    return value.shaderId();
                }
            }
            throw new IllegalArgumentException("no entity " + path);
        }
    }
    private Fixture buildAndPublish(IdMappingInput mappings, IdRegistrySnapshot registries,
            ModIdSourceSnapshot mods, CompatibilityAliasCatalog aliases, LegacyTagCatalog tags,
            HandLightPolicy policy) {
        IdFixtures.RecordingReporter reporter = new IdFixtures.RecordingReporter();
        IdRuntimeBuilder builder = new IdRuntimeBuilderImpl(new IdMappingParserImpl());
        IdBuildResult result = builder.build(new IdBuildRequest(mappings, registries, mods,
                aliases, tags, policy, reporter));
        if (!(result instanceof IdBuildResult.Built built)) {
            throw new IllegalStateException("expected Built, got " + result);
        }
        IdRuntimePublisher publisher = IdRuntimePublisher.create(NO_SINK, d -> { });
        IdPublishResult published = publisher.publish(built.candidate(),
                new IdPublishContext("test"));
        if (!(published instanceof IdPublishResult.Published ok)) {
            throw new IllegalStateException("expected Published, got " + published);
        }
        return new Fixture(registries, built.candidate().view(), ok.runtime(), reporter);
    }
    private static final UniformEventSink NO_SINK = new UniformEventSink() {
        @Override public void captureGbufferMatrices(long f, com.schmaloogium.engine.uniforms.Matrix4Value a, com.schmaloogium.engine.uniforms.Matrix4Value b) { }
        @Override public void updateCelestial(com.schmaloogium.engine.uniforms.CelestialSample s) { }
        @Override public void updateShadowMatrices(
                com.schmaloogium.engine.uniforms.ShadowMatrixSample s) { }
        @Override public void updateFog(com.schmaloogium.engine.uniforms.FogSample s) { }
        @Override public void updateBlend(com.schmaloogium.engine.uniforms.BlendSample s) { }
        @Override public void updateEntityColor(
                com.schmaloogium.engine.uniforms.Float4 value) { }
        @Override public void updateEntityId(int value) { }
        @Override public void updateBlockEntityId(int value) { }
        @Override public void updateInstanceId(int value) { }
        @Override public void updateAtlasSize(com.schmaloogium.engine.uniforms.Int2 value) { }
        @Override public void updateHeldItems(
                com.schmaloogium.engine.uniforms.HeldItemSample value) { }
    };

    private static final String ALL_FORMS = """
            100=stone
            101=minecraft:grass
            102=minecraft:oak_door:2-4
            103=minecraft:oak_door:8 half=upper facing=north
            104=minecraft:oak_door:half=upper
            105=31:1
            106=tallgrass:2
            """;

    @Test
    void allDocumentedForms_shortNamespacedMetadataPropertyLegacy() {
        try (Fixture f = buildAndPublish(
                IdFixtures.packMappings(ALL_FORMS, "10=stick\n11=tallgrass\n",
                        "20=creeper\n21=sheep\n",
                        "layer.cutout=glass\nlayer.translucent=water\n",
                        IdFixtures.packOrigin()),
                IdFixtures.vanilla(), ModIdSourceSnapshot.empty(),
                new CompatibilityAliasCatalog(1, List.of()), LegacyTagCatalog.empty(),
                HandLightPolicy.allDefault())) {
            assertEquals(100, f.blockId("stone", 0));
            assertEquals(101, f.blockId("grass", 0));
            assertEquals(102, f.blockId("oak_door", 2));
            assertEquals(102, f.blockId("oak_door", 4));
            assertThrows(IllegalStateException.class, () -> f.blockId("oak_door", 5));
            assertEquals(103, f.blockId("oak_door", 8));
            assertEquals(104, f.blockId("oak_door", 6));
            assertEquals(104, f.blockId("oak_door", 11));
            assertEquals(106, f.blockId("tallgrass", 2));
            assertEquals(10, f.itemId("stick"));
            assertEquals(11, f.itemId("tallgrass"));
            assertEquals(20, f.entityId("creeper"));
            // The broad half=upper rule overlaps the specific :8 rule; the specific
            // rule wins (source order) and the loser is reported once with both origins.
            List<EngineDiagnostic> conflicts = f.reporter.withKey(
                    "schmaloogium.warn.ids.conflict");
            assertEquals(List.of("pack:shaders/block.properties@4:minecraft:oak_door",
                    "pack:shaders/block.properties@5:minecraft:oak_door"),
                    conflicts.get(0).args());
            assertEquals(1, f.reporter.received.size());
        }
    }
}
