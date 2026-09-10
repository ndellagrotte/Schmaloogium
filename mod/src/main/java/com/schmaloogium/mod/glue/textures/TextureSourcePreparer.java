// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue.textures;

import com.schmaloogium.engine.buffers.CompanionKind;
import com.schmaloogium.engine.buffers.TextureParameterSpec;
import com.schmaloogium.engine.config.CustomTextureSpec;
import com.schmaloogium.engine.config.NoiseTextureSpec;
import com.schmaloogium.engine.config.TextureSidecarRef;
import com.schmaloogium.engine.config.TextureTarget;
import com.schmaloogium.engine.gl.ColorInternalFormat;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.pack.CompanionOptionMacros;
import com.schmaloogium.engine.pack.NormalizedPackPath;
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
import com.schmaloogium.engine.textures.TextureFailure;
import com.schmaloogium.engine.textures.TextureFailureCode;
import com.schmaloogium.engine.textures.TexturePreparation;
import com.schmaloogium.engine.textures.TexturePreparation.PreparedParameters;
import com.schmaloogium.engine.textures.TextureSourceAsset;
import com.schmaloogium.engine.textures.TextureSourceCatalog;
import com.schmaloogium.engine.textures.TextureSourceIdentity;
import com.schmaloogium.engine.textures.TextureUploadSpec;
import com.schmaloogium.engine.preprocess.DeclaredGlslType;
import com.schmaloogium.engine.preprocess.SampledKind;
import com.schmaloogium.engine.preprocess.TextureDimension;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


/**
 * Mod-glue catalog preparation (§4.3.2/§4.3.3, D-P13-30/D-P13-44): walks the typed
 * custom-spec list and noise override of the exact P3-issued configuration, acquires
 * every byte through the retained same-load {@link PackAssetSnapshot} (never a host
 * path), gates on the current schema and asset pairing first, and emits the closed
 * {@link TextureSourceAsset} vocabulary for the planner. Companion discovery rides the
 * {@link CompanionDiscovery} port (the production adapter is deferred); foreign live
 * sources ride {@link ForeignObjects}. Minecraft/LWJGL-free by construction.
 *
 * <p>Failure law (§4.3.2): Missing primary → {@code SOURCE_UNAVAILABLE}; impossible
 * primary Unreadable or InvalidReference → {@code IDENTITY_MISMATCH}; decode failure →
 * {@code SOURCE_DECODE_FAILED}; extent/payload-length errors → {@code SOURCE_SIZE_INVALID}
 * or {@code TARGET_FORMAT_UNSUPPORTED}; illegal effective parameters →
 * {@code PARAMETERIZATION_UNSUPPORTED}. A MALFORMED/UNREADABLE optional sidecar never
 * fails its entry (atomic baseline recovery); the noise override's Missing-primary and
 * decode failures emit no asset at all (§4.2.4's diagnosed generated fallback).
 *
 * <p>Cursors are never retained: bytes are copied out through one local read per asset,
 * so host replacement or deletion of the pack cannot change prepared bytes or digests.
 * Each distinct (path, role, target, format) sidecar interpretation is shared once per
 * {@link #prepare} call, so warning outcomes dedupe across stage/discriminator copies
 * while baselines stay role-specific.
 */
public final class TextureSourcePreparer {
    private static List<Integer> dimsList(int[] dims) {
        List<Integer> out = new ArrayList<>(dims.length);
        for (int dimension : dims) {
            out.add(dimension);
        }
        return List.copyOf(out);
    }

    /**
     * Live {@code minecraft:} asset resolver (test/production adapters). Empty means the
     * identity is not resolvable in this scope; {@code exists} mirrors the adapter's
     * positive resolution and {@code objectEpoch} its monotonic replacement counter.
     */
    public interface ForeignObjects {
        Optional<ForeignLive> resolve(String resourceIdentity, long resourceReloadEpoch);

        record ForeignLive(boolean exists, long objectEpoch) {
        }
    }

    /**
     * Companion resource discovery (§4.1.2): resolves a {@code _n}/{@code _s} resource
     * identity to full-resolution RGBA frames (frame 0 primary, extra frames for
     * animation). Empty answers mean the companion is absent and the planner's DefaultFill
     * applies. Invalid frame data is treated as undiscovered, never fabricated.
     */
    public interface CompanionDiscovery {
        Optional<DiscoveredCompanion> resolve(String resourceIdentity,
                                              long resourceReloadEpoch);

