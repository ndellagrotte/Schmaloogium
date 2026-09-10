// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

/**
 * Closed lease-rejection domain (§4.5.2) in exact check order, every rejection before the
 * lease count increments.
 */
public enum TextureLeaseRejection {
    PUBLICATION_UNAVAILABLE, PUBLICATION_ID_MISMATCH, REGISTRY_FINGERPRINT_MISMATCH,
    STALE_SELECTION, INVALID_BASE_BINDING, STALE_BASE_BINDING
}
