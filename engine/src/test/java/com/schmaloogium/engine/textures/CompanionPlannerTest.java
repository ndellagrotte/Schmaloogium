// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.buffers.AtlasId;
import com.schmaloogium.engine.buffers.CompanionKind;
import com.schmaloogium.engine.textures.internal.CompanionPlanner;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The companion §8 checks (PHASE_13_DOC §8.2): total per-sprite discovery, exact
 * base-atlas layout and mip-chain copying, the §4.1.4 contract default fills, the
 * §4.1.1/§4.1.6 independent-preference macro law with the plan-level pairing rule, and
 * the §4.8 full-mip-chain memory estimate.
 */
class CompanionPlannerTest {

    private static final AtlasId ATLAS = new AtlasId("minecraft:textures/atlas/blocks.png");

    // ------------------------------------------------------------------ fixtures

    private static AtlasCatalog catalog(AtlasDescriptor... atlases) {
        return new AtlasCatalog(List.of(atlases));
    }
    /** A non-square 4×6 atlas with mips 2 and two sprites in canonical icon order. */
    private static AtlasDescriptor atlas4x6() {
        return new AtlasDescriptor(ATLAS, 4, 6, 2, List.of(
            SpriteDescriptor.staticSprite("block/dirt", 2, 0, 2, 2),
            SpriteDescriptor.staticSprite("block/stone", 0, 0, 2, 2)));
    }

    private static CompanionPolicy bothEnabled() {
        return new CompanionPolicy(true, true, CompanionDemandSource.DECLARED_SAMPLERS);
    }

    private static CompanionPlanner.Discovery discoveryOf(Map<String, String> present) {
        return (base, kind, iconName) -> Optional.ofNullable(present.get(iconName));
    }

    private static String suffix(CompanionKind kind) {
        return kind == CompanionKind.NORMALS ? "_n" : "_s";
    }

    // ------------------------------------------------------------------ named checks

    /** §8.2 companion_discoveryPerSprite. */
    @Test
    void companion_discoveryPerSprite() {
        AtomicInteger calls = new AtomicInteger();
        CompanionPlanner.Discovery counting = (base, kind, iconName) -> {
            calls.incrementAndGet();
            return iconName.equals("block/stone")
                ? Optional.of("textures/block/stone" + suffix(kind) + ".png")
                : Optional.empty();
        };

        CompanionPlanner.Result result = CompanionPlanner.plan(catalog(atlas4x6()),
            bothEnabled(), new CompanionMacroState(true, true), counting);

        assertEquals(2, result.plans().size());
        assertEquals(4, calls.get(), "every base sprite × enabled kind is asked exactly once");

        CompanionAtlasPlan normals = result.plans().get(0);
        CompanionAtlasPlan specular = result.plans().get(1);
        assertEquals(CompanionKind.NORMALS, normals.kind());
        assertEquals(CompanionKind.SPECULAR, specular.kind());

        // Total discovery: per sprite/kind exactly one Resource or DefaultFill, never absent.
        assertEquals(2, normals.sprites().size());
        assertEquals(2, specular.sprites().size());
        // Canonical sprite order: dirt (index 0, undiscovered), stone (index 1, present).
        assertInstanceOf(CompanionOrigin.DefaultFill.class, normals.sprites().get(0).origin());
        assertInstanceOf(CompanionOrigin.Resource.class, normals.sprites().get(1).origin());
        assertEquals("textures/block/stone_n.png",
            ((CompanionOrigin.Resource) normals.sprites().get(1).origin()).resourceIdentity());
        assertInstanceOf(CompanionOrigin.DefaultFill.class, specular.sprites().get(0).origin());
        assertInstanceOf(CompanionOrigin.Resource.class, specular.sprites().get(1).origin());
        assertEquals("textures/block/stone_s.png",
            ((CompanionOrigin.Resource) specular.sprites().get(1).origin()).resourceIdentity());

        // Disabled kinds produce no plans.
        CompanionPlanner.Result normalsOnly = CompanionPlanner.plan(catalog(atlas4x6()),
            new CompanionPolicy(true, false, CompanionDemandSource.DECLARED_SAMPLERS),
            new CompanionMacroState(true, false), discoveryOf(Map.of()));
        assertEquals(1, normalsOnly.plans().size());
        assertEquals(CompanionKind.NORMALS, normalsOnly.plans().get(0).kind());
    }

