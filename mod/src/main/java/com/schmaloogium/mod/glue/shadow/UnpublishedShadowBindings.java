// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.shadow;

import com.schmaloogium.engine.buffers.BaseAtlasContext;
import com.schmaloogium.engine.buffers.TextureCandidateEntry;
import com.schmaloogium.engine.buffers.TextureCandidateTable;
import com.schmaloogium.engine.buffers.TextureHandleRef;
import com.schmaloogium.engine.buffers.TextureOverlayFingerprint;
import com.schmaloogium.engine.buffers.TextureOverlayLease;
import com.schmaloogium.engine.buffers.TextureOverlayPublicationId;
import com.schmaloogium.engine.pack.ConfigurationFingerprint;
import com.schmaloogium.engine.buffers.FixedSamplerName;
import com.schmaloogium.engine.registry.FixedSamplerPolicyFingerprint;
import com.schmaloogium.engine.registry.RegistryFingerprint;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.shadow.ShadowBindingSource;

import java.util.Objects;
import java.util.Optional;

/**
 * The v0.2 shadow binding source: Phase 13 has not published a texture overlay yet (Task H),
 * so the expected publication is the estate's own {@code "unpublished:v0.1"} id and the lease
 * is an empty, always-current one. Phase 5's {@code shadowBindings} accepts both as carried
 * and binds the sixteen estate rows itself; {@code ShadowBindingSource.absent()} would instead
 * suppress every shadow pass before it draws (PHASE_8_DOC §4.2 step 8, "Rejected gives no
 * lease and suppresses this undrawn shadow pass").
 */
public final class UnpublishedShadowBindings implements ShadowBindingSource {

    private static final TextureOverlayFingerprint UNPUBLISHED =
            new TextureOverlayFingerprint("unpublished:v0.1");

    private final TextureOverlayPublicationId id;
    private final RegistryFingerprint registry;
    private final long registryGeneration;
    private final long resourceReloadEpoch;
    private final ConfigurationFingerprint configuration;

    public UnpublishedShadowBindings(long estateGeneration, RegistryFingerprint registry,
            long registryGeneration, long resourceReloadEpoch, ConfigurationFingerprint configuration) {
        this.id = new TextureOverlayPublicationId(estateGeneration, UNPUBLISHED);
        this.registry = Objects.requireNonNull(registry, "registry");
        this.registryGeneration = registryGeneration;
        this.resourceReloadEpoch = resourceReloadEpoch;
        this.configuration = Objects.requireNonNull(configuration, "configuration");
    }

    @Override
    public Optional<TextureOverlayPublicationId> expectedOverlay() {
        return Optional.of(id);
    }

    @Override
    public Optional<TextureOverlayLease> acquire() {
        return Optional.of(new EmptyLease());
    }

    /** No overlay rows, no base atlas: every answer is the documented "unavailable" one. */
    private final class EmptyLease implements TextureOverlayLease {

        private volatile boolean closed;

        @Override
        public TextureOverlayPublicationId id() {
            return id;
        }

        @Override
        public RegistryFingerprint registryFingerprint() {
            return registry;
        }

        @Override
        public long registryGeneration() {
            return registryGeneration;
        }

        @Override
        public long resourceReloadEpoch() {
            return resourceReloadEpoch;
        }

        @Override
        public ConfigurationFingerprint configurationFingerprint() {
            return configuration;
        }

        @Override
        public FixedSamplerPolicyFingerprint policyFingerprint() {
            return new FixedSamplerPolicyFingerprint("unpublished:v0.1");
        }

        @Override
        public TextureCandidateTable candidates() {
            return new TextureCandidateTable() {
                @Override
                public TextureCandidateEntry entry(StageId expandedStage, FixedSamplerName name) {
                    return null;
                }
            };
        }

        @Override
        public BaseAtlasContext baseAtlasContext() {
            return new BaseAtlasContext.Unavailable();
        }

        @Override
        public Optional<TextureHandleRef> baseTexture() {
            return Optional.empty();
        }

        @Override
        public BaseAtlasContext atlasContext(TextureHandleRef base) {
            return new BaseAtlasContext.Unavailable();
        }

        @Override
        public boolean isCurrent() {
            return !closed;
        }

        @Override
        public void close() {
            closed = true;
        }
    }
}
