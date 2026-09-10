// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.textures;

/** Pure/total planning result (§2.2): Planned or Invalid, never partial. */
public sealed interface TexturePlanResult {
    record Planned(TexturePlan plan) implements TexturePlanResult {
        public Planned {
            java.util.Objects.requireNonNull(plan, "plan");
        }
    }

    record Invalid(TextureFailure failure) implements TexturePlanResult {
        public Invalid {
            java.util.Objects.requireNonNull(failure, "failure");
        }
    }
}