        record DiscoveredCompanion(int width, int height, List<byte[]> rgbaFrames,
                                   long objectEpoch) {
            public DiscoveredCompanion {
                Objects.requireNonNull(rgbaFrames, "rgbaFrames");
                rgbaFrames = List.copyOf(rgbaFrames);
            }
        }
    }

    /**
     * The documented headless capability default for the two-argument convenience entry:
     * generous 2D/RECT maxima and the spec-minimum 3D maximum. Real callers and limit
     * tests use the explicit-profile overload (D-P13-41 forbids guessing in production).
     */
    public static final GLCapabilityProfile HEADLESS_CAPABILITIES = new GLCapabilityProfile(
        4, 6, "4.60 headless profile", "schmaloogium", "headless preparation",
        8, 8, 16, 16, 16384, 256, 16384, java.util.Set.of());

    private static final DeclaredGlslType.Sampler SAMPLER_2D = new DeclaredGlslType.Sampler(
        SampledKind.FLOAT, TextureDimension.D2, false, false, false);
    private static final ColorInternalFormat RGBA8 = ColorInternalFormat.RGBA8;

    private TextureSourcePreparer() {
    }

    /** Headless entry with the default capability profile and no discovery ports. */
    public static TextureSourceCatalog prepare(PackConfiguration configuration,
                                               long resourceReloadEpoch) {
        return prepare(configuration, resourceReloadEpoch, HEADLESS_CAPABILITIES,
            AtlasCatalog.EMPTY, null, null);
    }

    /** Explicit-capability entry without discovery ports. */
    public static TextureSourceCatalog prepare(PackConfiguration configuration,
                                               long resourceReloadEpoch,
                                               GLCapabilityProfile capabilities) {
        return prepare(configuration, resourceReloadEpoch, capabilities,
            AtlasCatalog.EMPTY, null, null);
    }

    /** Discovery entry with the default capability profile. */
    public static TextureSourceCatalog prepare(PackConfiguration configuration,
                                               long resourceReloadEpoch,
                                               AtlasCatalog atlases,
                                               CompanionDiscovery discovery) {
        return prepare(configuration, resourceReloadEpoch, HEADLESS_CAPABILITIES,
            atlases, discovery, null);
    }

    /**
     * Full preparation: schema/pairing gate, then the custom specs in canonical order,
     * then companion discovery entries (unsigned-UTF8 atlas id, kind declaration order,
     * canonical sprite order), then the noise override. The result carries the epoch.
     */
    public static TextureSourceCatalog prepare(PackConfiguration configuration,
                                               long resourceReloadEpoch,
                                               GLCapabilityProfile capabilities,
                                               AtlasCatalog atlases,
                                               CompanionDiscovery discovery,
                                               ForeignObjects foreign) {
        Objects.requireNonNull(configuration, "configuration");
        Objects.requireNonNull(capabilities, "capabilities");
        Objects.requireNonNull(atlases, "atlases");
        // D-P13-30/TS-1 gate: exact CURRENT_SCHEMA_VERSION and the exact same-load asset
        // pairing, before any decode/retention. Callers translate to IDENTITY_MISMATCH.
        if (configuration.schemaVersion() != PackFrontEnd.CURRENT_SCHEMA_VERSION) {
            throw new IllegalStateException("schema gate: configuration schema "
                + configuration.schemaVersion() + " is not the current schema "
                + PackFrontEnd.CURRENT_SCHEMA_VERSION);
        }
        PackIdentity paired = configuration.pack();
        PackIdentity actual = configuration.assets().pack();
        if (actual == null || !actual.equals(paired)) {
            throw new IllegalStateException("schema gate: retained assets are not the "
                + "configuration's same-load snapshot");
        }
        SidecarInterpretations sidecars = new SidecarInterpretations(configuration.assets());
        List<TextureSourceAsset> assets = new ArrayList<>();
        for (CustomTextureSpec spec : configuration.properties().textures()) {
            if (spec instanceof CustomTextureSpec.PackPath packPath) {
                assets.add(preparePackPath(packPath, configuration, capabilities, sidecars));
            } else if (spec instanceof CustomTextureSpec.MinecraftResource resource) {
                assets.add(prepareMinecraftResource(resource, configuration,
                    resourceReloadEpoch, foreign));
            } else if (spec instanceof CustomTextureSpec.Raw raw) {
                assets.add(prepareRaw(raw, configuration, capabilities, sidecars));
            }
        }
        assets.addAll(prepareCompanions(configuration, resourceReloadEpoch, atlases, discovery));
        prepareNoise(configuration, capabilities, sidecars).ifPresent(assets::add);
        return new TextureSourceCatalog(resourceReloadEpoch, assets);
    }

