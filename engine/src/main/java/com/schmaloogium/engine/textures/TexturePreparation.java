// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.buffers.TextureParameterSpec;
import com.schmaloogium.engine.config.TextureSidecarRef;
import com.schmaloogium.engine.gl.ColorInternalFormat;
import com.schmaloogium.engine.gl.GLCapabilityProfile;
import com.schmaloogium.engine.gl.TextureAllocationTarget;
import com.schmaloogium.engine.pack.PackAssetAcquisition;
import com.schmaloogium.engine.pack.PackAssetSnapshot;
import com.schmaloogium.engine.textures.internal.NoiseGenerator;
import com.schmaloogium.engine.textures.internal.ParameterPolicy;
import com.schmaloogium.engine.textures.internal.SidecarOutcome;
import com.schmaloogium.engine.textures.internal.SidecarParser;
import com.schmaloogium.engine.textures.internal.SidecarPolicy;
import com.schmaloogium.engine.textures.internal.TextureDigests;

import java.util.Objects;
import java.util.Optional;

/**
 * Public preparation bridge over the internal texture policy (§4.3.2-§4.3.5, §4.2.2):
 * sidecar interpretation through the retained load snapshot, the fixed policy-role
 * baselines with atomic recovery, target/format legality and extent limits, and the
 * generated-noise recurrence. Every public signature uses public types only — the
 * {@code :mod} glue's internal-seam audit (C-2) forbids {@code engine.*.internal.*}
 * references — while the internal package stays implementation.
 */
public final class TexturePreparation {

    /** The closed policy-role vocabulary (§4.5.1 fingerprint role tag). */
    public enum Role {
        CUSTOM_PNG, RAW, NOISE_OVERRIDE, GENERATED_NOISE,
        RESOURCE_OWNER, COMPANION, DEFAULT_FILL, FOREIGN_OWNER
    }

    /** Public outcome tag mirroring the internal interpretation outcome. */
    public enum SidecarOutcomeTag { NOT_APPLICABLE, ABSENT, VALID, MALFORMED, UNREADABLE }

    /** Public failure-class tag mirroring the internal failure classes. */
    public enum SidecarFailureClass {
        NONE, ENCODING, JSON, SHAPE, DUPLICATE_MEMBER, BYTE_LIMIT, DEPTH_LIMIT, IO
    }

    /** Public field-presence tag mirroring the internal presence vocabulary. */
    public enum SidecarPresence { OMITTED, FALSE, TRUE, DISCARDED }

    /**
     * One source's resolved parameterization (§4.3.5): baseline per role, effective
     * parameters after atomic sidecar recovery, the canonical parameters fingerprint, the
     * outcome classification for warning emission, and the target/format legality verdict
     * on the effective parameters.
     */
    public record PreparedParameters(
            TextureParameterSpec baseline,
            TextureParameterSpec effective,
            String fingerprint,
            String sidecarDigest,
            SidecarOutcomeTag sidecarOutcome,
            SidecarFailureClass failureClass,
            SidecarPresence blurPresence,
            SidecarPresence clampPresence,
            Optional<Boolean> effectiveBlur,
            Optional<Boolean> effectiveClamp,
            boolean legal) {
    }

    /** The canonical no-reference sidecar digest (§4.5.1): distinct from ABSENT. */
    public static String absentSidecarDigest() {
        return SidecarOutcome.notApplicableOutcome().digest();
    }

    private TexturePreparation() {
    }

    /**
     * Resolves one source's full parameterization (§4.3.5): interprets the optional
     * sidecar against the retained load snapshot, applies VALID overrides to the role
     * baseline, recovers atomically to the baseline on MALFORMED/UNREADABLE, computes the
     * canonical fingerprint, and checks target/format legality. A missing reference whose
     * primary was issued is an {@link IllegalStateException} (P3 contradiction), never a
     * recovery path.
     */
    public static PreparedParameters prepareParameters(Role role, Optional<TextureSidecarRef> ref,
                                                       PackAssetSnapshot assets,
                                                       TextureAllocationTarget target,
                                                       ColorInternalFormat format) {
        Objects.requireNonNull(role, "role");
        Objects.requireNonNull(assets, "assets");
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(format, "format");
        var baseline = baseline(role);
        SidecarOutcome outcome = interpret(ref, assets);
        var applied = SidecarPolicy.apply(SidecarPolicy.Role.valueOf(role.name()),
            outcome);
        var effective = applied.effective();
        boolean legal = ParameterPolicy.isLegal(target, format, effective);
        String fingerprint = SidecarPolicy.fixedRoleFingerprint(
            sourceKindOf(role), SidecarPolicy.Role.valueOf(role.name()), effective);
        return new PreparedParameters(baseline, effective, fingerprint, outcome.digest(),
            SidecarOutcomeTag.valueOf(outcome.outcome().name()),
            SidecarFailureClass.valueOf(outcome.failureClass().name()),
            SidecarPresence.valueOf(outcome.blurPresence().name()),
            SidecarPresence.valueOf(outcome.clampPresence().name()),
            outcome.effectiveBlur(), outcome.effectiveClamp(), legal);
    }

