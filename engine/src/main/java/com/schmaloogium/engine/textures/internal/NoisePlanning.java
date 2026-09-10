// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures.internal;

import com.schmaloogium.engine.buffers.TextureParameterSpec;
import com.schmaloogium.engine.config.NoiseRequirement;
import com.schmaloogium.engine.config.NoiseTextureSpec;
import com.schmaloogium.engine.config.ResourceRequirements;
import com.schmaloogium.engine.gl.TextureMagFilter;
import com.schmaloogium.engine.gl.TextureMinFilter;
import com.schmaloogium.engine.gl.TextureWrap;
import com.schmaloogium.engine.textures.NoisePlan;
import com.schmaloogium.engine.textures.OwnedTextureSourceKind;
import com.schmaloogium.engine.textures.TextureMemoryEstimate;
import com.schmaloogium.engine.textures.TextureSourceIdentity;

import java.util.Objects;

/**
 * The pure noise planner (PHASE_13_DOC §4.2): enablement and resolution come from the
 * requirements, the generated identity carries the resolution and the signed-recurrence
 * schema tag, and an override keeps its own image — the declared resolution never resizes
 * a pack-authored override.
 */
public final class NoisePlanning {

    private NoisePlanning() {
    }

    /**
     * Resolves the noise cell. A null or disabled requirement is {@link NoisePlan.Disabled};
     * an enabled requirement with the generated source yields
     * {@link NoisePlan.Generated} at the requirement's resolution (positive, else
     * {@code IllegalArgumentException}); an enabled requirement with a pack override
     * yields {@link NoisePlan.FromPack} recording the declaring resolution.
     */
    public static NoisePlan resolve(ResourceRequirements requirements, NoiseTextureSpec spec) {
        NoiseRequirement requirement = requirements == null ? null : requirements.noise();
        if (requirement == null || !requirement.enabled()) {
            return NoisePlan.disabled();
        }
        Objects.requireNonNull(spec, "spec");
        if (spec instanceof NoiseTextureSpec.Generated && requirement.resolution() <= 0) {
            throw new IllegalArgumentException("noise resolution must be positive: "
                + requirement.resolution());
        }
        return switch (spec) {
            case NoiseTextureSpec.Generated generated ->
                new NoisePlan.Generated(requirement.resolution());
            case NoiseTextureSpec.Override override ->
                new NoisePlan.FromPack(override.image(), override.sidecar(),
                    requirement.resolution());
        };
    }

    /** The generated-noise parameter baseline: LINEAR min, LINEAR mag, REPEAT wrap. */
    public static TextureParameterSpec generatedParameters() {
        return new TextureParameterSpec(TextureMinFilter.LINEAR, TextureMagFilter.LINEAR,
            TextureWrap.REPEAT);
    }

    /**
     * The generated-noise owned-source identity: the logical source names the resolution
     * and the signed-recurrence schema tag; the content digest is the canonical
     * source-domain digest over the kind and that logical source.
     */
    public static TextureSourceIdentity generatedIdentity(int resolution,
            String configurationIdentity) {
        if (resolution <= 0) {
            throw new IllegalArgumentException("resolution must be positive: " + resolution);
        }
        Objects.requireNonNull(configurationIdentity, "configurationIdentity");
        String kind = OwnedTextureSourceKind.GENERATED_NOISE.name();
        String logicalSource = "generated:" + NoiseGenerator.RECURRENCE_SCHEMA_TAG
            + "/" + resolution;
        return new TextureSourceIdentity.OwnedUpload(
            OwnedTextureSourceKind.GENERATED_NOISE, logicalSource,
            TextureDigests.sourceDigest(kind, logicalSource), configurationIdentity);
    }

    /**
     * The §4.8 noise estimate: generated costs resolution² × 3 bytes; a pack override is
     * counted with the custom textures at preparation; disabled costs nothing.
     */
    public static TextureMemoryEstimate noiseMemory(NoisePlan plan) {
        Objects.requireNonNull(plan, "plan");
        return switch (plan) {
            case NoisePlan.Generated generated -> new TextureMemoryEstimate(0,
                (long) generated.resolution() * generated.resolution() * 3L, 0);
            case NoisePlan.FromPack fromPack -> new TextureMemoryEstimate(0, 0, 0);
            case NoisePlan.Disabled disabled -> new TextureMemoryEstimate(0, 0, 0);
        };
    }

    /** Generates the exact contract recurrence plane (delegation to the generator). */
    public static byte[] generate(int resolution) {
        return NoiseGenerator.generateRgb(resolution);
    }
}