    // ------------------------------------------------------------- custom specs

    private static TextureSourceAsset preparePackPath(CustomTextureSpec.PackPath spec,
                                                      PackConfiguration configuration,
                                                      GLCapabilityProfile capabilities,
                                                      SidecarInterpretations sidecars) {
        String logicalSource = "pack:" + spec.image().canonicalString();
        Acquisition acquired = acquire(spec.image(), configuration.assets(),
            OwnedTextureSourceKind.PACK_PNG, logicalSource);
        if (acquired.failure() != null) {
            return failed(acquired.failure());
        }
        byte[] png = copyBytes(acquired.bytes());
        String byteSha = TexturePreparation.contentSha256(png);
        PackTextureDecoder.Decoded decoded;
        try {
            decoded = PackTextureDecoder.decode(png);
        } catch (PackTextureDecoder.DecodeException e) {
            return failed(TextureFailureCode.SOURCE_DECODE_FAILED,
                OwnedTextureSourceKind.PACK_PNG.name(), logicalSource);
        }
        Optional<TextureFailureCode> extent = TexturePreparation.checkExtent(capabilities,
            TextureAllocationTarget.TEXTURE_2D, decoded.width(), decoded.height());
        if (extent.isPresent()) {
            return failed(extent.get(), OwnedTextureSourceKind.PACK_PNG.name(), logicalSource);
        }
        PreparedParameters parameters = sidecars.resolve(logicalSource,
            TexturePreparation.Role.CUSTOM_PNG, spec.sidecar(),
            TextureAllocationTarget.TEXTURE_2D, RGBA8);
        if (!parameters.legal()) {
            return failed(TextureFailureCode.PARAMETERIZATION_UNSUPPORTED,
                OwnedTextureSourceKind.PACK_PNG.name(), logicalSource);
        }
        return ready(OwnedTextureSourceKind.PACK_PNG, logicalSource, byteSha, parameters,
            configuration, TextureAllocationTarget.TEXTURE_2D,
            List.of(decoded.width(), decoded.height()), RGBA8, SAMPLER_2D);
    }

    private static TextureSourceAsset prepareMinecraftResource(
            CustomTextureSpec.MinecraftResource spec, PackConfiguration configuration,
            long resourceReloadEpoch, ForeignObjects foreign) {
        String identity = spec.resourceIdentity();
        boolean foreignForm = identity.startsWith("minecraft:dynamic/")
            || identity.startsWith("minecraft:textures/atlas/");
        String kindTag = foreignForm ? "FOREIGN_LIVE"
            : OwnedTextureSourceKind.MINECRAFT_DECODED_ASSET.name();
        if (!foreignForm || foreign == null) {
            // Not resolvable headless (no live resolver for decoded assets here).
            return failed(TextureFailureCode.SOURCE_UNAVAILABLE, kindTag, "minecraft:" + identity);
        }
        Optional<ForeignObjects.ForeignLive> resolved = foreign.resolve(identity,
            resourceReloadEpoch);
        if (resolved.isEmpty() || !resolved.get().exists()) {
            return failed(TextureFailureCode.SOURCE_UNAVAILABLE, kindTag, "minecraft:" + identity);
        }
        long objectEpoch = resolved.get().objectEpoch();
        // ForeignLive identity digest is the canonical logical source/reload/object
        // identity — never a hash of mutable live pixels (§2.3).
        String contentDigest = TexturePreparation.sourceDigest("FOREIGN_LIVE", identity,
            Long.toString(resourceReloadEpoch), Long.toString(objectEpoch));
        // The handle-free TwoD descriptor is compatibility metadata only, never guessed
        // storage authority; actual parameters live on the borrowed object.
        return new TextureSourceAsset.ReadyAsset(
            new TextureSourceIdentity.ForeignLive(identity, resourceReloadEpoch, objectEpoch),
            TextureAllocationTarget.TEXTURE_2D, List.of(1, 1), RGBA8, SAMPLER_2D,
            TexturePreparation.standaloneDefaultPolicy(), contentDigest,
            TexturePreparation.absentSidecarDigest());
    }

