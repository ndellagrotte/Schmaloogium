// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core.proxy;

/**
 * Side boundary (AGENTS.md: the template entry point's unconditional
 * {@code Minecraft.getMinecraft()} call is not a server-safe example; keep client-only
 * work behind this boundary). Phase 7 adds the frame-driver surface; Phase 12 adds the
 * GUI surface.
 */
public interface IProxy {

    /**
     * Installs side-specific engine services during {@code preInit}. The client installs
     * the chat/GUI diagnostic router; the common side installs nothing (engine
     * diagnostics then fall back to log-only, which is correct on a server).
     */
    default void installSideServices() {
    }
}
