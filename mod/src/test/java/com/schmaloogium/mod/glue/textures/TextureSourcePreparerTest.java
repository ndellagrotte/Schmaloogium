// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.textures;

import com.schmaloogium.engine.buffers.AtlasId;
import com.schmaloogium.engine.config.BufferMinima;
import com.schmaloogium.engine.config.ColorInternalFormat;
import com.schmaloogium.engine.config.CustomTextureSpec;
import com.schmaloogium.engine.config.EngineFlags;
import com.schmaloogium.engine.config.IdMappingFileFingerprint;
import com.schmaloogium.engine.config.IdMappingFileInput;
import com.schmaloogium.engine.config.IdMappingInput;
import com.schmaloogium.engine.config.IdMappingMacroEnvironment;
import com.schmaloogium.engine.config.MappingFileState;
import com.schmaloogium.engine.config.MappingKind;
import com.schmaloogium.engine.config.MacroDefinition;
import com.schmaloogium.engine.config.MacroConfiguration;
import com.schmaloogium.engine.config.MacroIdentityPolicy;
import com.schmaloogium.engine.config.NoiseRequirement;
import com.schmaloogium.engine.config.NoiseTextureSpec;
import com.schmaloogium.engine.config.OptionCatalogs;
import com.schmaloogium.engine.config.OptionConfiguration;
import com.schmaloogium.engine.config.PixelFormat;
import com.schmaloogium.engine.config.PixelType;
import com.schmaloogium.engine.config.ProgramStateModel;
import com.schmaloogium.engine.config.ResourceRequirements;
import com.schmaloogium.engine.config.ShaderPropertiesModel;
import com.schmaloogium.engine.config.TextureBindingKey;
import com.schmaloogium.engine.config.TexturePropertyStage;
import com.schmaloogium.engine.config.TextureSidecarRef;
import com.schmaloogium.engine.config.TextureTarget;
import com.schmaloogium.engine.pack.CompatibilityStatus;
import com.schmaloogium.engine.pack.CompanionOptionMacros;
import com.schmaloogium.engine.pack.ConfigurationFingerprint;
import com.schmaloogium.engine.pack.NormalizedPackPath;
import com.schmaloogium.engine.pack.PackAssetAcquisition;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.pack.PackAssetAcquisition;
import com.schmaloogium.engine.pack.PackAssetBytes;
import com.schmaloogium.engine.pack.PackAssetSnapshot;
import com.schmaloogium.engine.pack.PackConfiguration;
import com.schmaloogium.engine.pack.PackFrontEnd;
import com.schmaloogium.engine.pack.PackIdentity;
import com.schmaloogium.engine.textures.AtlasCatalog;
import com.schmaloogium.engine.textures.AtlasDescriptor;
import com.schmaloogium.engine.textures.OwnedTextureSourceKind;
import com.schmaloogium.engine.textures.SpriteDescriptor;
import com.schmaloogium.engine.textures.TextureFailureCode;
import com.schmaloogium.engine.textures.TexturePreparation;
import com.schmaloogium.engine.textures.TextureSourceAsset;
import com.schmaloogium.engine.textures.TextureSourceCatalog;
import com.schmaloogium.engine.textures.TextureSourceIdentity;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Catalog preparation over retained same-load assets (§8.2 assets_afterPackRemoval,
 * assets_failureAndPairing, assets_cursorIsolation, schema23_exactNestedGate,
 * sidecar_targetFormatLegality): exact failure classes, atomic sidecar recovery,
 * shared interpretations for duplicate consumers, and byte retention after the host
 * "pack" is gone.
 */
class TextureSourcePreparerTest {

    // ------------------------------------------------------------------ png fixture

    private static byte[] png(int[][] argbPixels) {
        BufferedImage image = new BufferedImage(argbPixels[0].length, argbPixels.length,
            BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < argbPixels.length; y++) {
            for (int x = 0; x < argbPixels[y].length; x++) {
                image.setRGB(x, y, argbPixels[y][x]);
            }
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new AssertionError("in-memory png encode failed", e);
        }
    }

    private static final byte[] TINY_PNG = png(new int[][] {
        {0xFF112233, 0xFF445566},
        {0xFF778899, 0xFFAABBCC},
    });

    // ------------------------------------------------------------------ snapshot fixture

    private enum Availability { AVAILABLE, MISSING, UNREADABLE }