    private static TextureSourceAsset prepareRaw(CustomTextureSpec.Raw spec,
                                                 PackConfiguration configuration,
                                                 GLCapabilityProfile capabilities,
                                                 SidecarInterpretations sidecars) {
        String logicalSource = "raw:" + spec.bytes().canonicalString();
        Acquisition acquired = acquire(spec.bytes(), configuration.assets(),
            OwnedTextureSourceKind.RAW_BYTES, logicalSource);
        if (acquired.failure() != null) {
            return failed(acquired.failure());
        }
        TextureAllocationTarget target = TextureAllocationTarget.valueOf(spec.target().name());
        ColorInternalFormat format = ColorInternalFormat.valueOf(spec.internalFormat().name());
        com.schmaloogium.engine.gl.PixelFormat pixelFormat =
            com.schmaloogium.engine.gl.PixelFormat.valueOf(spec.pixelFormat().name());
        com.schmaloogium.engine.gl.PixelType pixelType =
            com.schmaloogium.engine.gl.PixelType.valueOf(spec.pixelType().name());
        int[] dims = spec.dimensions().stream().mapToInt(Integer::intValue).toArray();
        if (dims.length != axesOf(spec.target())) {
            return failed(TextureFailureCode.SOURCE_SIZE_INVALID,
                OwnedTextureSourceKind.RAW_BYTES.name(), logicalSource);
        }
        for (int dimension : dims) {
            if (dimension <= 0) {
                return failed(TextureFailureCode.SOURCE_SIZE_INVALID,
                    OwnedTextureSourceKind.RAW_BYTES.name(), logicalSource);
            }
        }
        TextureUploadSpec upload = uploadOf(target, dims, format, pixelFormat, pixelType);
        byte[] payload = copyBytes(acquired.bytes());
        // Checked exact payload length before any capability work (D-P13-41 ordering).
        if (payload.length != upload.level0ByteSize()) {
            return failed(TextureFailureCode.SOURCE_SIZE_INVALID,
                OwnedTextureSourceKind.RAW_BYTES.name(), logicalSource);
        }
        Optional<TextureFailureCode> extent = extentFailure(capabilities, target, dims);
        if (extent.isPresent()) {
            return failed(extent.get(), OwnedTextureSourceKind.RAW_BYTES.name(), logicalSource);
        }
        PreparedParameters parameters = sidecars.resolve(logicalSource,
            TexturePreparation.Role.RAW, spec.sidecar(), target, format);
        if (!parameters.legal()) {
            return failed(TextureFailureCode.PARAMETERIZATION_UNSUPPORTED,
                OwnedTextureSourceKind.RAW_BYTES.name(), logicalSource);
        }
        String byteSha = TexturePreparation.contentSha256(payload);
        return ready(OwnedTextureSourceKind.RAW_BYTES, logicalSource, byteSha, parameters,
            configuration, target, dimsList(dims), format, shapeOf(target));
    }

    // ------------------------------------------------------------- companions

    private static List<TextureSourceAsset> prepareCompanions(PackConfiguration configuration,
                                                              long resourceReloadEpoch,
                                                              AtlasCatalog atlases,
                                                              CompanionDiscovery discovery) {
        if (discovery == null || atlases.atlases().isEmpty()) {
            return List.of();
        }
        CompanionOptionMacros macros = configuration.macros().companionOptionMacros();
        boolean normals = macros.normalMap();
        boolean specular = macros.specularMap();
        if (!normals && !specular) {
            return List.of();
        }
        List<AtlasDescriptor> orderedAtlases = new ArrayList<>(atlases.atlases());
        orderedAtlases.sort(Comparator.comparing(atlas -> atlas.id().value(),
            TextureSourcePreparer::unsignedUtf8Compare));
        List<TextureSourceAsset> assets = new ArrayList<>();
        for (AtlasDescriptor atlas : orderedAtlases) {
            TextureParameterSpec policy = TexturePreparation.companionAtlasPolicy(atlas.mipmapLevels());
            for (CompanionKind kind : List.of(CompanionKind.NORMALS, CompanionKind.SPECULAR)) {
                if (kind == CompanionKind.NORMALS && !normals) {
                    continue;
                }
                if (kind == CompanionKind.SPECULAR && !specular) {
                    continue;
                }
                collectCompanionKind(configuration, resourceReloadEpoch, discovery, atlas,
                    kind, policy, assets);
            }
        }
        return assets;
    }

