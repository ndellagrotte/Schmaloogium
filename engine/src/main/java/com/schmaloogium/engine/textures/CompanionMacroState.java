// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

/**
 * The required pre-load macro pair (PHASE_13_DOC §2.2/§4.1.6). Produced before Phase 3
 * load/jcpp; each bit is active pack AND fixed-unit capability AND that independent decoded
 * user preference. Never derived from linked-program demand.
 */
public record CompanionMacroState(boolean normalMap, boolean specularMap) {

    public static final CompanionMacroState OFF = new CompanionMacroState(false, false);
}
