// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

final class NormalizedPackPaths {

    private NormalizedPackPaths() {
    }

    /** Validates the canonical grammar; rejects rather than normalizes. */
    static void validate(String value) {
        java.util.Objects.requireNonNull(value, "canonicalString");
        if (value.isEmpty()) {
            throw new IllegalArgumentException("canonical path must be non-empty");
        }
        if (!java.text.Normalizer.isNormalized(value, java.text.Normalizer.Form.NFC)) {
            throw new IllegalArgumentException("canonical path must be Unicode NFC");
        }
        if (value.charAt(0) == '/' || value.charAt(value.length() - 1) == '/') {
            throw new IllegalArgumentException("canonical path must not lead/trail with '/'");
        }
        if (value.indexOf('\\') >= 0) {
            throw new IllegalArgumentException("canonical path must not contain a backslash");
        }
        if (value.indexOf('\u0000') >= 0) {
            throw new IllegalArgumentException("canonical path must not contain NUL");
        }
        String[] segments = value.split("/", -1);
        if (segments[0].matches("(?i)[a-z]:.*") || segments[0].contains(":")) {
            throw new IllegalArgumentException("canonical path must not carry a drive/URI prefix");
        }
        for (String segment : segments) {
            if (segment.isEmpty()) {
                throw new IllegalArgumentException("canonical path must not contain an empty segment");
            }
            if (segment.equals(".") || segment.equals("..")) {
                throw new IllegalArgumentException("canonical path must not contain '.' or '..'");
            }
        }
    }
}