    private record Row(NormalizedPackPath path, byte[] bytes, Availability availability) {
    }

    private static Row available(String path, byte[] bytes) {
        return new Row(path(path), bytes, Availability.AVAILABLE);
    }

    private static Row missing(String path) {
        return new Row(path(path), null, Availability.MISSING);
    }

    private static Row unreadable(String path) {
        return new Row(path(path), null, Availability.UNREADABLE);
    }

    private static NormalizedPackPath path(String canonical) {
        return new NormalizedPackPath(canonical);
    }

    private static final Method AVAILABLE_ROW = rowFactory("available",
        NormalizedPackPath.class, byte[].class);
    private static final Method MISSING_ROW = rowFactory("missing", NormalizedPackPath.class);
    private static final Method UNREADABLE_ROW = rowFactory("unreadable",
        NormalizedPackPath.class);
    private static final Constructor<?> SNAPSHOT_CTOR = snapshotConstructor();

    private static Method rowFactory(String name, Class<?>... parameters) {
        try {
            Method method = Class.forName("com.schmaloogium.engine.pack.PackAssetRow")
                .getDeclaredMethod(name, parameters);
            method.setAccessible(true);
            return method;
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("cannot reach the pack asset row factory", e);
        }
    }

    private static Constructor<?> snapshotConstructor() {
        try {
            Constructor<?> constructor =
                Class.forName("com.schmaloogium.engine.pack.PackAssetSnapshotImpl")
                    .getDeclaredConstructor(PackIdentity.class, Map.class);
            constructor.setAccessible(true);
            return constructor;
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("cannot reach the pack asset snapshot constructor", e);
        }
    }

    /**
     * Mints a real (sealed) PackAssetSnapshot from in-memory rows via the package-private
     * implementation — reflection is contained to this test until P3 ships a public
     * test factory.
     */
    private static PackAssetSnapshot snapshot(PackIdentity pack, Row... rows) {
        Map<NormalizedPackPath, Object> ordered = new HashMap<>();
        for (Row row : rows) {
            try {
                Object rowObject = switch (row.availability()) {
                    case AVAILABLE -> AVAILABLE_ROW.invoke(null, row.path(), row.bytes());
                    case MISSING -> MISSING_ROW.invoke(null, row.path());
                    case UNREADABLE -> UNREADABLE_ROW.invoke(null, row.path());
                };
                ordered.put(row.path(), rowObject);
            } catch (IllegalAccessException e) {
                throw new AssertionError("row factory invocation failed", e);
            } catch (InvocationTargetException e) {
                throw new AssertionError("row factory rejected fixture input",
                    e.getCause());
            }
        }
        try {
            return (PackAssetSnapshot) SNAPSHOT_CTOR.newInstance(pack, ordered);
        } catch (IllegalAccessException e) {
            throw new AssertionError("snapshot construction failed", e);
        } catch (InvocationTargetException e) {
            throw new AssertionError("snapshot rejected fixture rows", e.getCause());
        } catch (java.lang.InstantiationException e) {
            throw new AssertionError("snapshot instantiation failed", e);
        }
    }

    private static PackIdentity identity() {
        return new PackIdentity(path("shaders"), Map.of());
    }

    /**
     * Inert-minimal but genuinely validated PackConfiguration over the fixture snapshot;
     * only schemaVersion, textures, noise, assets, macros and resources vary.
     */
    private static PackConfiguration configuration(int schemaVersion,
                                                   List<CustomTextureSpec> textures,
                                                   NoiseTextureSpec noise,
                                                   PackAssetSnapshot assets,
                                                   CompanionOptionMacros macros) {
        PackIdentity pack = identity();
        var catalog = OptionCatalogs.create(List.of(), new Object(), false);
        ShaderPropertiesModel properties = new ShaderPropertiesModel(
            EngineFlags.allDefault(), List.of(), textures, List.of(), noise, List.of(),
            new ProgramStateModel(Map.of()), List.of());
        IdMappingFileInput absentBlock = absentMapping(MappingKind.BLOCK, "block-fp");
        IdMappingFileInput absentItem = absentMapping(MappingKind.ITEM, "item-fp");
        IdMappingFileInput absentEntity = absentMapping(MappingKind.ENTITY, "entity-fp");
        IdMappingFileInput absentLayer = absentMapping(MappingKind.LAYER, "layer-fp");
        List<MacroDefinition> optionMacros = List.of();
        if (macros.normalMap() || macros.specularMap()) {
            List<MacroDefinition> projected = new ArrayList<>();
            if (macros.normalMap()) {
                projected.add(new MacroDefinition("MC_NORMAL_MAP", "1"));
            }
            if (macros.specularMap()) {
                projected.add(new MacroDefinition("MC_SPECULAR_MAP", "1"));
            }
            optionMacros = List.copyOf(projected);
        }
        return new PackConfiguration(schemaVersion, pack, CompatibilityStatus.COMPATIBLE,
            Map.of(), new EmptySources(), assets,
            new OptionConfiguration(catalog, catalog.defaultState(), List.of(), null,
                Map.of(), null, Map.of()),
            new MacroConfiguration(MacroIdentityPolicy.OPTION_1, List.of(), optionMacros,
                macros, List.of(), List.of(), Map.of(), List.of()),
            properties,
            new ResourceRequirements(new BufferMinima(8, 1, 0, 0), null, null, null,
                null, null, null, new NoiseRequirement(true, 256)),
            new IdMappingInput(schemaVersion, new IdMappingMacroEnvironment(11904, List.of()),
                absentBlock, absentItem, absentEntity, absentLayer),
            List.of(), new ConfigurationFingerprint("config-fp"));
    }

