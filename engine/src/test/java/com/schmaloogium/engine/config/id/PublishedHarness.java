// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config.id;

import com.schmaloogium.engine.config.IdMappingInput;
import com.schmaloogium.engine.config.IdMappingParserImpl;
import com.schmaloogium.engine.uniforms.UniformEventSink;

/**
 * Shared build-and-publish harness for the headless Phase 9 suite: one inert sink, one
 * build, one publication per fixture; observations go through the public view and
 * published lookups.
 */
final class PublishedHarness {

    private PublishedHarness() {
    }

    static final UniformEventSink NO_SINK = new UniformEventSink() {
        @Override public void captureGbufferMatrices(long frameId,
                com.schmaloogium.engine.uniforms.Matrix4Value modelView,
                com.schmaloogium.engine.uniforms.Matrix4Value projection) { }
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

    /** One built-and-published runtime plus its observation surfaces. */
    record Published(IdRegistrySnapshot registries, IdRuntimeView view,
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

        int stateOrdinal(String path, int metadata) {
            for (BlockTypeRecord block : registries.blocks()) {
                if (!block.name().path().equals(path)) {
                    continue;
                }
                for (int ordinal : block.stateOrdinals()) {
                    if (registries.blockStates().get(ordinal).legacyMetadata() == metadata) {
                        return ordinal;
                    }
                }
            }
            throw new IllegalArgumentException("no state " + path + ":" + metadata);
        }

        int itemOrdinal(String path) {
            for (ItemTypeRecord item : registries.items()) {
                if (item.name().path().equals(path)) {
                    return item.itemOrdinal();
                }
            }
            throw new IllegalArgumentException("no item " + path);
        }

        int entityOrdinal(String path) {
            for (EntityTypeRecord entity : registries.entities()) {
                if (entity.name().path().equals(path)) {
                    return entity.entityTypeOrdinal();
                }
            }
            throw new IllegalArgumentException("no entity " + path);
        }
    }

    /** Builds and publishes one runtime over the given inputs. */
    static Published buildAndPublish(IdMappingInput mappings, IdRegistrySnapshot registries,
            ModIdSourceSnapshot mods, CompatibilityAliasCatalog aliases, LegacyTagCatalog tags,
            HandLightPolicy policy) {
        IdFixtures.RecordingReporter reporter = new IdFixtures.RecordingReporter();
        IdRuntimeBuilder builder = new IdRuntimeBuilderImpl(new IdMappingParserImpl());
        IdBuildResult result = builder.build(new IdBuildRequest(mappings, registries, mods,
                aliases, tags, policy, reporter));
        if (!(result instanceof IdBuildResult.Built built)) {
            throw new IllegalStateException("expected Built, got " + result);
        }
        IdRuntimeView view = built.candidate().view();
        IdRuntimePublisher publisher = IdRuntimePublisher.create(NO_SINK, d -> { });
        IdPublishResult published = publisher.publish(built.candidate(),
                new IdPublishContext("test"));
        if (!(published instanceof IdPublishResult.Published ok)) {
            throw new IllegalStateException("expected Published, got " + published);
        }
        return new Published(registries, view, ok.runtime(), reporter);
    }

    /** Builds without publishing; the candidate stays caller-owned. */
    static IdRuntimeCandidate buildOnly(IdMappingInput mappings, IdRegistrySnapshot registries,
            ModIdSourceSnapshot mods, CompatibilityAliasCatalog aliases, LegacyTagCatalog tags,
            HandLightPolicy policy, IdFixtures.RecordingReporter reporter) {
        IdRuntimeBuilder builder = new IdRuntimeBuilderImpl(new IdMappingParserImpl());
        IdBuildResult result = builder.build(new IdBuildRequest(mappings, registries, mods,
                aliases, tags, policy, reporter));
        if (!(result instanceof IdBuildResult.Built built)) {
            throw new IllegalStateException("expected Built, got " + result);
        }
        return built.candidate();
    }
}
