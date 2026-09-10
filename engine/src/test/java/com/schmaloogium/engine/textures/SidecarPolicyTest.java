// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.buffers.TextureParameterSpec;
import com.schmaloogium.engine.gl.TextureMagFilter;
import com.schmaloogium.engine.gl.TextureMinFilter;
import com.schmaloogium.engine.gl.TextureWrap;
import com.schmaloogium.engine.textures.internal.ParameterPolicy;
import com.schmaloogium.engine.textures.internal.SidecarOutcome;
import com.schmaloogium.engine.textures.internal.SidecarParser;
import com.schmaloogium.engine.textures.internal.SidecarPolicy;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * The D-P13-27 source-specific sidecar policy (§4.3.5): per-role baselines, the blur/clamp
 * override matrix (§8 custom_mcmetaBlurSetsFilter / custom_mcmetaClampSetsWrap), atomic
 * recovery (sidecar_atomicRecovery) and the COMPANION/DEFAULT_FILL fixed-role fingerprints.
 */
class SidecarPolicyTest {

    private static SidecarOutcome validSidecar(String textureObject) {
        return SidecarParser.parse(
            ("{\"texture\":" + textureObject + "}").getBytes(StandardCharsets.UTF_8),
            Optional.empty());
    }

    private static void assertSpec(TextureParameterSpec spec, TextureMinFilter min,
                                   TextureMagFilter mag, TextureWrap wrap) {
        assertEquals(min, spec.minFilter());
        assertEquals(mag, spec.magFilter());
        assertEquals(wrap, spec.wrap());
    }

    @Test
    void roleBaselines() {
        assertSpec(SidecarPolicy.baseline(SidecarPolicy.Role.CUSTOM_PNG),
            TextureMinFilter.NEAREST, TextureMagFilter.NEAREST, TextureWrap.REPEAT);
        assertSpec(SidecarPolicy.baseline(SidecarPolicy.Role.RAW),
            TextureMinFilter.LINEAR, TextureMagFilter.LINEAR, TextureWrap.CLAMP_TO_EDGE);
        assertSpec(SidecarPolicy.baseline(SidecarPolicy.Role.NOISE_OVERRIDE),
            TextureMinFilter.LINEAR, TextureMagFilter.LINEAR, TextureWrap.REPEAT);
        assertSpec(SidecarPolicy.baseline(SidecarPolicy.Role.GENERATED_NOISE),
            TextureMinFilter.LINEAR, TextureMagFilter.LINEAR, TextureWrap.REPEAT);
    }

    @Test
    void blurSetsFilterPerRole() {
        // custom_mcmetaBlurSetsFilter: absent/omitted baseline, true LINEAR, false NEAREST;
        // omission differs from explicit false in fingerprint presence, never in the
        // PNG spec itself.
        for (SidecarPolicy.Role role : new SidecarPolicy.Role[]{
            SidecarPolicy.Role.CUSTOM_PNG, SidecarPolicy.Role.RAW,
            SidecarPolicy.Role.NOISE_OVERRIDE}) {
            TextureParameterSpec base = SidecarPolicy.baseline(role);
            SidecarPolicy.Effective omitted =
                SidecarPolicy.apply(role, validSidecar("{}"));
            assertEquals(base, omitted.effective());
            SidecarPolicy.Effective blurTrue =
                SidecarPolicy.apply(role, validSidecar("{\"blur\":true}"));
            assertSpec(blurTrue.effective(), TextureMinFilter.LINEAR,
                TextureMagFilter.LINEAR, base.wrap());
            SidecarPolicy.Effective blurFalse =
                SidecarPolicy.apply(role, validSidecar("{\"blur\":false}"));
            assertSpec(blurFalse.effective(), TextureMinFilter.NEAREST,
                TextureMagFilter.NEAREST, base.wrap());
            assertEquals(SidecarOutcome.Presence.TRUE, blurTrue.sidecarOutcome().blurPresence());
            assertEquals(SidecarOutcome.Presence.FALSE,
                blurFalse.sidecarOutcome().blurPresence());
        }
    }

