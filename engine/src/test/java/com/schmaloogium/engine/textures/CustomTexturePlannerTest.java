// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.schmaloogium.engine.buffers.FixedSamplerName;
import com.schmaloogium.engine.buffers.TextureParameterSpec;
import com.schmaloogium.engine.config.ColorInternalFormat;
import com.schmaloogium.engine.config.CustomTextureSpec;
import com.schmaloogium.engine.config.EngineFlags;
import com.schmaloogium.engine.config.IdMappingFileFingerprint;
import com.schmaloogium.engine.config.IdMappingFileInput;
import com.schmaloogium.engine.config.IdMappingInput;
import com.schmaloogium.engine.config.IdMappingMacroEnvironment;
import com.schmaloogium.engine.config.MacroConfiguration;
import com.schmaloogium.engine.config.MacroIdentityPolicy;
import com.schmaloogium.engine.config.MappingFileState;
import com.schmaloogium.engine.config.MappingKind;
import com.schmaloogium.engine.config.NoiseTextureSpec;
import com.schmaloogium.engine.config.NoiseRequirement;
import com.schmaloogium.engine.config.OptionCatalogs;
import com.schmaloogium.engine.config.OptionConfiguration;
import com.schmaloogium.engine.config.PixelFormat;
import com.schmaloogium.engine.config.PixelType;
import com.schmaloogium.engine.config.ProgramStateModel;
import com.schmaloogium.engine.config.ResourceRequirements;
import com.schmaloogium.engine.config.ShaderPropertiesModel;
import com.schmaloogium.engine.config.TextureBindingKey;
import com.schmaloogium.engine.config.TexturePropertyStage;
import com.schmaloogium.engine.config.TextureTarget;
import com.schmaloogium.engine.pack.CompatibilityStatus;
import com.schmaloogium.engine.pack.CompanionOptionMacros;
import com.schmaloogium.engine.pack.ConfigurationFingerprint;
import com.schmaloogium.engine.pack.NormalizedPackPath;
import com.schmaloogium.engine.pack.PackAssetSnapshot;
import com.schmaloogium.engine.pack.PackConfiguration;
import com.schmaloogium.engine.pack.PackFrontEnd;
import com.schmaloogium.engine.pack.PackIdentity;
import com.schmaloogium.engine.preprocess.DeclaredGlslType;
import com.schmaloogium.engine.preprocess.SourceCatalog;
import com.schmaloogium.engine.registry.StageId;
import com.schmaloogium.engine.textures.internal.CustomTexturePlanner;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

/**
 * The planner-half §8 checks (PHASE_13_DOC §8.1): exact stage expansion, closed
 * unsupported diagnostics without enum sentinels, canonical ordinal retention over
 * same-name discriminators, and the raw/decoded upload and shape conversions of §4.3.3.
 */
class CustomTexturePlannerTest {

    // ------------------------------------------------------------------ fixtures

    private static final NormalizedPackPath IMAGE = new NormalizedPackPath("shaders/blocks/a.png");

    private static TextureBindingKey key(TexturePropertyStage stage, String sampler,
            OptionalInt discriminator) {
        return new TextureBindingKey(stage, sampler, discriminator);
    }

    private static CustomTextureSpec packPath(TextureBindingKey sampler) {
        return new CustomTextureSpec.PackPath(sampler, IMAGE, Optional.empty());
    }

    private static TextureSourceAsset.ReadyAsset ready2D(int width, int height,
            String configurationIdentity) {
        return new TextureSourceAsset.ReadyAsset(
            new TextureSourceIdentity.OwnedUpload(OwnedTextureSourceKind.PACK_PNG,
                IMAGE.canonicalString(), "content-digest", configurationIdentity),
            com.schmaloogium.engine.gl.TextureAllocationTarget.TEXTURE_2D,
            List.of(width, height, 1), com.schmaloogium.engine.gl.ColorInternalFormat.RGBA8,
            new DeclaredGlslType.Sampler(com.schmaloogium.engine.preprocess.SampledKind.FLOAT,
                com.schmaloogium.engine.preprocess.TextureDimension.D2, false, false, false),
            new TextureParameterSpec(com.schmaloogium.engine.gl.TextureMinFilter.NEAREST,
                com.schmaloogium.engine.gl.TextureMagFilter.NEAREST,
                com.schmaloogium.engine.gl.TextureWrap.REPEAT),
            "content-digest", "sidecar-digest");
    }