    private static PackConfiguration configuration(List<CustomTextureSpec> textures,
                                                   NoiseTextureSpec noise,
                                                   Row... rows) {
        return configuration(PackFrontEnd.CURRENT_SCHEMA_VERSION, textures, noise,
            snapshot(identity(), rows), new CompanionOptionMacros(false, false));
    }

    private static PackConfiguration configuration(List<CustomTextureSpec> textures,
                                                   NoiseTextureSpec noise,
                                                   CompanionOptionMacros macros) {
        return configuration(PackFrontEnd.CURRENT_SCHEMA_VERSION, textures, noise,
            snapshot(identity()), macros);
    }

    private static IdMappingFileInput absentMapping(MappingKind kind, String fingerprint) {
        return new IdMappingFileInput(kind, MappingFileState.ABSENT,
            List.of(), List.of(), new IdMappingFileFingerprint(fingerprint));
    }
    private static TextureBindingKey key(String sampler) {
        return new TextureBindingKey(TexturePropertyStage.GBUFFERS, sampler,
            OptionalInt.empty());
    }

    private static CustomTextureSpec.PackPath packPath(String image, String sidecar) {
        Optional<TextureSidecarRef> ref = sidecar == null
            ? Optional.empty()
            : Optional.of(new TextureSidecarRef(path(sidecar)));
        return new CustomTextureSpec.PackPath(key("tex"), path(image), ref);
    }
    private static TextureFailureCode failureCode(TextureSourceCatalog catalog, int index) {
        return assertInstanceOf(TextureSourceAsset.FailedAsset.class,
            catalog.assets().get(index)).failure().code();
    }

    private static TextureSourceAsset.ReadyAsset ready(TextureSourceCatalog catalog,
                                                       int index) {
        return assertInstanceOf(TextureSourceAsset.ReadyAsset.class,
            catalog.assets().get(index));
    }

    // ------------------------------------------------------------------ schema gate

    @Test
    void wrongSchemaVersionIsRejectedBeforePreparation() {
        PackConfiguration stale = configuration(22, List.of(),
            new NoiseTextureSpec.Generated(), snapshot(identity()), new CompanionOptionMacros(false, false));
        IllegalStateException rejection = assertThrows(IllegalStateException.class,
            () -> TextureSourcePreparer.prepare(stale, 1L));
        assertTrue(rejection.getMessage().contains("schema gate"));
    }

    @Test
    void currentSchemaPrepares() {
        PackConfiguration configuration = configuration(List.of(),
            new NoiseTextureSpec.Generated());
        TextureSourceCatalog catalog = TextureSourcePreparer.prepare(configuration, 4L);
        assertEquals(4L, catalog.resourceReloadEpoch());
        assertEquals(List.of(), catalog.assets());
    }

    // ------------------------------------------------------------------ assets_failureAndPairing

    @Test
    void missingPrimaryIsSourceUnavailable() {
        PackConfiguration configuration = configuration(
            List.of(packPath("textures/stone.png", null)), new NoiseTextureSpec.Generated(),
            missing("textures/stone.png"));
        assertEquals(TextureFailureCode.SOURCE_UNAVAILABLE,
            failureCode(TextureSourcePreparer.prepare(configuration, 1L), 0));
    }

