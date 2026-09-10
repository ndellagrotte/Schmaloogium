// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures.internal;

import com.schmaloogium.engine.textures.TextureFailureCode;

import java.util.List;

import static com.schmaloogium.engine.textures.internal.CanonicalFraming.atom;
import static com.schmaloogium.engine.textures.internal.CanonicalFraming.concat;
import static com.schmaloogium.engine.textures.internal.CanonicalFraming.seq;
import static com.schmaloogium.engine.textures.internal.CanonicalFraming.sha256Hex;

/**
 * The phase13 SHA-256 digest domains (§4.5.1/§6), exact lowercase 64-hex. Framing is P3
 * §4.10's canonical form. No exception message, diagnostic serial or wall time enters any
 * digest.
 */
public final class TextureDigests {

    public static final String PUBLICATION_DOMAIN = "phase13.texturePublication/v1";
    public static final String SOURCE_DOMAIN = "phase13.source/v1";
    public static final String PARAMETERS_DOMAIN = "phase13.parameters/v2";
    public static final String SIDECAR_DOMAIN = "phase13.sidecar/v1";
    public static final String FAILURE_DOMAIN = "phase13.failure/v1";
    public static final String SIDECAR_DIAGNOSTIC_DOMAIN = "phase13.sidecarDiagnostic/v1";

    private TextureDigests() {
    }

    /** Content digest over a list of opaque framed identity atoms. */
    public static String sourceDigest(String... framedAtoms) {
        byte[][] framed = new byte[framedAtoms.length + 1][];
        framed[0] = atom(SOURCE_DOMAIN);
        for (int i = 0; i < framedAtoms.length; i++) {
            framed[i + 1] = atom(framedAtoms[i]);
        }
        return sha256Hex(seq(framed));
    }

    /**
     * The §4.5.1 parameterization digest: domain; source-kind tag; policy-role tag; baseline
     * min/mag/wrap enum names; sidecarDigest; effective min/mag/wrap enum names.
     */
    public static String parameterFingerprint(String sourceKindTag, String policyRoleTag,
                                              String baselineMin, String baselineMag,
                                              String baselineWrap, String sidecarDigest,
                                              String effectiveMin, String effectiveMag,
                                              String effectiveWrap) {
        return sha256Hex(seq(
            atom(PARAMETERS_DOMAIN),
            atom(sourceKindTag),
            atom(policyRoleTag),
            atom(baselineMin),
            atom(baselineMag),
            atom(baselineWrap),
            atom(sidecarDigest),
            atom(effectiveMin),
            atom(effectiveMag),
            atom(effectiveWrap)));
    }

    /** The §4.5.1 sidecar outcome digest over the exact ordered tuple. */
    public static String sidecarDigest(String optionalPathTag, String canonicalPathOrEmpty,
                                       String outcome, String byteEvidence,
                                       String byteShaOrEmpty, String failureClass,
                                       String blurPresence, String clampPresence) {
        return sha256Hex(seq(
            atom(SIDECAR_DOMAIN),
            atom(optionalPathTag),
            atom(canonicalPathOrEmpty),
            atom(outcome),
            atom(byteEvidence),
            atom(byteShaOrEmpty),
            atom(failureClass),
            atom(blurPresence),
            atom(clampPresence)));
    }

    /** The §6 failure diagnostic id: domain, code name, logical identity. */
    public static String failureDiagnosticId(TextureFailureCode code,
                                             String logicalTextureIdentity) {
        return sha256Hex(seq(
            atom(FAILURE_DOMAIN),
            atom(code.name()),
            atom(logicalTextureIdentity)));
    }

    /** The §6 sidecar-recovery warning id: domain + sidecarDigest. */
    public static String sidecarDiagnosticId(String sidecarDigest) {
        return sha256Hex(seq(
            atom(SIDECAR_DIAGNOSTIC_DOMAIN),
            atom(sidecarDigest)));
    }

    /**
     * The publication content fingerprint over the plan's canonical, handle-free identity
     * inputs (§4.5.1): identity atoms first, then the pairing scalars.
     */
    public static String publicationFingerprint(List<String> orderedIdentityAtoms,
                                                String registryFingerprint,
                                                String configurationFingerprint,
                                                String policyFingerprint,
                                                long estateGeneration,
                                                long registryGeneration,
                                                long resourceReloadEpoch) {
        byte[][] framed = new byte[orderedIdentityAtoms.size() + 7][];
        framed[0] = atom(PUBLICATION_DOMAIN);
        for (int i = 0; i < orderedIdentityAtoms.size(); i++) {
            framed[i + 1] = atom(orderedIdentityAtoms.get(i));
        }
        int pos = orderedIdentityAtoms.size() + 1;
        framed[pos++] = atom(registryFingerprint);
        framed[pos++] = atom(configurationFingerprint);
        framed[pos++] = atom(policyFingerprint);
        framed[pos++] = atom(estateGeneration);
        framed[pos++] = atom(registryGeneration);
        framed[pos++] = atom(resourceReloadEpoch);
        return sha256Hex(seq(framed));
    }

    /** The §6 logical-source-identity digest used by TextureFailure.logicalTextureIdentity. */
    public static String logicalIdentity(String kind, String logicalSource) {
        return sha256Hex(seq(atom(SOURCE_DOMAIN), atom(kind), atom(logicalSource)));
    }

    /** Byte-level content digest over exact input bytes. */
    public static String contentSha256(byte[] bytes) {
        return sha256Hex(bytes);
    }
}
