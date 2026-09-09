// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.registry;

/**
 * The owning compiled registry (PHASE_4_DOC §2.2). Close is render-thread-only and idempotent
 * and deletes only Phase-4-owned handles; detached views survive it unchanged.
 */
public interface CompiledProgramRegistry extends ProgramRegistryView, AutoCloseable {

    @Override
    void close();
}
