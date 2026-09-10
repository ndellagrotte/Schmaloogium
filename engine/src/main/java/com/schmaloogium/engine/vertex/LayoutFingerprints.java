// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.vertex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Deterministic SHA-256 canonical-text fingerprints for vertex layouts
 * (PHASE_10_DOC §2.2): a fingerprint identifies immutable layout content — ordered
 * field descriptors plus stride — and never acts as a credential.
 */
final class LayoutFingerprints {

    private LayoutFingerprints() {
    }

    /** Lower-case hex SHA-256 over the canonical descriptor text of the content. */
    static String of(int strideBytes, Iterable<VertexField> fields) {
        StringBuilder canonical = new StringBuilder("stride=").append(strideBytes);
        for (VertexField field : fields) {
            canonical.append('|').append(field.name())
                    .append('@').append(field.byteOffset())
                    .append(':').append(field.components())
                    .append('x').append(field.storage())
                    .append(':').append(field.delivery());
        }
        return sha256Hex(canonical.toString());
    }

    private static String sha256Hex(String canonical) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(canonical.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(Character.forDigit((b >> 4) & 0xf, 16))
                        .append(Character.forDigit(b & 0xf, 16));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