    private static CustomTexturePlanner.PreparedParameterization parameterization() {
        return new CustomTexturePlanner.PreparedParameterization(
            new TextureParameterSpec(com.schmaloogium.engine.gl.TextureMinFilter.NEAREST,
                com.schmaloogium.engine.gl.TextureMagFilter.NEAREST,
                com.schmaloogium.engine.gl.TextureWrap.REPEAT),
            "parameter-fingerprint");
    }

    private static Map<CustomTextureSpec, TextureSourceAsset> readyAssets(
            List<CustomTextureSpec> specs) {
        Map<CustomTextureSpec, TextureSourceAsset> assets = new HashMap<>();
        for (CustomTextureSpec spec : specs) {
            assets.put(spec, ready2D(8, 4, "config-identity"));
        }
        return assets;
    }

    private static Map<CustomTextureSpec, CustomTexturePlanner.PreparedParameterization>
            parameterizations(List<CustomTextureSpec> specs) {
        Map<CustomTextureSpec, CustomTexturePlanner.PreparedParameterization> map =
            new HashMap<>();
        for (CustomTextureSpec spec : specs) {
            map.put(spec, parameterization());
        }
        return map;
    }

    private static CustomTexturePlanner.Result plan(List<CustomTextureSpec> specs) {
        PackConfiguration configuration = configuration(specs,
            new NoiseTextureSpec.Generated(),
            new NoiseRequirement(false, 256));
        return CustomTexturePlanner.plan(configuration, readyAssets(specs),
            parameterizations(specs));
    }

    /**
     * Inert-minimal but genuinely validated PackConfiguration; the planner reads only
     * {@code properties().textures()}.
     */
    private static PackConfiguration configuration(List<CustomTextureSpec> textures,
            NoiseTextureSpec noise, NoiseRequirement noiseRequirement) {
        int schemaVersion = PackFrontEnd.CURRENT_SCHEMA_VERSION;
        PackIdentity pack = new PackIdentity(new NormalizedPackPath("shaders"), Map.of());
        var catalog = OptionCatalogs.create(List.of(), new Object(), false);
        ShaderPropertiesModel properties = new ShaderPropertiesModel(
            EngineFlags.allDefault(), List.of(), List.copyOf(textures), List.of(),
            noise, List.of(), new ProgramStateModel(Map.of()), List.of());
        ResourceRequirements resources = new ResourceRequirements(null, Map.of(), null,
            null, Map.of(), null, null, noiseRequirement);
        IdMappingFileInput absentBlock = new IdMappingFileInput(MappingKind.BLOCK,
            MappingFileState.ABSENT, List.of(), List.of(),
            new IdMappingFileFingerprint("block-fp"));
        IdMappingFileInput absentItem = new IdMappingFileInput(MappingKind.ITEM,
            MappingFileState.ABSENT, List.of(), List.of(),
            new IdMappingFileFingerprint("item-fp"));
        IdMappingFileInput absentEntity = new IdMappingFileInput(MappingKind.ENTITY,
            MappingFileState.ABSENT, List.of(), List.of(),
            new IdMappingFileFingerprint("entity-fp"));
        IdMappingFileInput absentLayer = new IdMappingFileInput(MappingKind.LAYER,
            MappingFileState.ABSENT, List.of(), List.of(),
            new IdMappingFileFingerprint("layer-fp"));
        return new PackConfiguration(schemaVersion, pack, CompatibilityStatus.COMPATIBLE,
            Map.of(), new EmptySources(), emptyAssets(pack),
            new OptionConfiguration(catalog, catalog.defaultState(), List.of(), null,
                Map.of(), null, Map.of()),
            new MacroConfiguration(MacroIdentityPolicy.OPTION_1, List.of(), List.of(),
                new CompanionOptionMacros(false, false), List.of(), List.of(), Map.of(),
                List.of()),
            properties, resources,
            new IdMappingInput(schemaVersion, new IdMappingMacroEnvironment(11904, List.of()),
                absentBlock, absentItem, absentEntity, absentLayer),
            List.of(), new ConfigurationFingerprint("config-fp"));
    }