    @Test
    void invalidReferenceIsIdentityMismatch() {
        // The image path is not declared in the snapshot → InvalidReference(NOT_DECLARED).
        PackConfiguration configuration = configuration(
            List.of(packPath("textures/undeclared.png", null)),
            new NoiseTextureSpec.Generated());
        assertEquals(TextureFailureCode.IDENTITY_MISMATCH,
            failureCode(TextureSourcePreparer.prepare(configuration, 1L), 0));
    }

    @Test
    void decodeFailureIsSourceDecodeFailed() {
        PackConfiguration configuration = configuration(
            List.of(packPath("textures/stone.png", null)), new NoiseTextureSpec.Generated(),
            available("textures/stone.png", "garbage, not a png".getBytes()));
        assertEquals(TextureFailureCode.SOURCE_DECODE_FAILED,
            failureCode(TextureSourcePreparer.prepare(configuration, 1L), 0));
    }

    @Test
    void unreadableOptionalSidecarKeepsEntryReadyAtBaseline() {
        String image = "textures/stone.png";
        String sidecar = "textures/stone.png.mcmeta";
        PackConfiguration configuration = configuration(
            List.of(packPath(image, sidecar)), new NoiseTextureSpec.Generated(),
            available(image, TINY_PNG), unreadable(sidecar));
        TextureSourceCatalog catalog = TextureSourcePreparer.prepare(configuration, 1L);
        TextureSourceAsset.ReadyAsset asset = ready(catalog, 0);
        assertEquals(TexturePreparation.baseline(TexturePreparation.Role.CUSTOM_PNG),
            asset.parameters());
        // The sidecar digest is the canonical UNREADABLE outcome for that reference —
        // no fake digest of partial bytes — obtained through the same public bridge.
        TexturePreparation.PreparedParameters expected = TexturePreparation.prepareParameters(
            TexturePreparation.Role.CUSTOM_PNG,
            Optional.of(new TextureSidecarRef(path(sidecar))), configuration.assets(),
            com.schmaloogium.engine.gl.TextureAllocationTarget.TEXTURE_2D,
            com.schmaloogium.engine.gl.ColorInternalFormat.RGBA8);
        assertEquals(TexturePreparation.SidecarOutcomeTag.UNREADABLE,
            expected.sidecarOutcome());
        assertEquals(expected.sidecarDigest(), asset.sidecarDigest());
    }

    // ------------------------------------------------------------------ raw uploads

    @Test
    void rawExactLengthMismatchIsSourceSizeInvalid() {
        String raw = "raw/field.raw";
        byte[] payload = new byte[23]; // 3x2 RGBA8 expects 24
        CustomTextureSpec.Raw spec = new CustomTextureSpec.Raw(key("rawtex"), path(raw),
            TextureTarget.TEXTURE_2D, ColorInternalFormat.RGBA8, List.of(3, 2),
            PixelFormat.RGBA, PixelType.UNSIGNED_BYTE, Optional.empty());
        PackConfiguration configuration = configuration(List.of(spec),
            new NoiseTextureSpec.Generated(), available(raw, payload));
        assertEquals(TextureFailureCode.SOURCE_SIZE_INVALID,
            failureCode(TextureSourcePreparer.prepare(configuration, 1L), 0));
    }

    @Test
    void rawExactLengthMatchesAtTheBoundary() {
        String raw = "raw/field.raw";
        byte[] payload = new byte[3 * 2 * 4];
        CustomTextureSpec.Raw spec = new CustomTextureSpec.Raw(key("rawtex"), path(raw),
            TextureTarget.TEXTURE_2D, ColorInternalFormat.RGBA8, List.of(3, 2),
            PixelFormat.RGBA, PixelType.UNSIGNED_BYTE, Optional.empty());
        PackConfiguration configuration = configuration(List.of(spec),
            new NoiseTextureSpec.Generated(), available(raw, payload));
        TextureSourceAsset.ReadyAsset asset = ready(
            TextureSourcePreparer.prepare(configuration, 1L), 0);
        assertEquals(List.of(3, 2), asset.dimensions());
        assertEquals(OwnedTextureSourceKind.RAW_BYTES,
            assertInstanceOf(TextureSourceIdentity.OwnedUpload.class, asset.identity())
                .sourceKind());
    }

