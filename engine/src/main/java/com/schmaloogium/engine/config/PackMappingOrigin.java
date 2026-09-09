// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import com.schmaloogium.engine.pack.NormalizedPackPath;
import com.schmaloogium.engine.pack.PackIdentity;

/** Pack-load origin: the selected identity and the mapping-file path. */
public record PackMappingOrigin(PackIdentity pack, NormalizedPackPath source)
        implements MappingOrigin {

    public PackMappingOrigin {
        java.util.Objects.requireNonNull(pack, "pack");
        java.util.Objects.requireNonNull(source, "source");
    }
}
