// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.expr.api;

import java.util.Objects;

/** Typed immutable upload submission commands (§5.4). The declared-type mapping is fixed:
 * float→Float1, int→Int1, bool→Bool1 (Phase 6 owns GL integer encoding), vecN→FloatN;
 * Phase 11 never uses Int1 as an undocumented bool convention (§4.8). */
public sealed interface CustomUploadCommand {

    String name();

    record Float1(String name, float value) implements CustomUploadCommand {
        public Float1 {
            Objects.requireNonNull(name, "name");
            if (!Float.isFinite(value)) {
                throw new IllegalArgumentException("value must be finite");
            }
        }
    }

    record Int1(String name, int value) implements CustomUploadCommand {
        public Int1 {
            Objects.requireNonNull(name, "name");
        }
    }

    record Bool1(String name, boolean value) implements CustomUploadCommand {
        public Bool1 {
            Objects.requireNonNull(name, "name");
        }
    }

    record Float2(String name, float x, float y) implements CustomUploadCommand {
        public Float2 {
            Objects.requireNonNull(name, "name");
            requireFinite(x, y);
        }
    }

    record Float3(String name, float x, float y, float z) implements CustomUploadCommand {
        public Float3 {
            Objects.requireNonNull(name, "name");
            requireFinite(x, y, z);
        }
    }

    record Float4(String name, float x, float y, float z, float w) implements CustomUploadCommand {
        public Float4 {
            Objects.requireNonNull(name, "name");
            requireFinite(x, y, z, w);
        }
    }

    private static void requireFinite(float... values) {
        for (float v : values) {
            if (!Float.isFinite(v)) {
                throw new IllegalArgumentException("components must be finite");
            }
        }
    }
}