    @Test
    void rectSidecarRepeatIsRejectedBeforeAllocation() {
        String raw = "raw/rect.raw";
        String sidecar = "raw/rect.raw.mcmeta";
        CustomTextureSpec.Raw spec = new CustomTextureSpec.Raw(key("recttex"), path(raw),
            TextureTarget.RECTANGLE, ColorInternalFormat.RGBA8, List.of(4, 4),
            PixelFormat.RGBA, PixelType.UNSIGNED_BYTE,
            Optional.of(new TextureSidecarRef(path(sidecar))));
        PackConfiguration configuration = configuration(List.of(spec),
            new NoiseTextureSpec.Generated(),
            available(raw, new byte[4 * 4 * 4]),
            available(sidecar, "{\"texture\":{\"clamp\":false}}".getBytes()));
        // clamp=false selects REPEAT; RECTANGLE rejects REPEAT even from a valid sidecar.
        assertEquals(TextureFailureCode.PARAMETERIZATION_UNSUPPORTED,
            failureCode(TextureSourcePreparer.prepare(configuration, 1L), 0));
    }

    @Test
    void rectLegalClampKeepsExactState() {
        String raw = "raw/rect.raw";
        String sidecar = "raw/rect.raw.mcmeta";
        CustomTextureSpec.Raw spec = new CustomTextureSpec.Raw(key("recttex"), path(raw),
            TextureTarget.RECTANGLE, ColorInternalFormat.RGBA8, List.of(4, 4),
            PixelFormat.RGBA, PixelType.UNSIGNED_BYTE,
            Optional.of(new TextureSidecarRef(path(sidecar))));
        PackConfiguration configuration = configuration(List.of(spec),
            new NoiseTextureSpec.Generated(),
            available(raw, new byte[4 * 4 * 4]),
            available(sidecar, "{\"texture\":{\"clamp\":true,\"blur\":false}}".getBytes()));
        TextureSourceAsset.ReadyAsset asset = ready(
            TextureSourcePreparer.prepare(configuration, 1L), 0);
        assertEquals(com.schmaloogium.engine.gl.TextureWrap.CLAMP_TO_EDGE,
            asset.parameters().wrap());
        assertEquals(com.schmaloogium.engine.gl.TextureMinFilter.NEAREST,
            asset.parameters().minFilter());
        assertEquals(com.schmaloogium.engine.gl.TextureMagFilter.NEAREST,
            asset.parameters().magFilter());
    }

    @Test
    void rawIntegerStorageWithLinearBaselineIsRejected() {
        String raw = "raw/ints.raw";
        // R32UI storage: the RAW baseline LINEAR mag filter is illegal without blur=false.
        CustomTextureSpec.Raw spec = new CustomTextureSpec.Raw(key("inttex"), path(raw),
            TextureTarget.TEXTURE_2D, ColorInternalFormat.R32UI, List.of(2, 2),
            PixelFormat.RED_INTEGER, PixelType.UNSIGNED_INT, Optional.empty());
        PackConfiguration configuration = configuration(List.of(spec),
            new NoiseTextureSpec.Generated(),
            available(raw, new byte[2 * 2 * 4]));
        assertEquals(TextureFailureCode.PARAMETERIZATION_UNSUPPORTED,
            failureCode(TextureSourcePreparer.prepare(configuration, 1L), 0));
    }

    @Test
    void rectBeyondRecordedMaxIsSourceSizeInvalid() {
        String raw = "raw/big.raw";
        var caps = new com.schmaloogium.engine.gl.GLCapabilityProfile(4, 6, "4.60",
            "vendor", "renderer", 8, 8, 16, 16, 64, 256, 32, Set.of());
        CustomTextureSpec.Raw spec = new CustomTextureSpec.Raw(key("recttex"), path(raw),
            TextureTarget.RECTANGLE, ColorInternalFormat.RGBA8, List.of(64, 64),
            PixelFormat.RGBA, PixelType.UNSIGNED_BYTE, Optional.empty());
        PackConfiguration configuration = configuration(List.of(spec),
            new NoiseTextureSpec.Generated(), available(raw, new byte[64 * 64 * 4]));
        assertEquals(TextureFailureCode.SOURCE_SIZE_INVALID,
            failureCode(TextureSourcePreparer.prepare(configuration, 1L, caps), 0));
    }

