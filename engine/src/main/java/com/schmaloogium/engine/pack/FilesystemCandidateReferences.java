// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

final class FilesystemCandidateReferences {

    private static final java.util.regex.Pattern ENCODED =
        java.util.regex.Pattern.compile("(?s)(?:[0-9A-F]{2})*");

    private FilesystemCandidateReferences() {
    }

    /** Builds the durable reference for one direct-child entry name. */
    static FilesystemCandidateReference ofDirectory(String name) {
        return build('d', name);
    }

    /** Builds the durable reference for one direct-child archive entry name. */
    static FilesystemCandidateReference ofArchive(String name) {
        return build('a', name);
    }

    /** Builds the durable reference for one direct-child entry name. */
    static FilesystemCandidateReference build(char kind, String name) {
        java.util.Objects.requireNonNull(name, "name");
        return new FilesystemCandidateReference(encode(kind, name));
    }

    /** Canonical encoding: kind prefix + uppercase hex bytes of the NFC UTF-8 name. */
    private static String encode(char kind, String name) {
        String nfc = java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFC);
        byte[] bytes = nfc.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder(2 + 2 * bytes.length);
        sb.append(kind).append(':');
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /** Validates the canonical {@code d:}/{@code a:} + uppercase %HH form. */
    static void validate(String value) {
        java.util.Objects.requireNonNull(value, "canonicalValue");
        if (value.length() < 2 || (value.charAt(0) != 'd' && value.charAt(0) != 'a')
                || value.charAt(1) != ':') {
            throw new IllegalArgumentException("reference must start with d: or a:");
        }
        String encoded = value.substring(2);
        if (encoded.isEmpty()) {
            throw new IllegalArgumentException("reference name must be non-empty");
        }
        if (!ENCODED.matcher(encoded).matches()) {
            throw new IllegalArgumentException("reference payload must be uppercase %HH bytes");
        }
        byte[] raw = new byte[encoded.length() / 2];
        for (int i = 0; i < raw.length; i++) {
            raw[i] = (byte) Integer.parseInt(encoded.substring(2 * i, 2 * i + 2), 16);
        }
        String decoded = new String(raw, java.nio.charset.StandardCharsets.UTF_8);
        String nfc = java.text.Normalizer.normalize(decoded, java.text.Normalizer.Form.NFC);
        if (!nfc.equals(decoded)) {
            throw new IllegalArgumentException("reference name must be NFC");
        }
        // Round-trip: re-encoding the decoded name must reproduce the exact value.
        if (!encode(value.charAt(0), decoded).equals(value)) {
            throw new IllegalArgumentException("reference is not canonical for its decoded name");
        }
    }

    /** Decodes the direct-child entry name (diagnostic/display use only). */
    static String decode(FilesystemCandidateReference reference) {
        String encoded = reference.canonicalValue().substring(2);
        byte[] raw = new byte[encoded.length() / 2];
        for (int i = 0; i < raw.length; i++) {
            raw[i] = (byte) Integer.parseInt(encoded.substring(2 * i, 2 * i + 2), 16);
        }
        return new String(raw, java.nio.charset.StandardCharsets.UTF_8);
    }
}
