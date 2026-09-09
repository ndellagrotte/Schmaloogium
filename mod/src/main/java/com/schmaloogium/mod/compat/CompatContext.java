// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.mod.compat;

import com.schmaloogium.engine.gl.GLCapabilityProfile;

/** What a {@link CompatCheck} may probe (PHASE_1_DOC §4.10). */
public interface CompatContext {

    boolean isModLoaded(String modId);

    /** For detecting a replacement that ships unnamed. */
    boolean isClassPresent(String binaryName);

    /** Capability gates are compat checks too. */
    GLCapabilityProfile capabilities();
}
