// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures.internal;

import com.schmaloogium.engine.buffers.CandidateOrigin;
import com.schmaloogium.engine.buffers.CompanionKind;
import com.schmaloogium.engine.buffers.FixedSamplerName;
import com.schmaloogium.engine.buffers.TextureBindingCandidate;
import com.schmaloogium.engine.buffers.TextureCandidateEntry;
import com.schmaloogium.engine.buffers.TextureCandidateTable;
import com.schmaloogium.engine.buffers.TextureHandleRef;
import com.schmaloogium.engine.buffers.TextureOverlayAbsence;
import com.schmaloogium.engine.buffers.TextureParameterFingerprint;
import com.schmaloogium.engine.buffers.TextureParameterSpec;
import com.schmaloogium.engine.config.TextureTarget;
import com.schmaloogium.engine.gl.TextureHandle;
import com.schmaloogium.engine.preprocess.DeclaredGlslType;
import com.schmaloogium.engine.preprocess.SampledKind;
import com.schmaloogium.engine.preprocess.TextureDimension;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.textures.CompanionAtlasPlan;
import com.schmaloogium.engine.textures.CustomTexturePlanEntry;
import com.schmaloogium.engine.textures.NoisePlan;
import com.schmaloogium.engine.textures.OwnedTextureSourceKind;
import com.schmaloogium.engine.textures.TexturePlan;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The publication's candidate-table assembly (§4.3.1): custom textures in canonical plan
 * order, then per-atlas companion objects ascending atlas × kind, then the kind default
 * fills, then noise — every candidate carrying its exact live handle, canonical parameters
 * and fingerprint, and a per-stage ordinal assigned in exactly this order. Non-executable
 * keys never appear; unsupported keys live only in the plan's diagnostics.
 */
public final class MapTextureCandidateTable {

    private static final DeclaredGlslType.Sampler SAMPLER_2D = new DeclaredGlslType.Sampler(
        SampledKind.FLOAT, TextureDimension.D2, false, false, false);

    private MapTextureCandidateTable() {
    }

    /**
     * Assembles the immutable per-cell table from the accepted plan plus the allocated
     * live handles. The noise parameters/fingerprint distinguish the generated baseline
     * from a pack override's NOISE_OVERRIDE interpretation; {@code configurationIdentity}
     * is the retained configuration fingerprint value feeding owned identities.
     */
    public static TextureCandidateTable build(
            TexturePlan plan,
            Map<CompanionAtlasPlan, TextureHandle> companionHandles,
            Map<CompanionKind, TextureHandle> defaultHandles,
            Map<NoisePlan, TextureHandle> noiseHandles,
            TextureParameterSpec noiseParameters,
            String noiseFingerprint,
            Map<CustomTexturePlanEntry, TextureHandle> customHandles,
            String configurationIdentity) {
        Objects.requireNonNull(plan, "plan");
        Objects.requireNonNull(companionHandles, "companionHandles");
        Objects.requireNonNull(defaultHandles, "defaultHandles");
        Objects.requireNonNull(noiseHandles, "noiseHandles");
        Objects.requireNonNull(noiseParameters, "noiseParameters");
        Objects.requireNonNull(noiseFingerprint, "noiseFingerprint");
        Objects.requireNonNull(customHandles, "customHandles");
        Objects.requireNonNull(configurationIdentity, "configurationIdentity");

        Map<StageId, Map<FixedSamplerName, List<TextureBindingCandidate>>> byCell =
            new EnumMap<>(StageId.class);
        Map<StageId, Integer> nextOrdinal = new EnumMap<>(StageId.class);

        for (CustomTexturePlanEntry entry : plan.customTextures()) {
            var handle = customHandles.get(entry);
            Objects.requireNonNull(handle, "missing custom handle for " + entry.key());
            for (StageId stage : entry.stages()) {
                add(byCell, nextOrdinal, stage, entry.name(), new TextureBindingCandidate(
                    new CandidateOrigin.Custom(entry.key(), entry.phase3Ordinal()),
                    stage, entry.key().sampler(), entry.name(), entry.shape(),
                    TextureTarget.valueOf(entry.target().name()),
                    new TextureHandleRef.Owned(handle),
                    new com.schmaloogium.engine.buffers.TextureSourceIdentity(
                        identityValue(entry.source())),
                    entry.parameters(),
                    new TextureParameterFingerprint(
                        entry.parameterizationFingerprint().value()),
                    take(nextOrdinal, stage)));
            }
        }

        for (CompanionAtlasPlan companion : plan.companions()) {
            var handle = companionHandles.get(companion);
            Objects.requireNonNull(handle, "missing companion handle for "
                + companion.base().value());
            var parameters = ParameterPolicy.companionAtlasPolicy(companion.mipmapLevels());
            String fingerprint = SidecarPolicy.fixedRoleFingerprint(
                OwnedTextureSourceKind.COMPANION_RESOURCE, SidecarPolicy.Role.COMPANION,
                parameters);
            for (StageId stage : StageColumnPolicy.customStages()) {
                add(byCell, nextOrdinal, stage, FixedSamplerName.TEXTURE,
                    new TextureBindingCandidate(
                        new CandidateOrigin.Companion(companion.base(), companion.kind()),
                        stage, "texture", FixedSamplerName.TEXTURE, SAMPLER_2D,
                        TextureTarget.TEXTURE_2D, new TextureHandleRef.Owned(handle),
                        new com.schmaloogium.engine.buffers.TextureSourceIdentity(
                            companionIdentity(companion, configurationIdentity)),
                        parameters, new TextureParameterFingerprint(fingerprint),
                        take(nextOrdinal, stage)));
            }
        }

        for (CompanionKind kind : CompanionKind.values()) {
            var handle = defaultHandles.get(kind);
            if (handle == null) {
                continue;
            }
            var parameters = ParameterPolicy.standaloneDefaultPolicy();
            String fingerprint = SidecarPolicy.fixedRoleFingerprint(
                OwnedTextureSourceKind.DEFAULT_FILL, SidecarPolicy.Role.DEFAULT_FILL,
                parameters);
            for (StageId stage : StageColumnPolicy.customStages()) {
                add(byCell, nextOrdinal, stage, FixedSamplerName.TEXTURE,
                    new TextureBindingCandidate(
                        new CandidateOrigin.DefaultFill(kind), stage, "texture",
                        FixedSamplerName.TEXTURE, SAMPLER_2D, TextureTarget.TEXTURE_2D,
                        new TextureHandleRef.Owned(handle),
                        new com.schmaloogium.engine.buffers.TextureSourceIdentity(
                            defaultIdentity(kind, configurationIdentity)),
                        parameters, new TextureParameterFingerprint(fingerprint),
                        take(nextOrdinal, stage)));
            }
        }

        for (Map.Entry<NoisePlan, TextureHandle> noise : noiseHandles.entrySet()) {
            var handle = noise.getValue();
            for (StageId stage : StageColumnPolicy.customStages()) {
                add(byCell, nextOrdinal, stage, FixedSamplerName.NOISETEX,
                    new TextureBindingCandidate(
                        new CandidateOrigin.Noise(), stage,
                        FixedSamplerName.NOISETEX.exactName(),
                        FixedSamplerName.NOISETEX, SAMPLER_2D, TextureTarget.TEXTURE_2D,
                        new TextureHandleRef.Owned(handle),
                        new com.schmaloogium.engine.buffers.TextureSourceIdentity(
                            noiseIdentity(noise.getKey(), configurationIdentity)),
                        noiseParameters, new TextureParameterFingerprint(noiseFingerprint),
                        take(nextOrdinal, stage)));
            }
        }

        Map<StageId, Map<FixedSamplerName, TextureCandidateEntry>> immutable =
            new EnumMap<>(StageId.class);
        byCell.forEach((stage, cells) -> {
            Map<FixedSamplerName, TextureCandidateEntry> frozen = new HashMap<>();
            cells.forEach((name, list) -> frozen.put(name,
                new TextureCandidateEntry.Candidates(List.copyOf(list))));
            immutable.put(stage, java.util.Collections.unmodifiableMap(frozen));
        });
        var stageView = java.util.Collections.unmodifiableMap(immutable);
        return (expandedStage, name) -> stageView
            .getOrDefault(expandedStage, Map.of())
            .getOrDefault(name, new TextureCandidateEntry.Absent(
                TextureOverlayAbsence.NOT_APPLICABLE_TO_STAGE));
    }