    private static PackAssetSnapshot emptyAssets(PackIdentity pack) {
        try {
            Constructor<?> constructor = Class
                .forName("com.schmaloogium.engine.pack.PackAssetSnapshotImpl")
                .getDeclaredConstructor(PackIdentity.class, Map.class);
            constructor.setAccessible(true);
            return (PackAssetSnapshot) constructor.newInstance(pack, Map.of());
        } catch (ReflectiveOperationException failure) {
            throw new AssertionError("cannot mint an empty pack-asset snapshot", failure);
        }
    }

    private static final class EmptySources implements SourceCatalog {
        @Override
        public List<com.schmaloogium.engine.preprocess.SourceDocument> sources() {
            return List.of();
        }

        @Override
        public List<com.schmaloogium.engine.preprocess.SourceKey> roots() {
            return List.of();
        }

        @Override
        public Set<com.schmaloogium.engine.config.ProgramKey> executablePrograms() {
            return Set.of();
        }

        @Override
        public Optional<com.schmaloogium.engine.preprocess.SourceDocument> source(
                com.schmaloogium.engine.preprocess.SourceId id) {
            return Optional.empty();
        }

        @Override
        public List<com.schmaloogium.engine.preprocess.IncludeEdge> includeEdges() {
            return List.of();
        }

        @Override
        public com.schmaloogium.engine.preprocess.SourceMaterializer materializer() {
            return null;
        }
    }

    // ------------------------------------------------------------------ named checks

    /** §8.1 custom_stageExpansionExact. */
    @Test
    void custom_stageExpansionExact() {
        List<CustomTextureSpec> specs = List.of(
            packPath(key(TexturePropertyStage.GBUFFERS, "texture", OptionalInt.empty())),
            packPath(key(TexturePropertyStage.DEFERRED, "colortex1", OptionalInt.empty())),
            packPath(key(TexturePropertyStage.COMPOSITE, "colortex1", OptionalInt.empty())),
            packPath(key(TexturePropertyStage.GBUFFERS, "tex", OptionalInt.of(2))));
        CustomTexturePlanner.Result result = plan(specs);

        assertEquals(Set.of(StageId.GBUFFERS, StageId.SHADOW),
            result.entries().get(0).stages());
        assertEquals(Set.of(StageId.DEFERRED), result.entries().get(1).stages());
        assertEquals(Set.of(StageId.COMPOSITE, StageId.FINAL),
            result.entries().get(2).stages());

        // tex is legal only in SHADOW: the GBUFFERS expansion yields an entry covering
        // SHADOW only and a typed STAGE_COLUMN diagnostic for GBUFFERS.
        assertEquals(Set.of(StageId.SHADOW), result.entries().get(3).stages());
        assertEquals(1, result.unsupported().size());
        UnsupportedBinding unsupported = result.unsupported().get(0);
        assertEquals(UnsupportedReason.STAGE_COLUMN, unsupported.reason());
        assertEquals(StageId.GBUFFERS, unsupported.expandedStage());
        assertEquals(FixedSamplerName.TEX,
            assertInstanceOf(RequestedTextureTarget.KnownSampler.class,
                unsupported.requestedTarget()).sampler());
        assertEquals(TexturePropertyStage.GBUFFERS, unsupported.key().stage());
        assertEquals("tex", unsupported.key().sampler());
    }

