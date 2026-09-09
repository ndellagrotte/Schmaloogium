// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The compiler-issued opaque candidate (PHASE_4_DOC §2.2). Minted only by the compiler around
 * its private registry and compiler-origin credential. Caller-owned until publication
 * acceptance; close is idempotent before transfer and a no-op after it. {@link #view()}
 * copies an immutable detached metadata snapshot that stays safe after close, rejection,
 * recovery or transfer and never keeps the registry or any GL handle alive.
 */
public final class CompiledRegistryCandidate implements AutoCloseable {

    private final com.schmaloogium.engine.registry.internal.CompiledProgramRegistryImpl registry;
    private volatile boolean closed;

    public CompiledRegistryCandidate(com.schmaloogium.engine.registry.internal.CompiledProgramRegistryImpl registry) {  // package-private mint
        this.registry = java.util.Objects.requireNonNull(registry, "registry");
    }

    public ProgramRegistryView view() {
        return registry.detachedView();
    }

    public com.schmaloogium.engine.registry.internal.CompiledProgramRegistryImpl registry() {
        return registry;
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        if (!registry.ownershipTransferred()) {
            registry.close();
        }
    }

    @Override
    public String toString() {
        return "CompiledRegistryCandidate[closed=" + closed + "]";
    }
}