    /** §8.2 companion_layoutAndMipChainMatchBase. */
    @Test
    void companion_layoutAndMipChainMatchBase() {
        CompanionPlanner.Result result = CompanionPlanner.plan(catalog(atlas4x6()),
            bothEnabled(), new CompanionMacroState(true, true),
            discoveryOf(Map.of("block/stone", "textures/block/stone_n.png")));

        for (CompanionAtlasPlan plan : result.plans()) {
            assertEquals(4, plan.width(), "companion copies the base extent exactly");
            assertEquals(6, plan.height(), "companion copies the base extent exactly");
            assertEquals(2, plan.mipmapLevels(), "companion copies the base mip chain");
            assertEquals(2, plan.sprites().size());
            assertEquals(List.of("block/dirt", "block/stone"),
                plan.sprites().stream().map(CompanionSpriteSource::iconName).toList(),
                "sprite order is the base atlas's canonical order");
        }
        assertEquals(ATLAS, result.plans().get(0).base());

        // The missing-sprite fill inside a mipped full atlas inherits the atlas object's
        // mipped policy (D-P13-42), never the standalone NEAREST policy.
        var mipped = com.schmaloogium.engine.textures.internal.ParameterPolicy
            .filledSpritePolicy(2);
        var nonmipped = com.schmaloogium.engine.textures.internal.ParameterPolicy
            .filledSpritePolicy(0);
        assertEquals(com.schmaloogium.engine.gl.TextureMinFilter.NEAREST_MIPMAP_LINEAR,
            mipped.minFilter());
        assertEquals(com.schmaloogium.engine.gl.TextureMinFilter.NEAREST,
            nonmipped.minFilter());
        assertEquals(com.schmaloogium.engine.textures.internal.ParameterPolicy
            .companionAtlasPolicy(2), mipped);
    }

    /** §8.2 companion_missingNormalUsesContractDefault. */
    @Test
    void companion_missingNormalUsesContractDefault() {
        assertArrayEquals(new byte[] {(byte) 0xFF, 0x7F, 0x7F, (byte) 0xFF},
            CompanionPlanner.defaultFillBytes(CompanionKind.NORMALS),
            "the MSB-first literal §4.1.4 byte pattern, never silently re-encoded");

        CompanionPlanner.Result result = CompanionPlanner.plan(catalog(atlas4x6()),
            new CompanionPolicy(true, false, CompanionDemandSource.ALWAYS_ON_FALLBACK),
            new CompanionMacroState(true, false), discoveryOf(Map.of()));
        CompanionAtlasPlan normals = result.plans().get(0);
        assertEquals(0xFF7F7FFF, normals.defaultFill());
        assertEquals(0xFF7F7FFF,
            ((CompanionOrigin.DefaultFill) normals.sprites().get(1).origin()).packedRgba());
    }

    /** §8.2 companion_missingSpecularUsesZeroDefault. */
    @Test
    void companion_missingSpecularUsesZeroDefault() {
        assertArrayEquals(new byte[] {0, 0, 0, 0},
            CompanionPlanner.defaultFillBytes(CompanionKind.SPECULAR));

        CompanionPlanner.Result result = CompanionPlanner.plan(catalog(atlas4x6()),
            new CompanionPolicy(false, true, CompanionDemandSource.ALWAYS_ON_FALLBACK),
            new CompanionMacroState(false, true), discoveryOf(Map.of()));
        CompanionAtlasPlan specular = result.plans().get(0);
        assertEquals(0x00000000, specular.defaultFill());
        for (CompanionSpriteSource sprite : specular.sprites()) {
            assertEquals(0x00000000,
                ((CompanionOrigin.DefaultFill) sprite.origin()).packedRgba());
        }
    }

    /** §8.1 macro_independentPreferencesBeforeJcpp. */
    @Test
    void macro_independentPreferencesBeforeJcpp() {
        // All four preference pairs with an active, capable pack.
        assertEquals(new CompanionMacroState(false, false),
            CompanionMacroPolicy.preliminaryMacroState(
                new PreliminaryCompanionDemand(true, true, false, false)));
        assertEquals(new CompanionMacroState(true, false),
            CompanionMacroPolicy.preliminaryMacroState(
                new PreliminaryCompanionDemand(true, true, true, false)));
        assertEquals(new CompanionMacroState(false, true),
            CompanionMacroPolicy.preliminaryMacroState(
                new PreliminaryCompanionDemand(true, true, false, true)));
        assertEquals(new CompanionMacroState(true, true),
            CompanionMacroPolicy.preliminaryMacroState(
                new PreliminaryCompanionDemand(true, true, true, true)));

        // Off short-circuits: an inactive pack yields OFF even with true preferences.
        assertEquals(CompanionMacroState.OFF, CompanionMacroPolicy.preliminaryMacroState(
            new PreliminaryCompanionDemand(false, true, true, true)));

        // Incapability yields OFF for the preference pair that was decoded.
        assertEquals(CompanionMacroState.OFF, CompanionMacroPolicy.preliminaryMacroState(
            new PreliminaryCompanionDemand(true, false, true, true)));

        // Pairing matrix: a true macro bit over a disabled policy bit is inconsistent.
        assertTrue(CompanionPlanner.pairingConsistent(
            new CompanionPolicy(true, true, CompanionDemandSource.DECLARED_SAMPLERS),
            new CompanionMacroState(true, true)));
        assertTrue(CompanionPlanner.pairingConsistent(
            new CompanionPolicy(true, false, CompanionDemandSource.DECLARED_SAMPLERS),
            new CompanionMacroState(true, false)));
        assertTrue(CompanionPlanner.pairingConsistent(
            new CompanionPolicy(false, false, CompanionDemandSource.CAPABILITY_GATED_OFF),
            CompanionMacroState.OFF));
        assertTrue(CompanionPlanner.pairingConsistent(
            new CompanionPolicy(true, true, CompanionDemandSource.DECLARED_SAMPLERS),
            CompanionMacroState.OFF),
            "an enabled kind with a false macro is consistent (R4 may only reduce)");
        var inconsistent = new CompanionPolicy(false, true,
            CompanionDemandSource.CAPABILITY_GATED_OFF);
        var macroOverDisabled = new CompanionMacroState(true, false);
        assertTrue(!CompanionPlanner.pairingConsistent(inconsistent, macroOverDisabled));
        var inconsistentSpecular = new CompanionPolicy(true, false,
            CompanionDemandSource.CAPABILITY_GATED_OFF);
        assertTrue(!CompanionPlanner.pairingConsistent(inconsistentSpecular,
            new CompanionMacroState(false, true)));

        // The inconsistent pair is INVALID_REQUEST at plan level.
        assertThrows(IllegalArgumentException.class, () -> CompanionPlanner.plan(
            catalog(atlas4x6()), inconsistent, macroOverDisabled, discoveryOf(Map.of())));
    }