    @Test
    void unsupportedRectZeroMaxIsTargetFormatUnsupported() {
        String raw = "raw/rect.raw";
        var caps = new com.schmaloogium.engine.gl.GLCapabilityProfile(4, 6, "4.60",
            "vendor", "renderer", 8, 8, 16, 16, 64, 256, 0, Set.of());
        CustomTextureSpec.Raw spec = new CustomTextureSpec.Raw(key("recttex"), path(raw),
            TextureTarget.RECTANGLE, ColorInternalFormat.RGBA8, List.of(4, 4),
            PixelFormat.RGBA, PixelType.UNSIGNED_BYTE, Optional.empty());
        PackConfiguration configuration = configuration(List.of(spec),
            new NoiseTextureSpec.Generated(), available(raw, new byte[4 * 4 * 4]));
        assertEquals(TextureFailureCode.TARGET_FORMAT_UNSUPPORTED,
            failureCode(TextureSourcePreparer.prepare(configuration, 1L, caps), 0));
    }

    // ------------------------------------------------------------------ assets_afterPackRemoval

    @Test
    void preparationUsesRetainedBytesAfterHostDisappears() {
        byte[] hostBytes = TINY_PNG.clone();
        String image = "textures/stone.png";
        PackConfiguration configuration = configuration(
            List.of(packPath(image, null)), new NoiseTextureSpec.Generated(),
            available(image, hostBytes));
        // "Delete"/corrupt the host copy: preparation must only see retained bytes.
        hostBytes[0] ^= 0xFF;
        hostBytes[1] ^= 0xFF;
        long before = contentSha(TINY_PNG);
        TextureSourceCatalog catalog = TextureSourcePreparer.prepare(configuration, 7L);
        TextureSourceAsset.ReadyAsset asset = ready(catalog, 0);
        assertEquals(contentSha(TINY_PNG), before);
        // contentDigest = sourceDigest(kind, logical, byteSha, parameters fingerprint).
        String fingerprint = TexturePreparation.prepareParameters(
            TexturePreparation.Role.CUSTOM_PNG, Optional.empty(), configuration.assets(),
            com.schmaloogium.engine.gl.TextureAllocationTarget.TEXTURE_2D,
            com.schmaloogium.engine.gl.ColorInternalFormat.RGBA8).fingerprint();
        String expected = TexturePreparation.sourceDigest("PACK_PNG", "pack:" + image,
            TexturePreparation.contentSha256(TINY_PNG), fingerprint);
        assertEquals(expected, asset.contentDigest());
        assertEquals("config-fp", assertInstanceOf(TextureSourceIdentity.OwnedUpload.class,
            asset.identity()).configurationIdentity());
    }

    private static long contentSha(byte[] bytes) {
        return java.util.Arrays.hashCode(bytes);
    }

    // ------------------------------------------------------------------ assets_cursorIsolation

    @Test
    void cursorsAreIndependentAndReadOnly() {
        PackAssetSnapshot snapshot = snapshot(identity(),
            available("textures/stone.png", TINY_PNG));
        PackAssetAcquisition.Acquired acquired =
            assertInstanceOf(PackAssetAcquisition.Acquired.class,
                snapshot.acquire(path("textures/stone.png")));
        PackAssetBytes bytes = acquired.bytes();
        ByteBuffer first = bytes.openCursor();
        ByteBuffer second = bytes.openCursor();
        assertEquals(0, first.position());
        assertEquals(0, second.position());
        assertEquals(TINY_PNG.length, second.remaining());
        first.get(); // advancing one cursor must not disturb the other
        assertEquals(TINY_PNG.length, second.remaining());
        byte[] throughSecond = new byte[second.remaining()];
        second.get(throughSecond);
        assertArrayEquals(TINY_PNG, throughSecond);
        assertThrows(java.nio.ReadOnlyBufferException.class, () -> first.put((byte) 0));
        // Re-acquisition yields a fresh position-0 view over unchanged bytes.
        byte[] again = new byte[TINY_PNG.length];
        PackAssetAcquisition.Acquired reacquired =
            assertInstanceOf(PackAssetAcquisition.Acquired.class,
                snapshot.acquire(path("textures/stone.png")));
        reacquired.bytes().openCursor().get(again);
        assertArrayEquals(TINY_PNG, again);
    }