    /** §8.1 unsupported_noEnumSentinel. */
    @Test
    void unsupported_noEnumSentinel() {
        CustomTexturePlanner.Result unknown = plan(List.of(
            packPath(key(TexturePropertyStage.GBUFFERS, "notafixedname",
                OptionalInt.empty()))));
        assertTrue(unknown.entries().isEmpty());
        assertEquals(1, unknown.unsupported().size());
        UnsupportedBinding binding = unknown.unsupported().get(0);
        assertEquals(UnsupportedReason.KEY_DOMAIN, binding.reason());
        assertEquals("notafixedname",
            assertInstanceOf(RequestedTextureTarget.UnknownSampler.class,
                binding.requestedTarget()).exactName());
        assertEquals(StageId.GBUFFERS, binding.expandedStage());

        // normals is a WORLD-column name; a DEFERRED key is a stage-column rejection
        // carrying the typed enum, never an invented sentinel.
        CustomTexturePlanner.Result knownOutOfStage = plan(List.of(
            packPath(key(TexturePropertyStage.DEFERRED, "normals", OptionalInt.empty()))));
        assertTrue(knownOutOfStage.entries().isEmpty());
        assertEquals(1, knownOutOfStage.unsupported().size());
        UnsupportedBinding stageColumn = knownOutOfStage.unsupported().get(0);
        assertEquals(UnsupportedReason.STAGE_COLUMN, stageColumn.reason());
        assertEquals(FixedSamplerName.NORMALS,
            assertInstanceOf(RequestedTextureTarget.KnownSampler.class,
                stageColumn.requestedTarget()).sampler());
        assertEquals(StageId.DEFERRED, stageColumn.expandedStage());
    }

    /** §8.1 sharedUnit_exactNameAndOrdinal, planner half: ordinals follow canonical order. */
    @Test
    void sharedUnit_exactNameAndOrdinal() {
        List<CustomTextureSpec> shuffled = new ArrayList<>(List.of(
            packPath(key(TexturePropertyStage.DEFERRED, "gaux1", OptionalInt.of(9))),
            packPath(key(TexturePropertyStage.DEFERRED, "gaux1", OptionalInt.empty())),
            packPath(key(TexturePropertyStage.DEFERRED, "gaux1", OptionalInt.of(0)))));
        shuffled.sort(Comparator.comparingInt(spec -> {
            OptionalInt discriminator = spec instanceof CustomTextureSpec.PackPath packPath
                ? packPath.key().duplicateDiscriminator()
                : OptionalInt.empty();
            return discriminator.isPresent() ? discriminator.getAsInt() + 1 : 0;
        }));

        CustomTexturePlanner.Result result = plan(shuffled);
        assertEquals(3, result.entries().size());
        assertTrue(result.unsupported().isEmpty());
        for (int i = 0; i < result.entries().size(); i++) {
            CustomTexturePlanEntry entry = result.entries().get(i);
            assertEquals(i, entry.phase3Ordinal(),
                "phase3Ordinal is the zero-based canonical list position");
            assertEquals(FixedSamplerName.GAUX1, entry.name());
            assertEquals(Set.of(StageId.DEFERRED), entry.stages());
        }
        // The canonical order (absent then 0..9) retained each key's own discriminator.
        assertEquals(OptionalInt.empty(), result.entries().get(0).key().duplicateDiscriminator());
        assertEquals(OptionalInt.of(0), result.entries().get(1).key().duplicateDiscriminator());
        assertEquals(OptionalInt.of(9), result.entries().get(2).key().duplicateDiscriminator());
    }

    // ------------------------------------------------------------------ §4.3.3 conversions