    private static void collectCompanionKind(PackConfiguration configuration,
                                             long resourceReloadEpoch,
                                             CompanionDiscovery discovery,
                                             AtlasDescriptor atlas, CompanionKind kind,
                                             TextureParameterSpec policy, List<TextureSourceAsset> assets) {
        String suffix = kind == CompanionKind.NORMALS ? "_n" : "_s";
        for (SpriteDescriptor sprite : atlas.sprites()) {
            String logicalSource = "companion:" + atlas.id().value() + ":" + kind.name()
                + ":" + sprite.iconName();
            Optional<CompanionDiscovery.DiscoveredCompanion> found = discovery.resolve(
                companionResourceIdentity(sprite.iconName(), suffix), resourceReloadEpoch);
            if (found.isEmpty()) {
                continue; // absent companion → planner DefaultFill, nothing here
            }
            CompanionDiscovery.DiscoveredCompanion companion = found.get();
            if (companion.width() <= 0 || companion.height() <= 0
                    || companion.rgbaFrames().isEmpty() || companion.objectEpoch() < 0) {
                continue; // invalid discovery data is undiscovered, never fabricated
            }
            long expected = (long) companion.width() * companion.height() * 4;
            List<String> frameTags = new ArrayList<>();
            frameTags.add(Integer.toString(companion.rgbaFrames().size()));
            frameTags.add(Long.toString(companion.objectEpoch()));
            boolean valid = true;
            for (byte[] frame : companion.rgbaFrames()) {
                if (frame == null || frame.length != expected) {
                    valid = false;
                    break;
                }
                frameTags.add(TexturePreparation.contentSha256(frame));
            }
            if (!valid) {
                continue;
            }
            List<String> atoms = new ArrayList<>();
            atoms.add(OwnedTextureSourceKind.COMPANION_RESOURCE.name());
            atoms.add(logicalSource);
            atoms.add(atlas.id().value());
            atoms.add(Long.toString(resourceReloadEpoch));
            atoms.addAll(frameTags);
            String contentDigest = TexturePreparation.sourceDigest(atoms.toArray(String[]::new));
            assets.add(new TextureSourceAsset.ReadyAsset(
                new TextureSourceIdentity.OwnedUpload(
                    OwnedTextureSourceKind.COMPANION_RESOURCE, logicalSource, contentDigest,
                    configuration.fingerprint().value()),
                TextureAllocationTarget.TEXTURE_2D,
                List.of(companion.width(), companion.height()), RGBA8, SAMPLER_2D, policy,
                contentDigest, TexturePreparation.absentSidecarDigest()));
        }
    }

    /**
     * Appends the companion suffix to the final path segment before the extension
     * (§4.1.2); extension-less icons take the bare suffix.
     */
    static String companionResourceIdentity(String iconName, String suffix) {
        int slash = iconName.lastIndexOf('/');
        int dot = iconName.lastIndexOf('.');
        if (dot > slash + 1) {
            return iconName.substring(0, dot) + suffix + iconName.substring(dot);
        }
        return iconName + suffix;
    }

    // ------------------------------------------------------------- noise

