// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

/** Closed texture failure classes (§2.3/§6); first reached class wins, preflight first. */
public enum TextureFailureCode {
    INVALID_REQUEST, WRONG_THREAD, OWNER_UNAVAILABLE, IDENTITY_MISMATCH,
    SOURCE_UNAVAILABLE, SOURCE_DECODE_FAILED, SOURCE_SIZE_INVALID,
    TARGET_FORMAT_UNSUPPORTED, PARAMETERIZATION_UNSUPPORTED,
    BACKEND_FAILURE, UNEXPECTED_INTERNAL
}
