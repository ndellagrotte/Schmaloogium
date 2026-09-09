// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.gl;

import java.util.Optional;
import java.util.Objects;

/**
 * Where {@code :engine} holds the installed {@link ForeignTextureProvider} (PHASE_1_DOC
 * §4.7.3): the same shape §4.9.1 uses for {@code LogSink} — a {@code :mod}-implemented SPI
 * that {@code :engine} reaches from wherever it binds. Must be an {@code :engine} type: a
 * provider declared in {@code mod.glue} and consumed by {@code :engine} would invert the
 * {@code :mod → :engine} edge.
 *
 * <p>{@code mod.core} installs it at §4.13 stage 2, where the GL context and the engine
 * bootstrap already are; resolution is lazy per call, so installation does not require the
 * vanilla textures to exist yet. Render thread, like every other {@code engine.gl} call.
 * Before install — and in every headless test that does not script one — {@link #active()}
 * answers empty for every key, the same degradation {@code LogSink} takes.
 */
public final class ForeignTextures {

    private static final ForeignTextureProvider ABSENT = key -> Optional.empty();

    private static volatile ForeignTextureProvider active = ABSENT;

    private ForeignTextures() {
    }

    /** Installs the provider. Called once by {@code mod.core} at §4.13 stage 2. */
    public static void install(ForeignTextureProvider provider) {
        active = Objects.requireNonNull(provider, "provider");
    }

    /** The installed provider; before install, answers empty for every key. */
    public static ForeignTextureProvider active() {
        return active;
    }
}
