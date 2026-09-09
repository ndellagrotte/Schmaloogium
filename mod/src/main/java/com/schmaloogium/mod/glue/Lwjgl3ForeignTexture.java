// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.glue;

import com.schmaloogium.engine.gl.TextureHandle;

import java.util.Optional;
import java.util.function.IntSupplier;

/**
 * An ORDINARY FOREIGN texture — one Minecraft owns (PHASE_1_DOC §4.7.3, [D-P1-36]).
 * The same {@link TextureHandle} type as an engine-created texture under a narrower
 * contract: bind-and-label only, outside the handle-lifetime rule. The GL name is never
 * captured at issuance: the handle resolves the underlying object at EACH USE, so
 * validity across a vanilla reload is the backend's problem, not the caller's. Backend
 * authentication is the concrete class plus the owning-device identity — the public
 * marker alone proves nothing ([D-P1-40]).
 */
final class Lwjgl3ForeignTexture implements TextureHandle, Lwjgl3Handle {

    private final Lwjgl3GLDevice device;
    private final String key;
    private final IntSupplier resolver;

    Lwjgl3ForeignTexture(Lwjgl3GLDevice owner, String key, IntSupplier resolver) {
        this.device = owner;
        this.key = key;
        this.resolver = resolver;
    }

    @Override
    public Lwjgl3GLDevice owner() {
        return device;
    }

    @Override
    public boolean deleted() {
        return false; // foreign objects are never lifetime-managed by this backend
    }

    @Override
    public String subjectLabel() {
        return key;
    }

    String key() {
        return key;
    }

    /**
     * Resolves the native name at use time. Empty when the platform no longer (or not
     * yet) has the texture under this key.
     */
    Optional<Integer> resolve() {
        try {
            int name = resolver.getAsInt();
            return name > 0 ? Optional.of(name) : Optional.empty();
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }
}
