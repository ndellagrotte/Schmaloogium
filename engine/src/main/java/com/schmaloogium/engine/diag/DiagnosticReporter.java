// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.diag;

/**
 * Receives {@link EngineDiagnostic}s. The engine holds one; in {@code :mod} the
 * implementation fans out (CHAT to the client player, SHADER_GUI into the per-pack
 * error store, everything to the log).
 */
public interface DiagnosticReporter {

    void report(EngineDiagnostic d);
}
