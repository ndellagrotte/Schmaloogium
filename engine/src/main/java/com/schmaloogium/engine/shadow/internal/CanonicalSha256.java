// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.shadow.internal;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Canonical SHA-256 hashing for the shadow subsystem (PHASE_8_DOC §4.13.1, §2.2): UTF-8,
 * LF-terminated preimage text, lowercase 64-hex digest. The preimage is always supplied
 * complete — the hasher owns no formatting policy.
 */
public final class CanonicalSha256 {

    private CanonicalSha256() {
    }

    /** Lowercase 64-hex SHA-256 of the UTF-8 bytes of {@code preimage}. */
    public static String of(String preimage) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
        byte[] hash = digest.digest(preimage.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            hex.append(Character.forDigit((b >> 4) & 0xF, 16));
            hex.append(Character.forDigit(b & 0xF, 16));
        }
        return hex.toString();
    }
}
