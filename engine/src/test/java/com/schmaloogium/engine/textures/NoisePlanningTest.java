// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.config.NoiseRequirement;
import com.schmaloogium.engine.config.NoiseTextureSpec;
import com.schmaloogium.engine.config.ResourceRequirements;
import com.schmaloogium.engine.gl.TextureMagFilter;
import com.schmaloogium.engine.gl.TextureMinFilter;
import com.schmaloogium.engine.gl.TextureWrap;
import com.schmaloogium.engine.pack.NormalizedPackPath;
import com.schmaloogium.engine.textures.internal.NoiseGenerator;
import com.schmaloogium.engine.textures.internal.NoisePlanning;

import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * The noise §8 checks (PHASE_13_DOC §8.2): enablement and resolution follow the
 * requirements and the absent-directive baseline, the generated identity carries the
 * resolution and the signed-recurrence schema tag, and the estimate is resolution² × 3.
 */
class NoisePlanningTest {

    private static ResourceRequirements requirements(NoiseRequirement noise) {
        return new ResourceRequirements(null, java.util.Map.of(), null, null,
            java.util.Map.of(), null, null, noise);
    }

    private static final NoiseRequirement DISABLED = new NoiseRequirement(false, 256);
    private static final NoiseRequirement ENABLED_256 = new NoiseRequirement(true, 256);

    /** §8.2 noise_resolutionFromRequirementsAndBaseline. */
    @Test
    void noise_resolutionFromRequirementsAndBaseline() {
        // A null requirement is the absent-directive baseline: disabled.
        assertEquals(NoisePlan.Disabled.class,
            NoisePlanning.resolve(null, new NoiseTextureSpec.Generated()).getClass());
        // An explicitly disabled requirement allocates nothing.
        assertInstanceOf(NoisePlan.Disabled.class,
            NoisePlanning.resolve(requirements(DISABLED),
                new NoiseTextureSpec.Generated()));
        // Enabled generated: the requirement's resolution wins.
        assertEquals(new NoisePlan.Generated(256),
            NoisePlanning.resolve(requirements(ENABLED_256),
                new NoiseTextureSpec.Generated()));
        assertEquals(new NoisePlan.Generated(512),
            NoisePlanning.resolve(requirements(new NoiseRequirement(true, 512)),
                new NoiseTextureSpec.Generated()));
        // A nonpositive generated resolution is a broken requirement.
        assertThrows(IllegalArgumentException.class, () -> NoisePlanning.resolve(
            requirements(new NoiseRequirement(true, 0)),
            new NoiseTextureSpec.Generated()));

        // Enabled override: the declared resolution is recorded, never applied to the image.
        NormalizedPackPath image = new NormalizedPackPath("shaders/noise.png");
        NoisePlan plan = NoisePlanning.resolve(requirements(new NoiseRequirement(true, 512)),
            new NoiseTextureSpec.Override(image, Optional.empty()));
        NoisePlan.FromPack fromPack = assertInstanceOf(NoisePlan.FromPack.class, plan);
        assertEquals(image, fromPack.image());
        assertEquals(512, fromPack.declaredResolution());
        assertTrue(fromPack.sidecar().isEmpty(),
            "the override's own dimensions win later; the plan carries none");
    }

    /** §4.8: generated noise is resolution² × 3 bytes; override and disabled cost nothing. */
    @Test
    void noiseMemoryFollowsPlanShape() {
        assertEquals(new TextureMemoryEstimate(0, 196608, 0),
            NoisePlanning.noiseMemory(new NoisePlan.Generated(256)));
        assertEquals(new TextureMemoryEstimate(0, 12, 0),
            NoisePlanning.noiseMemory(new NoisePlan.Generated(2)));
        assertEquals(new TextureMemoryEstimate(0, 0, 0),
            NoisePlanning.noiseMemory(new NoisePlan.FromPack(
                new NormalizedPackPath("shaders/noise.png"), Optional.empty(), 256)));
        assertEquals(new TextureMemoryEstimate(0, 0, 0),
            NoisePlanning.noiseMemory(NoisePlan.disabled()));
    }

    @Test
    void generatedParametersAreLinearLinearRepeat() {
        assertEquals(new com.schmaloogium.engine.buffers.TextureParameterSpec(
            TextureMinFilter.LINEAR, TextureMagFilter.LINEAR, TextureWrap.REPEAT),
            NoisePlanning.generatedParameters());
    }

    @Test
    void generatedIdentityCarriesResolutionAndSchemaTag() {
        TextureSourceIdentity identity =
            NoisePlanning.generatedIdentity(256, "config-identity");
        TextureSourceIdentity.OwnedUpload owned =
            assertInstanceOf(TextureSourceIdentity.OwnedUpload.class, identity);
        assertEquals(OwnedTextureSourceKind.GENERATED_NOISE, owned.sourceKind());
        assertEquals("generated:phase13.noise/xorshift-signed32-v1/256",
            owned.logicalSource());
        assertEquals("config-identity", owned.configurationIdentity());
        assertEquals(
            com.schmaloogium.engine.textures.internal.TextureDigests.sourceDigest(
                "GENERATED_NOISE", "generated:phase13.noise/xorshift-signed32-v1/256"),
            owned.contentDigest());
        assertEquals(identity, NoisePlanning.generatedIdentity(256, "config-identity"),
            "identity is stable for a fixed resolution and configuration");
        assertThrows(IllegalArgumentException.class,
            () -> NoisePlanning.generatedIdentity(0, "config-identity"));
    }

    @Test
    void generateDelegatesToTheContractRecurrence() {
        byte[] expected = NoiseGenerator.generateRgb(4);
        byte[] actual = NoisePlanning.generate(4);
        assertEquals(expected.length, 4 * 4 * 3);
        assertArrayEquals(expected, actual);
        // The §4.2.2 required vector at (x,y,c)=(1,1,1): uploaded byte 141.
        int index = (1 * 4 + 1) * 3 + 1;
        assertEquals((byte) 141, actual[index]);
        assertThrows(IllegalArgumentException.class, () -> NoisePlanning.generate(0));
    }
}
