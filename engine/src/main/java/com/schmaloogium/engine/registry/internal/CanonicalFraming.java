// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry.internal;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

/**
 * The P3 §4.10 canonical length-prefixed framing used by every Phase 4 digest (D-P4-43):
 * {@code atom(s)} is the decimal UTF-8 byte length, {@code ':'}, then the exact bytes;
 * {@code seq(v...)} is the decimal element count, {@code '['}, each element as decimal byte
 * length {@code ':'} bytes, then {@code ']'}. Booleans/ints/enums/strings are atoms;
 * optionals are {@code seq(atom("Absent"))} / {@code seq(atom("Present"), v)}. Digests are
 * SHA-256 hex. Producer canonicalization, not a portable wire format.
 */
public final class CanonicalFraming {

    /** Uniform-layout digest for the canonical empty layout. */
    public static final String EMPTY_UNIFORM_LAYOUT_DIGEST =
        sha256Hex(seq(atom("ProgramUniformLayout/v1"), atom("empty")));

    /** Uniform-layout digest domain tag. */
    public static final String UNIFORM_LAYOUT_DOMAIN = "ProgramUniformLayout/v1";
    /** Sampler-layout digest domain tag. */
    public static final String SAMPLER_LAYOUT_DOMAIN = "ProgramSamplerLayout/v1";
    /** Fixed-sampler-policy digest domain tag. */
    public static final String SAMPLER_POLICY_DOMAIN = "FixedSamplerPolicy/v1";
    /** Registry fingerprint domain tag (D-P4-43). */
    public static final String REGISTRY_DOMAIN = "RegistryFingerprint/profile-selection-v3";

    private CanonicalFraming() {
    }

    /** One length-prefixed UTF-8 atom. */
    public static byte[] atom(String s) {
        byte[] body = s.getBytes(StandardCharsets.UTF_8);
        return concat(Integer.toString(body.length).getBytes(StandardCharsets.UTF_8),
            new byte[] {':'}, body);
    }

    /** One length-prefixed atom of a base-10 integer. */
    public static byte[] atom(int v) {
        return atom(Integer.toString(v));
    }

    /** Boolean atoms are exactly {@code true}/{@code false}. */
    public static byte[] atom(boolean v) {
        return atom(Boolean.toString(v));
    }

    /** A counted sequence over pre-encoded elements. */
    public static byte[] seq(byte[]... elements) {
        StringBuilder count = new StringBuilder().append(elements.length).append('[');
        byte[] head = count.toString().getBytes(StandardCharsets.UTF_8);
        byte[][] framed = new byte[elements.length][];
        int total = head.length + 1;
        for (int i = 0; i < elements.length; i++) {
            byte[] len = Integer.toString(elements[i].length).getBytes(StandardCharsets.UTF_8);
            framed[i] = concat(len, new byte[] {':'}, elements[i]);
            total += framed[i].length;
        }
        byte[] out = new byte[total];
        int pos = 0;
        pos = copy(out, pos, head);
        for (byte[] framedElement : framed) {
            pos = copy(out, pos, framedElement);
        }
        out[pos] = ']';
        return out;
    }

    /** Optional encoding: Absent or Present plus the encoded value. */
    public static byte[] optional(boolean present, byte[] valueIfPresent) {
        return present ? seq(atom("Present"), valueIfPresent) : seq(atom("Absent"));
    }

    /** Optional string encoding. */
    public static byte[] optionalString(Optional<String> value) {
        return optional(value.isPresent(), atom(value.orElse("")));
    }

    /** Convenience: sequence over string elements. */
    public static byte[] seqOfStrings(List<String> values) {
        byte[][] parts = new byte[values.size()][];
        for (int i = 0; i < values.size(); i++) {
            parts[i] = atom(values.get(i));
        }
        return seq(parts);
    }

    /** SHA-256 of the framed payload, lowercase hex. */
    public static String sha256Hex(byte[]... payloadParts) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
        for (byte[] part : payloadParts) {
            digest.update(part);
        }
        StringBuilder hex = new StringBuilder(64);
        for (byte b : digest.digest()) {
            hex.append(Character.forDigit((b >> 4) & 0xF, 16));
            hex.append(Character.forDigit(b & 0xF, 16));
        }
        return hex.toString();
    }

    private static byte[] concat(byte[] a, byte[] b, byte[] c) {
        byte[] out = new byte[a.length + b.length + c.length];
        int pos = copy(out, 0, a);
        pos = copy(out, pos, b);
        copy(out, pos, c);
        return out;
    }

    private static int copy(byte[] dst, int pos, byte[] src) {
        System.arraycopy(src, 0, dst, pos, src.length);
        return pos + src.length;
    }
}