    @Test
    void clampSetsWrapPerRole() {
        // custom_mcmetaClampSetsWrap: PNG/noise REPEAT baseline, raw CLAMP_TO_EDGE baseline;
        // explicit booleans override independently of blur.
        for (SidecarPolicy.Role role : new SidecarPolicy.Role[]{
            SidecarPolicy.Role.CUSTOM_PNG, SidecarPolicy.Role.RAW,
            SidecarPolicy.Role.NOISE_OVERRIDE}) {
            TextureParameterSpec base = SidecarPolicy.baseline(role);
            assertEquals(base.wrap(),
                SidecarPolicy.apply(role, validSidecar("{}")).effective().wrap());
            assertEquals(TextureWrap.CLAMP_TO_EDGE,
                SidecarPolicy.apply(role, validSidecar("{\"clamp\":true}"))
                    .effective().wrap());
            assertEquals(TextureWrap.REPEAT,
                SidecarPolicy.apply(role, validSidecar("{\"clamp\":false}"))
                    .effective().wrap());
        }
        // independence: one field explicit, the other omitted
        SidecarPolicy.Effective mixed = SidecarPolicy.apply(SidecarPolicy.Role.RAW,
            validSidecar("{\"blur\":false,\"clamp\":true}"));
        assertSpec(mixed.effective(), TextureMinFilter.NEAREST, TextureMagFilter.NEAREST,
            TextureWrap.CLAMP_TO_EDGE);
    }

    @Test
    void pngOmittedBlurEqualsFalseSpecButDiffersInFingerprint() {
        SidecarPolicy.Effective omitted =
            SidecarPolicy.apply(SidecarPolicy.Role.CUSTOM_PNG, validSidecar("{}"));
        SidecarPolicy.Effective explicitFalse =
            SidecarPolicy.apply(SidecarPolicy.Role.CUSTOM_PNG, validSidecar("{\"blur\":false}"));
        // same effective spec...
        assertEquals(omitted.effective(), explicitFalse.effective());
        assertEquals(omitted.baseline(), explicitFalse.baseline());
        // ...but different sidecar digests: OMITTED vs FALSE participate in identity
        assertNotEquals(SidecarOutcome.Presence.OMITTED,
            explicitFalse.sidecarOutcome().blurPresence());
        assertNotEquals(omitted.fingerprint(OwnedTextureSourceKind.PACK_PNG),
            explicitFalse.fingerprint(OwnedTextureSourceKind.PACK_PNG));
    }

    @Test
    void atomicRecoveryResetsBothFieldsToBaseline() {
        // sidecar_atomicRecovery: valid blur then malformed clamp discards ALL overrides
        SidecarOutcome outcome = validSidecar("{\"blur\":true,\"clamp\":\"x\"}");
        assertEquals(SidecarOutcome.Outcome.MALFORMED, outcome.outcome());
        assertEquals(SidecarOutcome.FailureClass.SHAPE, outcome.failureClass());
        for (SidecarPolicy.Role role : new SidecarPolicy.Role[]{
            SidecarPolicy.Role.CUSTOM_PNG, SidecarPolicy.Role.RAW,
            SidecarPolicy.Role.NOISE_OVERRIDE}) {
            SidecarPolicy.Effective recovered = SidecarPolicy.apply(role, outcome);
            assertEquals(SidecarPolicy.baseline(role), recovered.effective());
            assertEquals(SidecarPolicy.baseline(role), recovered.baseline());
        }
        // other fatal sidecar outcomes recover atomically the same way
        SidecarOutcome duplicate = SidecarParser.parse(
            "{\"texture\":{\"blur\":false,\"blur\":true}}".getBytes(StandardCharsets.UTF_8),
            Optional.empty());
        assertEquals(SidecarOutcome.FailureClass.DUPLICATE_MEMBER, duplicate.failureClass());
        assertEquals(SidecarPolicy.baseline(SidecarPolicy.Role.RAW),
            SidecarPolicy.apply(SidecarPolicy.Role.RAW, duplicate).effective());
        SidecarOutcome absent = SidecarOutcome.absent();
        assertEquals(SidecarPolicy.baseline(SidecarPolicy.Role.CUSTOM_PNG),
            SidecarPolicy.apply(SidecarPolicy.Role.CUSTOM_PNG, absent).effective());
    }