    @Test
    void rawTargetShapeAndUploadConvertOneToOne() {
        TextureBindingKey texture3D =
            key(TexturePropertyStage.DEFERRED, "gaux1", OptionalInt.empty());
        CustomTextureSpec.Raw raw3D = new CustomTextureSpec.Raw(texture3D,
            new NormalizedPackPath("shaders/raw/volume.raw"), TextureTarget.TEXTURE_3D,
            ColorInternalFormat.RGB32UI, List.of(2, 3, 4), PixelFormat.RGB_INTEGER,
            PixelType.UNSIGNED_INT, Optional.empty());
        TextureBindingKey rectangle =
            key(TexturePropertyStage.DEFERRED, "gaux2", OptionalInt.empty());
        CustomTextureSpec.Raw rawRect = new CustomTextureSpec.Raw(rectangle,
            new NormalizedPackPath("shaders/raw/plane.raw"), TextureTarget.RECTANGLE,
            ColorInternalFormat.RGBA32F, List.of(6, 7), PixelFormat.RGBA,
            PixelType.FLOAT, Optional.empty());

        CustomTexturePlanner.Result result = plan(List.of(raw3D, rawRect));

        assertEquals(2, result.entries().size());
        CustomTexturePlanEntry entry3D = result.entries().get(0);
        assertEquals(com.schmaloogium.engine.gl.TextureAllocationTarget.TEXTURE_3D,
            entry3D.target());
        assertEquals(com.schmaloogium.engine.preprocess.SampledKind.UNSIGNED_INT,
            entry3D.shape().sample());
        assertEquals(com.schmaloogium.engine.preprocess.TextureDimension.D3,
            entry3D.shape().dimension());
        TextureUploadSpec.ThreeD upload3D = assertInstanceOf(TextureUploadSpec.ThreeD.class,
            entry3D.upload());
        assertEquals(2, upload3D.width());
        assertEquals(3, upload3D.height());
        assertEquals(4, upload3D.depth());
        assertEquals(com.schmaloogium.engine.gl.ColorInternalFormat.RGB32UI,
            upload3D.internalFormat());
        assertEquals(com.schmaloogium.engine.gl.PixelFormat.RGB_INTEGER,
            upload3D.pixelFormat());
        assertEquals(com.schmaloogium.engine.gl.PixelType.UNSIGNED_INT,
            upload3D.pixelType());

        CustomTexturePlanEntry entryRect = result.entries().get(1);
        assertEquals(com.schmaloogium.engine.gl.TextureAllocationTarget.RECTANGLE,
            entryRect.target());
        assertEquals(com.schmaloogium.engine.preprocess.TextureDimension.RECTANGLE,
            entryRect.shape().dimension());
        TextureUploadSpec.Rectangle uploadRect =
            assertInstanceOf(TextureUploadSpec.Rectangle.class, entryRect.upload());
        assertEquals(6, uploadRect.width());
        assertEquals(7, uploadRect.height());
    }

    @Test
    void rawInternalFormatSignednessFollowsSpelling() {
        TextureBindingKey signed = key(TexturePropertyStage.DEFERRED, "gaux1",
            OptionalInt.empty());
        CustomTextureSpec.Raw rawSigned = new CustomTextureSpec.Raw(signed,
            new NormalizedPackPath("shaders/raw/s.raw"), TextureTarget.TEXTURE_2D,
            ColorInternalFormat.RGB32I, List.of(1, 2), PixelFormat.RGB_INTEGER,
            PixelType.INT, Optional.empty());

        CustomTexturePlanner.Result result = plan(List.of(rawSigned));
        assertEquals(com.schmaloogium.engine.preprocess.SampledKind.SIGNED_INT,
            result.entries().get(0).shape().sample());
    }