    private static Optional<TextureSourceAsset> prepareNoise(PackConfiguration configuration,
                                                             GLCapabilityProfile capabilities,
                                                             SidecarInterpretations sidecars) {
        if (!(configuration.properties().noise() instanceof NoiseTextureSpec.Override override)) {
            return Optional.empty(); // generated noise never enters the catalog
        }
        int declaredResolution = configuration.resources().noise().resolution();
        String logicalSource = "noise:" + override.image().canonicalString();
        Acquisition acquired = acquire(override.image(), configuration.assets(),
            OwnedTextureSourceKind.PACK_PNG, logicalSource);
        if (acquired.failure() != null) {
            // Missing primary and decode failure keep §4.2.4's diagnosed generated
            // fallback: no noise asset here. Impossible unreadable/invalid stays identity.
            if (acquired.failure().code() == TextureFailureCode.SOURCE_UNAVAILABLE) {
                return Optional.empty();
            }
            return Optional.of(failed(acquired.failure()));
        }
        byte[] png = copyBytes(acquired.bytes());
        String byteSha = TexturePreparation.contentSha256(png);
        PackTextureDecoder.Decoded decoded;
        try {
            decoded = PackTextureDecoder.decode(png);
        } catch (PackTextureDecoder.DecodeException e) {
            return Optional.empty(); // diagnosed generated fallback
        }
        Optional<TextureFailureCode> extent = TexturePreparation.checkExtent(capabilities,
            TextureAllocationTarget.TEXTURE_2D, decoded.width(), decoded.height());
        if (extent.isPresent()) {
            return Optional.of(failed(extent.get(), OwnedTextureSourceKind.PACK_PNG.name(),
                logicalSource));
        }
        PreparedParameters parameters = sidecars.resolve(logicalSource,
            TexturePreparation.Role.NOISE_OVERRIDE, override.sidecar(),
            TextureAllocationTarget.TEXTURE_2D, RGBA8);
        if (!parameters.legal()) {
            return Optional.of(failed(TextureFailureCode.PARAMETERIZATION_UNSUPPORTED,
                OwnedTextureSourceKind.PACK_PNG.name(), logicalSource));
        }
        String contentDigest = TexturePreparation.sourceDigest(
            OwnedTextureSourceKind.PACK_PNG.name(), logicalSource, byteSha,
            parameters.fingerprint(), "NOISE_OVERRIDE", Integer.toString(declaredResolution));
        return Optional.of(new TextureSourceAsset.ReadyAsset(
            new TextureSourceIdentity.OwnedUpload(OwnedTextureSourceKind.PACK_PNG,
                logicalSource, contentDigest, configuration.fingerprint().value()),
            TextureAllocationTarget.TEXTURE_2D, List.of(decoded.width(), decoded.height()),
            RGBA8, SAMPLER_2D, parameters.effective(), contentDigest,
            parameters.sidecarDigest()));
    }

    // ------------------------------------------------------------- shared plumbing

    private static TextureSourceAsset ready(OwnedTextureSourceKind kind, String logicalSource,
                                            String byteSha, PreparedParameters parameters,
                                            PackConfiguration configuration,
                                            TextureAllocationTarget target, List<Integer> dims,
                                            ColorInternalFormat format,
                                            DeclaredGlslType.Sampler shape) {
        String contentDigest = TexturePreparation.sourceDigest(kind.name(), logicalSource,
            byteSha, parameters.fingerprint());
        return new TextureSourceAsset.ReadyAsset(
            new TextureSourceIdentity.OwnedUpload(kind, logicalSource, contentDigest,
                configuration.fingerprint().value()),
            target, dims, format, shape, parameters.effective(), contentDigest,
            parameters.sidecarDigest());
    }

    private record Acquisition(PackAssetBytes bytes, TextureFailure failure) {
    }

    private static Acquisition acquire(NormalizedPackPath path, PackAssetSnapshot assets,
                                       OwnedTextureSourceKind kind, String logicalSource) {
        PackAssetAcquisition acquired = assets.acquire(path);
        if (acquired instanceof PackAssetAcquisition.Acquired success) {
            return new Acquisition(success.bytes(), null);
        }
        TextureFailureCode code;
        if (acquired instanceof PackAssetAcquisition.Missing) {
            code = TextureFailureCode.SOURCE_UNAVAILABLE;
        } else {
            // Impossible primary Unreadable and InvalidReference are identity failures,
            // never recovery shortcuts (§4.3.2).
            code = TextureFailureCode.IDENTITY_MISMATCH;
        }
        return new Acquisition(null, failure(code, kind.name(), logicalSource));
    }

    /** One local position-0 read; cursors are never retained or shared (assets_cursorIsolation). */
    private static byte[] copyBytes(PackAssetBytes bytes) {
        ByteBuffer cursor = bytes.openCursor();
        cursor.order(ByteOrder.BIG_ENDIAN).position(0);
        byte[] out = new byte[cursor.remaining()];
        cursor.get(out); // bulk get is byte-order agnostic
        return out;
    }

    private static TextureSourceAsset failed(TextureFailure failure) {
        return new TextureSourceAsset.FailedAsset(failure);
    }

    private static TextureSourceAsset failed(TextureFailureCode code, String kindTag,
                                             String logicalSource) {
        return failed(failure(code, kindTag, logicalSource));
    }

    private static TextureFailure failure(TextureFailureCode code, String kindTag,
                                          String logicalSource) {
        return TextureFailure.of(code,
            TexturePreparation.sourceDigest(kindTag, logicalSource));
    }

