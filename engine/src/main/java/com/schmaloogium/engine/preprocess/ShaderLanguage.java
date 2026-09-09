// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import java.util.List;
import java.util.Optional;

public record ShaderLanguage(
        int version, boolean explicitVersion, Optional<GlslProfile> explicitProfile,
        List<ShaderExtensionDirective> extensions) {

    public ShaderLanguage {
        explicitProfile = explicitProfile == null ? Optional.empty() : explicitProfile;
        extensions = List.copyOf(extensions);
    }

    /** The absent-directive language: version 110, no explicit version or profile. */
    public static ShaderLanguage implicit110() {
        return new ShaderLanguage(110, false, Optional.empty(), List.of());
    }
}
