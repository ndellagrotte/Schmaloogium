// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

/** Durable serialized filesystem-selection reference: {@code d:}/{@code a:} + %HH NFC name. */
public record FilesystemCandidateReference(String canonicalValue) implements java.io.Serializable {

    public FilesystemCandidateReference {
        FilesystemCandidateReferences.validate(canonicalValue);
    }
}