    @Test
    void duplicateConsumersShareOneInterpretationAndEqualDigests() {
        String shared = "textures/shared.png.mcmeta";
        PackConfiguration configuration = configuration(
            List.of(
                packPath("textures/stone.png", shared),
                packPath("textures/dirt.png", shared)),
            new NoiseTextureSpec.Generated(),
            available("textures/stone.png", TINY_PNG),
            available("textures/dirt.png", TINY_PNG),
            available(shared, "{\"texture\":{\"blur\":true}}".getBytes()));
        TextureSourceCatalog catalog = TextureSourcePreparer.prepare(configuration, 1L);
        TextureSourceAsset.ReadyAsset first = ready(catalog, 0);
        TextureSourceAsset.ReadyAsset second = ready(catalog, 1);
        assertNotEquals(first.contentDigest(), second.contentDigest());
        assertEquals(first.sidecarDigest(), second.sidecarDigest());
        assertEquals(first.parameters(), second.parameters());
    }

    @Test
    void samePathDistinctRolesShareEvidenceNotBaselines() {
        String shared = "textures/shared.png.mcmeta";
        CustomTextureSpec.Raw rawSpec = new CustomTextureSpec.Raw(key("rawtex"),
            path("raw/field.raw"), TextureTarget.TEXTURE_2D, ColorInternalFormat.RGBA8,
            List.of(2, 2), PixelFormat.RGBA, PixelType.UNSIGNED_BYTE,
            Optional.of(new TextureSidecarRef(path(shared))));
        PackConfiguration configuration = configuration(
            List.of(packPath("textures/stone.png", shared), rawSpec),
            new NoiseTextureSpec.Generated(),
            available("textures/stone.png", TINY_PNG),
            available("raw/field.raw", new byte[2 * 2 * 4]),
            available(shared, "{\"texture\":{\"blur\":true}}".getBytes()));
        TextureSourceCatalog catalog = TextureSourcePreparer.prepare(configuration, 1L);
        TextureSourceAsset.ReadyAsset png = ready(catalog, 0);
        TextureSourceAsset.ReadyAsset raw = ready(catalog, 1);
        // Same bytes/outcome evidence, role-specific effective baselines.
        assertEquals(png.sidecarDigest(), raw.sidecarDigest());
        assertEquals(com.schmaloogium.engine.gl.TextureWrap.REPEAT, png.parameters().wrap());
        assertEquals(com.schmaloogium.engine.gl.TextureWrap.CLAMP_TO_EDGE,
            raw.parameters().wrap());
        assertEquals(com.schmaloogium.engine.gl.TextureMinFilter.LINEAR,
            png.parameters().minFilter()); // explicit blur=true overrides NEAREST
        assertEquals(com.schmaloogium.engine.gl.TextureMinFilter.LINEAR,
            raw.parameters().minFilter());
    }

    // ------------------------------------------------------------------ foreign sources

    @Test
    void foreignLiveSourcesCarryIdentityNotPayloads() {
        String identity = "minecraft:dynamic/lightmap_1";
        CustomTextureSpec.MinecraftResource spec = new CustomTextureSpec.MinecraftResource(
            key("lightmap"), identity);
        PackConfiguration configuration = configuration(List.of(spec),
            new NoiseTextureSpec.Generated());
        TextureSourceCatalog catalog = TextureSourcePreparer.prepare(configuration, 9L,
            TextureSourcePreparer.HEADLESS_CAPABILITIES, AtlasCatalog.EMPTY, null,
            (resource, epoch) -> Optional.of(new TextureSourcePreparer.ForeignObjects.ForeignLive(
                true, 42L)));
        TextureSourceAsset.ReadyAsset asset = ready(catalog, 0);
        TextureSourceIdentity.ForeignLive live =
            assertInstanceOf(TextureSourceIdentity.ForeignLive.class, asset.identity());
        assertEquals(identity, live.exactResourceIdentity());
        assertEquals(9L, live.resourceReloadEpoch());
        assertEquals(42L, live.objectEpoch());
        assertEquals(List.of(1, 1), asset.dimensions());
    }

    @Test
    void unresolvableForeignSourcesAreSourceUnavailable() {
        CustomTextureSpec.MinecraftResource foreign = new CustomTextureSpec.MinecraftResource(
            key("lightmap"), "minecraft:dynamic/lightmap_1");
        CustomTextureSpec.MinecraftResource staticAsset = new CustomTextureSpec.MinecraftResource(
            key("static"), "minecraft:textures/env/water.png");
        PackConfiguration configuration = configuration(List.of(foreign, staticAsset),
            new NoiseTextureSpec.Generated());
        TextureSourceCatalog catalog = TextureSourcePreparer.prepare(configuration, 9L,
            TextureSourcePreparer.HEADLESS_CAPABILITIES, AtlasCatalog.EMPTY, null,
            (resource, epoch) -> Optional.empty());
        assertEquals(TextureFailureCode.SOURCE_UNAVAILABLE, failureCode(catalog, 0));
        assertEquals(TextureFailureCode.SOURCE_UNAVAILABLE, failureCode(catalog, 1));
    }

