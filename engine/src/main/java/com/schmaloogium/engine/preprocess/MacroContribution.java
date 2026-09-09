// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

/** The singular reserved Phase 6 contribution. */
public sealed interface MacroContribution {
    record Empty() implements MacroContribution {}
    record DefineCenterDepthSmooth(String replacementTokens) implements MacroContribution {

        public DefineCenterDepthSmooth {
            java.util.Objects.requireNonNull(replacementTokens, "replacementTokens");
        }
    }
}
