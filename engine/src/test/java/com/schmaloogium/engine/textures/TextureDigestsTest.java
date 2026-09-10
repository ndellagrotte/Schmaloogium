// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.textures.internal.TextureDigests;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Canonical-framing vectors for the phase13 digest domains (§4.5.1/§6), asserted through
 * the public digest APIs only: determinism, per-member input sensitivity (the reload epoch
 * always participates) and the exact diagnostic-id framing recomputed in-test.
 */
class TextureDigestsTest {

    /** P3 §4.10 canonical atom: decimal UTF-8 byte length, ':', exact bytes. */
    private static byte[] atom(String s) {
        byte[] raw = s.getBytes(StandardCharsets.UTF_8);
        byte[] prefix = Integer.toString(raw.length).getBytes(StandardCharsets.US_ASCII);
        byte[] out = new byte[prefix.length + 1 + raw.length];
        System.arraycopy(prefix, 0, out, 0, prefix.length);
        out[prefix.length] = ':';
        System.arraycopy(raw, 0, out, prefix.length + 1, raw.length);
        return out;
    }

    /** P3 §4.10 canonical sequence: count, '[', length-prefixed elements, ']'. */
    private static byte[] seq(byte[]... elements) {
        byte[] count = Integer.toString(elements.length).getBytes(StandardCharsets.US_ASCII);
        int total = count.length + 1 + 1;
        byte[][] framed = new byte[elements.length][];
        for (int i = 0; i < elements.length; i++) {
            byte[] len = Integer.toString(elements[i].length)
                .getBytes(StandardCharsets.US_ASCII);
            framed[i] = new byte[len.length + 1 + elements[i].length];
            System.arraycopy(len, 0, framed[i], 0, len.length);
            framed[i][len.length] = ':';
            System.arraycopy(elements[i], 0, framed[i], len.length + 1, elements[i].length);
            total += framed[i].length;
        }
        byte[] out = new byte[total];
        int pos = 0;
        System.arraycopy(count, 0, out, pos, count.length);
        pos += count.length;
        out[pos++] = '[';
        for (byte[] framedElement : framed) {
            System.arraycopy(framedElement, 0, out, pos, framedElement.length);
            pos += framedElement.length;
        }
        out[pos] = ']';
        return out;
    }

    private static String sha256Hex(byte[] bytes) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(bytes);
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static String framed(String... atoms) {
        byte[][] framedAtoms = new byte[atoms.length][];
        for (int i = 0; i < atoms.length; i++) {
            framedAtoms[i] = atom(atoms[i]);
        }
        return sha256Hex(seq(framedAtoms));
    }

    @Test
    void sidecarDigestDeterministicAndInputSensitive() {
        String digest = TextureDigests.sidecarDigest("PRESENT", "a.png.mcmeta", "VALID",
            "COMPLETE", "ab12", "NONE", "TRUE", "OMITTED");
        assertEquals(digest, TextureDigests.sidecarDigest("PRESENT", "a.png.mcmeta", "VALID",
            "COMPLETE", "ab12", "NONE", "TRUE", "OMITTED"));
        // each tuple member participates
        String[] variants = {
            TextureDigests.sidecarDigest("ABSENT", "a.png.mcmeta", "VALID",
                "COMPLETE", "ab12", "NONE", "TRUE", "OMITTED"),
            TextureDigests.sidecarDigest("PRESENT", "b.png.mcmeta", "VALID",
                "COMPLETE", "ab12", "NONE", "TRUE", "OMITTED"),
            TextureDigests.sidecarDigest("PRESENT", "a.png.mcmeta", "MALFORMED",
                "COMPLETE", "ab12", "NONE", "TRUE", "OMITTED"),
            TextureDigests.sidecarDigest("PRESENT", "a.png.mcmeta", "VALID",
                "NONE", "ab12", "NONE", "TRUE", "OMITTED"),
            TextureDigests.sidecarDigest("PRESENT", "a.png.mcmeta", "VALID",
                "COMPLETE", "cd34", "NONE", "TRUE", "OMITTED"),
            TextureDigests.sidecarDigest("PRESENT", "a.png.mcmeta", "VALID",
                "COMPLETE", "ab12", "SHAPE", "TRUE", "OMITTED"),
            TextureDigests.sidecarDigest("PRESENT", "a.png.mcmeta", "VALID",
                "COMPLETE", "ab12", "NONE", "FALSE", "OMITTED"),
            TextureDigests.sidecarDigest("PRESENT", "a.png.mcmeta", "VALID",
                "COMPLETE", "ab12", "NONE", "TRUE", "DISCARDED"),
        };
        for (String variant : variants) {
            assertNotEquals(digest, variant);
        }
    }

