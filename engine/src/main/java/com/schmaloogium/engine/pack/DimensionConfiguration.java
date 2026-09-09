// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.pack;

import com.schmaloogium.engine.preprocess.SourceKey;

import com.schmaloogium.engine.pack.DimensionKey;

import java.util.List;
import java.util.Optional;

public record DimensionConfiguration(
        DimensionKey key,
        DimensionMode mode,
        Optional<DimensionKey> baseDimension,
        List<SourceKey> sourceRoots) {

    public DimensionConfiguration {
        java.util.Objects.requireNonNull(key, "key");
        java.util.Objects.requireNonNull(mode, "mode");
        baseDimension = baseDimension == null ? Optional.empty() : baseDimension;
        sourceRoots = List.copyOf(sourceRoots);
    }
}
