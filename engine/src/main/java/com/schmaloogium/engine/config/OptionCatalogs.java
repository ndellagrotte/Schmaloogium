// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.config;

import java.util.List;

/** Public factory over the sealed catalog; the only way another package issues one. */
public final class OptionCatalogs {

    private OptionCatalogs() {
    }

    public static OptionCatalog create(List<OptionDefinition> definitions, Object packKey,
            boolean internalOrigin) {
        return new OptionCatalogValue(definitions, packKey, internalOrigin);
    }
}