    @Test
    void parameterFingerprintDeterministicAndInputSensitive() {
        String digest = TextureDigests.parameterFingerprint("PACK_PNG", "CUSTOM_PNG",
            "NEAREST", "NEAREST", "REPEAT", "side", "LINEAR", "LINEAR", "CLAMP_TO_EDGE");
        assertEquals(digest, TextureDigests.parameterFingerprint("PACK_PNG", "CUSTOM_PNG",
            "NEAREST", "NEAREST", "REPEAT", "side", "LINEAR", "LINEAR", "CLAMP_TO_EDGE"));
        assertNotEquals(digest, TextureDigests.parameterFingerprint("RAW_BYTES", "CUSTOM_PNG",
            "NEAREST", "NEAREST", "REPEAT", "side", "LINEAR", "LINEAR", "CLAMP_TO_EDGE"));
        assertNotEquals(digest, TextureDigests.parameterFingerprint("PACK_PNG", "RAW",
            "NEAREST", "NEAREST", "REPEAT", "side", "LINEAR", "LINEAR", "CLAMP_TO_EDGE"));
        assertNotEquals(digest, TextureDigests.parameterFingerprint("PACK_PNG", "CUSTOM_PNG",
            "LINEAR", "NEAREST", "REPEAT", "side", "LINEAR", "LINEAR", "CLAMP_TO_EDGE"));
        assertNotEquals(digest, TextureDigests.parameterFingerprint("PACK_PNG", "CUSTOM_PNG",
            "NEAREST", "LINEAR", "REPEAT", "side", "LINEAR", "LINEAR", "CLAMP_TO_EDGE"));
        assertNotEquals(digest, TextureDigests.parameterFingerprint("PACK_PNG", "CUSTOM_PNG",
            "NEAREST", "NEAREST", "CLAMP_TO_EDGE", "side", "LINEAR", "LINEAR",
            "CLAMP_TO_EDGE"));
        assertNotEquals(digest, TextureDigests.parameterFingerprint("PACK_PNG", "CUSTOM_PNG",
            "NEAREST", "NEAREST", "REPEAT", "other", "LINEAR", "LINEAR", "CLAMP_TO_EDGE"));
        assertNotEquals(digest, TextureDigests.parameterFingerprint("PACK_PNG", "CUSTOM_PNG",
            "NEAREST", "NEAREST", "REPEAT", "side", "NEAREST", "LINEAR", "CLAMP_TO_EDGE"));
        assertNotEquals(digest, TextureDigests.parameterFingerprint("PACK_PNG", "CUSTOM_PNG",
            "NEAREST", "NEAREST", "REPEAT", "side", "LINEAR", "NEAREST", "CLAMP_TO_EDGE"));
        assertNotEquals(digest, TextureDigests.parameterFingerprint("PACK_PNG", "CUSTOM_PNG",
            "NEAREST", "NEAREST", "REPEAT", "side", "LINEAR", "LINEAR", "REPEAT"));
    }