    // ------------------------------------------------------------------ estimate and order

    /** §4.8: exact full mip-chain RGBA8 bytes plus 4 bytes per enabled kind. */
    @Test
    void memoryIsExactFullMipChainPlusStandaloneDefaults() {
        CompanionPlanner.Result both = CompanionPlanner.plan(catalog(atlas4x6()),
            bothEnabled(), new CompanionMacroState(true, true), discoveryOf(Map.of()));
        // 4x6 mips 2: level0 24 texels + level1 6 + level2 1 = 31 texels × 4 bytes = 124
        // per kind per atlas, plus the 1×1 standalone default per kind.
        assertEquals(124L * 2 + 4L * 2, both.memory().companionBytes());
        assertEquals(0, both.memory().noiseBytes());
        assertEquals(0, both.memory().customBytes());

        CompanionPlanner.Result normalsOnly = CompanionPlanner.plan(catalog(atlas4x6()),
            new CompanionPolicy(true, false, CompanionDemandSource.DECLARED_SAMPLERS),
            new CompanionMacroState(true, false), discoveryOf(Map.of()));
        assertEquals(124L + 4L, normalsOnly.memory().companionBytes());

        CompanionPlanner.Result none = CompanionPlanner.plan(catalog(atlas4x6()),
            new CompanionPolicy(false, false, CompanionDemandSource.CAPABILITY_GATED_OFF),
            CompanionMacroState.OFF, discoveryOf(Map.of()));
        assertTrue(none.plans().isEmpty());
        assertEquals(new TextureMemoryEstimate(0, 0, 0), none.memory());
    }

    @Test
    void planCoversEveryEnabledKindPerAtlasInCanonicalAtlasOrder() {
        AtlasDescriptor atlasB = atlas4x6();
        AtlasDescriptor atlasA = new AtlasDescriptor(new AtlasId("minecraft:textures/atlas/a.png"),
            8, 8, 0, List.of(SpriteDescriptor.staticSprite("block/planks", 0, 0, 4, 4)));
        // Canonical ascending AtlasId UTF-8 order: a.png then blocks.png.
        CompanionPlanner.Result result = CompanionPlanner.plan(catalog(atlasA, atlasB),
            bothEnabled(), new CompanionMacroState(true, true), discoveryOf(Map.of()));
        assertEquals(4, result.plans().size());
        assertEquals(atlasA.id(), result.plans().get(0).base());
        assertEquals(CompanionKind.NORMALS, result.plans().get(0).kind());
        assertEquals(CompanionKind.SPECULAR, result.plans().get(1).kind());
        assertEquals(atlasB.id(), result.plans().get(2).base());
        assertEquals(CompanionKind.NORMALS, result.plans().get(2).kind());
        assertEquals(CompanionKind.SPECULAR, result.plans().get(3).kind());
    }

    @Test
    void nonCanonicalAtlasOrderIsRejected() {
        AtlasDescriptor atlasA = new AtlasDescriptor(new AtlasId("minecraft:textures/atlas/a.png"),
            8, 8, 0, List.of(SpriteDescriptor.staticSprite("block/planks", 0, 0, 4, 4)));
        List<AtlasDescriptor> descending = new ArrayList<>(List.of(atlas4x6(), atlasA));
        assertThrows(IllegalArgumentException.class,
            () -> CompanionPlanner.plan(catalog(descending.toArray(AtlasDescriptor[]::new)),
                bothEnabled(), new CompanionMacroState(true, true), discoveryOf(Map.of())));
        List<AtlasDescriptor> duplicated = new ArrayList<>(List.of(atlas4x6(), atlas4x6()));
        assertThrows(IllegalArgumentException.class,
            () -> CompanionPlanner.plan(catalog(duplicated.toArray(AtlasDescriptor[]::new)),
                bothEnabled(), new CompanionMacroState(true, true), discoveryOf(Map.of())));
    }
}