    @Test
    void digestsDeterministic() {
        SidecarOutcome outcome = validSidecar("{\"blur\":true,\"clamp\":false}");
        assertEquals(outcome.digest(), validSidecar("{\"blur\":true,\"clamp\":false}").digest());
        SidecarPolicy.Effective effective =
            SidecarPolicy.apply(SidecarPolicy.Role.CUSTOM_PNG, outcome);
        // role change propagates through the parameter fingerprint
        assertNotEquals(effective.fingerprint(OwnedTextureSourceKind.PACK_PNG),
            SidecarPolicy.apply(SidecarPolicy.Role.RAW, outcome)
                .fingerprint(OwnedTextureSourceKind.RAW_BYTES));
    }
    @Test
    void companionAndDefaultFillUseNotApplicableSidecar() {
        // D-P13-42: mipped full atlas vs level-zero standalone policy values
        TextureParameterSpec mipped = ParameterPolicy.companionAtlasPolicy(3);
        TextureParameterSpec levelZero = ParameterPolicy.companionAtlasPolicy(0);
        assertSpec(mipped, TextureMinFilter.NEAREST_MIPMAP_LINEAR, TextureMagFilter.NEAREST,
            TextureWrap.REPEAT);
        assertSpec(levelZero, TextureMinFilter.NEAREST, TextureMagFilter.NEAREST,
            TextureWrap.REPEAT);
        TextureParameterSpec standalone = ParameterPolicy.standaloneDefaultPolicy();
        assertSpec(standalone, TextureMinFilter.NEAREST, TextureMagFilter.NEAREST,
            TextureWrap.REPEAT);

        String notApplicableDigest = SidecarPolicy.notApplicable().digest();
        assertSame(SidecarOutcome.Outcome.NOT_APPLICABLE,
            SidecarPolicy.notApplicable().outcome());

        // The fixed-role fingerprint frames baseline == effective == the D-P13-42 spec
        // triple with the NOT_APPLICABLE sidecar digest in its slot.
        assertEquals(
            framedFingerprint(OwnedTextureSourceKind.COMPANION_RESOURCE,
                SidecarPolicy.Role.COMPANION, mipped, notApplicableDigest),
            SidecarPolicy.fixedRoleFingerprint(OwnedTextureSourceKind.COMPANION_RESOURCE,
                SidecarPolicy.Role.COMPANION, mipped));
        assertEquals(
            framedFingerprint(OwnedTextureSourceKind.COMPANION_RESOURCE,
                SidecarPolicy.Role.COMPANION, levelZero, notApplicableDigest),
            SidecarPolicy.fixedRoleFingerprint(OwnedTextureSourceKind.COMPANION_RESOURCE,
                SidecarPolicy.Role.COMPANION, levelZero));
        assertEquals(
            framedFingerprint(OwnedTextureSourceKind.DEFAULT_FILL,
                SidecarPolicy.Role.DEFAULT_FILL, standalone, notApplicableDigest),
            SidecarPolicy.fixedRoleFingerprint(OwnedTextureSourceKind.DEFAULT_FILL,
                SidecarPolicy.Role.DEFAULT_FILL, standalone));
        // a changed spec changes the fixed-role fingerprint
        assertNotEquals(
            SidecarPolicy.fixedRoleFingerprint(OwnedTextureSourceKind.COMPANION_RESOURCE,
                SidecarPolicy.Role.COMPANION, mipped),
            SidecarPolicy.fixedRoleFingerprint(OwnedTextureSourceKind.COMPANION_RESOURCE,
                SidecarPolicy.Role.COMPANION, levelZero));
    }

    /** The v2 parameter fingerprint framed from the documented tuple, independently. */
    private static String framedFingerprint(OwnedTextureSourceKind sourceKind,
                                            SidecarPolicy.Role role,
                                            TextureParameterSpec spec,
                                            String sidecarDigest) {
        return com.schmaloogium.engine.textures.internal.TextureDigests.parameterFingerprint(
            sourceKind.name(), role.name(),
            spec.minFilter().name(), spec.magFilter().name(), spec.wrap().name(),
            sidecarDigest,
            spec.minFilter().name(), spec.magFilter().name(), spec.wrap().name());
    }
}
