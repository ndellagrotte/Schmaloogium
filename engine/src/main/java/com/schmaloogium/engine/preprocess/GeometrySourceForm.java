// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import java.util.List;

public sealed interface GeometrySourceForm {
    record None() implements GeometrySourceForm {}
    record CoreLayout(GeometryLayout effective, List<GeometryLayoutDeclaration> declarations)
        implements GeometrySourceForm {

        public CoreLayout {
            java.util.Objects.requireNonNull(effective, "effective");
            declarations = List.copyOf(declarations);
        }
    }

    record NativeLegacy(LegacyGeometryConfig config, GeometryLayout effective,
        List<GeometryLayoutDeclaration> declarations) implements GeometrySourceForm {

        public NativeLegacy {
            java.util.Objects.requireNonNull(config, "config");
            java.util.Objects.requireNonNull(effective, "effective");
            declarations = List.copyOf(declarations);
        }
    }
}