    /** The fixed baseline parameters for a policy role (§4.3.5). */
    public static TextureParameterSpec baseline(Role role) {
        return SidecarPolicy.baseline(SidecarPolicy.Role.valueOf(role.name()));
    }

    /** The D-P13-42 full-companion-atlas policy for an accepted mip count. */
    public static TextureParameterSpec companionAtlasPolicy(int mipmapLevels) {
        return ParameterPolicy.companionAtlasPolicy(mipmapLevels);
    }

    /** The D-P13-42 standalone 1×1 level-zero default policy. */
    public static TextureParameterSpec standaloneDefaultPolicy() {
        return ParameterPolicy.standaloneDefaultPolicy();
    }

    /** Target/format legality (§4.3.5): RECT wrap/mip and integer nearest-only rules. */
    public static boolean isLegal(TextureAllocationTarget target, ColorInternalFormat format,
                                  TextureParameterSpec parameters) {
        return ParameterPolicy.isLegal(target, format, parameters);
    }

    /**
     * Target-specific extent limits (D-P13-41): the applicable capability maximum per
     * target; a zero maximum on a gated target is {@code TARGET_FORMAT_UNSUPPORTED}, an
     * exceeded or nonpositive axis is {@code SOURCE_SIZE_INVALID}. {@code axes} are the
     * declared extents for the target's arity.
     */
    public static Optional<TextureFailureCode> checkExtent(GLCapabilityProfile capabilities,
                                                           TextureAllocationTarget target,
                                                           int... axes) {
        Objects.requireNonNull(capabilities, "capabilities");
        Objects.requireNonNull(target, "target");
        int maximum = switch (target) {
            case TEXTURE_1D, TEXTURE_2D, RECTANGLE -> capabilities.maxTextureSize();
            case TEXTURE_3D -> capabilities.max3DTextureSize();
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

    /** The contract noise recurrence rendered to row-major RGB bytes (§4.2.2). */
    public static byte[] generateNoiseRgb(int resolution) {
        return NoiseGenerator.generateRgb(resolution);
    }

    /** Lowercase hex SHA-256 over exact input bytes. */
    public static String contentSha256(byte[] bytes) {
        return TextureDigests.contentSha256(bytes);
    }

    /** Framed source digest over explicit atoms (§4.5.1). */
    public static String sourceDigest(String... atoms) {
        return TextureDigests.sourceDigest(atoms);
    }

    private static SidecarOutcome interpret(Optional<TextureSidecarRef> ref,
                                            PackAssetSnapshot assets) {
        if (ref.isEmpty()) {
            return SidecarOutcome.notApplicableOutcome();
        }
        var path = ref.get().path();
        return switch (assets.acquire(path)) {
            case PackAssetAcquisition.Acquired acquired -> {
                var cursor = acquired.bytes().openCursor();
                var all = new byte[cursor.remaining()];
                cursor.get(all);
                yield SidecarParser.parse(all, Optional.of(path.canonicalString()));
            }
            case PackAssetAcquisition.Unreadable unreadable -> new SidecarOutcome(
                SidecarOutcome.Outcome.UNREADABLE, Optional.of(path.canonicalString()),
                SidecarOutcome.ByteEvidence.NONE, Optional.empty(),
                SidecarOutcome.FailureClass.IO,
                SidecarOutcome.Presence.DISCARDED, SidecarOutcome.Presence.DISCARDED,
                Optional.empty(), Optional.empty());
            case PackAssetAcquisition.Missing missing -> throw new IllegalStateException(
                "P3 issued a sidecar reference whose primary is missing: "
                    + path.canonicalString());
            case PackAssetAcquisition.InvalidReference invalid -> throw new IllegalStateException(
                "P3 issued a sidecar reference that fails reference validation: "
                    + path.canonicalString());
        };
    }

    private static OwnedTextureSourceKind sourceKindOf(Role role) {
        return switch (role) {
            case CUSTOM_PNG -> OwnedTextureSourceKind.PACK_PNG;
            case RAW -> OwnedTextureSourceKind.RAW_BYTES;
            case NOISE_OVERRIDE, GENERATED_NOISE -> OwnedTextureSourceKind.GENERATED_NOISE;
            case RESOURCE_OWNER, FOREIGN_OWNER ->
                OwnedTextureSourceKind.MINECRAFT_DECODED_ASSET;
            case COMPANION -> OwnedTextureSourceKind.COMPANION_RESOURCE;
            case DEFAULT_FILL -> OwnedTextureSourceKind.DEFAULT_FILL;
        };
    }
}