    // ------------------------------------------------------------------ companions

    @Test
    void enabledKindDiscoveryEmitsResourceEntriesAndAbsenceEmitsNothing() {
        AtlasDescriptor atlas = new AtlasDescriptor(
            new AtlasId("minecraft:textures/atlas/blocks.png"), 64, 64, 4,
            List.of(SpriteDescriptor.staticSprite("block/dirt", 16, 0, 16, 16),
                SpriteDescriptor.staticSprite("block/stone", 0, 0, 16, 16)));
        PackConfiguration configuration = configuration(List.of(),
            new NoiseTextureSpec.Generated(), new CompanionOptionMacros(true, false));
        TextureSourceCatalog catalog = TextureSourcePreparer.prepare(configuration, 3L,
            new AtlasCatalog(List.of(atlas)),
            (resource, epoch) -> {
                if (resource.equals("block/stone_n")) {
                    byte[] frame = new byte[16 * 16 * 4];
                    return Optional.of(new TextureSourcePreparer.CompanionDiscovery
                        .DiscoveredCompanion(16, 16, List.of(frame), 5L));
                }
                return Optional.empty(); // dirt has no _n companion → DefaultFill there
            });
        assertEquals(1, catalog.assets().size());
        TextureSourceAsset.ReadyAsset companion = ready(catalog, 0);
        TextureSourceIdentity.OwnedUpload upload =
            assertInstanceOf(TextureSourceIdentity.OwnedUpload.class, companion.identity());
        assertEquals(OwnedTextureSourceKind.COMPANION_RESOURCE, upload.sourceKind());
        assertEquals("companion:minecraft:textures/atlas/blocks.png:NORMALS:block/stone",
            upload.logicalSource());
        assertEquals(List.of(16, 16), companion.dimensions());
        assertEquals(TexturePreparation.companionAtlasPolicy(4), companion.parameters());
        assertEquals(TexturePreparation.absentSidecarDigest(), companion.sidecarDigest());
    }

    // ------------------------------------------------------------------ noise

    @Test
    void validNoiseOverridePreparesWithItsOwnDimensions() {
        String image = "noise.png";
        PackConfiguration configuration = configuration(List.of(),
            new NoiseTextureSpec.Override(path(image), Optional.empty()),
            available(image, TINY_PNG));
        TextureSourceCatalog catalog = TextureSourcePreparer.prepare(configuration, 1L);
        TextureSourceAsset.ReadyAsset noise = ready(catalog, 0);
        TextureSourceIdentity.OwnedUpload upload =
            assertInstanceOf(TextureSourceIdentity.OwnedUpload.class, noise.identity());
        assertEquals("noise:" + image, upload.logicalSource());
        assertEquals(List.of(2, 2), noise.dimensions());
        assertEquals(TexturePreparation.baseline(TexturePreparation.Role.NOISE_OVERRIDE),
            noise.parameters());
    }

    @Test
    void noiseOverrideDecodeFailureEmitsNoAssetAtAll() {
        PackConfiguration configuration = configuration(List.of(),
            new NoiseTextureSpec.Override(path("noise.png"), Optional.empty()),
            available("noise.png", "not a png".getBytes()));
        assertEquals(List.of(), TextureSourcePreparer.prepare(configuration, 1L).assets());
    }

    @Test
    void missingNoiseOverrideKeepsTheDiagnosedGeneratedFallback() {
        PackConfiguration configuration = configuration(List.of(),
            new NoiseTextureSpec.Override(path("noise.png"), Optional.empty()),
            missing("noise.png"));
        assertEquals(List.of(), TextureSourcePreparer.prepare(configuration, 1L).assets());
    }

    // ------------------------------------------------------------------ seam helper

    /** Inert-minimal SourceCatalog: preparation never touches pack sources. */
    private static final class EmptySources
            implements com.schmaloogium.engine.preprocess.SourceCatalog {

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
}
