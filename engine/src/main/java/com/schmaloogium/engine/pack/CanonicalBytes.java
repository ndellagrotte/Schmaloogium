// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

final class CanonicalBytes {

    private CanonicalBytes() {
    }

    /** atom(s): decimal UTF-8 byte length, ':', then the exact UTF-8 bytes. */
    static byte[] atom(String s) {
        byte[] raw = s.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] prefix = Integer.toString(raw.length).getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        byte[] out = new byte[prefix.length + 1 + raw.length];
        System.arraycopy(prefix, 0, out, 0, prefix.length);
        out[prefix.length] = ':';
        System.arraycopy(raw, 0, out, prefix.length + 1, raw.length);
        return out;
    }

    /** decimal element count, '[', per-element decimal length + ':' + bytes, ']'. */
    static byte[] seq(byte[]... elements) {
        byte[] count = Integer.toString(elements.length).getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        int total = count.length + 1;
        byte[][] framed = new byte[elements.length][];
        for (int i = 0; i < elements.length; i++) {
            byte[] len = Integer.toString(elements[i].length)
                .getBytes(java.nio.charset.StandardCharsets.US_ASCII);
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

    static long sizeOf(byte[] bytes) {
        return bytes.length;
    }
}
