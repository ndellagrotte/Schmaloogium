// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.pack.NormalizedPackPath;

public record DecisionSource(NormalizedPackPath path, int logicalLineCount, String sha256) {

    public DecisionSource {
        java.util.Objects.requireNonNull(path, "path");
        java.util.Objects.requireNonNull(sha256, "sha256");
    }
}
