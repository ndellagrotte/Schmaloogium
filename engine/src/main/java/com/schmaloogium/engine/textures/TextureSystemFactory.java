// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

import com.schmaloogium.engine.diag.DiagnosticReporter;
import com.schmaloogium.engine.gl.GLDevice;
import com.schmaloogium.engine.textures.internal.EngineTextureSystem;

/**
 * The texture-owner factory (§2.2). The factory creates an INACTIVE owner: no GL allocation,
 * {@code atlasSize} Unknown. Phase 7 alone retains build/close authority. The creating thread
 * is this owner's render thread for its whole lifetime.
 */
public interface TextureSystemFactory {

    TextureSystemCreationResult create(GLDevice gl, DiagnosticReporter diagnostics);

    /** The canonical factory. */
    static TextureSystemFactory factory() {
        return EngineTextureSystem::create;
    }
}