    private static int axesOf(TextureTarget target) {
        return switch (target) {
            case TEXTURE_1D -> 1;
            case TEXTURE_2D, RECTANGLE -> 2;
            case TEXTURE_3D -> 3;
        };
    }

    private static TextureUploadSpec uploadOf(TextureAllocationTarget target, int[] dims,
                                              ColorInternalFormat format,
                                              com.schmaloogium.engine.gl.PixelFormat pixelFormat,
                                              com.schmaloogium.engine.gl.PixelType pixelType) {
        return switch (target) {
            case TEXTURE_1D -> new TextureUploadSpec.OneD(dims[0], format, pixelFormat, pixelType);
            case TEXTURE_2D -> new TextureUploadSpec.TwoD(dims[0], dims[1], format, pixelFormat,
                pixelType);
            case TEXTURE_3D -> new TextureUploadSpec.ThreeD(dims[0], dims[1], dims[2], format,
                pixelFormat, pixelType);
            case RECTANGLE -> new TextureUploadSpec.Rectangle(dims[0], dims[1], format,
                pixelFormat, pixelType);
        };
    }

    private static DeclaredGlslType.Sampler shapeOf(TextureAllocationTarget target) {
        TextureDimension dimension = switch (target) {
            case TEXTURE_1D -> TextureDimension.D1;
            case TEXTURE_2D -> TextureDimension.D2;
            case TEXTURE_3D -> TextureDimension.D3;
            case RECTANGLE -> TextureDimension.RECTANGLE;
        };
        return new DeclaredGlslType.Sampler(SampledKind.FLOAT, dimension, false, false, false);
    }

    private static int unsignedUtf8Compare(String a, String b) {
        byte[] ab = a.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] bb = b.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        int shared = Math.min(ab.length, bb.length);
        for (int i = 0; i < shared; i++) {
            int cmp = (ab[i] & 0xFF) - (bb[i] & 0xFF);
            if (cmp != 0) {
                return cmp;
            }
        }
        return ab.length - bb.length;
    }

    /**
     * Shares one sidecar interpretation per distinct (path, role, target, format) per
     * prepare call: equal role/bytes/outcome share input evidence and warning outcomes,
     * while effective baselines stay role-specific (assets_cursorIsolation).
     */
    /**
     * D-P13-41 target-specific extent limits: 1D/2D use GL_MAX_TEXTURE_SIZE, 3D uses
     * GL_MAX_3D_TEXTURE_SIZE and RECTANGLE uses GL_MAX_RECTANGLE_TEXTURE_SIZE; a zero
     * maximum on a gated target is TARGET_FORMAT_UNSUPPORTED, an out-of-bounds or
     * nonpositive axis is SOURCE_SIZE_INVALID.
     */
    private static Optional<TextureFailureCode> extentFailure(GLCapabilityProfile capabilities,
                                                              TextureAllocationTarget target,
                                                              int... axes) {
        int maximum = switch (target) {
            case TEXTURE_1D, TEXTURE_2D -> capabilities.maxTextureSize();
            case TEXTURE_3D -> capabilities.max3DTextureSize();
            case RECTANGLE -> capabilities.maxRectangleTextureSize();
        };
        if (maximum <= 0) {
            return Optional.of(TextureFailureCode.TARGET_FORMAT_UNSUPPORTED);
        }
        for (int axis : axes) {
            if (axis <= 0 || axis > maximum) {
                return Optional.of(TextureFailureCode.SOURCE_SIZE_INVALID);
            }
        }
        return Optional.empty();
    }

    private static final class SidecarInterpretations {

        private record Key(String path, TexturePreparation.Role role,
                           TextureAllocationTarget target, ColorInternalFormat format) {
        }

        private final PackAssetSnapshot assets;
        private final Map<Key, PreparedParameters> resolved = new HashMap<>();

        SidecarInterpretations(PackAssetSnapshot assets) {
            this.assets = Objects.requireNonNull(assets, "assets");
        }

        PreparedParameters resolve(String path, TexturePreparation.Role role,
                                   Optional<TextureSidecarRef> ref,
                                   TextureAllocationTarget target, ColorInternalFormat format) {
            return resolved.computeIfAbsent(
                new Key(path, role, target, format),
                key -> TexturePreparation.prepareParameters(role, ref, assets, target, format));
        }
    }

}
