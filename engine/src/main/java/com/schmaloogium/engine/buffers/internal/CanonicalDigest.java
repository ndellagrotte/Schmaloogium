// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.buffers.internal;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * P3 §4.10 canonical length-prefixed framing for Phase 5 digests (mirrors the Phase 4
 * framing convention; PHASE_5_DOC §4.12.1): {@code atom(s)} is the decimal UTF-8 byte
 * length, {@code ':'}, then the exact bytes; {@code seq(v...)} is the decimal element
 * count, {@code '['}, each element as decimal byte length {@code ':'} bytes, then
 * {@code ']'}. Digests are SHA-256 lowercase hex. Producer canonicalization, not a
 * portable wire format.
 */
public final class CanonicalDigest {

    /** Fixed-sampler-policy digest domain/schema tag (PHASE_5_DOC §4.12.1). */
    public static final String SAMPLER_POLICY_DOMAIN = "FixedSamplerPolicy/v1";

    private CanonicalDigest() {
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

    /** Convenience: sequence over string elements. */
    public static byte[] seqOfStrings(String... values) {
        byte[][] parts = new byte[values.length][];
        for (int i = 0; i < values.length; i++) {
            parts[i] = atom(values[i]);
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