    private static void add(
            Map<StageId, Map<FixedSamplerName, List<TextureBindingCandidate>>> byCell,
            Map<StageId, Integer> nextOrdinal, StageId stage, FixedSamplerName name,
            TextureBindingCandidate candidate) {
        byCell.computeIfAbsent(stage, s -> new EnumMap<>(FixedSamplerName.class))
            .computeIfAbsent(name, n -> new ArrayList<>())
            .add(candidate);
        // take() must stay in sync with insertion order; consume one ordinal per add.
        take(nextOrdinal, stage);
    }

    private static int take(Map<StageId, Integer> nextOrdinal, StageId stage) {
        return nextOrdinal.merge(stage, 1, Integer::sum) - 1;
    }

    private static String identityValue(
            com.schmaloogium.engine.textures.TextureSourceIdentity identity) {
        return switch (identity) {
            case com.schmaloogium.engine.textures.TextureSourceIdentity.OwnedUpload owned ->
                owned.contentDigest() + ":" + owned.logicalSource();
            case com.schmaloogium.engine.textures.TextureSourceIdentity.ForeignLive foreign ->
                foreign.exactResourceIdentity() + ":" + foreign.objectEpoch();
        };
    }

    private static String companionIdentity(CompanionAtlasPlan plan,
                                            String configurationIdentity) {
        String logical = "companion:" + plan.base().value() + ":" + plan.kind().name();
        return TextureDigests.sourceDigest(TextureDigests.SOURCE_DOMAIN, logical,
            Integer.toString(plan.defaultFill()), configurationIdentity);
    }

    private static String defaultIdentity(CompanionKind kind, String configurationIdentity) {
        String logical = "default:" + kind.name();
        return TextureDigests.sourceDigest(TextureDigests.SOURCE_DOMAIN, logical,
            configurationIdentity);
    }

    private static String noiseIdentity(NoisePlan plan, String configurationIdentity) {
        String logical = switch (plan) {
            case NoisePlan.Generated g -> "generated:" + g.resolution();
            case NoisePlan.FromPack f -> "pack:" + f.image().canonicalString();
            case NoisePlan.Disabled d -> throw new IllegalStateException(
                "disabled noise owns no object");
        };
        return TextureDigests.sourceDigest(TextureDigests.SOURCE_DOMAIN, logical,
            configurationIdentity);
    }
}
