// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 the Schmaloogium contributors

package com.schmaloogium.engine.preprocess;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record SourceMap(Map<Integer, SourceId> files, List<SourceMapping> mappings) {

    public SourceMap {
        files = Map.copyOf(files);
        mappings = List.copyOf(mappings);
    }

    public Optional<SourceId> sourceForFileNumber(int fileNumber) {
        return Optional.ofNullable(files.get(fileNumber));
    }
}
