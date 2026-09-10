// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures.internal;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Phase 3 §4.10 canonical framing, reimplemented for the phase13 digest domains (the P3
 * helper is package-private): base-10 ASCII scalars; UTF-8 strings as decimal byte-length +
 * ':'; lists as element count, '[', per-element length-prefixed bytes, ']'.
 */
final class CanonicalFraming {

    private CanonicalFraming() {
    }

    /** atom(s): decimal UTF-8 byte length, ':', then the exact UTF-8 bytes. */
    static byte[] atom(String s) {
        byte[] raw = s.getBytes(StandardCharsets.UTF_8);
        byte[] prefix = Integer.toString(raw.length).getBytes(StandardCharsets.US_ASCII);
        byte[] out = new byte[prefix.length + 1 + raw.length];
        System.arraycopy(prefix, 0, out, 0, prefix.length);
        out[prefix.length] = ':';
        System.arraycopy(raw, 0, out, prefix.length + 1, raw.length);
        return out;
    }

    /** atom(long): base-10 ASCII scalar without padding. */
    static byte[] atom(long v) {
        return atom(Long.toString(v));
    }

    /** decimal element count, '[', per-element decimal length + ':' + bytes, ']'. */
    static byte[] seq(byte[]... elements) {
        byte[] count = Integer.toString(elements.length).getBytes(StandardCharsets.US_ASCII);
        int total = count.length + 1;
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
        total += 1; // closing ']'
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

    static byte[] concat(byte[]... parts) {
        int total = 0;
        for (byte[] part : parts) {
            total += part.length;
        }
        byte[] out = new byte[total];
        int pos = 0;
        for (byte[] part : parts) {
            System.arraycopy(part, 0, out, pos, part.length);
            pos += part.length;
        }
        return out;
    }

    /** Lowercase 64-hex SHA-256 digest of the exact bytes. */
    static String sha256Hex(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(bytes);
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    static byte[] utf8(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }
}
