// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures.internal;

import com.schmaloogium.engine.buffers.AtlasId;
import com.schmaloogium.engine.buffers.CompanionKind;
import com.schmaloogium.engine.config.EngineOptionData;
import com.schmaloogium.engine.textures.AtlasCatalog;
import com.schmaloogium.engine.textures.AtlasDescriptor;
import com.schmaloogium.engine.textures.CompanionAtlasPlan;
import com.schmaloogium.engine.textures.CompanionOrigin;
import com.schmaloogium.engine.textures.CompanionPolicy;
import com.schmaloogium.engine.textures.CompanionMacroState;
import com.schmaloogium.engine.textures.CompanionSpriteSource;
import com.schmaloogium.engine.textures.TextureMemoryEstimate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The pure companion planner (PHASE_13_DOC §4.1.2/§4.1.3/§4.1.4/§4.8): one full companion
 * atlas plan per enabled kind per base atlas, with the base atlas's exact extent and mip
 * chain, total per-sprite discovery (Resource or DefaultFill, never a hole), the §4.1.4
 * contract default fills, and the exact full-mip-chain memory estimate.
 */
public final class CompanionPlanner {

    /** The §4.1.4 contract default for a missing normal sprite (MSB-first FF 7F 7F FF). */
    public static final int NORMALS_DEFAULT_PACKED = 0xFF7F7FFF;

    /** The §4.1.4 zero specular default. */
    public static final int SPECULAR_DEFAULT_PACKED = 0x00000000;

    private CompanionPlanner() {
    }

    /** Per-sprite companion discovery: present means a discovered pack companion resource. */
    public interface Discovery {
        Optional<String> companionResource(AtlasId base, CompanionKind kind, String iconName);
    }

    /** Plan output: plans in atlas-list then kind order, plus the §4.8 companion estimate. */
    public record Result(List<CompanionAtlasPlan> plans, TextureMemoryEstimate memory) {
        public Result {
            Objects.requireNonNull(plans, "plans");
            Objects.requireNonNull(memory, "memory");
            plans = List.copyOf(plans);
        }
    }

    /**
     * Plans the enabled kinds. Discovery is total: for every base sprite of every enabled
     * kind exactly one {@link CompanionSpriteSource} is produced, in base atlas sprite
     * order. Disabled kinds produce zero plans and zero memory.
     *
     * @throws IllegalArgumentException if the atlas list is not in ascending AtlasId
     *     unsigned-UTF8 order, or if the macro pair contradicts the policy
     *     (a true macro bit over a disabled kind is INVALID_REQUEST at plan level).
     */
    public static Result plan(AtlasCatalog atlases, CompanionPolicy policy,
            CompanionMacroState macros, Discovery discovery) {
        Objects.requireNonNull(atlases, "atlases");
        Objects.requireNonNull(policy, "policy");
        Objects.requireNonNull(macros, "macros");
        Objects.requireNonNull(discovery, "discovery");
        if (!pairingConsistent(policy, macros)) {
            throw new IllegalArgumentException(
                "INVALID_REQUEST: companion macro pair contradicts the companion policy");
        }

        List<AtlasDescriptor> descriptors = atlases.atlases();
        requireCanonicalAtlasOrder(descriptors);

        List<CompanionAtlasPlan> plans = new ArrayList<>();
        long companionBytes = 0;
        for (AtlasDescriptor atlas : descriptors) {
            for (CompanionKind kind : CompanionKind.values()) {
                if (!enabled(policy, kind)) {
                    continue;
                }
                int defaultFill = defaultFillPacked(kind);
                List<CompanionSpriteSource> sprites =
                    new ArrayList<>(atlas.sprites().size());
                for (var sprite : atlas.sprites()) {
                    Optional<String> resource = discovery.companionResource(
                        atlas.id(), kind, sprite.iconName());
                    CompanionOrigin origin = resource.isPresent()
                        ? new CompanionOrigin.Resource(resource.get())
                        : new CompanionOrigin.DefaultFill(defaultFill);
                    sprites.add(new CompanionSpriteSource(kind, sprite.iconName(), origin));
                }
                plans.add(new CompanionAtlasPlan(atlas.id(), kind, atlas.width(),
                    atlas.height(), atlas.mipmapLevels(), sprites, defaultFill));
                companionBytes += mipChainBytes(atlas.width(), atlas.height(),
                    atlas.mipmapLevels());
            }
        }
        for (CompanionKind kind : CompanionKind.values()) {
            if (enabled(policy, kind)) {
                companionBytes += 4;
            }
        }
        return new Result(plans, new TextureMemoryEstimate(companionBytes, 0, 0));
    }

    /**
     * Every true macro bit requires the same-kind policy bit enabled; a true macro over a
     * disabled kind is inconsistent (INVALID_REQUEST at plan level). Policy-only bits with
     * a false macro never occur in consistent pairs' inputs and are consistent here.
     */
    public static boolean pairingConsistent(CompanionPolicy policy, CompanionMacroState macros) {
        Objects.requireNonNull(policy, "policy");
        Objects.requireNonNull(macros, "macros");
        if (macros.normalMap() && !policy.normalsEnabled()) {
            return false;
        }
        if (macros.specularMap() && !policy.specularEnabled()) {
            return false;
        }
        return true;
    }

    /** The §4.1.4 default fill bytes, written most significant byte first. */
    public static byte[] defaultFillBytes(CompanionKind kind) {
        Objects.requireNonNull(kind, "kind");
        int packed = defaultFillPacked(kind);
        return new byte[] {
            (byte) (packed >>> 24),
            (byte) (packed >>> 16),
            (byte) (packed >>> 8),
            (byte) packed,
        };
    }

    private static boolean enabled(CompanionPolicy policy, CompanionKind kind) {
        return kind == CompanionKind.NORMALS
            ? policy.normalsEnabled()
            : policy.specularEnabled();
    }

    private static int defaultFillPacked(CompanionKind kind) {
        return kind == CompanionKind.NORMALS
            ? NORMALS_DEFAULT_PACKED
            : SPECULAR_DEFAULT_PACKED;
    }

    /** Exact full mip-chain RGBA8 bytes (§4.8): Σ_l max(1,w>>l)·max(1,h>>l)·4. */
    private static long mipChainBytes(int width, int height, int mipmapLevels) {
        long bytes = 0;
        for (int level = 0; level <= mipmapLevels; level++) {
            long levelWidth = Math.max(1, width >> level);
            long levelHeight = Math.max(1, height >> level);
            bytes += levelWidth * levelHeight * 4L;
        }
        return bytes;
    }

    private static void requireCanonicalAtlasOrder(List<AtlasDescriptor> descriptors) {
        for (int i = 1; i < descriptors.size(); i++) {
            String previous = descriptors.get(i - 1).id().value();
            String current = descriptors.get(i).id().value();
            if (EngineOptionData.compareUnsignedUtf8(previous, current) >= 0) {
                throw new IllegalArgumentException(
                    "atlas catalog must be in ascending AtlasId unsigned-UTF8 order: "
                    + previous + " then " + current);
            }
        }
    }
}
