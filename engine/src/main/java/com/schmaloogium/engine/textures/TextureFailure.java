// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import java.util.Objects;

/**
 * The exact immutable failure record (§6). Every field non-null; {@code logicalTextureIdentity}
 * is the lowercase SHA-256 of the framed logical source identity or the fixed token
 * {@code TEXTURE_SYSTEM}; {@code messageKey} is {@code schmaloogium.error.texture.} plus the
 * lowercase code name; {@code diagnosticId} is the lowercase SHA-256 over
 * {@code phase13.failure/v1}, the code name and the logical identity. No source text, host
 * path, GL number or exception message crosses this record.
 */
public record TextureFailure(TextureFailureCode code, String messageKey,
                             String diagnosticId, String logicalTextureIdentity) {
    public TextureFailure {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(messageKey, "messageKey");
        Objects.requireNonNull(diagnosticId, "diagnosticId");
        Objects.requireNonNull(logicalTextureIdentity, "logicalTextureIdentity");
    }

    /** Canonical factory enforcing the closed key/id law (§6). */
    public static TextureFailure of(TextureFailureCode code, String logicalTextureIdentity) {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(logicalTextureIdentity, "logicalTextureIdentity");
        String messageKey = "schmaloogium.error.texture."
            + code.name().toLowerCase(java.util.Locale.ROOT);
        String diagnosticId = com.schmaloogium.engine.textures.internal.TextureDigests
            .failureDiagnosticId(code, logicalTextureIdentity);
        return new TextureFailure(code, messageKey, diagnosticId, logicalTextureIdentity);
    }

    /** The non-source-specific logical identity token (§6). */
    public static final String SYSTEM_IDENTITY = "TEXTURE_SYSTEM";
}
