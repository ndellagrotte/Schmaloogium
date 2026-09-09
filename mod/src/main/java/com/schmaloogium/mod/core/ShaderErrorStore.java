// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.core;

import com.schmaloogium.engine.diag.EngineDiagnostic;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Per-pack error store for {@code SHADER_GUI} diagnostics (PHASE_1_DOC §4.9.4). The
 * store exists at v0.1; Phase 12's screen renders it (§9: routing to SHADER_GUI is the
 * v0.4 milestone — the sink arrives with the screen).
 */
public final class ShaderErrorStore {

    private static final List<EngineDiagnostic> DIAGNOSTICS = new CopyOnWriteArrayList<>();

    private ShaderErrorStore() {
    }

    public static void add(EngineDiagnostic d) {
        DIAGNOSTICS.add(d);
    }

    public static List<EngineDiagnostic> snapshot() {
        return List.copyOf(DIAGNOSTICS);
    }

    public static void clear() {
        DIAGNOSTICS.clear();
    }
}
