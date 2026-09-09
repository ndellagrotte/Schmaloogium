// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.compat;

/**
 * The narrower context an {@link EarlyCompatCheck} sees (PHASE_1_DOC §4.10, D-P1-50):
 * resource/metadata probes only — no {@code Class.forName} initialization, no Minecraft
 * singletons, no Forge mod-list assumptions, no GL capability profile.
 */
public interface EarlyCompatContext {

    boolean isClassPresent(String binaryName);
}