    @Test
    void decodedAssetsCarryRgba8TwoDimensionalUploadFromPreparedExtent() {
        List<CustomTextureSpec> specs = List.of(
            packPath(key(TexturePropertyStage.GBUFFERS, "texture", OptionalInt.empty())),
            new CustomTextureSpec.MinecraftResource(
                key(TexturePropertyStage.GBUFFERS, "lightmap", OptionalInt.empty()),
                "minecraft:textures/environment/lightmap"));
        CustomTexturePlanner.Result result = plan(specs);
        assertEquals(2, result.entries().size());
        for (CustomTexturePlanEntry entry : result.entries()) {
            assertEquals(com.schmaloogium.engine.gl.TextureAllocationTarget.TEXTURE_2D,
                entry.target());
            TextureUploadSpec.TwoD upload = assertInstanceOf(TextureUploadSpec.TwoD.class,
                entry.upload());
            assertEquals(8, upload.width());
            assertEquals(4, upload.height());
            assertEquals(com.schmaloogium.engine.gl.ColorInternalFormat.RGBA8,
                upload.internalFormat());
            assertEquals(com.schmaloogium.engine.gl.PixelFormat.RGBA, upload.pixelFormat());
            assertEquals(com.schmaloogium.engine.gl.PixelType.UNSIGNED_BYTE,
                upload.pixelType());
            assertEquals("config-identity",
                assertInstanceOf(TextureSourceIdentity.OwnedUpload.class, entry.source())
                    .configurationIdentity());
        }
    }

    @Test
    void rawArityMismatchIsDefensive() {
        CustomTextureSpec.Raw malformed = new CustomTextureSpec.Raw(
            key(TexturePropertyStage.DEFERRED, "gaux1", OptionalInt.empty()),
            new NormalizedPackPath("shaders/raw/two.raw"), TextureTarget.TEXTURE_2D,
            ColorInternalFormat.RGBA8, List.of(2, 3, 4), PixelFormat.RGBA,
            PixelType.UNSIGNED_BYTE, Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> plan(List.of(malformed)));
    }

    @Test
    void missingOrFailedPreparationYieldsNeitherEntryNorKeyDiagnostic() {
        CustomTextureSpec missing = packPath(
            key(TexturePropertyStage.GBUFFERS, "texture", OptionalInt.empty()));
        PackConfiguration configuration = configuration(List.of(missing),
            new NoiseTextureSpec.Generated(), new NoiseRequirement(false, 256));
        CustomTexturePlanner.Result result = CustomTexturePlanner.plan(configuration,
            Map.of(), Map.of());
        assertTrue(result.entries().isEmpty());
        assertTrue(result.unsupported().isEmpty());

        CustomTextureSpec failed = packPath(
            key(TexturePropertyStage.GBUFFERS, "texture", OptionalInt.empty()));
        PackConfiguration configurationFailed = configuration(List.of(failed),
            new NoiseTextureSpec.Generated(), new NoiseRequirement(false, 256));
        CustomTexturePlanner.Result failedResult = CustomTexturePlanner.plan(
            configurationFailed,
            Map.of(failed, new TextureSourceAsset.FailedAsset(new TextureFailure(
                TextureFailureCode.SOURCE_DECODE_FAILED, "decode.failed", "diag-1",
                "logical"))),
            Map.of());
        assertTrue(failedResult.entries().isEmpty());
        assertTrue(failedResult.unsupported().isEmpty());
    }

    @Test
    void entriesPreserveCanonicalInputListOrderAndKeys() {
        List<CustomTextureSpec> specs = List.of(
            packPath(key(TexturePropertyStage.COMPOSITE, "colortex0", OptionalInt.empty())),
            packPath(key(TexturePropertyStage.GBUFFERS, "normals", OptionalInt.of(3))),
            packPath(key(TexturePropertyStage.DEFERRED, "noisetex", OptionalInt.empty())));
        CustomTexturePlanner.Result result = plan(specs);
        assertEquals(3, result.entries().size());
        for (int i = 0; i < specs.size(); i++) {
            assertEquals(i, result.entries().get(i).phase3Ordinal());
            assertEquals(((CustomTextureSpec.PackPath) specs.get(i)).key(),
                result.entries().get(i).key());
        }
        assertTrue(result.entries().get(1).stages().contains(StageId.SHADOW));
        assertTrue(result.entries().get(1).stages().contains(StageId.GBUFFERS));
    }
}