    @Test
    void publicationFingerprintDeterministicAndInputSensitive() {
        List<String> atoms = List.of("atom1", "atom2");
        String digest = TextureDigests.publicationFingerprint(
            atoms, "reg", "cfg", "pol", 7L, 11L, 13L);
        assertEquals(digest, TextureDigests.publicationFingerprint(
            atoms, "reg", "cfg", "pol", 7L, 11L, 13L));
        assertNotEquals(digest, TextureDigests.publicationFingerprint(
            List.of("atom1", "ATOM2"), "reg", "cfg", "pol", 7L, 11L, 13L));
        assertNotEquals(digest, TextureDigests.publicationFingerprint(
            List.of("atom1", "atom2", "atom3"), "reg", "cfg", "pol", 7L, 11L, 13L));
        assertNotEquals(digest, TextureDigests.publicationFingerprint(
            atoms, "REG", "cfg", "pol", 7L, 11L, 13L));
        assertNotEquals(digest, TextureDigests.publicationFingerprint(
            atoms, "reg", "CFG", "pol", 7L, 11L, 13L));
        assertNotEquals(digest, TextureDigests.publicationFingerprint(
            atoms, "reg", "cfg", "POL", 7L, 11L, 13L));
        assertNotEquals(digest, TextureDigests.publicationFingerprint(
            atoms, "reg", "cfg", "pol", 8L, 11L, 13L));
        assertNotEquals(digest, TextureDigests.publicationFingerprint(
            atoms, "reg", "cfg", "pol", 7L, 12L, 13L));
        // the reload epoch always participates (§4.5.1)
        assertNotEquals(digest, TextureDigests.publicationFingerprint(
            atoms, "reg", "cfg", "pol", 7L, 11L, 14L));
    }

    @Test
    void failureDiagnosticIdAndMessageKey() {
        String identity = TextureFailure.SYSTEM_IDENTITY;
        TextureFailure failure = TextureFailure.of(
            TextureFailureCode.PARAMETERIZATION_UNSUPPORTED, identity);
        assertEquals("TEXTURE_SYSTEM", failure.logicalTextureIdentity());
        assertEquals("schmaloogium.error.texture.parameterization_unsupported",
            failure.messageKey());
        assertEquals(framed("phase13.failure/v1",
            TextureFailureCode.PARAMETERIZATION_UNSUPPORTED.name(), identity),
            failure.diagnosticId());

        TextureFailure sourceFailure = TextureFailure.of(
            TextureFailureCode.SOURCE_UNAVAILABLE,
            TextureDigests.logicalIdentity("PACK_PNG", "minecraft:textures/block/stone.png"));
        assertEquals("schmaloogium.error.texture.source_unavailable", sourceFailure.messageKey());
        assertEquals(framed("phase13.failure/v1",
            TextureFailureCode.SOURCE_UNAVAILABLE.name(),
            sourceFailure.logicalTextureIdentity()),
            sourceFailure.diagnosticId());

        TextureFailure invalid = TextureFailure.of(TextureFailureCode.INVALID_REQUEST,
            TextureFailure.SYSTEM_IDENTITY);
        assertEquals("schmaloogium.error.texture.invalid_request", invalid.messageKey());
        assertEquals(framed("phase13.failure/v1",
            TextureFailureCode.INVALID_REQUEST.name(), identity), invalid.diagnosticId());

        // logical identity is itself the framed source digest and input-sensitive
        assertEquals(TextureDigests.logicalIdentity("PACK_PNG", "x"),
            TextureDigests.logicalIdentity("PACK_PNG", "x"));
        assertNotEquals(TextureDigests.logicalIdentity("PACK_PNG", "x"),
            TextureDigests.logicalIdentity("PACK_PNG", "y"));
        assertNotEquals(TextureDigests.logicalIdentity("PACK_PNG", "x"),
            TextureDigests.logicalIdentity("RAW_BYTES", "x"));
    }

    @Test
    void sidecarDiagnosticIdFraming() {
        String sidecarDigest = TextureDigests.sidecarDigest("PRESENT", "a.png.mcmeta",
            "MALFORMED", "COMPLETE", "ab12", "SHAPE", "DISCARDED", "DISCARDED");
        assertEquals(sidecarDigest,
            TextureDigests.sidecarDigest("PRESENT", "a.png.mcmeta",
                "MALFORMED", "COMPLETE", "ab12", "SHAPE", "DISCARDED", "DISCARDED"));
        assertEquals(framed("phase13.sidecarDiagnostic/v1", sidecarDigest),
            TextureDigests.sidecarDiagnosticId(sidecarDigest));
        String other = TextureDigests.sidecarDigest("ABSENT", "", "ABSENT", "NONE", "",
            "NONE", "OMITTED", "OMITTED");
        assertNotEquals(sidecarDigest, other);
        assertNotEquals(TextureDigests.sidecarDiagnosticId(sidecarDigest),
            TextureDigests.sidecarDiagnosticId(other));
    }
}
